package com.research.paper_service.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.research.paper_service.config.RabbitMQConfig;
import com.research.paper_service.event.PaperDeletedEvent;
import com.research.paper_service.event.PaperUploadedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaperEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PaperEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaperUploadedEvent(Long paperId) {

        PaperUploadedEvent event = new PaperUploadedEvent(paperId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAPER_EXCHANGE,
                RabbitMQConfig.PAPER_ROUTING_KEY,
                event);

        log.info("Published Upload Event for Paper ID={}", paperId);
    }

    public void publishPaperDeletedEvent(Long paperId) {

        PaperDeletedEvent event = new PaperDeletedEvent(paperId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAPER_EXCHANGE,
                RabbitMQConfig.PAPER_DELETE_ROUTING_KEY,
                event);

        log.info("Published Delete Event for Paper ID={}", paperId);
    }
}