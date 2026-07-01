package com.research.ai_analysis_service.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "paper_analysis")
@Data
public class PaperAnalysis {

    @Id
    private String id;

    // ---------------- Paper Metadata ----------------
    private Long paperId;

    private String uploadedBy;

    private String title;

    private String pdfUrl;

    private LocalDateTime uploadedAt;

    // ---------------- AI Analysis ----------------
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