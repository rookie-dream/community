package com.rookie.community;

import com.rookie.community.pojo.DiscussPost;
import com.rookie.community.service.DiscussPostServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class CaffeineTest {
    @Autowired
    private DiscussPostServiceImpl discussPostService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("111111111111111111111111111222222222222222223333333333335411111111111111");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache(){
        System.out.println(discussPostService.selectDiscussPosts(0, 0,10 , 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0,10 , 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0,10 , 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0,10 , 0));
    }
}
