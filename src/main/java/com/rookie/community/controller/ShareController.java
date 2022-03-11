package com.rookie.community.controller;

import com.rookie.community.event.EventProducer;
import com.rookie.community.pojo.Event;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qinniu.bucket.share.name}")
    private String shareBucketName;
    @Value("${qinniu.bucket.share.url}")
    private String shareBucketUrl;

    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl) {
        //生成随机文件名
        String fileName = CommunityUtil.generateUUID();

        //异步的生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        //返回访问路径
        Map<String,Object> map = new HashMap<>();
//        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);
        map.put("shareUrl",shareBucketUrl+"/"+fileName);
        return CommunityUtil.getJSONString(0,null, map);

    }

    //废弃
    //获取长图
    @GetMapping("/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }

        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        ServletOutputStream os = null;
        FileInputStream fis = null;
        try {
            os = response.getOutputStream();
            fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len=0;
            while ((len=fis.read(buffer))!= 0){
                os.write(buffer,0,len);
            }
        } catch (IOException e) {
            logger.error("获取长图失败： " + e.getMessage());
        }finally {
            if (os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error("关闭输出流失败：" + e.getMessage());
                }
            }

            if (fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
