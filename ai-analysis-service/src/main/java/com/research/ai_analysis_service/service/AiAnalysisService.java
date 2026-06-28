package com.research.ai_analysis_service.service;

import java.util.List;

import com.research.ai_analysis_service.dto.AnalysisResponse;
import com.research.ai_analysis_service.entity.PaperAnalysis;

public interface AiAnalysisService {

    AnalysisResponse analyzePaper(String paperText);

    PaperAnalysis getAnalysisByPaperId(Long paperId);

    List<PaperAnalysis> getAllAnalyses();

    void deleteAnalysis(Long id);
}