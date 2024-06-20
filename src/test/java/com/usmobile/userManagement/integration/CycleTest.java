package com.usmobile.userManagement.integration;

import com.usmobile.userManagement.entity.Cycle;
import com.usmobile.userManagement.entity.DailyUsage;
import com.usmobile.userManagement.repository.CycleRepository;
import com.usmobile.userManagement.repository.DailyUsageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CycleTest {

    private static final String CYCLE_HISTORY_API = "/api/v1/cycle-history";
    private static final String CURRENT_CYCLE_REPORT_API = "/api/v1/current-cycle-report";
    private static final String USER_ID = "6671d6cdd518422008b3d9fb";
    private static final String MDN = "1234567890";
    private static final String USER_ID_KEY = "userId";
    private static final String MDN_KEY = "mdn";

    @Autowired
    private CycleRepository cycleRepository;

    @Autowired
    private DailyUsageRepository dailyUsageRepository;

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.11");

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        cycleRepository.deleteAll();
        dailyUsageRepository.deleteAll();
    }

    @Test
    void testGetCycleHistory_Success() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle1 = new Cycle();
        cycle1.setUserId(USER_ID);
        cycle1.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle1.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle1.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle1);

        Cycle cycle2 = new Cycle();
        cycle2.setUserId(USER_ID);
        cycle2.setMdn(MDN);
        // Set cycle start date 40 days before current date, and end date 30 days before current date
        cycle2.setStartDate(currentInstant.minus(40, ChronoUnit.DAYS).toEpochMilli());
        cycle2.setEndDate(currentInstant.minus(30, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle2);

        // Test get cycle history
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].startDate").value(formatEpochToUTC(cycle1.getStartDate())))
                .andExpect(jsonPath("$[0].endDate").value(formatEpochToUTC(cycle1.getEndDate())))
                .andExpect(jsonPath("$[1].startDate").value(formatEpochToUTC(cycle2.getStartDate())))
                .andExpect(jsonPath("$[1].endDate").value(formatEpochToUTC(cycle2.getEndDate())));
    }

    @Test
    void testGetCycleHistory_MultipleMdnsSuccess() throws Exception {

            Instant currentInstant = Instant.now();

            Cycle cycle1 = new Cycle();
            cycle1.setUserId(USER_ID);
            cycle1.setMdn(MDN);
            // Set cycle start date 10 days before current date, and end date 20 days after current date
            cycle1.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
            cycle1.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
            cycleRepository.save(cycle1);

            Cycle cycle2 = new Cycle();
            cycle2.setUserId(USER_ID);
            cycle2.setMdn("0987654321");
            // Set cycle start date 40 days before current date, and end date 30 days before current date
            cycle2.setStartDate(currentInstant.minus(40, ChronoUnit.DAYS).toEpochMilli());
            cycle2.setEndDate(currentInstant.minus(30, ChronoUnit.DAYS).toEpochMilli());
            cycleRepository.save(cycle2);

            // Test get cycle history
            mockMvc.perform(get(CYCLE_HISTORY_API)
                    .param(USER_ID_KEY, USER_ID)
                    .param(MDN_KEY, MDN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].startDate").value(formatEpochToUTC(cycle1.getStartDate())))
                    .andExpect(jsonPath("$[0].endDate").value(formatEpochToUTC(cycle1.getEndDate())));
    }

    @Test
    void testGetCycleHistory_MultipleUsersSuccess() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle1 = new Cycle();
        cycle1.setUserId(USER_ID);
        cycle1.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle1.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle1.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle1);

        Cycle cycle2 = new Cycle();
        cycle2.setUserId("6671d6cdd518422008b3d9fc");
        cycle2.setMdn(MDN);
        // Set cycle start date 40 days before current date, and end date 30 days before current date
        cycle2.setStartDate(currentInstant.minus(40, ChronoUnit.DAYS).toEpochMilli());
        cycle2.setEndDate(currentInstant.minus(30, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle2);

        // Test get cycle history
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].startDate").value(formatEpochToUTC(cycle1.getStartDate())))
                .andExpect(jsonPath("$[0].endDate").value(formatEpochToUTC(cycle1.getEndDate())));
    }

    @Test
    void testGetCycleHistory_MultipleUsersAndMdnsSuccess() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle1 = new Cycle();
        cycle1.setUserId(USER_ID);
        cycle1.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle1.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle1.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle1);

        Cycle cycle2 = new Cycle();
        cycle2.setUserId("6671d6cdd518422008b3d9fc");
        cycle2.setMdn("0987654321");
        // Set cycle start date 40 days before current date, and end date 30 days before current date
        cycle2.setStartDate(currentInstant.minus(40, ChronoUnit.DAYS).toEpochMilli());
        cycle2.setEndDate(currentInstant.minus(30, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle2);

        // Test get cycle history
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].startDate").value(formatEpochToUTC(cycle1.getStartDate())))
                .andExpect(jsonPath("$[0].endDate").value(formatEpochToUTC(cycle1.getEndDate())));
    }

    @Test
    void testGetCycleHistory_NoCycleFound() throws Exception {
        // Test get cycle history when no cycle found
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(
                        String.format("No cycles found for this user: %s and mdn: %s.", USER_ID, MDN)));
    }

    @Test
    void testGetCycleHistory_UserIdMissing() throws Exception {
        // Test get cycle history with missing user id
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(MDN_KEY, MDN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    void testGetCycleHistory_MdnMissing() throws Exception {
        // Test get cycle history with missing mdn
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required request parameter 'mdn' for method parameter type String is not present"));
    }

    @Test
    void testGetCycleHistory_InvalidUserIdAndMdn() throws Exception {
        // Test get cycle history with invalid input
        mockMvc.perform(get(CYCLE_HISTORY_API)
                .param(USER_ID_KEY, "")
                .param(MDN_KEY, ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void testGetCurrentCycleReport_Success() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle = new Cycle();
        cycle.setUserId(USER_ID);
        cycle.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle);

        // Add daily usage for current date
        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setMdn(MDN);
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setUsedInMb(256);
        dailyUsage1.setUsageDate(currentInstant.toEpochMilli());

        dailyUsageRepository.save(dailyUsage1);

        // Add daily usage for previous date
        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId(USER_ID);
        dailyUsage2.setMdn(MDN);
        dailyUsage2.setUserId(USER_ID);
        dailyUsage2.setUsedInMb(512);
        dailyUsage2.setUsageDate(currentInstant.minus(1, ChronoUnit.DAYS).toEpochMilli());

        dailyUsageRepository.save(dailyUsage2);

        // Test get current cycle report
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date").value(formatEpochToUTC(dailyUsage1.getUsageDate())))
                .andExpect(jsonPath("$[0].dailyUsage").value(dailyUsage1.getUsedInMb()))
                .andExpect(jsonPath("$[1].date").value(formatEpochToUTC(dailyUsage2.getUsageDate())))
                .andExpect(jsonPath("$[1].dailyUsage").value(dailyUsage2.getUsedInMb()));
    }

    @Test
    void testGetCurrentCycleReport_NoDailyUsageFound() throws Exception {
        // Test get current cycle report when no daily usage found
        Instant currentInstant = Instant.now();

        Cycle cycle = new Cycle();
        cycle.setUserId(USER_ID);
        cycle.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle);

        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetCurrentCycleReport_NoCycleFound() throws Exception {
        // Test get current cycle report when no cycle found
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(
                        String.format("No current cycle found for this user: %s and mdn: %s.", USER_ID, MDN)));
    }

    @Test
    void testGetCurrentCycleReport_UserIdMissing() throws Exception {
        // Test get current cycle report with missing user id
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(MDN_KEY, MDN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    void testGetCurrentCycleReport_MdnMissing() throws Exception {
        // Test get current cycle report with missing mdn
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required request parameter 'mdn' for method parameter type String is not present"));
    }

    @Test
    void testGetCurrentCycleReport_InvalidUserIdAndMdn() throws Exception {
        // Test get current cycle report with invalid input
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, "")
                .param(MDN_KEY, ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void testGetCurrentCycleReport_MultipleMdnsSuccess() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle = new Cycle();
        cycle.setUserId(USER_ID);
        cycle.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle);

        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setMdn(MDN);
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setUsedInMb(256);
        dailyUsage1.setUsageDate(currentInstant.toEpochMilli());

        dailyUsageRepository.save(dailyUsage1);

        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId(USER_ID);
        dailyUsage2.setMdn("0987654321");
        dailyUsage2.setUserId(USER_ID);
        dailyUsage2.setUsedInMb(512);
        dailyUsage2.setUsageDate(currentInstant.minus(1, ChronoUnit.DAYS).toEpochMilli());

        dailyUsageRepository.save(dailyUsage2);

        // Test get current cycle report
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dailyUsage").value(dailyUsage1.getUsedInMb()))
                .andExpect(jsonPath("$[0].date").value(formatEpochToUTC(dailyUsage1.getUsageDate())));
    }

    @Test
    void testGetCurrentCycleReport_MultipleUsersSuccess() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle = new Cycle();
        cycle.setUserId(USER_ID);
        cycle.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle);

        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setMdn(MDN);
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setUsedInMb(256);
        dailyUsage1.setUsageDate(currentInstant.toEpochMilli());

        dailyUsageRepository.save(dailyUsage1);

        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fc");
        dailyUsage2.setMdn(MDN);
        dailyUsage2.setUsedInMb(512);
        dailyUsage2.setUsageDate(currentInstant.minus(1, ChronoUnit.DAYS).toEpochMilli());

        dailyUsageRepository.save(dailyUsage2);

        // Test get current cycle report
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dailyUsage").value(dailyUsage1.getUsedInMb()))
                .andExpect(jsonPath("$[0].date").value(formatEpochToUTC(dailyUsage1.getUsageDate())));
    }

    @Test
    void testGetCurrentCycleReport_MultipleUsersAndMdnsSuccess() throws Exception {

        Instant currentInstant = Instant.now();

        Cycle cycle = new Cycle();
        cycle.setUserId(USER_ID);
        cycle.setMdn(MDN);
        // Set cycle start date 10 days before current date, and end date 20 days after current date
        cycle.setStartDate(currentInstant.minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(currentInstant.plus(20, ChronoUnit.DAYS).toEpochMilli());
        cycleRepository.save(cycle);

        DailyUsage dailyUsage1 = new DailyUsage();
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setMdn(MDN);
        dailyUsage1.setUserId(USER_ID);
        dailyUsage1.setUsedInMb(256);
        dailyUsage1.setUsageDate(currentInstant.toEpochMilli());

        dailyUsageRepository.save(dailyUsage1);

        DailyUsage dailyUsage2 = new DailyUsage();
        dailyUsage2.setUserId("6671d6cdd518422008b3d9fc");
        dailyUsage2.setMdn("0987654321");
        dailyUsage2.setUsedInMb(512);
        dailyUsage2.setUsageDate(currentInstant.minus(1, ChronoUnit.DAYS).toEpochMilli());

        dailyUsageRepository.save(dailyUsage2);

        // Test get current cycle report
        mockMvc.perform(get(CURRENT_CYCLE_REPORT_API)
                .param(USER_ID_KEY, USER_ID)
                .param(MDN_KEY, MDN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dailyUsage").value(dailyUsage1.getUsedInMb()))
                .andExpect(jsonPath("$[0].date").value(formatEpochToUTC(dailyUsage1.getUsageDate())));
    }

    private static String formatEpochToUTC(Long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .appendOffset("+HH:MM", "+00:00")
                .toFormatter();
        return formatter.format(zonedDateTime);
    }

}
