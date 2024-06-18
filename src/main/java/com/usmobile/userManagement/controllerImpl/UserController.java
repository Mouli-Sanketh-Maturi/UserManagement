package com.usmobile.userManagement.controllerImpl;

import com.usmobile.userManagement.model.CreateUserRequest;
import com.usmobile.userManagement.model.UpdateUserRequest;
import com.usmobile.userManagement.model.UserResponse;
import com.usmobile.userManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user", produces = "application/json")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest user) {
        UserResponse savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UpdateUserRequest user) {
        UserResponse savedUser = userService.updateUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

}
