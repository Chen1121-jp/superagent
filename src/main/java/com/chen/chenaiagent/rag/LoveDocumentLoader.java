package com.chen.chenaiagent.rag;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoveDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    LoveDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdownDocuments() {

        log.info("Loading documents from {}", resourcePatternResolver);
        List<Document> all=new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:*.md");
            for (Resource resource : resources) {
                String filename=resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                all.addAll(reader.get());
            }

        } catch (IOException e) {
            log.error("加载文档失败", e);
        }

        return all;
    }


}
