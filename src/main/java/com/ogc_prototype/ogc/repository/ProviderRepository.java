package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {

    Optional<Provider> findByName(String name);

    Optional<Provider> findByEmail(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Provider> findAllByActiveTrue();

    List<Provider> findAllByActiveFalse();
}
