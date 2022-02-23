package com.rookie.community.controller;

import com.rookie.community.pojo.DiscussPost;
import com.rookie.community.pojo.User;
import com.rookie.community.service.DiscussPostServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    DiscussPostServiceImpl discussPostService;
    @GetMapping({"/index","/"})
    public String getIndexPage(Model model, Page page){
        page.setPath("/index");
        page.setRows(discussPostService.selectDiscussPostRows(0));
        List<DiscussPost> discussPostList = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        for (DiscussPost post : discussPostList) {
            Map<String,Object> map = new HashMap<>();
            map.put("post", post);
            User user = userService.selectById(post.getUserId());
            map.put("user",user);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("page",page);

        return "/index";
    }

}
