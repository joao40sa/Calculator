package com.wit.calculator_core;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value(value = "${queueKey}")
    private String queueKey;
    @Value("${exchangeKey}")
    private String exchangeKey;
    @Value("${routingKey}")
    private String routingKey;

    @Bean
    public Queue calculatorQueue() {
        return new Queue(queueKey, true);
    }

    @Bean
    public Binding calculatorBinding(Queue calculatorQueue, TopicExchange calculatorExchange) {
        return BindingBuilder.bind(calculatorQueue).to(calculatorExchange).with(routingKey);
    }

    @Bean
    public TopicExchange calculatorExchange() {
        return new TopicExchange(exchangeKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}