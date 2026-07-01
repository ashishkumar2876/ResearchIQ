package com.research.ai_analysis_service.service;

import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractionServiceImpl.class);

    @Override
    public String extractTextFromPdf(String pdfUrl) {

        log.info("Starting PDF extraction for URL: {}", pdfUrl);

        try (InputStream inputStream = java.net.URI.create(pdfUrl)
                .toURL()
                .openStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            log.debug("PDF loaded successfully, extracting text...");

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            log.info("PDF extraction completed successfully. Extracted characters: {}", text.length());

            return text;

        } catch (Exception e) {
            log.error("Failed to extract PDF text from URL: {}", pdfUrl, e);
            throw new RuntimeException("Failed to extract PDF text", e);
        }
    }
}