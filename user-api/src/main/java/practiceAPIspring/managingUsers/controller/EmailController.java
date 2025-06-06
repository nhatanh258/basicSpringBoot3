package practiceAPIspring.managingUsers.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.config.UserRegisteredEvent;
import practiceAPIspring.managingUsers.dto.request.event.EventRequest;
import practiceAPIspring.managingUsers.service.EmailService;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String to,
                                            @RequestParam String title,
                                            @RequestParam String message) {
        emailService.sendSimpleEmail(to, title, message);
        return ResponseEntity.ok("Email sent!");
    }



    // gui su kien ket hop lang nghe qua event
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/email/event")
    public void informSomeDetails(@RequestBody EventRequest eventRequest) {
        log.info("thong tin event: " + eventRequest.getName());

        // Gửi sự kiện
        eventPublisher.publishEvent(new UserRegisteredEvent(eventRequest.getName(),eventRequest.getDescription()));
    }

    //doc email tu boxes
    @GetMapping("/emails")
    public List<String> getEmails() {
        return emailService.readEmails();
    }

    //gui themleaf

    @PostMapping("/send-template")
    public ResponseEntity<String> sendTemplateEmail(@RequestParam String to,
                                                    @RequestParam String subject,
                                                    @RequestParam String username) {
        try {
            emailService.sendTemplateEmail(to, subject, username);
            return ResponseEntity.ok("Template email sent!");
        } catch (Exception e) {
            log.error("Không gửi được email template: ", e);
            return ResponseEntity.status(500).body("Gửi email thất bại!");
        }
    }

    // goi bat dong bo send email
    @GetMapping("/test-async")
    public String testAsync() {
        for (int i = 1; i <= 10; i++) {
            emailService.sendEmail("user" + i + "@gmail.com", "Subject", "Body");
        }
        return "Emails are being sent asynchronously!";
    }
}
