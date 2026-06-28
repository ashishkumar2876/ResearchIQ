package com.research.paper_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.research.paper_service.entity.Paper;

public interface PaperService {

    String uploadPaper(MultipartFile file, String uploadedBy);

    Paper getPaperById(Long id);

    void deletePaper(Long id);

}