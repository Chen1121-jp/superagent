package com.chen.chenaiagent.app;

import com.chen.chenaiagent.advisor.ReReadingAdvisor;
import com.chen.chenaiagent.chatmemory.FileBasedChatMemory;
import com.chen.chenaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.chen.chenaiagent.rag.LoveVectorStoreConfig;
import com.chen.chenaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;

    @Resource
    private  VectorStore loveVectorStore;

    @Autowired
    private VectorStore pgVectorStore;

    @Resource
    private ToolCallback[] allTools;
    @Resource
    private Advisor loveRagCloudAdvisor;
    @Resource
    private QueryRewriter queryRewriter;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private QueryTransformer queryTranslation;

    /*@Autowired
    JdbcChatMemoryRepository chatMemoryRepository;*/

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    private static final String SYSTEM_PROMPT2 = "你是深耕恋爱心理领域的专家兼红娘。开场向用户表明你的双重身份：既能倾听恋爱难题、提供专业建议，也能根据用户条件从缘分墙中推荐合适的对象。" +
            "恋爱咨询方面，围绕单身、恋爱、已婚三种状态提问：单身询问社交圈拓展及追求困扰；" +
            "恋爱询问沟通、习惯差异引发的矛盾；已婚询问家庭责任与亲属关系处理。" +
            "红娘推荐方面，当用户表达想找对象的意愿或描述理想型时，综合利用缘分墙中检索到的候选人信息，" +
            "匹配年龄、城市、职业、星座、性格、兴趣爱好等维度进行推荐。" +
            "推荐时说明匹配理由，每次推荐不超过3位，用自然亲切的语气呈现，并在最后询问用户是否有更具体的要求以便进一步筛选。" +
            "如果缘分墙中没有匹配的人选，如实告知并建议用户放宽条件或换个方向试试。";

    // 2. 日志 Advisor（新增）
    SimpleLoggerAdvisor myloggerAdvisor = new SimpleLoggerAdvisor(
            request -> "AI Request: " + request.prompt().getUserMessage(),
            response -> "AI Response: " + response.getResult().getOutput().getText(),
            0
    );
    /**
     * 初始化记忆器以及对话客户端
     * 构造函数
     * @param dashscopechatModel
     * @param chatMemoryRepository 对话记录存储器（数据库中）
     */
    public LoveApp(ChatModel dashscopechatModel, ChatMemoryRepository chatMemoryRepository){
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        ChatMemory chatMemorySaveInFile = new FileBasedChatMemory(fileDir);
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
        ChatMemory JdbcchatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
        chatClient = ChatClient.builder(dashscopechatModel)
                //.defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(JdbcchatMemory).build(),
                        myloggerAdvisor
                        //重读问题，提高质量
                        //new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 聊天
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user( message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }
    public Flux<String> doChatByStream(String message, String chatId){
        return  chatClient
                .prompt()
                .user( message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();

    }

    record LoveReport(String title, List<String> suggestions){
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);

        log.info("loveReport:{}", loveReport);
        return loveReport;
    }

    public String doChatWithVectorStore(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(myloggerAdvisor)
                .advisors(QuestionAnswerAdvisor.builder(loveVectorStore).build())
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    public String doChatWithCloudeVectorStore(String message, String chatId) {
        ChatResponse chatResponse=chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(myloggerAdvisor)
                .advisors(loveRagCloudAdvisor)
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    public String doChatWithLoveVectorStore(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(SYSTEM_PROMPT2)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(myloggerAdvisor)
                .advisors(QuestionAnswerAdvisor.builder(loveVectorStore).build())
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }


    public String doChatWithPgVectorStore(String message, String chatId) {
        String rewriteMessage = queryRewriter.doRewrite(message);
        ChatResponse chatResponse=chatClient
                .prompt()
                .user(rewriteMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                //.advisors(myloggerAdvisor)
                //.advisors(QuestionAnswerAdvisor.builder(pgVectorStore).build())
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(pgVectorStore))
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    public String doChatWithTranslation(String message, String chatId) {
        Query query=new Query(message);
        Query transform = queryTranslation.transform(query);
        String rewriteMessage = transform.text();
        log.info("rewriteMessage:{}",rewriteMessage);
        return "yes";
    }

    public String doChatWithTools(String message, String chatId){
        ChatResponse chatResponse=chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(myloggerAdvisor)
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }
    public String doChatWithMcp(String message, String chatId){
        ChatResponse chatResponse=chatClient
                .prompt()
                .user(message)
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content=chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }


}
