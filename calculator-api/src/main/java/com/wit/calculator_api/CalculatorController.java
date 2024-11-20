package com.wit.calculator_api;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final RabbitTemplate rabbitTemplate;
    @Value("${exchangeKey}")
    private String exchangeKey;
    @Value("${routingKey}")
    private String routingKey;

    public CalculatorController(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping(value = "/sum")
    public ResponseEntity getSum(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            Map<String, Object> res = sendRequest("add", a, b);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/subtraction")
    public ResponseEntity getSubtraction(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            Map<String, Object> res = sendRequest("subtract", a, b);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/multiplication")
    public ResponseEntity getMultiplication(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            Map<String, Object> res = sendRequest("multiply", a, b);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    @GetMapping(value = "/division")
    public ResponseEntity getDivision(@RequestParam BigDecimal a, @RequestParam BigDecimal b){
        try {
            Map<String, Object> res = sendRequest("divide", a, b);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }

    private Map<String, Object> sendRequest(String operation, BigDecimal a, BigDecimal b) {
        Map<String, Object> message = new HashMap<>();
        message.put("operation", operation);
        message.put("a", a);
        message.put("b", b);

        BigDecimal result = (BigDecimal) rabbitTemplate.convertSendAndReceive(exchangeKey, routingKey, message);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        return response;
    }
}
