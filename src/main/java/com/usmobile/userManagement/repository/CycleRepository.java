package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.Cycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CycleRepository extends MongoRepository<Cycle, String> {
    /**
     * Find the active cycle based on provided date by userId and mdn
     * @param userId user id
     * @param mdn mdn of the user
     * @param currentDate date for which active cycle is to be found
     * @return an optional of cycle
     */
    @Query("{ 'userId' : ?0, 'mdn' : ?1, 'startDate' : { $lte : ?2 }, 'endDate' : { $gte : ?2 } }")
    Optional<Cycle> findCurrentCycleByUserIdAndMdn(String userId, String mdn, Long currentDate);

    /**
     * Find all the cycles based on provided userId and mdn
     * @param userId user id
     * @param mdn mdn of the user
     * @return a List of cycles, which can be empty
     */
    List<Cycle> findByUserIdAndMdnOrderByStartDateDesc(String userId, String mdn);
}
