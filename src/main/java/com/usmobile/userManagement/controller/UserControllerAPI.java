package com.usmobile.userManagement.controller;

import com.usmobile.userManagement.model.CreateUserRequest;
import com.usmobile.userManagement.model.UpdateUserRequest;
import com.usmobile.userManagement.model.UserResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
public interface UserControllerAPI {

    /**
     * Create user with given details
     * @param user - user details
     * @return saved user details
     */
    @PostMapping(path = "/user", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation =
                    UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest user);

    /**
     * Update user with given details
     * @param user - user details
     * @return updated user details
     */
    @PutMapping(path = "/user", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_PROBLEM_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user details",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation =
                    UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType =
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest user);

}
