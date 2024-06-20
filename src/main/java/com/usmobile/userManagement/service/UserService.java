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

/**
 * Service class for user management
 */
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

    /**
     * Create a new user
     *
     * @param user user details
     * @return saved user details including generated id and encoded password
     */
    public UserResponse createUser(CreateUserRequest user) {
        // If user with email already exists, throw UserAlreadyExistsException, and return 400 Bad Request
        if (userRepository.existsByEmail(user.email())) {
            logger.error("User with email already exists");
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.email()));
        }
        User savedUser = userRepository.save(new User(user.firstName(), user.lastName(), user.email(),
                passwordEncoder.encode(user.password())));
        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }

    /**
     * Update an existing user
     *
     * @param user user details
     * @return updated user details
     */
    public UserResponse updateUser(UpdateUserRequest user) {
        // If user with id not found, throw UserNotFoundException, and return 404 Not Found
        User existingUser = userRepository.findById(user.id())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", user.id())));

        // If user tries to update the email which another user already has, throw UserAlreadyExistsException, and return 400 Bad Request
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
