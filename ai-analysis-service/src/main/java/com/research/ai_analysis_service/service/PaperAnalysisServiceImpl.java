package com.research.ai_analysis_service.service;

import org.springframework.stereotype.Service;

import com.research.ai_analysis_service.client.PaperServiceClient;
import com.research.ai_analysis_service.dto.PaperResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaperAnalysisServiceImpl implements PaperAnalysisService {

    private final PaperServiceClient paperServiceClient;

    @Override
    public void analyzePaper(Long paperId) {

        log.info("Fetching paper details for Paper ID={}", paperId);

        PaperResponse paper = paperServiceClient.getPaperById(paperId);

        log.info("Successfully fetched paper details.");
        log.info("Paper ID={}", paperId);
        log.info("Paper Title={}", paper.getTitle());
        log.info("PDF URL={}", paper.getPdfUrl());

    }
}