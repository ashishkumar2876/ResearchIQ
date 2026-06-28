package com.research.ai_analysis_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.ai_analysis_service.dto.AnalysisResponse;
import com.research.ai_analysis_service.dto.GeminiRequest;
import com.research.ai_analysis_service.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

        private final WebClient webClient;
        private final ObjectMapper objectMapper;

        @Value("${gemini.api.key}")
        private String apiKey;

        public AiAnalysisServiceImpl(WebClient webClient, ObjectMapper objectMapper) {
                this.webClient = webClient;
                this.objectMapper = objectMapper;
        }

        @Override
        public AnalysisResponse analyzePaper(String paperText) {

                String prompt = """
                                You are an expert research paper reviewer.

                                Analyze the following research paper.

                                Return ONLY valid JSON.

                                Do not include markdown.
                                Do not include ```json.
                                Do not explain anything.

                                Return exactly this format:

                                {
                                  "summary": "",
                                  "keywords": [],
                                  "researchDomain": "",
                                  "noveltyScore": 0,
                                  "researchGap": "",
                                  "futureWork": "",
                                  "limitations": "",
                                  "difficulty": ""
                                }

                                Research Paper:

                                %s
                                """.formatted(paperText);

                GeminiRequest request = new GeminiRequest(
                                List.of(
                                                new GeminiRequest.Content(
                                                                List.of(
                                                                                new GeminiRequest.Part(prompt)))));

                GeminiResponse response = webClient.post()
                                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                                                + apiKey)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(GeminiResponse.class)
                                .block();

                String json = response.getCandidates()
                                .get(0)
                                .getContent()
                                .getParts()
                                .get(0)
                                .getText();

                try {
                        return objectMapper.readValue(json, AnalysisResponse.class);
                } catch (Exception e) {
                        throw new RuntimeException("Failed to parse Gemini response", e);
                }
        }
}