package com.research.ai_analysis_service.listener;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.research.ai_analysis_service.client.PaperServiceClient;
import com.research.ai_analysis_service.config.RabbitMQConfig;
import com.research.ai_analysis_service.dto.AnalysisResponse;
import com.research.ai_analysis_service.dto.PaperResponse;
import com.research.ai_analysis_service.entity.PaperAnalysis;
import com.research.ai_analysis_service.event.PaperUploadedEvent;
import com.research.ai_analysis_service.repository.PaperAnalysisRepository;
import com.research.ai_analysis_service.service.AiAnalysisService;
import com.research.ai_analysis_service.service.PdfExtractionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaperUploadListener {

    private final PaperServiceClient paperServiceClient;
    private final PdfExtractionService pdfExtractionService;
    private final AiAnalysisService aiAnalysisService;
    PaperAnalysisRepository paperAnalysisRepository;

    public PaperUploadListener(PaperServiceClient paperServiceClient, PdfExtractionService pdfExtractionService,
            AiAnalysisService aiAnalysisService, PaperAnalysisRepository paperAnalysisRepository) {
        this.paperServiceClient = paperServiceClient;
        this.pdfExtractionService = pdfExtractionService;
        this.aiAnalysisService = aiAnalysisService;
        this.paperAnalysisRepository = paperAnalysisRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.PAPER_UPLOAD_QUEUE)
    public void receivePaperUploadedEvent(PaperUploadedEvent event) {

        log.info("Received Paper ID : {}", event.getPaperId());

        PaperResponse paper = paperServiceClient.getPaperById(event.getPaperId());

        log.info("Title : {}", paper.getTitle());
        log.info("PDF URL : {}", paper.getPdfUrl());

        String extractedText = pdfExtractionService.extractTextFromPdf(paper.getPdfUrl());

        AnalysisResponse analysis = aiAnalysisService.analyzePaper(extractedText);

        PaperAnalysis paperAnalysis = new PaperAnalysis();

        // -------- Paper Metadata --------
        paperAnalysis.setPaperId(paper.getId());

        paperAnalysis.setUploadedBy(paper.getUploadedBy());

        paperAnalysis.setTitle(paper.getTitle());

        paperAnalysis.setPdfUrl(paper.getPdfUrl());

        paperAnalysis.setUploadedAt(paper.getUploadedAt());

        // -------- AI Analysis --------
        paperAnalysis.setSummary(analysis.getSummary());

        paperAnalysis.setKeywords(analysis.getKeywords());

        paperAnalysis.setResearchDomain(analysis.getResearchDomain());

        paperAnalysis.setNoveltyScore(analysis.getNoveltyScore());

        paperAnalysis.setResearchGap(analysis.getResearchGap());

        paperAnalysis.setFutureWork(analysis.getFutureWork());

        paperAnalysis.setLimitations(analysis.getLimitations());

        paperAnalysis.setDifficulty(analysis.getDifficulty());

        paperAnalysis.setAnalyzedAt(LocalDateTime.now());

        paperAnalysisRepository.save(paperAnalysis);

        log.info("Analysis saved successfully into MongoDB");

        log.info("Summary : {}", analysis.getSummary());
        log.info("Keywords : {}", analysis.getKeywords());
        log.info("Domain : {}", analysis.getResearchDomain());
        log.info("Novelty : {}", analysis.getNoveltyScore());

    }
}