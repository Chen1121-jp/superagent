package com.chen.chenaiagent.Tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://search.bilibili.com/all?vt=08377775&keyword=%E8%B6%85%E5%B8%82%E5%90%8E%E9%97%A8%E5%90%9E%E4%BA%91%E5%90%90%E9%9B%BE%E7%9A%84%E4%BA%8C%E4%BA%BA&from_source=webhistory_search&spm_id_from=333.1007&search_source=3";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
        log.info("{}",result);
    }
}