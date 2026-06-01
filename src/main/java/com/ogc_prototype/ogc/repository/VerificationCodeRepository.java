package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.VerificationCode;
import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    Optional<VerificationCode> findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
            String email, VerificationCodePurpose purpose);

    void deleteByEmailAndPurpose(String email, VerificationCodePurpose purpose);
}
