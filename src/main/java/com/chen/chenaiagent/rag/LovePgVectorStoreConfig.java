package com.chen.chenaiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
@Slf4j
public class LovePgVectorStoreConfig {

    @Resource
    private LoveDocumentLoader loveDocumentLoader;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;
    @Bean
    VectorStore pgVectorStore(EmbeddingModel dashscopeEmbeddingModel, JdbcTemplate jdbcTemplate) {
        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("pgvectors_store")
                .dimensions(1024)
                .maxDocumentBatchSize(10000)
                .build();
        // 检查表里是否已有数据
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pgvectors_store", Integer.class);
        if (count != null && count > 0) {
            log.info("向量库已有 {} 条数据，跳过初始化", count);
            return pgVectorStore;
        }
        List<Document> documents = loveDocumentLoader.loadMarkdownDocuments();
        List<Document> enrichedDocument = myKeywordEnricher.enrichDocuments(documents);
        int batchSize = 10;
        for (int i = 0; i < enrichedDocument.size(); i += batchSize) {
            int end = Math.min(i + batchSize, enrichedDocument.size());
            List<Document> batch = enrichedDocument.subList(i, end);
            log.info("Adding documents batch {}/{}", (i / batchSize + 1), (enrichedDocument.size() + batchSize - 1) / batchSize);
            pgVectorStore.add(batch);
        }
        return pgVectorStore;
    }
}
