package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.DailyUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface DailyUsageRepository extends MongoRepository<DailyUsage, String> {
    List<DailyUsage> findByMdnAndUserIdAndUsageDateBetweenOrderByUsageDateDesc(String mdn, String userId, Date startDate, Date endDate);
}
