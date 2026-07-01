package com.research.ai_analysis_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.research.ai_analysis_service.entity.PaperAnalysis;

@Repository
public interface PaperAnalysisRepository extends MongoRepository<PaperAnalysis, String> {

    Optional<PaperAnalysis> findByPaperId(Long paperId);

    void deleteByPaperId(Long paperId);

    List<PaperAnalysis> findByUploadedBy(String uploadedBy);

}