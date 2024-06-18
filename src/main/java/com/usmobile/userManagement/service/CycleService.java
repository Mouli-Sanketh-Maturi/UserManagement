package com.usmobile.userManagement.service;

import com.usmobile.userManagement.entity.Cycle;
import com.usmobile.userManagement.entity.DailyUsage;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import com.usmobile.userManagement.model.LineInfo;
import com.usmobile.userManagement.repository.CycleRepository;
import com.usmobile.userManagement.repository.DailyUsageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CycleService {

    CycleRepository cycleRepository;

    DailyUsageRepository dailyUsageRepository;

    @Autowired
    public CycleService(CycleRepository cycleRepository, DailyUsageRepository dailyUsageRepository) {
        this.cycleRepository = cycleRepository;
        this.dailyUsageRepository = dailyUsageRepository;
    }

    public List<DailyUsageReport> getDailyUsageReport(@Valid LineInfo lineInfo) {

        Cycle cycle = cycleRepository.findCurrentCycleByUserIdAndMdn(lineInfo.userId(), lineInfo.mdn());

        List<DailyUsage> dailyUsages = dailyUsageRepository
                .findByMdnAndUserIdAndUsageDateBetweenOrderByUsageDateAsc(lineInfo.mdn(), lineInfo.userId(),
                        cycle.getStartDate(), cycle.getEndDate());

        return dailyUsages.stream().map(du -> new DailyUsageReport(du.getUsageDate(), du.getUsedInMb())).toList();

    }

    public List<CycleInfo> getCycleHistory(@Valid LineInfo lineInfo) {

        List<Cycle> cycles = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(lineInfo.userId(), lineInfo.mdn());

        return cycles.stream().map(c -> new CycleInfo(c.getId(), c.getStartDate(), c.getEndDate())).toList();

    }

}
