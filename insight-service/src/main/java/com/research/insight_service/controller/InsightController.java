package com.research.insight_service.controller;

import com.research.insight_service.dto.CompareRequest;
import com.research.insight_service.dto.LiteratureReviewRequest;
import com.research.insight_service.dto.MarkdownResponse;
import com.research.insight_service.dto.ResearchGapRequest;
import com.research.insight_service.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    /**
     * Get detailed AI-generated insights for a paper
     */
    @GetMapping("/paper/{paperId}")
    public MarkdownResponse getPaperInsights(@PathVariable Long paperId) {

        return insightService.getPaperInsights(paperId);

    }

    @PostMapping("/compare")
    public MarkdownResponse comparePapers(@RequestBody CompareRequest request) {

        return insightService.comparePapers(request);

    }

    @PostMapping("/research-gap")
    public MarkdownResponse discoverResearchGap(
            @RequestBody ResearchGapRequest request) {

        return insightService.discoverResearchGap(request);

    }

    @PostMapping("/literature-review")
    public MarkdownResponse generateLiteratureReview(
            @RequestBody LiteratureReviewRequest request) {

        return insightService.generateLiteratureReview(request);

    }

}