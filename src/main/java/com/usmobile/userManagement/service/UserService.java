package com.usmobile.userManagement.service;

import com.usmobile.userManagement.entity.User;
import com.usmobile.userManagement.exception.UserAlreadyExistsException;
import com.usmobile.userManagement.exception.UserNotFoundException;
import com.usmobile.userManagement.model.CreateUserRequest;
import com.usmobile.userManagement.model.UpdateUserRequest;
import com.usmobile.userManagement.model.UserResponse;
import com.usmobile.userManagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest user) {
        if (userRepository.existsByEmail(user.email())) {
            logger.error("User with email already exists");
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.email()));
        }
        User savedUser = userRepository.save(new User(user.firstName(), user.lastName(), user.email(),
                passwordEncoder.encode(user.password())));
        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }

    public UserResponse updateUser(UpdateUserRequest user) {
        User existingUser = userRepository.findById(user.id())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", user.id())));
        if (!existingUser.getEmail().equals(user.email()) && userRepository.existsByEmail(user.email())) {
            logger.error("User with email already exists");
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.email()));
        }
        existingUser.setFirstName(user.firstName());
        existingUser.setLastName(user.lastName());
        existingUser.setEmail(user.email());
        User savedUser = userRepository.save(existingUser);
        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }

}
