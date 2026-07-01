package com.research.ai_analysis_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.research.ai_analysis_service.config.RabbitMQConfig;
import com.research.ai_analysis_service.event.PaperDeletedEvent;
import com.research.ai_analysis_service.repository.PaperAnalysisRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaperDeletedListener {

    private final PaperAnalysisRepository paperAnalysisRepository;

    public PaperDeletedListener(PaperAnalysisRepository paperAnalysisRepository) {
        this.paperAnalysisRepository = paperAnalysisRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.PAPER_DELETE_QUEUE)
    public void receivePaperDeletedEvent(PaperDeletedEvent event) {

        log.info("Received Delete Event for Paper ID : {}", event.getPaperId());

        paperAnalysisRepository.deleteByPaperId(event.getPaperId());

        log.info("Analysis deleted successfully for Paper ID : {}", event.getPaperId());

    }
}