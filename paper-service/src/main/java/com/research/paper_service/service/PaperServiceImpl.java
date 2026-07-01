package com.research.paper_service.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.research.paper_service.entity.Paper;
import com.research.paper_service.exception.PaperNotFoundException;
import com.research.paper_service.exception.UnauthorizedException;
import com.research.paper_service.publisher.PaperEventPublisher;
import com.research.paper_service.repository.PaperRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaperServiceImpl implements PaperService {

        private final Cloudinary cloudinary;
        private final PaperRepository paperRepository;
        private final PaperEventPublisher paperEventPublisher;

        public PaperServiceImpl(
                        Cloudinary cloudinary,
                        PaperRepository paperRepository,
                        PaperEventPublisher paperEventPublisher) {

                this.cloudinary = cloudinary;
                this.paperRepository = paperRepository;
                this.paperEventPublisher = paperEventPublisher;
        }

        @Override
        public String uploadPaper(MultipartFile file, String uploadedBy) {

                try {

                        log.info("Checking whether the file is uploaded");

                        if (file == null || file.isEmpty()) {
                                throw new IllegalArgumentException("Please upload a PDF file.");
                        }

                        if (!"application/pdf".equals(file.getContentType())) {
                                throw new IllegalArgumentException("Only PDF files are allowed.");
                        }


                        if (uploadedBy == null || uploadedBy.isBlank()) {
                                throw new IllegalArgumentException("User email is required.");
                        }

                        log.info("Uploading paper '{}' by user '{}'",
                                        file.getOriginalFilename(), uploadedBy);

                        Map uploadResult = cloudinary.uploader().upload(
                                        file.getBytes(),
                                        ObjectUtils.asMap("resource_type", "raw"));

                        log.info("Cloudinary upload successful");

                        String pdfUrl = uploadResult.get("secure_url").toString();

                        Paper paper = new Paper();
                        paper.setTitle(file.getOriginalFilename());
                        paper.setPdfUrl(pdfUrl);
                        paper.setUploadedBy(uploadedBy);
                        paper.setUploadedAt(LocalDateTime.now());

                        log.info("Saving paper metadata into MySQL");

                        Paper savedPaper = paperRepository.save(paper);

                        log.info("Paper saved successfully. Paper ID={}", savedPaper.getId());

                        paperEventPublisher.publishPaperUploadedEvent(savedPaper.getId());

                        log.info("Paper upload event published for Paper ID={}", savedPaper.getId());

                        return "Uploaded successfully";

                } catch (Exception e) {

                        log.error("Failed to upload paper '{}'",
                                        file.getOriginalFilename(), e);

                        throw new RuntimeException(e);
                }
        }

        @Override
        public Paper getPaperById(Long id) {

                log.info("Fetching paper with ID={}", id);

                return paperRepository.findById(id)
                                .orElseThrow(() -> new PaperNotFoundException("Paper Not Found"));
        }

        @Override
        public void deletePaper(Long id, String userEmail) {

                log.info("Delete request received for Paper ID={} by user '{}'",
                                id, userEmail);

                Paper paper = paperRepository.findById(id)
                                .orElseThrow(() -> new PaperNotFoundException("Paper Not Found"));

                if (!paper.getUploadedBy().equals(userEmail)) {

                        log.warn("Unauthorized delete attempt. Paper ID={}, Requested By={}, Owner={}",
                                        id, userEmail, paper.getUploadedBy());

                        throw new UnauthorizedException(
                                        "You are not authorized to delete this paper");
                }

                paperRepository.delete(paper);

                log.info("Paper {} deleted successfully", id);

                paperEventPublisher.publishPaperDeletedEvent(id);

                log.info("Paper delete event published for Paper ID={}", id);
        }
}