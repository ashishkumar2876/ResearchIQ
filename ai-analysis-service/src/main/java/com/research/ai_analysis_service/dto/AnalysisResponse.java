package com.research.ai_analysis_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {

    private String summary;

    private List<String> keywords;

    private String researchDomain;

    private Integer noveltyScore;

    private String researchGap;

    private String futureWork;

    private String limitations;

    private String difficulty;

}