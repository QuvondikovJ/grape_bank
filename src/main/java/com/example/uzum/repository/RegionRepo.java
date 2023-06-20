package com.example.uzum.repository;

import com.example.uzum.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepo extends JpaRepository<Region, Integer> {

    Optional<Region> findByNameEnOrNameUz(String nameEn, String nameUz);

    @Query(value = "SELECT rg FROM Region AS rg WHERE rg.id=:id AND rg.isActive=:active")
    Optional<Region> findByIdAndActive(Integer id, boolean active);

    @Query(value = "SELECT rg FROM Region AS rg WHERE rg.isActive=:active")
    List<Region> findAllByActive(boolean active);

    @Query(value = "SELECT CASE WHEN COUNT(br)>0 THEN TRUE ELSE FALSE END FROM Branch AS br WHERE br.region.id=:regionId AND br.isActive=TRUE ")
    boolean existsBranchesByRegionId(Integer regionId);


    @Query(value = "SELECT reg FROM Region AS reg WHERE (reg.nameEn=:nameEn OR reg.nameUz=:nameUz) AND reg.id<>:id")
    Region getByNameEnOrNameUzAndIdNot(Integer id, String nameEn, String nameUz);
}
