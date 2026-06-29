package com.research.insight_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResearchGapRequest {

    private List<Long> paperIds;

}