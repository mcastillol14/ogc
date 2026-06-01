package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Integer> {

    List<PasswordHistory> findTop5ByCustomerIdOrderByCreatedAtDesc(Integer customerId);
}
