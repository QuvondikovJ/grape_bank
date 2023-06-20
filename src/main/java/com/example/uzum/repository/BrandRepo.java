package com.example.uzum.repository;

import com.example.uzum.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Integer> {

    Optional<Brand> findByNameAndIsActive(String name, Boolean isActive);

    List<Brand> getAllByIsActiveOrderByNameAsc(Boolean isActive);

    Optional<Brand> findByIdAndIsActive(Integer id, Boolean isActive);

    boolean existsByNameAndIsActiveAndIdNot(String name, Boolean isActive, Integer id);
}
