package com.chen.chenaiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class MyGithubDocumentRearer implements DocumentReader {

    private String url;
    private RestTemplate restTemplate;
    public MyGithubDocumentRearer(String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }
    @Override
    public List<Document> get() {
        String text = restTemplate.getForObject(url, String.class);
        Document doc = new Document(text);
        return List.of(doc);
    }

}
