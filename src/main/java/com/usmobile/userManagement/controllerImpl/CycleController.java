package com.usmobile.userManagement.controllerImpl;

import com.usmobile.userManagement.controller.CycleControllerAPI;
import com.usmobile.userManagement.model.LineInfo;
import com.usmobile.userManagement.service.CycleService;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CycleController implements CycleControllerAPI {

    CycleService cycleService;

    @Autowired
    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    public ResponseEntity<List<DailyUsageReport>> getDailyUsageReport(String userId, String mdn) {
        LineInfo effectiveLineInfo =  new LineInfo(userId, mdn);
        List<DailyUsageReport> dailyUsageReports = cycleService.getDailyUsageReport(effectiveLineInfo);
        return ResponseEntity.ok(dailyUsageReports);
    }

    public ResponseEntity<List<CycleInfo>> getCycleHistory(String userId, String mdn) {
        LineInfo effectiveLineInfo =  new LineInfo(userId, mdn);
        List<CycleInfo> cycleInfos = cycleService.getCycleHistory(effectiveLineInfo);
        return ResponseEntity.ok(cycleInfos);
    }

}
