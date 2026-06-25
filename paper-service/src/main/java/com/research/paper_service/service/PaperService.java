package com.research.paper_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface PaperService {

    String uploadPaper(MultipartFile file, String uploadedBy);

}