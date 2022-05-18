package com.rookie.community.controller;

import com.rookie.community.pojo.DiscussPost;
import com.rookie.community.pojo.User;
import com.rookie.community.service.DiscussPostServiceImpl;
import com.rookie.community.service.LikeServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.HostHolder;
import com.rookie.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MyDiscuss implements CommunityConstant {

    @Autowired
    private DiscussPostServiceImpl discussPostService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private LikeServiceImpl likeService;
    @Autowired
    private HostHolder hostHolder;


    @GetMapping({"/myDiscuss/{userId}"})
    public String getIndexPage(Model model, Page page,@PathVariable("userId") int userId){
        page.setLimit(POST_LIMIT);
        page.setPath("/myDiscuss/"+userId);
        page.setRows(discussPostService.selectDiscussPostRows(userId));
        List<DiscussPost> discussPostList = discussPostService.selectDiscussPosts(userId, page.getOffset(), page.getLimit(),0);
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
        model.addAttribute("user", userService.findUserById(userId));

        return "/site/myDiscuss";
    }
}

