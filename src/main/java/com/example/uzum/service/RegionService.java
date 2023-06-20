package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Region;
import org.springframework.stereotype.Service;


@Service
public interface RegionService {
    Result<?> add(Region region);

    Result<?> getAll();

    Result<?> getById(Integer id);

    Result<?> edit(Integer id, Region region);

    Result<?> delete(Integer id);

    Result<?> completelyDelete(Integer id);
}
