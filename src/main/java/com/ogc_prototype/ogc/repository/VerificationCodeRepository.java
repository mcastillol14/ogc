package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    Optional<VerificationCode> findTopByEmailAndUsedFalseOrderByExpiresAtDesc(String email);

    void deleteByEmail(String email);
}
