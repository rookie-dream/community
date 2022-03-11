package com.rookie.community.controller;

import com.rookie.community.pojo.DiscussPost;
import com.rookie.community.pojo.User;
import com.rookie.community.service.DiscussPostServiceImpl;
import com.rookie.community.service.LikeServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private LikeServiceImpl likeService;

    @GetMapping({"/index","/"})
    public String getIndexPage(Model model, Page page,@RequestParam(name ="orderMode",defaultValue = "0") int orderMode){
        page.setLimit(POST_LIMIT);

        page.setPath("/index?orderMode=" + orderMode);
        page.setRows(discussPostService.selectDiscussPostRows(0));
        List<DiscussPost> discussPostList = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        for (DiscussPost post : discussPostList) {
            Map<String,Object> map = new HashMap<>();
            map.put("post", post);
            User user = userService.findUserById(post.getUserId());
            map.put("user",user);
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
            map.put("likeCount",likeCount);

            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("page",page);
        model.addAttribute("orderMode", orderMode);

        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "error/404";
    }



}
