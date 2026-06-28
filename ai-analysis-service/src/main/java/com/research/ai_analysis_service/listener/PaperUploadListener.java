package com.research.ai_analysis_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.research.ai_analysis_service.client.PaperServiceClient;
import com.research.ai_analysis_service.config.RabbitMQConfig;
import com.research.ai_analysis_service.dto.PaperResponse;
import com.research.ai_analysis_service.event.PaperUploadedEvent;
import com.research.ai_analysis_service.service.PdfExtractionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaperUploadListener {

    private final PaperServiceClient paperServiceClient;
    private final PdfExtractionService pdfExtractionService;

    public PaperUploadListener(PaperServiceClient paperServiceClient, PdfExtractionService pdfExtractionService) {
        this.paperServiceClient = paperServiceClient;
        this.pdfExtractionService = pdfExtractionService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAPER_UPLOAD_QUEUE)
    public void receivePaperUploadedEvent(PaperUploadedEvent event) {

        log.info("Received Paper ID : {}", event.getPaperId());

        PaperResponse paper = paperServiceClient.getPaperById(event.getPaperId());

        log.info("Title : {}", paper.getTitle());
        log.info("PDF URL : {}", paper.getPdfUrl());

        String extractedText = pdfExtractionService.extractTextFromPdf(paper.getPdfUrl());

        log.info("============== PDF TEXT ==============");
        log.info(extractedText);
        log.info("======================================");
    }
}