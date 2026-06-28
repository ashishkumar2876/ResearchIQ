package com.research.ai_analysis_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperResponse {

    private Long id;

    private String title;

    private String pdfUrl;

    private String uploadedBy;

    private LocalDateTime uploadedAt;

}