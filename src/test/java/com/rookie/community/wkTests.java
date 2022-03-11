package com.rookie.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class wkTests {
    @Test
    public void wktest(){
        String cmd = "D:/JetBrains/Toolbox/tool/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com E:/rookie/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
