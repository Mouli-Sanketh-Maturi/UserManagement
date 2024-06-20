package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.DailyUsage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface DailyUsageRepository extends MongoRepository<DailyUsage, String> {
    /**
     * Get daily usage details by user id and mdn for usage date between and order by usage date desc.
     *
     * @param userId    the user id
     * @param mdn       the mdn
     * @param startDate the start date of the usage (inclusive)
     * @param endDate   the end date of the usage (inclusive)
     * @param sort      the sorting criteria
     * @return the list of daily usage details for the given criteria
     */
    @Query("{'userId': ?0, 'mdn': ?1, 'usageDate': {$gte: ?2, $lte: ?3}}")
    List<DailyUsage> findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc(String userId, String mdn, Long startDate,
                                                                               Long endDate, Sort sort);
}
