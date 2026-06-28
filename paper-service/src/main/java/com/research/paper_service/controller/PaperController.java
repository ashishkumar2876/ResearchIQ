package com.research.paper_service.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.research.paper_service.entity.Paper;
import com.research.paper_service.service.PaperService;

@RestController
@RequestMapping("/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping("/upload")
    public String uploadPaper(@RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy) {
        return paperService.uploadPaper(file, uploadedBy);
    }

    @GetMapping("/{id}")
    public Paper getPaperById(
            @PathVariable Long id) {
        return paperService.getPaperById(id);
    }

    @DeleteMapping("/{id}")
    public String deletePaper(
            @PathVariable Long id) {
        paperService.deletePaper(id);

        return "Paper deleted successfully";
    }
}
