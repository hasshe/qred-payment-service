package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    @GetMapping("/contract/{contractNumber}")
    public ResponseEntity<String> getPaymentsByContractNumber(@PathVariable String contractNumber) {
        return ResponseEntity.ok().body(contractNumber);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<String> getPaymentsByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok().body(clientId);
    }

    @PostMapping
    public ResponseEntity<String> createPayment(@Valid @RequestBody String value) {
        return ResponseEntity.ok().body(value);
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(file.getOriginalFilename());
    }
}
