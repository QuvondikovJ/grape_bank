package com.example.uzum.service;

import com.example.uzum.dto.branch.BranchDTO;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

@Service
public interface BranchService {


    Result<?> add(BranchDTO dto);

    Result<?> getByRegionId(Integer regionId);

    Result<?> getById(Integer id);

    Result<?> edit(Integer id, BranchDTO dto);

    Result<?> delete(Integer id);

    Result<?> completelyDelete(Integer id);
}
