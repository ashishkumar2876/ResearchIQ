package com.research.ai_analysis_service.client;

import com.research.ai_analysis_service.dto.PaperResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "paper-service", url = "http://localhost:8082")
public interface PaperServiceClient {

    @GetMapping("/papers/{id}")
    PaperResponse getPaperById(@PathVariable Long id);
}