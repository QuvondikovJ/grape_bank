package com.example.uzum.serviceImpl;

import com.example.uzum.dto.branch.BranchDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.entity.Branch;
import com.example.uzum.entity.Region;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.BranchRepo;
import com.example.uzum.repository.OrderRepo;
import com.example.uzum.repository.RegionRepo;
import com.example.uzum.service.BranchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepo branchRepo;
    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private OrderRepo orderRepo;

    private static final Logger logger = LogManager.getLogger(BranchServiceImpl.class);

    @Override
    public Result<?> add(BranchDTO dto) {
        Optional<Region> optional = regionRepo.findByIdAndActive(dto.getRegionId(), true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        Region region = optional.get();
        boolean existsByNameEnOrNameUzAndRegionId = branchRepo.existsByNameEnOrNameUzAndRegionId(dto.getNameEn(), dto.getNameUz(), dto.getRegionId());
        if (existsByNameEnOrNameUzAndRegionId) return new Result<>(false, Messages.THIS_BRANCH_ALREADY_ADDED);
        Branch branch = Branch.builder()
                .nameEn(dto.getNameEn())
                .nameUz(dto.getNameUz())
                .region(region)
                .startTimeOfWorking(dto.getStartTimeOfWorking())
                .endTimeOfWorking(dto.getEndTimeOfWorking())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .isActive(Boolean.TRUE)
                .build();
        branch = branchRepo.save(branch);
        logger.info("New branch add. ID : {}, Region ID : {}", branch.getId(), region.getId());
        return new Result<>(true, Messages.BRANCH_ADDED);
    }

    @Override
    public Result<?> getByRegionId(Integer regionId) {
        Optional<Region> optional = regionRepo.findByIdAndActive(regionId, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_REGION_ID_NOT_EXIST);
        List<Branch> branches = branchRepo.getByRegionIdAndActive(regionId, true);
        if (branches.isEmpty()) return new Result<>(false, Messages.BRANCHES_HAVE_NOT_BEEN_ADDED_YET);
        return new Result<>(true, branches);
    }

    @Override
    public Result<?> getById(Integer id) {
        Optional<Branch> optional = branchRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        return new Result<>(true, optional.get());
    }

    @Override
    public Result<?> edit(Integer id, BranchDTO dto) {
        Optional<Branch> optional = branchRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Branch branch = optional.get();
        boolean existsByNameEnOrNameUzAndRegionId = branchRepo.existsByNameEnOrNameUzAndRegionIdAndIdNot(dto.getNameEn(), dto.getNameUz(), branch.getRegion().getId(), id);
        if (existsByNameEnOrNameUzAndRegionId) return new Result<>(false, Messages.THIS_BRANCH_ALREADY_ADDED);
        branch.setNameEn(dto.getNameEn());
        branch.setNameUz(dto.getNameUz());
        branch.setStartTimeOfWorking(dto.getStartTimeOfWorking());
        branch.setEndTimeOfWorking(dto.getEndTimeOfWorking());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branchRepo.save(branch);
        logger.info("Branch information updated. ID : {}", id);
        return new Result<>(true, Messages.BRANCH_UPDATED);
    }

    @Override
    public Result<?> delete(Integer id) {
        Optional<Branch> optional = branchRepo.findByIdAndActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        Branch branch = optional.get();
        branch.setActive(false);
        branchRepo.save(branch);
        logger.info("Branch deactivated. Id : {}", id);
        return new Result<>(true, Messages.BRANCH_DELETED);
    }

    @Override
    public Result<?> completelyDelete(Integer id) {
        Optional<Branch> optional = branchRepo.findById(id);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRANCH_ID_NOT_EXIST);
        boolean existsOrdersByBranchId = orderRepo.existsOrdersByBranchId(id);
        if (existsOrdersByBranchId) return new Result<>(false, Messages.THIS_BRANCH_HAS_ORDERS);
        branchRepo.deleteById(id);
        logger.info("Branch deleted. ID : {}", id);
        return new Result<>(true, Messages.BRANCH_COMPLETELY_DELETED);
    }
}
