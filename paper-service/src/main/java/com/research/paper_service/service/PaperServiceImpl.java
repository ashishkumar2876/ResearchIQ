package com.research.paper_service.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.research.paper_service.entity.Paper;
import com.research.paper_service.publisher.PaperEventPublisher;
import com.research.paper_service.repository.PaperRepository;

@Service
public class PaperServiceImpl implements PaperService {

    private final Cloudinary cloudinary;
    private final PaperRepository paperRepository;
    private final PaperEventPublisher paperEventPublisher;

    public PaperServiceImpl(Cloudinary cloudinary, PaperRepository paperRepository,
            PaperEventPublisher paperEventPublisher) {
        this.cloudinary = cloudinary;
        this.paperRepository = paperRepository;
        this.paperEventPublisher = paperEventPublisher;
    }

    @Override
    public String uploadPaper(MultipartFile file, String uploadedBy) {

        try {

            System.out.println("STEP 1 - Uploading to Cloudinary");

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "raw"));

            System.out.println("STEP 2 - Cloudinary Upload Success");

            String pdfUrl = uploadResult.get("secure_url").toString();

            Paper paper = new Paper();
            paper.setTitle(file.getOriginalFilename());
            paper.setPdfUrl(pdfUrl);
            paper.setUploadedBy(uploadedBy);
            paper.setUploadedAt(LocalDateTime.now());

            System.out.println("STEP 3 - Saving to Database");

            Paper savedPaper = paperRepository.save(paper);

            System.out.println("STEP 4 - Saved with ID = " + savedPaper.getId());

            paperEventPublisher.publishPaperUploadedEvent(savedPaper.getId());

            System.out.println("STEP 5 - RabbitMQ Event Published");

            return "Uploaded successfully";

        } catch (Exception e) {

            System.out.println("EXCEPTION OCCURRED");
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    @Override
    public Paper getPaperById(Long id) {
        return paperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
    }

    @Override
    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }
}
