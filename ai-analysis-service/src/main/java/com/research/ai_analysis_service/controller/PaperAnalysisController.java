package com.research.ai_analysis_service.controller;

import com.research.ai_analysis_service.service.PaperAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class PaperAnalysisController {

    private final PaperAnalysisService paperAnalysisService;

    @PostMapping("/{paperId}")
    public String analyze(@PathVariable Long paperId){

        paperAnalysisService.analyzePaper(paperId);

        return "Analysis Started";
    }

}