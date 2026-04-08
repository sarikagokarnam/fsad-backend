package com.certifypro.backend.controller;

import com.certifypro.backend.model.*;
import com.certifypro.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation
    .AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "http://localhost:3000")
public class CertificationController {

    @Autowired
    private CertificationRepository certRepo;

    @Autowired
    private RenewalRequestRepository renewalRepo;

    @GetMapping
    public List<Certification> getMyCerts(
        @AuthenticationPrincipal User user) {
        return certRepo
            .findByUserOrderByCreatedAtDesc(user);
    }

    @PostMapping
    public ResponseEntity<?> addCert(
        @AuthenticationPrincipal User user,
        @RequestBody Certification cert) {
        cert.setUser(user);
        return ResponseEntity.ok(certRepo.save(cert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCert(
        @AuthenticationPrincipal User user,
        @PathVariable Long id) {
        certRepo.findById(id).ifPresent(c -> {
            if (c.getUser().getId()
                .equals(user.getId()))
                certRepo.delete(c);
        });
        return ResponseEntity.ok("Deleted");
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<?> requestRenewal(
        @AuthenticationPrincipal User user,
        @PathVariable Long id) {
        return certRepo.findById(id).map(cert -> {
            RenewalRequest r = new RenewalRequest();
            r.setUser(user);
            r.setCertification(cert);
            return ResponseEntity.ok(
                renewalRepo.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }
}