package com.research.paper_service.publisher;

import com.research.paper_service.config.RabbitMQConfig;
import com.research.paper_service.event.PaperUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                event
        );

        System.out.println("Published Event: " + paperId);
    }
}