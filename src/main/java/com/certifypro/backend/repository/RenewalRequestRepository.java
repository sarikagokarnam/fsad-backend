package com.certifypro.backend.repository;

import com.certifypro.backend.model.RenewalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RenewalRequestRepository 
    extends JpaRepository<RenewalRequest, Long> {
    List<RenewalRequest> findAllByOrderByCreatedAtDesc();
}
