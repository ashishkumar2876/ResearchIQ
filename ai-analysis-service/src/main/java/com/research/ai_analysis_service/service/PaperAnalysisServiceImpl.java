package com.research.ai_analysis_service.service;

import com.research.ai_analysis_service.client.PaperServiceClient;
import com.research.ai_analysis_service.dto.PaperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaperAnalysisServiceImpl implements PaperAnalysisService {

    private final PaperServiceClient paperServiceClient;

    @Override
    public void analyzePaper(Long paperId) {

        PaperResponse paper =
                paperServiceClient.getPaperById(paperId);

        System.out.println("Paper Title : " + paper.getTitle());

        System.out.println("PDF URL : " + paper.getPdfUrl());

    }
}