package com.research.ai_analysis_service.controller;

import com.research.ai_analysis_service.entity.PaperAnalysis;
import com.research.ai_analysis_service.service.AiAnalysisService;
import com.research.ai_analysis_service.service.PaperAnalysisService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class PaperAnalysisController {

    private final PaperAnalysisService paperAnalysisService;
    private final AiAnalysisService aiAnalysisService;

    @PostMapping("/{paperId}")
    public String analyze(@PathVariable Long paperId) {

        paperAnalysisService.analyzePaper(paperId);

        return "Analysis Started";
    }

    @GetMapping
    public List<PaperAnalysis> getAllAnalyses() {
        return aiAnalysisService.getAllAnalyses();
    }

    @GetMapping("/{paperId}")
    public PaperAnalysis getAnalysisByPaperId(@PathVariable Long paperId) {
        return aiAnalysisService.getAnalysisByPaperId(paperId);
    }

    
    @GetMapping("/dashboard")
    public List<PaperAnalysis> getDashboard(
            @RequestHeader("X-User-Email") String userEmail) {

        return aiAnalysisService.getDashboard(userEmail);
    }
}