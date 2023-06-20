package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Region;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.BranchRepo;
import com.example.uzum.repository.RegionRepo;
import com.example.uzum.service.RegionService;
import org.apache.el.lang.ELArithmetic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private BranchRepo branchRepo;

    private static final Logger logger = LogManager.getLogger(RegionServiceImpl.class);

    @Override
    public Result<?> add(Region region) {
        Optional<Region> optional = regionRepo.findByNameEnOrNameUz(region.getNameEn(), region.getNameUz());
        if (optional.isPresent()) {
            if (optional.get().isActive())
                return new Result<>(false, Messages.THIS_REGION_HAS_BEEN_ALREADY_ADDED);
            else return new Result<>(false, Messages.THIS_REGION_ADDED_AND_DELETED_ETC);
        }

        region = regionRepo.save(region);
        logger.info("New region added. ID: {}",region.getId());
        return new Result<>(true, Messages.REGION_ADDED);
    }

    @Override
    public Result<?> getAll() {
        List<Region> regions = regionRepo.findAllByActive(true);
        if (regions.isEmpty()) return new Result<>(false, Messages.REGIONS_HAVE_NOT_BEEN_ADDED_YET);
        return new Result<>(true, regions);
    }

    @Override
    public Result<?> getById(Integer id) {
        Optional<Region> optional = regionRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        return new Result<>(true, optional.get());
    }

    @Override
    public Result<?> edit(Integer id, Region newRegion) {
        Optional<Region> optional = regionRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        Region region = optional.get();
        Region existsByNewName = regionRepo.getByNameEnOrNameUzAndIdNot(id, newRegion.getNameEn(), newRegion.getNameUz());
        if (existsByNewName != null){
            if (existsByNewName.isActive()){
                return new Result<>(false, Messages.THIS_REGION_HAS_BEEN_ALREADY_ADDED);
            }else return new Result<>(false, Messages.THIS_REGION_ADDED_AND_DELETED_ETC);
        }
        region.setNameEn(newRegion.getNameEn());
        region.setNameUz(newRegion.getNameUz());
        region.setLongitude(newRegion.getLongitude());
        region.setLatitude(newRegion.getLatitude());
        regionRepo.save(region);
        logger.info("Region information updated. ID : {}",id);
        return new Result<>(true, Messages.REGION_UPDATED);
    }

    @Override
    public Result<?> delete(Integer id) {
        Optional<Region> optional = regionRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        Region region = optional.get();
        region.setActive(false);
        regionRepo.save(region);
        branchRepo.disActivateBranchesByRegionId(region.getId());
        logger.info("Region deactivated. ID : {}", id);
        return new Result<>(true, Messages.REGION_DELETED);
    }

    @Override
    public Result<?> completelyDelete(Integer id) {
        Optional<Region> optional = regionRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        boolean existsBranchesByRegionId = regionRepo.existsBranchesByRegionId(id);
        if (existsBranchesByRegionId) return new Result<>(false, Messages.THIS_REGION_CONNECTED_BRANCH_ETC);
        regionRepo.deleteById(id);
        logger.info("Region deleted. ID : {}", id);
        return new Result<>(true, Messages.REGION_COMPLETELY_DELETED);
    }
}
