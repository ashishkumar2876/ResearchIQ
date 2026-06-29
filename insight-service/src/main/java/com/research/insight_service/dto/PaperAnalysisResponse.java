package com.research.insight_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaperAnalysisResponse {

    private String id;

    private Long paperId;

    private String summary;

    private List<String> keywords;

    private String researchDomain;

    private Integer noveltyScore;

    private String researchGap;

    private String futureWork;

    private String limitations;

    private String difficulty;

    private LocalDateTime analyzedAt;

}