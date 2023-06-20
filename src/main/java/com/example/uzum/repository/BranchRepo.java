package com.example.uzum.repository;

import com.example.uzum.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepo extends JpaRepository<Branch, Integer> {


    @Transactional
    @Modifying
    @Query(value = "UPDATE Branch AS br SET br.isActive=FALSE WHERE br.region.id=:regionId")
    void disActivateBranchesByRegionId(Integer regionId);

    @Query(value = "SELECT COUNT(br)>0 FROM Branch AS br WHERE (br.nameEn=:nameEn OR br.nameUz=:nameUz) AND br.region.id=:regionId AND br.isActive=TRUE AND br.id<>:branchId ")
    boolean existsByNameEnOrNameUzAndRegionIdAndIdNot(String nameEn, String nameUz, Integer regionId, Integer branchId);

    @Query(value = "SELECT COUNT(br)>0 FROM Branch AS br WHERE (br.nameEn=:nameEn OR br.nameUz=:nameUz) AND br.region.id=:regionId AND br.isActive=TRUE ")
    boolean existsByNameEnOrNameUzAndRegionId(String nameEn, String nameUz, Integer regionId);

    @Query(value = "SELECT br FROM Branch AS br WHERE br.region.id=:regionId AND br.isActive=:active")
    List<Branch> getByRegionIdAndActive(Integer regionId, Boolean active);

    @Query(value = "SELECT br FROM Branch AS br WHERE br.id=:id AND br.isActive=:active")
    Optional<Branch> findByIdAndActive(Integer id, Boolean active);

}
