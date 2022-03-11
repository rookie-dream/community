package com.rookie.community.controller;

import com.rookie.community.event.EventProducer;
import com.rookie.community.pojo.Event;
import com.rookie.community.pojo.User;
import com.rookie.community.service.FollowServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.CommunityUtil;
import com.rookie.community.util.HostHolder;
import com.rookie.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowServiceImpl followService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    //关注
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType,int entityId) {
        User user = hostHolder.getUser();
        if (user==null){
            return CommunityUtil.getJSONString(1, "请您先登录！");
        }
        followService.follow(user.getId(), entityType, entityId);

        //出发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "已关注！");
    }
    //取消关注
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType,int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注！");
    }
    //关注列表
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw  new IllegalArgumentException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(FOLLOWEE_LIMIT);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String,Object>> userList = followService.findFollowees(userId,page.getOffset(), page.getLimit());

        if (userList!=null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

    //粉丝列表
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw  new IllegalArgumentException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(FOLLOWER_LIMIT);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER,userId));
        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getOffset(), page.getLimit());

        if (userList!=null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }
    //判断当前用户是否关注
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),CommunityConstant.ENTITY_TYPE_USER,userId);
    }
}
