package com.research.paper_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Upload Event
    public static final String PAPER_UPLOAD_QUEUE = "paper.upload.queue";
    public static final String PAPER_ROUTING_KEY = "paper.uploaded";

    // Delete Event
    public static final String PAPER_DELETE_QUEUE = "paper.delete.queue";
    public static final String PAPER_DELETE_ROUTING_KEY = "paper.delete";

    // Common Exchange
    public static final String PAPER_EXCHANGE = "paper.exchange";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());

        return rabbitTemplate;
    }

    // ==========================
    // Queues
    // ==========================

    @Bean
    public Queue paperUploadQueue() {
        return new Queue(PAPER_UPLOAD_QUEUE);
    }

    @Bean
    public Queue paperDeleteQueue() {
        return new Queue(PAPER_DELETE_QUEUE);
    }

    // ==========================
    // Exchange
    // ==========================

    @Bean
    public TopicExchange paperExchange() {
        return new TopicExchange(PAPER_EXCHANGE);
    }

    // ==========================
    // Bindings
    // ==========================

    @Bean
    public Binding uploadBinding(
            Queue paperUploadQueue,
            TopicExchange paperExchange) {

        return BindingBuilder.bind(paperUploadQueue)
                .to(paperExchange)
                .with(PAPER_ROUTING_KEY);
    }

    @Bean
    public Binding deleteBinding(
            Queue paperDeleteQueue,
            TopicExchange paperExchange) {

        return BindingBuilder.bind(paperDeleteQueue)
                .to(paperExchange)
                .with(PAPER_DELETE_ROUTING_KEY);
    }
}