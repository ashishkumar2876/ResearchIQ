package com.research.insight_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompareRequest {

    private List<Long> paperIds;

}