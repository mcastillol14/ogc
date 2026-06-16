package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackRepository extends JpaRepository<Pack, Integer> {

    List<Pack> findAllByActiveTrue();
}
