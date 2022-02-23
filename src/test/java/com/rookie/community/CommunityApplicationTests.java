package com.rookie.community;

import com.rookie.community.mapper.DiscussPostMapper;
import com.rookie.community.mapper.UserMapper;
import com.rookie.community.pojo.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    UserMapper userMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    void contextLoads() {
        System.out.println(userMapper.selectByName("nowcoder11"));
    }
    @Test
    public void testDiscussPostMapper(){
        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

}
