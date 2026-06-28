package com.research.ai_analysis_service.service;

import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {

    @Override
    public String extractTextFromPdf(String pdfUrl) {

        try (InputStream inputStream = java.net.URI.create(pdfUrl)
                .toURL()
                .openStream();
                PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract PDF text", e);
        }
    }
}