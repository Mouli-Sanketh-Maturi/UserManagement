package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.Cycle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataMongoTest
@Testcontainers
public class CycleRepositoryTest {

    @Autowired
    CycleRepository cycleRepository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.11");

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        cycleRepository.deleteAll();
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_Found() {
        // Start date is yesterday and end date is tomorrow
        Cycle cycle = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn("6671d6cdd518422008b3d9fb",
                "1234567890", System.currentTimeMillis());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),"6671d6cdd518422008b3d9fb");
        Assertions.assertEquals(result.get().getMdn(),"1234567890");
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_NoCyclesInDb() {
        // No cycles in the db
        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn("6671d6cdd518422008b3d9fb",
                "1234567890", System.currentTimeMillis());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_NoCurrentCycle() {
        // End date is before current date
        Cycle cycle = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() - 43200000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn("6671d6cdd518422008b3d9fb",
                "1234567890", System.currentTimeMillis());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_CycleStartDateOnCurrentDate() {
        Long currentDate = System.currentTimeMillis();
        // Start date is now and end date is tomorrow
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", currentDate,
                System.currentTimeMillis() + 86400000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle1);
        // Start date is tomorrow and end date is day after tomorrow
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fa", "1234567890", System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn("6671d6cdd518422008b3d9fb",
                "1234567890", currentDate);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),cycle1.getUserId());
        Assertions.assertEquals(result.get().getMdn(),cycle1.getMdn());
        Assertions.assertEquals(result.get().getId(),cycle1.getId());
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_CycleEndDateOnCurrentDate() {
        Long currentDate = System.currentTimeMillis();
        // Start date is yesterday and end date is now
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890",currentDate - 86400000,
                currentDate, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle1);
        // Start date is 1ms from now and end date is tomorrow
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fa", "1234567890", currentDate + 1,
                currentDate + 86400000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn("6671d6cdd518422008b3d9fb",
                "1234567890", currentDate);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),cycle1.getUserId());
        Assertions.assertEquals(result.get().getMdn(),cycle1.getMdn());
        Assertions.assertEquals(result.get().getId(),cycle1.getId());
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_MultipleUsers() {
        String userId1 = "667d6cdd518422008b3d9fb";
        String userId2 = "667d6cdd518422008b3d9fc";
        // Start date is yesterday and end date is tomorrow
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId1);
        cycleRepository.save(cycle1);
        // Start date is yesterday and end date is tomorrow
        Cycle cycle2 = new Cycle("6671d6f6d518422008b3d9fd", "2764552340", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId2);
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn(userId2,
                "2764552340", System.currentTimeMillis());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),userId2);
        Assertions.assertEquals(result.get().getMdn(),"2764552340");
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_MultipleMdns() {
        String userId = "667d6cdd518422008b3d9fb";
        String mdn1 = "1234567890";
        String mdn2 = "2764552340";
        // Start date is yesterday and end date is tomorrow
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", mdn1, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId);
        cycleRepository.save(cycle1);
        // Start date is yesterday and end date is tomorrow
        Cycle cycle2 = new Cycle("6671d6f6d518422008b3d9fd", mdn2, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId);
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn(userId,
                mdn1, System.currentTimeMillis());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),userId);
        Assertions.assertEquals(result.get().getMdn(),mdn1);
    }

    @Test
    void testFindCurrentCycleByUserIdAndMdn_MultipleUsersAndMdns() {
        String userId1 = "667d6cdd518422008b3d9fb";
        String userId2 = "667d6cdd518422008b3d9fc";
        String mdn1 = "1234567890";
        String mdn2 = "2764552340";
        // Start date is yesterday and end date is tomorrow
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", mdn1, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId1);
        cycleRepository.save(cycle1);
        // Start date is yesterday and end date is tomorrow
        Cycle cycle2 = new Cycle("6671d6f6d518422008b3d9fd", mdn2, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId2);
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findCurrentCycleByUserIdAndMdn(userId2,
                mdn2, System.currentTimeMillis());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getUserId(),userId2);
        Assertions.assertEquals(result.get().getMdn(),mdn2);
    }

    @Test
    void testFindCyclesByUserIdAndMdnOrderByStartDateDesc_NotFound() {
        // No cycles in the db
        Optional<Cycle> result = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc("6671d6cdd518422008b3d9fb",
                "1234567890").stream().findFirst();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindCyclesByUserIdAndMdnOrderByStartDateDesc() {
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, "6671d6cdd518422008b3d9fb");
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fa", "1234567890", System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle1);
        cycleRepository.save(cycle2);

        Optional<Cycle> result = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc("6671d6cdd518422008b3d9fb",
                "1234567890").stream().findFirst();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(),cycle2.getId());
        Assertions.assertEquals(result.get().getUserId(),cycle2.getUserId());
        Assertions.assertEquals(result.get().getMdn(),cycle2.getMdn());
    }

    @Test
    void testFindCyclesByUserIdAndMdnOrderByStartDateDesc_MultipleCycles() {

        Date currentDate = new Date();
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, "6671d6cdd518422008b3d9fb");
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fa", "1234567890", System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, "6671d6cdd518422008b3d9fb");
        Cycle cycle3 = new Cycle("66711abbd518422008b3d9fa", "1234567890", System.currentTimeMillis() + 172800001,
                System.currentTimeMillis() + 259200000, "6671d6cdd518422008b3d9fb");
        cycleRepository.save(cycle1);
        cycleRepository.save(cycle2);
        cycleRepository.save(cycle3);

        Optional<Cycle> result = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc("6671d6cdd518422008b3d9fb",
                "1234567890").stream().findFirst();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(),cycle3.getId());
        Assertions.assertEquals(result.get().getUserId(),cycle3.getUserId());
        Assertions.assertEquals(result.get().getMdn(),cycle3.getMdn());
    }

    @Test
    void testFindCyclesByUserIdAndMdnOrderByStartDateDesc_MultipleUsers() {
        String userId1 = "6671d6cdd518422008b3d9fb";
        String userId2 = "6671d6cdd518422008b3d9fc";
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fa", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId1);
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fb", "1234567890", System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, userId1);
        Cycle cycle3 = new Cycle("66711abbd518422008b3d9fc", "1234567890", System.currentTimeMillis() + 172800001,
                System.currentTimeMillis() + 259200000, userId1);
        Cycle cycle4 = new Cycle("6671d6f6d518422008b3d9fc", "1234567890", System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId2);
        Cycle cycle5 = new Cycle("66711abbd518422008b3d9fd", "1234567890", System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, userId2);
        Cycle cycle6 = new Cycle("66711abbd518422008b3d9fe", "1234567890", System.currentTimeMillis() + 172800001,
                System.currentTimeMillis() + 259200000, userId2);
        cycleRepository.save(cycle1);
        cycleRepository.save(cycle2);
        cycleRepository.save(cycle3);
        cycleRepository.save(cycle4);
        cycleRepository.save(cycle5);
        cycleRepository.save(cycle6);

        Optional<Cycle> result = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(userId2,
                "1234567890").stream().findFirst();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(), cycle6.getId());
        Assertions.assertEquals(result.get().getUserId(), cycle6.getUserId());
        Assertions.assertEquals(result.get().getMdn(), cycle6.getMdn());
    }

    @Test
    void testFindCyclesByUserIdAndMdnOrderByStartDateDesc_MultipleUsersAndMdns() {
        String userId1 = "6671d6cdd518422008b3d9fb";
        String userId2 = "6671d6cdd518422008b3d9fc";
        String mdn1 = "1234567890";
        String mdn2 = "1234567891";
        Cycle cycle1 = new Cycle("6671d6f6d518422008b3d9fa", mdn1, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId1);
        Cycle cycle2 = new Cycle("66711abbd518422008b3d9fb", mdn1, System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, userId1);
        Cycle cycle3 = new Cycle("66711abbd518422008b3d9fc", mdn1, System.currentTimeMillis() + 172800001,
                System.currentTimeMillis() + 259200000, userId1);
        Cycle cycle4 = new Cycle("6671d6f6d518422008b3d9fc", mdn2, System.currentTimeMillis() - 86400000,
                System.currentTimeMillis() + 86400000, userId2);
        Cycle cycle5 = new Cycle("66711abbd518422008b3d9fd", mdn2, System.currentTimeMillis() + 86400001,
                System.currentTimeMillis() + 172800000, userId2);
        Cycle cycle6 = new Cycle("66711abbd518422008b3d9fe", mdn2, System.currentTimeMillis() + 172800001,
                System.currentTimeMillis() + 259200000, userId2);
        cycleRepository.save(cycle1);
        cycleRepository.save(cycle2);
        cycleRepository.save(cycle3);
        cycleRepository.save(cycle4);
        cycleRepository.save(cycle5);
        cycleRepository.save(cycle6);

        List<Cycle> result = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(userId2, mdn2);

        Assertions.assertEquals(result.size(), 3);

        Assertions.assertEquals(result.get(0).getId(), cycle6.getId());
        Assertions.assertEquals(result.get(0).getUserId(), cycle6.getUserId());
        Assertions.assertEquals(result.get(0).getMdn(), cycle6.getMdn());
    }

}
