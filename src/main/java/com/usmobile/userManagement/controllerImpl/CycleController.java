package com.usmobile.userManagement.controllerImpl;

import com.usmobile.userManagement.controller.CycleControllerAPI;
import com.usmobile.userManagement.model.LineInfo;
import com.usmobile.userManagement.service.CycleService;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class CycleController implements CycleControllerAPI {

    CycleService cycleService;

    @Autowired
    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    public ResponseEntity<List<DailyUsageReport>> getCurrentCycleReport(String userId, String mdn) {
        List<DailyUsageReport> dailyUsageReports = cycleService.getDailyUsageReport(userId, mdn);
        return ResponseEntity.ok(dailyUsageReports);
    }

    public ResponseEntity<List<CycleInfo>> getCycleHistory(String userId, String mdn) {
        List<CycleInfo> cycleInfos = cycleService.getCycleHistory(userId, mdn);
        return ResponseEntity.ok(cycleInfos);
    }

}
