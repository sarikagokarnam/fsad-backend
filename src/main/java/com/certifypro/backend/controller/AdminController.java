package com.certifypro.backend.controller;

import com.certifypro.backend.model.*;
import com.certifypro.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired 
    private UserRepository userRepo;
    
    @Autowired 
    private CertificationRepository certRepo;
    
    @Autowired 
    private RenewalRequestRepository renewalRepo;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/certifications")
    public List<Certification> getAllCerts() {
        return certRepo.findAll();
    }

    @GetMapping("/renewals")
    public List<RenewalRequest> getAllRenewals() {
        return renewalRepo
            .findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/renewals/{id}")
    public ResponseEntity<?> updateRenewal(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {
        return renewalRepo.findById(id).map(r -> {
            r.setStatus(body.get("status"));
            if (body.get("newExpDate") != null) {
                LocalDate newDate = LocalDate.parse(
                    body.get("newExpDate"));
                r.setNewExpDate(newDate);
                if ("approved".equals(
                    body.get("status"))) {
                    r.getCertification()
                        .setExpDate(newDate);
                    certRepo.save(
                        r.getCertification());
                }
            }
            return ResponseEntity.ok(
                renewalRepo.save(r));
        }).orElse(
            ResponseEntity.notFound().build());
    }
}
