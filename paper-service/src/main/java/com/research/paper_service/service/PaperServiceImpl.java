package com.research.paper_service.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.research.paper_service.entity.Paper;
import com.research.paper_service.repository.PaperRepository;

@Service
public class PaperServiceImpl implements PaperService {

    private final Cloudinary cloudinary;
    private final PaperRepository paperRepository;

    public PaperServiceImpl(Cloudinary cloudinary, PaperRepository paperRepository) {
        this.cloudinary = cloudinary;
        this.paperRepository = paperRepository;
    }

    @Override
    public String uploadPaper(MultipartFile file,String uploadedBy){
        try {
            Map uploadResult=cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type","raw")
            );

            String pdfUrl=uploadResult.get("secure_url").toString();
            Paper paper=new Paper();

            paper.setTitle(paper.getTitle());
            paper.setPdfUrl(pdfUrl);
            paper.setUploadedBy(uploadedBy);
            paper.setUploadedAt(LocalDateTime.now());

            paperRepository.save(paper);

        } catch (Exception e) {
           throw new RuntimeException("Failed to upload paper",e);
        }
        return "";
    }
}
