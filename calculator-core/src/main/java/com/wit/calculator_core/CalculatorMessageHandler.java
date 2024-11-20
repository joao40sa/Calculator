package com.wit.calculator_core;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class CalculatorMessageHandler {

    private final CalculatorService calculatorService;

    public CalculatorMessageHandler() {
        this.calculatorService = new CalculatorService();
    }

    @RabbitListener(queues = "${queueKey}")
    public BigDecimal processMessage(Map<String, Object> message) {
        String operation = (String) message.get("operation");
        BigDecimal a = new BigDecimal(message.get("a").toString());
        BigDecimal b = new BigDecimal(message.get("b").toString());

        switch (operation) {
            case "add":
                return calculatorService.add(a, b);
            case "subtract":
                return calculatorService.subtract(a, b);
            case "multiply":
                return calculatorService.multiply(a, b);
            case "divide":
                return calculatorService.divide(a, b);
            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
