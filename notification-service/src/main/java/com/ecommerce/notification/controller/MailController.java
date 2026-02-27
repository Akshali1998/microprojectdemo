package com.ecommerce.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
public class MailController {

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMail(@RequestBody Map<String, String> payload) {
        // For local/dev runs we just log the payload; in real deployments configure JavaMailSender
        log.info("Simulated send email to {} with subject {} and body {}",
                payload.getOrDefault("to", "<no-to>"),
                payload.getOrDefault("subject", "<no-subject>"),
                payload.getOrDefault("body", "<no-body>"));

        return ResponseEntity.ok(Map.of("status", "sent-simulated"));
    }
}

