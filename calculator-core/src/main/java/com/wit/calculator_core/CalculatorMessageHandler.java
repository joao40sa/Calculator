package com.wit.calculator_core;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalculatorMessageHandler {

    private final CalculatorService calculatorService;
    @Value("${uniqueIdHeaderKey}")
    private String UNIQUE_ID_HEADER;
    @Value("${mdcUniqueIdKey}")
    private String MDC_UNIQUE_ID;

    @Autowired
    private ObjectMapper objectMapper;

    public CalculatorMessageHandler() {
        this.calculatorService = new CalculatorService();
    }

    @RabbitListener(queues = "${queueKey}")
    public BigDecimal processMessage(Message message) {
        String uniqueId = (String) message.getMessageProperties().getHeaders().get(UNIQUE_ID_HEADER);
        MDC.put(MDC_UNIQUE_ID, uniqueId);

        try {
            String messageBody = new String(message.getBody());
            HashMap<String, Object> map = objectMapper.readValue(messageBody, HashMap.class);

            String operation = (String) map.get("operation");
            BigDecimal a = new BigDecimal(map.get("a").toString());
            BigDecimal b = new BigDecimal(map.get("b").toString());

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

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deserializing the message body");
        }
        return null;
    }
}
