package com.rookie.community;

import com.rookie.community.mapper.LoginTicketMapper;
import com.rookie.community.mapper.MessageMapper;
import com.rookie.community.pojo.LoginTicket;
import com.rookie.community.pojo.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@SpringBootTest
public class MapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        Date date = new Date(System.currentTimeMillis());
        System.out.println(date);
        long l = System.currentTimeMillis();
        Date date1 = new Date();
        loginTicket.setExpired(new Timestamp(date1.getTime()+1000*60*10));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket abc = loginTicketMapper.selectByTicket("abc");
        System.out.println(abc);
    }
    @Test
    public void testUpdateLoginTicket(){
        loginTicketMapper.updateStatus("abc", 1);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> list1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list1) {
            System.out.println(message);
        }
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
        int i1 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i1);
    }

}
