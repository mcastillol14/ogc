package com.ogc_prototype.ogc.repository;

import com.ogc_prototype.ogc.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Category> findAllByActiveTrue();

    List<Category> findAllByActiveFalse();
}
