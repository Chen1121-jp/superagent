package com.chen.chenaiagent.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class ChenManus extends ToolCallAgent{
    // 2. 日志 Advisor（新增）
    SimpleLoggerAdvisor myloggerAdvisor = new SimpleLoggerAdvisor(
            request -> "AI Request: " + request.prompt().getUserMessage(),
            response -> "AI Response: " + response.getResult().getOutput().getText(),
            0
    );
    public ChenManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("chenManus");
        String SYSTEM_PROMPT = """  
                You are ChenManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(5);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(myloggerAdvisor)
                .build();
        this.setChatClient(chatClient);
    }
}
