package com.research.insight_service.client;
import com.research.insight_service.dto.PaperAnalysisResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ai-analysis-service")
public interface AiAnalysisClient {

    @GetMapping("/analysis/{paperId}")
    PaperAnalysisResponse getAnalysis(@PathVariable Long paperId);

}