package com.rookie.community;

import com.rookie.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter(){
        String text = "这里的释放空间规划赌博，是个复读生嫖娼哦ii饿哦三个吸毒数量可观还是看开票是两个会离开看看就过来上课";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);

       text = "这里的释放空间规划赌,博，是个复读生@!嫖~娼%哦ii饿哦三个吸毒数量可观还是看开*票是两个会离开看看就过来上课";
       filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
