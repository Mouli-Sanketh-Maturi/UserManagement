package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.DailyUsage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface DailyUsageRepository extends MongoRepository<DailyUsage, String> {
    //@Query("{ 'userId' : ?0, 'mdn' : ?1, 'usageDate' : { $gte : ?2, $lte : ?3 }, Sort : { 'usageDate' : -1 } }")
    @Query("{'userId': ?0, 'mdn': ?1, 'usageDate': {$gte: ?2, $lte: ?3}}")
    List<DailyUsage> findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc(String userId, String mdn, Long startDate,
                                                                               Long endDate, Sort sort);
}
