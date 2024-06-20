package com.usmobile.userManagement.controller;

import com.usmobile.userManagement.model.CycleInfo;
import com.usmobile.userManagement.model.DailyUsageReport;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Cycle Controller API
 * This interface defines the APIs for cycle controller
 */

@Validated
public interface CycleControllerAPI {

    /**
     * Get daily usage report
     * @param userId - subscriber user id
     * @param mdn - subscriber mdn
     * @return list of current cycle daily usage reports
     */
    @GetMapping(path = "/current-cycle-report", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current cycle report",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema =
                    @Schema(implementation = DailyUsageReport.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No Cycle is currently active", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<List<DailyUsageReport>> getCurrentCycleReport(@NotBlank @RequestParam String userId,
                                                                      @NotBlank @RequestParam String mdn);

    /**
     * Get cycle history
     * @param userId - subscriber user id
     * @param mdn - subscriber mdn
     * @return list of cycle history for the user id and mdn
     */
    @GetMapping(path = "/cycle-history", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cycle history",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema =
                    @Schema(implementation = CycleInfo.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Cycle history not found", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<List<CycleInfo>> getCycleHistory(@NotBlank @RequestParam String userId, @NotBlank @RequestParam String mdn);

}