package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByPhoneNumber(String phoneNumber);
}
