package com.usmobile.userManagement.service;

import com.usmobile.userManagement.entity.Cycle;
import com.usmobile.userManagement.entity.DailyUsage;
import com.usmobile.userManagement.exception.NoCyclesFoundException;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import com.usmobile.userManagement.repository.CycleRepository;
import com.usmobile.userManagement.repository.DailyUsageRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

@Service
@Validated
public class CycleService {

    CycleRepository cycleRepository;

    DailyUsageRepository dailyUsageRepository;

    @Autowired
    public CycleService(CycleRepository cycleRepository, DailyUsageRepository dailyUsageRepository) {
        this.cycleRepository = cycleRepository;
        this.dailyUsageRepository = dailyUsageRepository;
    }

    /**
     * Get daily usage report
     * @param userId userId of the subscriber
     * @param mdn mdn of the subscriber
     * @return List of daily usage report
     */
    @Validated
    public List<DailyUsageReport> getDailyUsageReport(@NotBlank String userId, @NotBlank String mdn) {

        Cycle cycle = cycleRepository.findCurrentCycleByUserIdAndMdn(userId, mdn, new Date().getTime())
                .orElseThrow(() -> new NoCyclesFoundException(
                        String.format("No current cycle found for this user: %s and mdn: %s.", userId, mdn)));

        Sort sort = Sort.by(Sort.Direction.DESC, "usageDate");
        List<DailyUsage> dailyUsages = dailyUsageRepository
                .findByUserIdAndMdnAndUsageDateBetweenOrderByUsageDateDesc(userId, mdn, cycle.getStartDate(),
                        cycle.getEndDate(), sort);

        return dailyUsages.stream().map(du -> new DailyUsageReport(new Date(du.getUsageDate()), du.getUsedInMb())).toList();

    }

    /**
     * Get cycle history
     * @param userId userId of the subscriber
     * @param mdn mdn of the subscriber
     * @return List of cycle history
     */

    @Validated
    public List<CycleInfo> getCycleHistory(@NotBlank String userId, @NotBlank String mdn) {

        List<Cycle> cycles = cycleRepository.findByUserIdAndMdnOrderByStartDateDesc(userId, mdn);

        if (cycles.isEmpty()) {
            throw new NoCyclesFoundException(String.format("No cycles found for this user: %s and mdn: %s.", userId, mdn));
        }

        return cycles.stream().map(c -> new CycleInfo(c.getId(), new Date(c.getStartDate()), new Date(c.getEndDate()))).toList();

    }

}
