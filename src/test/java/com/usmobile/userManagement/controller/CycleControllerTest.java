package com.usmobile.userManagement.controller;

import com.usmobile.userManagement.controllerImpl.CycleController;
import com.usmobile.userManagement.exception.NoCyclesFoundException;
import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import com.usmobile.userManagement.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CycleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CycleService cycleService;

    String currentCycleReportPath;

    String cycleHistoryPath;

    @BeforeEach
    public void setUp() {
        this.currentCycleReportPath = "/api/v1/current-cycle-report";
        this.cycleHistoryPath = "/api/v1/cycle-history";
    }

    @Test
    public void testGetDailyUsageReport_Success() throws Exception {

        Mockito.when(cycleService.getDailyUsageReport(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(getDummyDailyUsageReport());
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-06-19T03:35:10.950+00:00"))
                .andExpect(jsonPath("$[0].dailyUsage").value(256));
    }

    @Test
    public void testGetDailyUsageReport_WhenDailyUsageReportIsEmpty() throws Exception {
        Mockito.when(cycleService.getDailyUsageReport(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(List.of());
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testGetDailyUsageReport_WhenNoCycleFound() throws Exception {
        Mockito.when(cycleService.getDailyUsageReport(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new NoCyclesFoundException(String.format("No current cycle found for this user: %s and mdn: %s.",
                        "6671d6cdd518422008b3d9fb", "1234567890")));
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        String.format("No current cycle found for this user: %s and mdn: %s.", "6671d6cdd518422008b3d9fb",
                                "1234567890")));
    }

    @Test
    public void testGetDailyUsageReport_WhenMdnIsMissing() throws Exception {
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'mdn' for method parameter type String is not present"));
    }

    @Test
    public void testGetDailyUsageReport_WhenUserIdIsMissing() throws Exception {
        mockMvc.perform(get(currentCycleReportPath)
                .param("mdn", "1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    public void testGetDailyUsageReport_WhenUserIdAndMdnAreMissing() throws Exception {
        mockMvc.perform(get(currentCycleReportPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    public void testGetDailyUsageReport_WhenUserIdIsEmpty() throws Exception {
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "")
                .param("mdn", "1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "getCurrentCycleReport.userId: must not be blank"));
    }

    @Test
    public void testGetDailyUsageReport_WhenMdnIsEmpty() throws Exception {
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "getCurrentCycleReport.mdn: must not be blank"));
    }

    @Test
    public void testGetDailyUsageReport_WhenExceptionIsThrown() throws Exception {
        Mockito.when(cycleService.getDailyUsageReport(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Internal Server Error"));
        mockMvc.perform(get(currentCycleReportPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Internal Server Error, please try again later"));
    }

    @Test
    public void testGetCycleHistory_NotFound() throws Exception {
        Mockito.when(cycleService.getCycleHistory(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new NoCyclesFoundException(String.format("No cycles found for this user: %s and mdn: %s.",
                        "6671d6cdd518422008b3d9fb", "1234567890")));
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        String.format("No cycles found for this user: %s and mdn: %s.", "6671d6cdd518422008b3d9fb",
                                "1234567890")));
    }

    @Test
    public void testGetCycleHistory_Success() throws Exception {
        Mockito.when(cycleService.getCycleHistory(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(getDummyCycleHistory());
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cycleId").value("6671d6cde518422008b3d9a7"))
                .andExpect(jsonPath("$[0].startDate").value("2024-06-19T03:35:10.950+00:00"))
                .andExpect(jsonPath("$[0].endDate").value("2024-07-19T03:35:10.950+00:00"));
    }

    @Test
    public void testGetCycleHistory_WhenMdnIsMissing() throws Exception {
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "6671d6cdd518422008b3d9fb"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'mdn' for method parameter type String is not present"));
    }

    @Test
    public void testGetCycleHistory_WhenUserIdIsMissing() throws Exception {
        mockMvc.perform(get(cycleHistoryPath)
                .param("mdn", "1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    public void testGetCycleHistory_WhenUserIdAndMdnAreMissing() throws Exception {
        mockMvc.perform(get(cycleHistoryPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Required request parameter 'userId' for method parameter type String is not present"));
    }

    @Test
    public void testGetCycleHistory_WhenUserIdIsEmpty() throws Exception {
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "")
                .param("mdn", "1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "getCycleHistory.userId: must not be blank"));
    }

    @Test
    public void testGetCycleHistory_WhenMdnIsEmpty() throws Exception {
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "getCycleHistory.mdn: must not be blank"));
    }

    @Test
    public void testGetCycleHistory_WhenExceptionIsThrown() throws Exception {
        Mockito.when(cycleService.getCycleHistory(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Internal Server Error"));
        mockMvc.perform(get(cycleHistoryPath)
                .param("userId", "6671d6cdd518422008b3d9fb")
                .param("mdn", "1234567890"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Internal Server Error, please try again later"));
    }


    private List<DailyUsageReport> getDummyDailyUsageReport() throws ParseException {
        String dateString = "2024-06-19T03:35:10.950";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return List.of(new DailyUsageReport(sdf.parse(dateString),256));
    }

    private List<CycleInfo> getDummyCycleHistory() throws ParseException {
        String startDate = "2024-06-19T03:35:10.950";
        String endDate = "2024-07-19T03:35:10.950";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return List.of(new CycleInfo("6671d6cde518422008b3d9a7",sdf.parse(startDate),sdf.parse(endDate)));
    }
}
