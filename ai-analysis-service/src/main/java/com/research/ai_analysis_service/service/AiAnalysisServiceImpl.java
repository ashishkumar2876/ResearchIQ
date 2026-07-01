package com.research.ai_analysis_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.ai_analysis_service.dto.AnalysisResponse;
import com.research.ai_analysis_service.dto.GeminiRequest;
import com.research.ai_analysis_service.dto.GeminiResponse;
import com.research.ai_analysis_service.entity.PaperAnalysis;
import com.research.ai_analysis_service.exception.AnalysisNotFoundException;
import com.research.ai_analysis_service.repository.PaperAnalysisRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PaperAnalysisRepository paperAnalysisRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    public AiAnalysisServiceImpl(
            WebClient webClient,
            ObjectMapper objectMapper,
            PaperAnalysisRepository paperAnalysisRepository) {

        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.paperAnalysisRepository = paperAnalysisRepository;
    }

    @Override
    public AnalysisResponse analyzePaper(String paperText) {

        log.info("Starting AI analysis");

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

        log.info("Sending request to Gemini");

        GeminiResponse response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        log.info("Received response from Gemini");

        String json = response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();

        try {

            AnalysisResponse analysis =
                    objectMapper.readValue(json, AnalysisResponse.class);

            log.info("Successfully parsed Gemini response");

            return analysis;

        } catch (Exception e) {

            log.error("Failed to parse Gemini response", e);

            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

    @Override
    public List<PaperAnalysis> getAllAnalyses() {

        log.info("Fetching all analyses");

        return paperAnalysisRepository.findAll();
    }

    @Override
    public PaperAnalysis getAnalysisByPaperId(Long paperId) {

        log.info("Fetching analysis for Paper ID={}", paperId);

        return paperAnalysisRepository.findByPaperId(paperId)
                .orElseThrow(() -> new AnalysisNotFoundException("Analysis Not Found"));
    }

    @Override
    public List<PaperAnalysis> getDashboard(String uploadedBy) {

        log.info("Fetching dashboard for user '{}'", uploadedBy);

        return paperAnalysisRepository.findByUploadedBy(uploadedBy);
    }
}