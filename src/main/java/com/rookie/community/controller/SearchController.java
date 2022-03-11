package com.rookie.community.controller;

import com.rookie.community.pojo.DiscussPost;
import com.rookie.community.service.ElasticsearchServiceImpl;
import com.rookie.community.service.LikeServiceImpl;
import com.rookie.community.service.UserServiceImpl;
import com.rookie.community.util.CommunityConstant;
import com.rookie.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchServiceImpl elasticsearchService;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private LikeServiceImpl likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {

        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult
                = elasticsearchService.searchDiscussPost(keyword, page.current - 1, page.getLimit());
        //聚合数据
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (searchResult!=null) {
            for (DiscussPost post : searchResult) {
                Map<String,Object> map = new HashMap<>();

                //帖子
                map.put("post", post);
                //作者
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword",keyword);

        //设置分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null?0: elasticsearchService.searchDiscussPostRows(keyword));

        return "/site/search";
    }
}
