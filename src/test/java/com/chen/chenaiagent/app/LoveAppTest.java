package com.chen.chenaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;
    @Test
    void testdoChat() {
        String chatId= UUID.randomUUID().toString();
        String message="你好,我是小陈";
        String result=loveApp.doChat(message,chatId);
        message="我想让我的对象小张更爱我";
        result=loveApp.doChat(message,chatId);
        message="我的第一次跟你说了什么来着，你原话搬过来";
        result=loveApp.doChat(message,chatId);
    }
    @Test
    void testdoChatWithReport() {
        String chatId= UUID.randomUUID().toString();
        String message="你好,我是小陈，我想要跟另一半分手，不知道怎么说";
        LoveApp.LoveReport result=loveApp.doChatWithReport(message,chatId);

    }
}