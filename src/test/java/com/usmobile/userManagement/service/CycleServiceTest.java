package com.usmobile.userManagement.service;

import com.usmobile.userManagement.entity.Cycle;
import com.usmobile.userManagement.entity.DailyUsage;
import com.usmobile.userManagement.exception.NoCyclesFoundException;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import com.usmobile.userManagement.repository.CycleRepository;
import com.usmobile.userManagement.repository.DailyUsageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CycleServiceTest {
    
    private static final String USER_ID = "6671d6cdd518422008b3d9fb";
    private static final String MDN = "1234567890";

    @Mock
    private CycleRepository cycleRepository;

    @Mock
    private DailyUsageRepository dailyUsageRepository;

    @InjectMocks
    private CycleService cycleService;

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public MethodValidationPostProcessor bean() {
            return new MethodValidationPostProcessor();
        }
    }

    @Test
    void getDailyUsageReport_WhenNoCyclesFound() {
        Mockito.when(cycleRepository.findCurrentCycleByUserIdAndMdn(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> cycleService.getDailyUsageReport(USER_ID, MDN))
                .isInstanceOf(NoCyclesFoundException.class)
                .hasMessage("No current cycle found for this user: " + USER_ID + " and mdn: "+ MDN + ".");
    }

    @Test
    void getDailyUsageReport_WhenCyclesFound() {
        Cycle cycle = new Cycle();
        cycle.setStartDate(Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli());
        Mockito.when(cycleRepository.findCurrentCycleByUserIdAndMdn(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(cycle));
        List<DailyUsage> dailyUsages = List.of(new DailyUsage(USER_ID,MDN,USER_ID,Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli(), 100),
                new DailyUsage(USER_ID,MDN,USER_ID,Instant.now().toEpochMilli(), 128));
        Mockito.when(dailyUsageRepository
                        .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(dailyUsages);
        List<DailyUsageReport> dailyUsageReports = cycleService.getDailyUsageReport(USER_ID, MDN);
        Assertions.assertThat(dailyUsageReports).isNotEmpty();
        Assertions.assertThat(dailyUsageReports).hasSize(2);
        Assertions.assertThat(dailyUsageReports.get(0).dailyUsage()).isEqualTo(100);
        Assertions.assertThat(dailyUsageReports.get(1).dailyUsage()).isEqualTo(128);
    }

    @Test
    void getDailyUsageReport_WhenCyclesFoundAndNoDailyUsages() {
        Cycle cycle = new Cycle();
        cycle.setStartDate(Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli());
        cycle.setEndDate(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli());
        Mockito.when(cycleRepository.findCurrentCycleByUserIdAndMdn(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(cycle));
        Mockito.when(dailyUsageRepository
                        .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any() ))
                .thenReturn(List.of());
        List<DailyUsageReport> dailyUsageReports = cycleService.getDailyUsageReport(USER_ID, MDN);
        Assertions.assertThat(dailyUsageReports).isEmpty();
    }

    @Test
    void getCycleHistory_WhenNoCyclesFound() {
        Mockito.when(cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());
        Assertions.assertThatThrownBy(() -> cycleService.getCycleHistory(USER_ID, MDN))
                .isInstanceOf(NoCyclesFoundException.class)
                .hasMessage(String.format("No cycles found for this user: %s and mdn: %s.", USER_ID, MDN));
    }

    @Test
    void getCycleHistory_WhenCyclesFound() {
        Cycle cycle1 = new Cycle();
        Long cycle1StartDate = Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli();
        Long cycle1EndDate = Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli();
        cycle1.setStartDate(cycle1StartDate);
        cycle1.setEndDate(cycle1EndDate);
        cycle1.setId("1");
        cycle1.setUserId(USER_ID);
        cycle1.setMdn(MDN);
        Cycle cycle2 = new Cycle();
        Long cycle2StartDate = Instant.now().minus(40, ChronoUnit.DAYS).toEpochMilli();
        Long cycle2EndDate = Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli();
        cycle2.setStartDate(cycle2StartDate);
        cycle2.setEndDate(cycle2EndDate);
        cycle2.setId("2");
        cycle2.setUserId(USER_ID);
        cycle2.setMdn(MDN);
        Mockito.when(cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(cycle1, cycle2));
        List<CycleInfo> cycleInfos = cycleService.getCycleHistory(USER_ID, MDN);
        Assertions.assertThat(cycleInfos).isNotEmpty();
        Assertions.assertThat(cycleInfos).hasSize(2);
        Assertions.assertThat(cycleInfos.get(0).cycleId()).isEqualTo("1");
        Assertions.assertThat(cycleInfos.get(0).startDate()).isEqualTo(new Date(cycle1StartDate));
        Assertions.assertThat(cycleInfos.get(0).endDate()).isEqualTo(new Date(cycle1EndDate));
        Assertions.assertThat(cycleInfos.get(1).cycleId()).isEqualTo("2");
        Assertions.assertThat(cycleInfos.get(1).startDate()).isEqualTo(new Date(cycle2StartDate));
        Assertions.assertThat(cycleInfos.get(1).endDate()).isEqualTo(new Date(cycle2EndDate));
    }

}
