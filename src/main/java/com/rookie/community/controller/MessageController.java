package com.rookie.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.rookie.community.pojo.Message;
import com.rookie.community.pojo.User;
import com.rookie.community.service.MessageServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.CommunityUtil;
import com.rookie.community.util.HostHolder;
import com.rookie.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController implements CommunityConstant {
    //私信列表
    @Autowired
    private MessageServiceImpl messageService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HostHolder hostHolder;

    //私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(LETTER_LIMIT);
        page.setPath("/message/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //查询私信未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //查询通知未读消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";

    }

    //私信详情
    @GetMapping("/letter/detail/{conversationId}")
    private String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model, @RequestParam("current") int lastCurrent) {

        User user = hostHolder.getUser();
        page.setLimit(LETTER_DETAIL_LIMIT);
        page.setPath("/message/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {

            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                User user1 = userService.findUserById(message.getFromId());
                if (user.getId() == user1.getId()) {
                    user1.setUsername("我");
                }
                map.put("fromUser", user1);
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        //上一级页数
        model.addAttribute("lastCurrent", lastCurrent);

        //设置成已读
        List<Integer> ids = getLetterIds(letterList);
        if (ids.size()!=0){
            messageService.readMessage(ids);
        }


        return "/site/letter-detail";
    }


    //返回私信对象
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        User user = hostHolder.getUser();
        int id1 = Integer.parseInt(ids[0]);
        int id2 = Integer.parseInt(ids[1]);
        if (user.getId() == id1) {
            return userService.findUserById(id2);
        } else {
            return userService.findUserById(id1);
        }
    }

    //提取未读的消息
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList!=null) {
            for (Message message : letterList) {
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    //发私信
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        //目标用户
        User target = userService.findUserByName(toName);
        //当前用户
        User user = hostHolder.getUser();
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        if (content==null||content.equals("")){
            return CommunityUtil.getJSONString(1);
        }
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        //查询评论类的通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String,Object> messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message!=null) {
            HashMap data = JSONObject.parseObject(message.getContent(), HashMap.class);
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread",unreadCount);
        }
        model.addAttribute("commentNotice",messageVO);

        //查询点赞类的通知
       message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message!=null) {
            HashMap data = JSONObject.parseObject(message.getContent(), HashMap.class);
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread",unreadCount);
        }
        model.addAttribute("likeNotice",messageVO);
        //查询关注类的通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message!=null) {
            HashMap data = JSONObject.parseObject(message.getContent(), HashMap.class);
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread",unreadCount);
        }
        model.addAttribute("followNotice",messageVO);

        //查询私信未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //查询通知未读消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model) {
        User user = hostHolder.getUser();
        page.setLimit(NOTICE_LIMIT);
        page.setPath("/message/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> messageList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if (messageList!=null){
            for (Message message : messageList) {
                Map<String,Object> map = new HashMap<>();
                //通知
                map.put("notice", message);
                //内容
                Map<String,Object> data = JSONObject.parseObject(message.getContent(), HashMap.class);
                map.put("user", userService.findUserById((Integer)data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId", data.get("postId"));
                //
                map.put("fromUser",userService.findUserById(message.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(messageList);
        if (ids.size()!=0){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

}
