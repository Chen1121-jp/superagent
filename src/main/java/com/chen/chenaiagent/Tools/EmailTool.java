package com.chen.chenaiagent.Tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
@Slf4j
public class EmailTool {

    private  JavaMailSender mailSender;

    public EmailTool(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Tool(description = "Send an email")
    public String sendEmail(String to,String form,String subject,String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(form);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            log.info("Email sent successfully.");
            return "Email sent successfully.";
        } catch (Exception e) {
            log.error("Error sending email: " + e.getMessage());
            return "Error sending email: " + e.getMessage();
        }
    }

}
