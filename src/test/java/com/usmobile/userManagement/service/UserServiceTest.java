package com.usmobile.userManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.usmobile.userManagement.entity.User;
import com.usmobile.userManagement.exception.UserAlreadyExistsException;
import com.usmobile.userManagement.exception.UserNotFoundException;
import com.usmobile.userManagement.model.CreateUserRequest;
import com.usmobile.userManagement.model.UpdateUserRequest;
import com.usmobile.userManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private PasswordEncoder actualPasswordEncoder;

    @BeforeEach
    void setUp() {
        actualPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testCreateUserSuccessfully() {
        // Create a CreateUserRequest object with test data
        CreateUserRequest request = getDummyCreateUserRequest();

        // Set up mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("6671d6cdd518422008b3d9fb");
            return user;
        });
        when(passwordEncoder.encode("password123")).thenReturn(actualPasswordEncoder.encode("password123"));

        // ArgumentCaptor to capture the User object saved in the repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Perform the test action
        userService.createUser(request);

        // Verify that userRepository.save() was called and capture the passed user
        verify(userRepository).save(userCaptor.capture());

        // Retrieve the captured User object
        User savedUser = userCaptor.getValue();

        // Verify that the password encoder was used
        verify(passwordEncoder).encode("password123");

        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertTrue(actualPasswordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void testCreateUserUserAlreadyExistsException() {
        // Create a CreateUserRequest object with test data
        CreateUserRequest request = getDummyCreateUserRequest();

        // Set up mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Perform the test action
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request))
                .getMessage().equals(String.format("User with email %s already exists", request.email()));
    }

    @Test
    void testUpdateUserSuccessfully() {
        // Create a CreateUserRequest object with test data
        UpdateUserRequest request = getDummyUpdateUserRequest();

        // Set up mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User("6671d6cdd518422008b3d9fb", "John", "Doe",
                "john.doe@example.com", actualPasswordEncoder.encode("password123"))));

        // ArgumentCaptor to capture the User object saved in the repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Perform the test action
        userService.updateUser(request);

        // Verify that userRepository.save() was called and capture the passed user
        verify(userRepository).save(userCaptor.capture());

        // Retrieve the captured User object
        User savedUser = userCaptor.getValue();

        assertNotNull(savedUser);
        assertEquals("Jane", savedUser.getFirstName());
        assertEquals("Porter", savedUser.getLastName());
        assertEquals("jane.porter@example.com", savedUser.getEmail());
        assertTrue(actualPasswordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void testUpdateUserEmailAlreadyExistsException() {
        // Create a CreateUserRequest object with test data
        UpdateUserRequest request = getDummyUpdateUserRequest();

        // Set up mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User("6671d6cdd518422008b3d9fb", "John", "Doe",
                "john@example.com", actualPasswordEncoder.encode("password123"))));

        // Perform the test action
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(request))
                .getMessage().equals(String.format("User with email %s already exists", request.email()));
    }

    @Test
    void testUpdateUserUserNotFoundException() {
        // Create a CreateUserRequest object with test data
        UpdateUserRequest request = getDummyUpdateUserRequest();

        // Set up mocks
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // Perform the test action
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(request))
                .getMessage().equals(String.format("User with id %s not found", request.id()));
    }



    private CreateUserRequest getDummyCreateUserRequest() {
        return new CreateUserRequest("John", "Doe",
                "john.doe@example.com", "password123");
    }

    private UpdateUserRequest getDummyUpdateUserRequest() {
        return new UpdateUserRequest("6671d6cdd518422008b3d9fb", "Jane", "Porter",
                "jane.porter@example.com");
    }
}
