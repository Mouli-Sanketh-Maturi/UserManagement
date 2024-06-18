package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.Cycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CycleRepository extends MongoRepository<Cycle, String> {
    @Query("{ 'userId' : ?0, 'mdn' : ?1, 'startDate' : { $lte : ?2 }, 'endDate' : { $gte : ?2 } }")
    Optional<Cycle> findCurrentCycleByUserIdAndMdn(String userId, String mdn, Date currentDate);

    List<Cycle> findByUserIdAndMdnOrderByStartDateDesc(String userId, String mdn);
}
