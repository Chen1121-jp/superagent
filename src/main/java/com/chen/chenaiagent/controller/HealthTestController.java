package com.chen.chenaiagent.controller;

import com.chen.chenaiagent.exception.BusinessException;
import com.chen.chenaiagent.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthTestController {

    @GetMapping
    public String health() {
        return "ok";
    }

    @GetMapping("/test-ai-timeout")
    public String testAiTimeout() {
        throw new BusinessException(ErrorCode.AI_TIMEOUT);
    }
}
