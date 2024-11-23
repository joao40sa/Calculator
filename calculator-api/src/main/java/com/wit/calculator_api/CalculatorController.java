package com.wit.calculator_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    @Autowired
    private ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    @Value("${exchangeKey}")
    private String exchangeKey;
    @Value("${routingKey}")
    private String routingKey;
    @Value("${uniqueIdHeaderKey}")
    private String UNIQUE_ID_HEADER;

    public CalculatorController(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping(value = "/sum")
    public ResponseEntity getSum(HttpServletResponse response, @RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            String uniqueId = response.getHeader(UNIQUE_ID_HEADER);
            Map<String, Object> res = sendRequest("add", a, b, uniqueId);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/subtraction")
    public ResponseEntity getSubtraction(HttpServletResponse response, @RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            String uniqueId = response.getHeader(UNIQUE_ID_HEADER);
            Map<String, Object> res = sendRequest("subtract", a, b, uniqueId);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/multiplication")
    public ResponseEntity getMultiplication(HttpServletResponse response, @RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            String uniqueId = response.getHeader(UNIQUE_ID_HEADER);
            Map<String, Object> res = sendRequest("multiply", a, b, uniqueId);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/division")
    public ResponseEntity getDivision(HttpServletResponse response, @RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            String uniqueId = response.getHeader(UNIQUE_ID_HEADER);
            Map<String, Object> res = sendRequest("divide", a, b, uniqueId);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    private Map<String, Object> sendRequest(String operation, BigDecimal a, BigDecimal b, String uniqueId) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("operation", operation);
        body.put("a", a);
        body.put("b", b);

        String messageBody = objectMapper.writeValueAsString(body);
        Message message = MessageBuilder.withBody(messageBody.getBytes())
                .setHeader(UNIQUE_ID_HEADER, uniqueId)
                .build();

        BigDecimal result = (BigDecimal) rabbitTemplate.convertSendAndReceive(exchangeKey, routingKey, message);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        return response;
    }
}
