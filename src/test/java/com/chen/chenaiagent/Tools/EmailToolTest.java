package com.chen.chenaiagent.Tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class EmailToolTest {

    @Autowired
    private JavaMailSender mailSender;
    @Test
    void testSendEmail() {
        EmailTool tool = new EmailTool(mailSender);
        String result = tool.sendEmail("jianpeng879@gmail.com","1441607327@qq.com","测试邮件","测试测试");
        assertNotNull(result);
        log.info("{}",result);
    }

}