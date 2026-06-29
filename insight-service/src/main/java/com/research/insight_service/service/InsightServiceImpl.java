package com.research.insight_service.service;

import com.research.insight_service.client.AiAnalysisClient;
import com.research.insight_service.client.GeminiClient;
import com.research.insight_service.dto.CompareRequest;
import com.research.insight_service.dto.LiteratureReviewRequest;
import com.research.insight_service.dto.MarkdownResponse;
import com.research.insight_service.dto.PaperAnalysisResponse;
import com.research.insight_service.dto.ResearchGapRequest;
import com.research.insight_service.util.PromptBuilder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {

    private final AiAnalysisClient aiAnalysisClient;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;

    @Override
    public MarkdownResponse getPaperInsights(Long paperId) {

        // Fetch analysis from AI Analysis Service
        PaperAnalysisResponse analysis = aiAnalysisClient.getAnalysis(paperId);

        // Convert analysis into text
        String analysisText = """
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

        // Build prompt
        String prompt = promptBuilder.buildInsightPrompt(analysisText);

        // Ask Gemini
        String markdown = geminiClient.generateContent(prompt);

        // Return markdown
        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse comparePapers(CompareRequest request) {

        // Step 1: Fetch analyses of selected papers
        List<PaperAnalysisResponse> analyses = request.getPaperIds()
                .stream()
                .map(aiAnalysisClient::getAnalysis)
                .toList();

        // Step 2: Convert all analyses into text
        String analysesText = analyses.stream()
                .map(this::convertAnalysisToText)
                .collect(Collectors.joining("\n\n----------------------------------------\n\n"));

        // Step 3: Build Gemini prompt
        String prompt = promptBuilder.buildComparisonPrompt(analysesText);

        // Step 4: Ask Gemini
        String markdown = geminiClient.generateContent(prompt);

        // Step 5: Return result
        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse discoverResearchGap(ResearchGapRequest request) {

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

        return new MarkdownResponse(markdown);
    }

    @Override
    public MarkdownResponse generateLiteratureReview(LiteratureReviewRequest request) {

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

}