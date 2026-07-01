package com.research.insight_service.service;

import com.research.insight_service.client.AiAnalysisClient;
import com.research.insight_service.client.GeminiClient;
import com.research.insight_service.dto.*;
import com.research.insight_service.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {

    private final AiAnalysisClient aiAnalysisClient;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;

    @Override
    public MarkdownResponse getPaperInsights(Long paperId) {

        log.info("Fetching insights for paperId={}", paperId);

        PaperAnalysisResponse analysis = aiAnalysisClient.getAnalysis(paperId);

        log.debug("Analysis fetched for paperId={} with noveltyScore={}",
                paperId, analysis.getNoveltyScore());

        String analysisText = buildAnalysisText(analysis);

        String prompt = promptBuilder.buildInsightPrompt(analysisText);

        log.debug("Generated prompt for paperId={}", paperId);

        String markdown = geminiClient.generateContent(prompt);

        log.info("Insights generated successfully for paperId={}", paperId);

        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse comparePapers(CompareRequest request) {

        log.info("Comparing papers: {}", request.getPaperIds());

        List<PaperAnalysisResponse> analyses = request.getPaperIds()
                .stream()
                .map(aiAnalysisClient::getAnalysis)
                .toList();

        log.debug("Fetched {} analyses for comparison", analyses.size());

        String analysesText = analyses.stream()
                .map(this::convertAnalysisToText)
                .collect(Collectors.joining("\n\n----------------------------------------\n\n"));

        String prompt = promptBuilder.buildComparisonPrompt(analysesText);

        String markdown = geminiClient.generateContent(prompt);

        log.info("Comparison generated for {} papers", request.getPaperIds().size());

        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse discoverResearchGap(ResearchGapRequest request) {

        log.info("Finding research gaps for papers: {}", request.getPaperIds());

        List<PaperAnalysisResponse> analyses = request.getPaperIds()
                .stream()
                .map(aiAnalysisClient::getAnalysis)
                .toList();

        StringBuilder analysisText = new StringBuilder();

        for (PaperAnalysisResponse analysis : analyses) {
            analysisText.append(convertAnalysisToText(analysis));
        }

        String prompt = promptBuilder.buildResearchGapPrompt(analysisText.toString());

        String markdown = geminiClient.generateContent(prompt);

        log.info("Research gap analysis completed");

        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse generateLiteratureReview(LiteratureReviewRequest request) {

        log.info("Generating literature review for papers: {}", request.getPaperIds());

        List<PaperAnalysisResponse> analyses = request.getPaperIds()
                .stream()
                .map(aiAnalysisClient::getAnalysis)
                .toList();

        StringBuilder analysisText = new StringBuilder();

        for (PaperAnalysisResponse analysis : analyses) {
            analysisText.append(convertAnalysisToText(analysis));
        }

        String prompt = promptBuilder.buildLiteratureReviewPrompt(
                analysisText.toString());

        String markdown = geminiClient.generateContent(prompt);

        log.info("Literature review generated successfully");

        return new MarkdownResponse(markdown);
    }

    private String convertAnalysisToText(PaperAnalysisResponse analysis) {

        return """
                Paper ID: %d

                Summary:
                %s

                Keywords:
                %s

                Research Domain:
                %s

                Novelty Score:
                %d

                Research Gap:
                %s

                Future Work:
                %s

                Limitations:
                %s

                Difficulty:
                %s
                """.formatted(
                analysis.getPaperId(),
                analysis.getSummary(),
                String.join(", ", analysis.getKeywords()),
                analysis.getResearchDomain(),
                analysis.getNoveltyScore(),
                analysis.getResearchGap(),
                analysis.getFutureWork(),
                analysis.getLimitations(),
                analysis.getDifficulty());
    }

    // Optional helper (cleaner logs + reuse)
    private String buildAnalysisText(PaperAnalysisResponse analysis) {
        return convertAnalysisToText(analysis);
    }
}