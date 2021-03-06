package com.rookie.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.rookie.community.annotation.LoginRequired;
import com.rookie.community.config.AliyunConfig;
import com.rookie.community.pojo.User;
import com.rookie.community.service.FollowServiceImpl;
import com.rookie.community.service.LikeServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.CommunityUtil;
import com.rookie.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    String uploadPath;
    @Value("${community.path.domain}")
    String domain;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${aliyun.oss.headUrl}")
    private String headUrl;

    @Value("${qinniu.key.access}")
    private String accessKey;
    @Value("${qinniu.key.secret}")
    private String secretKey;
    @Value("${qinniu.bucket.header.name}")
    private String headerBucketName;
    @Value("${qinniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private LikeServiceImpl likeService;
    @Autowired
    private FollowServiceImpl followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private AliyunConfig aliyunConfig;


    //??????
    @LoginRequired
    @GetMapping("/modifyHead")
    public String getSettingPage(Model model) {
        //???????????????????????????
        String fileName = CommunityUtil.generateUUID();
        //?????????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        //??????????????????
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);

        return "site/modifyHead";
    }

    //??????????????????
    @PostMapping("/header/url")
    @ResponseBody
    public String uploadHeaderUrl(String fileName){
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1,"????????????????????????");
        }

        String url = headerBucketUrl + "/" +fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }


    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "???????????????????????????");
            return "/site/modifyHead";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/modifyHead";
        }
        User user = hostHolder.getUser();

//        ?????????????????????
        filename = CommunityUtil.generateUUID() + suffix;
        try {
            String headerUrl = aliyunConfig.uploadFileImg(headerImage, headUrl+filename);

            //        ????????????????????????????????????
            userService.updateHeader(user.getId(), headerUrl);
        } catch (IOException e) {
            throw new IllegalArgumentException("?????????????????????"+e.getMessage());
        }
        return "redirect:/user/profile/"+user.getId();
    }

    //??????
    //???????????????user??????????????????
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
//        ?????????????????????
        fileName = uploadPath + "/" + fileName;
//        ????????????
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //????????????
        response.setContentType("image/" + suffix);

        ServletOutputStream os =null;
        FileInputStream fis = null;
        try {
            os = response.getOutputStream();
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer,0,len);
            }
        } catch (IOException e) {
            logger.error("???????????????????????????" + e.getMessage());
        } finally {
            try {
                if (fis!=null)
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os!=null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //????????????
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw  new RuntimeException("?????????????????????");
        }

        //??????
        model.addAttribute("user",user);
        //????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //???????????????
        boolean hasFollowed  = false;
        if (hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";

    }

    //?????????????????????
    @GetMapping("/setting")
    public String getSettingPage1() {
        return "site/setting";
    }
    //????????????
    @PostMapping("/pwdModify")
    public String pwdModify(Model model,String oldpassword,String newpassword,String rnewpassword,@CookieValue("ticket") String ticket){
        model.addAttribute("oldpassword",oldpassword);
        model.addAttribute("newpassword",newpassword);
        model.addAttribute("rnewpassword",rnewpassword);
        final User user = hostHolder.getUser();
        if (oldpassword==null||oldpassword.equals("")){
            model.addAttribute("oldpasswordMsg", "????????????????????????");
           return  "site/setting";
        }
        oldpassword += user.getSalt();

        if (!CommunityUtil.md5(oldpassword).equals(user.getPassword())){
            model.addAttribute("oldpasswordMsg", "?????????????????????");
            return  "site/setting";
        }
        if (newpassword==null||newpassword.equals("")){
            model.addAttribute("newpasswordMsg", "?????????????????????");
            return  "site/setting";
        }
        if (!newpassword.equals(rnewpassword)){
            model.addAttribute("rnewpasswordMsg", "????????????????????????");
            return  "site/setting";
        }
        userService.updatePassword(user.getId(), CommunityUtil.md5(newpassword += user.getSalt()));
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }


}
