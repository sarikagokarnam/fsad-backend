package com.certifypro.backend.repository;

import com.certifypro.backend.model.Certification;
import com.certifypro.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationRepository 
    extends JpaRepository<Certification, Long> {
    List<Certification> findByUserOrderByCreatedAtDesc(
        User user);
}
