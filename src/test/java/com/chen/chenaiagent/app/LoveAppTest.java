package com.chen.chenaiagent.app;



import com.chen.chenaiagent.rag.MyGithubDocumentRearer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.UUID;


@SpringBootTest
@Slf4j
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
    @Test
    void doChatWithVectorStore() {
        String chatId= UUID.randomUUID().toString();
        String message="我已经结婚了，但是和老婆有点紧张怎么办？";
        String result=loveApp.doChatWithVectorStore(message,chatId);
    }

    @Test
    void doChatWithCloudeVectorStore(){
        String chatId= UUID.randomUUID().toString();
        String message="我已经结婚了，但是和老婆有点紧张怎么办？";
        String result=loveApp.doChatWithCloudeVectorStore(message,chatId);
    }
    @Test
    void doChatWithLoveVectorStore(){
        String chatId= UUID.randomUUID().toString();
        String message="我今年26岁，在广州做程序员，平时比较宅，想找一个性格活泼开朗一点的女生，最好也在珠三角的，年龄跟我差不多就\n" +
                "  行，最好有钱一点，没有也行";
        String result=loveApp.doChatWithLoveVectorStore(message,chatId);
    }

    @Test
    void doChatWithPgVectorStore(){
        String chatId= UUID.randomUUID().toString();
        String message="java";
        String result=loveApp.doChatWithPgVectorStore(message,chatId);
    }

    @Test
    void MyGithubDocumentRearer(){
        MyGithubDocumentRearer githubDocumentRearer=new MyGithubDocumentRearer
                ("https://github.com/Chen1121-jp/superagent/blob/master/src/main/java/com/chen/chenaiagent/controller/HealthTestController.java");
        List<Document> documents=githubDocumentRearer.get();
        for (Document document : documents) {
            log.info("{}",document.getText());
        }
    }

    @Test
    void doChatWithTranslation(){
        String chatId= UUID.randomUUID().toString();
        String message="你好，在超时后面吞烟吐雾的两人";
        String result=loveApp.doChatWithTranslation(message,chatId);
    }
    @Test
    void doChatWithTools() {

        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");


        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");


        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");


        testMessage("执行 Python3 脚本来生成数据分析报告");


        testMessage("保存我的恋爱档案为文件");


        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        String message = "我的另一半居住在广东省江门市，请帮我找到 5 公里内合适的约会地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);
    }

    @Test
    void doChatWithUsMcp() {
        String chatId = UUID.randomUUID().toString();
        String message = "帮我搜索一些能让对象高兴的图片";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}