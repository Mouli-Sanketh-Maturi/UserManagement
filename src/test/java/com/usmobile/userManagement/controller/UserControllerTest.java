package com.usmobile.userManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usmobile.userManagement.controllerImpl.UserController;
import com.usmobile.userManagement.exception.UserAlreadyExistsException;
import com.usmobile.userManagement.exception.UserNotFoundException;
import com.usmobile.userManagement.model.CreateUserRequest;
import com.usmobile.userManagement.model.UpdateUserRequest;
import com.usmobile.userManagement.model.UserResponse;
import com.usmobile.userManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private static final String USER_ID = "6671d6cdd518422008b3d9fb";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    String userPath;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.userPath = "/api/v1/user";
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void createUser_Success() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", "john.doe@gmail.com"));
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getCreateUserRequest()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"));
    }

    @Test
    void createUser_MissingFirstName() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        null, "Doe", "john.doe@gmail.com"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                null, "Doe", "john.doe@gmail.com", "password");
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.firstName").value("firstName is required"));
    }

    @Test
    void createUser_MissingLastName() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", null, "john.doe@gmail.com"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", null, "john.doe@gmail.com", "password");
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.lastName").value("lastName is required"));
    }

    @Test
    void createUser_MissingEmail() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", null));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", "Doe", null, "password");
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.email").value("email is required"));
    }

    @Test
    void createUser_InvalidEmail() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", "john.doe"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe", "password");
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.email").value("email is invalid"));
    }

    @Test
    void createUser_MissingPassword() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", "john.doe@gmail.com"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe@gmail.com", null);
        mockMvc.perform(post(userPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.password").value("password is required"));
    }

    @Test
    void createUser_PasswordLengthLessThan8() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", "john.doe@gmail.com"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe@gmail.com", "pass");
        mockMvc.perform(post(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.password").value("password must be at least 8 characters long"));
    }

    @Test
    void createUser_EmailAlreadyExists() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).
                thenReturn(new UserResponse(USER_ID,
                        "John", "Doe", "john.doe@gmail.com"));
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe@gmail.com", "password");
        Mockito.when(userService.createUser(createUserRequest)).thenThrow(
                new UserAlreadyExistsException("User with email john.doe@gmail.com already exists"));
        mockMvc.perform(post(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("User with email john.doe@gmail.com already exists"));
    }

    @Test
    void createUser_InvalidRequest() throws Exception {
        mockMvc.perform(post(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void createUser_InvalidMediaType() throws Exception {
        mockMvc.perform(post(userPath)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(objectMapper.writeValueAsString(getCreateUserRequest()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void updateUser_Success() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", "Porter", "jane.porter@gmail.com"));
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUpdateUserRequest()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Porter"))
                .andExpect(jsonPath("$.email").value("jane.porter@gmail.com"));
    }

    @Test
    void updateUser_MissingUserId() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", "Porter", "jane.porter@gmail.com"));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                null, "Jane", "Porter", "jane.porter@gmail.com");
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.id").value("user id is required"));
    }

    @Test
    void updateUser_MissingFirstName() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                null, "Porter", "jane.porter@gmail.com"));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                USER_ID, null, "Porter", "jane.porter@gmail.com");
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.firstName").value("firstName is required"));
    }

    @Test
    void updateUser_MissingLastName() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", null, "jane.porter@gmail.com"));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                USER_ID, "Jane", null, "jane.porter@gmail.com");
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.lastName").value("lastName is required"));
    }

    @Test
    void updateUser_MissingEmail() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", "Porter", null));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                USER_ID, "Jane", "Porter", null);
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.email").value("email is required"));
    }

    @Test
    void updateUser_InvalidEmail() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", "Porter", "jane.porter"));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                USER_ID, "Jane", "Porter", "jane.porter");
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.email").value("email is invalid"));
    }

    @Test
    void updateUser_InvalidRequest() throws Exception {
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateUser_InvalidMediaType() throws Exception {
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(objectMapper.writeValueAsString(getUpdateUserRequest()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void updateUser_UserNotFound() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenThrow(
                new UserNotFoundException("User with id 6671d6cdd518422008b3d9fb not found"));
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUpdateUserRequest()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("User with id 6671d6cdd518422008b3d9fb not found"));
    }

    @Test
    void updateUser_EmailAlreadyExists() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new UserResponse(USER_ID,
                "Jane", "Porter", "jane.porter@gmail.com"));
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                USER_ID, "Jane", "Porter", "jane.porter@gmail.com");
        Mockito.when(userService.updateUser(updateUserRequest)).thenThrow(
                new UserAlreadyExistsException("User with email jane.porter@gmail.com already exists"));
        mockMvc.perform(put(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("User with email jane.porter@gmail.com already exists"));
    }

    private CreateUserRequest getCreateUserRequest() {
        return new CreateUserRequest("John", "Doe", "john.doe@gmail.com", "password");
    }

    private UpdateUserRequest getUpdateUserRequest() {
            return new UpdateUserRequest(USER_ID, "Jane", "Porter",
                    "jane.porter@gmail.com");
    }

}
