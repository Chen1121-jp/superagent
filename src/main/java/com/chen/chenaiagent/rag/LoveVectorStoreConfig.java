package com.chen.chenaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoveVectorStoreConfig {

    @Resource
    private LoveDocumentLoader loveDocumentLoader;


    @Bean
    VectorStore loveVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        simpleVectorStore.add(loveDocumentLoader.loadMarkdownDocuments());
        return simpleVectorStore;
    }
}
