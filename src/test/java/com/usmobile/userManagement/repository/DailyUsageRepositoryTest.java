package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.DailyUsage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@DataMongoTest
@Testcontainers
public class DailyUsageRepositoryTest {

    @Autowired
    private DailyUsageRepository dailyUsageRepository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.11");

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "usageDate");

    @AfterEach
    void tearDown() {
        dailyUsageRepository.deleteAll();
    }

    @Test
    void testDailyUsageRepository_NoData() {
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", System.currentTimeMillis(), System.currentTimeMillis() + 86400000, sort);
        Assertions.assertTrue(dailyUsageList.isEmpty());
    }

    @Test
    void testDailyUsageRepository_WithSingleDataUsage() {
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage = new DailyUsage();
        dailyUsage.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage.setMdn("1234567890");
        dailyUsage.setUsageDate(currentDate);
        dailyUsage.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage);

        System.out.println("currentDate: " + currentDate);

        //Fetch data usage for the past and next 24 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", System.currentTimeMillis() - 86400000,
                        System.currentTimeMillis() + 86400000, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(1, dailyUsageList.size());
        Assertions.assertEquals(100, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(0).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithMultipleDataUsage() {
        //Current Day Data Usage
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage1.setMdn("1234567890");
        dailyUsage1.setUsageDate(currentDate);
        dailyUsage1.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage1);

        //Previous Day Data Usage
        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage2.setMdn("1234567890");
        dailyUsage2.setUsageDate(currentDate - 86400000);
        dailyUsage2.setUsedInMb(200);
        dailyUsageRepository.save(dailyUsage2);

        //Next Day Data Usage
        DailyUsage dailyUsage3 = new DailyUsage();
        dailyUsage3.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage3.setMdn("1234567890");
        dailyUsage3.setUsageDate(currentDate + 86400000);
        dailyUsage3.setUsedInMb(300);
        dailyUsageRepository.save(dailyUsage3);

        //Fetch data usage for the past and next 48 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", System.currentTimeMillis() - 172800000,
                        System.currentTimeMillis() + 172800000,sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(3, dailyUsageList.size());
        Assertions.assertEquals(300, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(100, dailyUsageList.get(1).getUsedInMb());
        Assertions.assertEquals(200, dailyUsageList.get(2).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(1).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithMultipleDataUsageAndDifferentUser() {
        //Current Day Data Usage
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage1.setMdn("1234567890");
        dailyUsage1.setUsageDate(currentDate);
        dailyUsage1.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage1);

        //Previous Day Data Usage
        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage2.setMdn("1234567890");
        dailyUsage2.setUsageDate(currentDate- 86400000);
        dailyUsage2.setUsedInMb(200);
        dailyUsageRepository.save(dailyUsage2);

        //Next Day Data Usage
        DailyUsage dailyUsage3 = new DailyUsage();
        dailyUsage3.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage3.setMdn("1234567890");
        dailyUsage3.setUsageDate(currentDate + 86400000);
        dailyUsage3.setUsedInMb(300);
        dailyUsageRepository.save(dailyUsage3);

        //Different User Data Usage
        DailyUsage dailyUsage4 = new DailyUsage();
        dailyUsage4.setUserId("6671d6cdd518422008b3d9fc");
        dailyUsage4.setMdn("1234567890");
        dailyUsage4.setUsageDate(currentDate + 90000000);
        dailyUsage4.setUsedInMb(400);
        dailyUsageRepository.save(dailyUsage4);

        //Fetch data usage for the past and next 48 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", System.currentTimeMillis() - 172800000,
                        System.currentTimeMillis() + 172800000, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(3, dailyUsageList.size());
        Assertions.assertEquals(300, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate + 86400000, dailyUsageList.get(0).getUsageDate());
        Assertions.assertEquals(100, dailyUsageList.get(1).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(1).getUsageDate());
        Assertions.assertEquals(200, dailyUsageList.get(2).getUsedInMb());
        Assertions.assertEquals(currentDate - 86400000, dailyUsageList.get(2).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithMultipleDataUsageAndDifferentMdn() {
        //Current Day Data Usage
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage1.setMdn("1234567890");
        dailyUsage1.setUsageDate(currentDate);
        dailyUsage1.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage1);

        //Previous Day Data Usage
        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage2.setMdn("1234567890");
        dailyUsage2.setUsageDate(currentDate - 86400000);
        dailyUsage2.setUsedInMb(200);
        dailyUsageRepository.save(dailyUsage2);

        //Current Day Data Usage
        DailyUsage dailyUsage3 = new DailyUsage();
        dailyUsage3.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage3.setMdn("1234567891");
        dailyUsage3.setUsageDate(currentDate);
        dailyUsage3.setUsedInMb(300);
        dailyUsageRepository.save(dailyUsage3);

        //Fetch data usage for the past and next 48 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", System.currentTimeMillis() - 172800000,
                        System.currentTimeMillis() + 172800000, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(2, dailyUsageList.size());
        Assertions.assertEquals(100, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(0).getUsageDate());
        Assertions.assertEquals(200, dailyUsageList.get(1).getUsedInMb());
        Assertions.assertEquals(currentDate - 86400000, dailyUsageList.get(1).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithMultipleDataUsageAndDifferentUserAndMdn() {
        //Current Day Data Usage
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage1.setMdn("1234567890");
        dailyUsage1.setUsageDate(currentDate);
        dailyUsage1.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage1);

        //Previous Day Data Usage
        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage2.setMdn("1234567890");
        dailyUsage2.setUsageDate(currentDate - 86400000);
        dailyUsage2.setUsedInMb(200);
        dailyUsageRepository.save(dailyUsage2);

        //Different User Data Usage
        DailyUsage dailyUsage3 = new DailyUsage();
        dailyUsage3.setUserId("6671d6cdd518422008b3d9fc");
        dailyUsage3.setMdn("1234567890");
        dailyUsage3.setUsageDate(currentDate);
        dailyUsage3.setUsedInMb(300);
        dailyUsageRepository.save(dailyUsage3);

        //Different User Data Usage
        DailyUsage dailyUsage4 = new DailyUsage();
        dailyUsage4.setUserId("6671d6cdd518422008b3d9fc");
        dailyUsage4.setMdn("1234567891");
        dailyUsage4.setUsageDate(currentDate);
        dailyUsage4.setUsedInMb(400);
        dailyUsageRepository.save(dailyUsage4);

        //Fetch data usage for the past and next 48 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890",System.currentTimeMillis() - 172800000,
                        System.currentTimeMillis() + 172800000, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(2, dailyUsageList.size());
        Assertions.assertEquals(100, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(0).getUsageDate());
        Assertions.assertEquals(200, dailyUsageList.get(1).getUsedInMb());
        Assertions.assertEquals(currentDate - 86400000, dailyUsageList.get(1).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithDataUsageOnStartDate() {
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage = new DailyUsage();
        dailyUsage.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage.setMdn("1234567890");
        dailyUsage.setUsageDate(currentDate);
        dailyUsage.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage);

        //Fetch data usage for the past and next 24 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", currentDate, currentDate + 86400000, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(1, dailyUsageList.size());
        Assertions.assertEquals(100, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(0).getUsageDate());
    }

    @Test
    void testDailyUsageRepository_WithDataUsageOnEndDate() {
        Long currentDate = System.currentTimeMillis();
        DailyUsage dailyUsage = new DailyUsage();
        dailyUsage.setUserId("6671d6cdd518422008b3d9fb");
        dailyUsage.setMdn("1234567890");
        dailyUsage.setUsageDate(currentDate);
        dailyUsage.setUsedInMb(100);
        dailyUsageRepository.save(dailyUsage);

        //Fetch data usage for the past and next 24 hours
        List<DailyUsage> dailyUsageList = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc("6671d6cdd518422008b3d9fb",
                        "1234567890", currentDate - 86400000, currentDate, sort);

        Assertions.assertFalse(dailyUsageList.isEmpty());
        Assertions.assertEquals(1, dailyUsageList.size());
        Assertions.assertEquals(100, dailyUsageList.get(0).getUsedInMb());
        Assertions.assertEquals(currentDate, dailyUsageList.get(0).getUsageDate());
    }

}
