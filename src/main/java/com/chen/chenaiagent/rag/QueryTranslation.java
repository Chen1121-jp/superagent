package com.chen.chenaiagent.rag;


import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class QueryTranslation implements QueryTransformer {


    @Value("${aliyun.translate.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.translate.access-key-secret:}")
    private String accessKeySecret;


    private Client client;
    @Override
    public Query transform(Query query) {
        String userQuery = query.text();
        String translatedQuery = translateViaApi(userQuery);
        return query.mutate().text(translatedQuery).build();
    }

    private String translateViaApi(String userQuery) {
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setRegionId("cn-hangzhou")
                    .setEndpoint("mt.cn-hangzhou.aliyuncs.com");
            this.client = new Client(config);

            TranslateGeneralRequest request = new TranslateGeneralRequest()
                    .setSourceText(userQuery)
                    .setSourceLanguage("auto")
                    .setTargetLanguage("ja")
                    .setFormatType("text");
            return client.translateGeneral(request).getBody().getData().getTranslated();
        }catch (Exception e){
            log.error("翻译失败",e);
            return userQuery;
        }


    }
}
