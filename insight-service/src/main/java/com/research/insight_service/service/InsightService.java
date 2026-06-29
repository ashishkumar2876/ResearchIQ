package com.research.insight_service.service;

import com.research.insight_service.dto.CompareRequest;
import com.research.insight_service.dto.LiteratureReviewRequest;
import com.research.insight_service.dto.MarkdownResponse;
import com.research.insight_service.dto.ResearchGapRequest;

public interface InsightService {

    MarkdownResponse getPaperInsights(Long paperId);

    MarkdownResponse comparePapers(CompareRequest request);

    MarkdownResponse discoverResearchGap(ResearchGapRequest request);

    MarkdownResponse generateLiteratureReview(LiteratureReviewRequest request);

}