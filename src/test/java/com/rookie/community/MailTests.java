package com.rookie.community;

import com.rookie.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void tetsTestMail(){
        mailClient.setMail("a1823577945@163.com", "test", "测试成功");
    }
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "sunday");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.setMail("a1823577945@163.com", "Html", process);

    }
}
