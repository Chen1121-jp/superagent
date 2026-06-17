package com.chen.chenaiagent.Tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
    @SpringBootTest
    public class WebSearchToolTest {

        @Value("${search-api.api-key}")
        private String searchApiKey;

        @Test
        public void testSearchWeb() {
            WebSearchTool tool = new WebSearchTool(searchApiKey);
            String query = "躲在超市后门抽烟的两人";
            String result = tool.searchWeb(query);
            assertNotNull(result);
            log.info("{}",result);
        }
    }
