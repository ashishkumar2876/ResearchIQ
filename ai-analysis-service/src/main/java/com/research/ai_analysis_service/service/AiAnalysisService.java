package com.research.ai_analysis_service.service;

import com.research.ai_analysis_service.dto.AnalysisResponse;

public interface AiAnalysisService {

    AnalysisResponse analyzePaper(String paperText);
}