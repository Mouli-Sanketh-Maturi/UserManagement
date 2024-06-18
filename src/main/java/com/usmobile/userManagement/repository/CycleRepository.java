package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.Cycle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CycleRepository extends MongoRepository<Cycle, String> {
    public Cycle findCurrentCycleByUserIdAndMdn(String userId, String mdn);

    public List<Cycle> findByUserIdAndMdnOrderByStartDateDesc(String userId, String mdn);
}
