package com.usmobile.userManagement.integration;

import com.jayway.jsonpath.JsonPath;
import com.usmobile.userManagement.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserTest {

    private static final String USER_API = "/api/v1/user";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.11");

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_Success() throws Exception {
        mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@gmail.com",
                          "password": "youcantguessit"
                        }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"));

        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void createMultipleUsers_Success() throws Exception {
        mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@gmail.com",
                          "password": "youcantguessit"
                        }"""))
                .andExpect(status().isCreated());

        mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "Jane",
                          "lastName": "Doe",
                          "email": "jane.doe@gmail.com",
                          "password": "youmightguessit"
                        }"""))
                .andExpect(status().isCreated());

        Assertions.assertEquals(2, userRepository.count());
    }

    @Test
    void createUser_FirstNameMissingFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail.com",
                                  "password": "youcantguessit"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.firstName").value("firstName is required"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void createUser_LastNameMissingFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "email": "john.doe@gmail.com",
                                  "password": "youcantguessit"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.lastName").value("lastName is required"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void createUser_EmailMissingFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "password": "youcantguessit"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.email").value("email is required"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void createUser_PasswordMissingFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail.com"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.password").value("password is required"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void createUser_EmailInvalidFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail",
                                  "password": "youcantguessit"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.email").value("email is invalid"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void createUser_EmailAlreadyExistsFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail.com",
                                  "password": "youcantguessit"
                                }"""))
                .andExpect(status().isCreated());

        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail.com",
                                  "password": "youmightguessit"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("User with email john.doe@gmail.com already exists"));

        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void createUser_PasswordTooShortFailure() throws Exception {
        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john.doe@gmail.com",
                                  "password": "short"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.password").value("password must be at least 8 characters long"));
    }

    @Test
    void updateUser_Success() throws Exception {

        String response = mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@gmail.com",
                          "password": "youcantguessit"
                        }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        String savedUserId = JsonPath.read(response, "$.id");

        mockMvc.perform(put(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\": \"" + savedUserId + "\",\n" +
                        "  \"firstName\": \"Jane\",\n" +
                        "  \"lastName\": \"Porter\",\n" +
                        "  \"email\": \"jane.porter@gmail.com\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(savedUserId))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Porter"))
                .andExpect(jsonPath("$.email").value("jane.porter@gmail.com"));

        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void updateUser_UserNotFoundFailure() throws Exception {
        mockMvc.perform(put(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "6671d6cdd518422008b3d9fb",
                                  "firstName": "Jane",
                                  "lastName": "Porter",
                                  "email": "jane.porter@gmail.com"
                                }"""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("User with id 6671d6cdd518422008b3d9fb not found"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void updateUser_EmailAlreadyExistsFailure() throws Exception {

        // Create a user with email john.doe@gmail.com
        String response = mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@gmail.com",
                          "password": "youcantguessit"
                        }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        String savedUserId = JsonPath.read(response, "$.id");

        // Create a user with email jane.porter@gmail.com
        mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "Jane",
                          "lastName": "Porter",
                          "email": "jane.porter@gmail.com",
                          "password": "youmightguessit"
                        }"""))
                .andExpect(status().isCreated());

        // Update the user with email john.doe@gmail.com to have email jane.porter@gmail.com
        mockMvc.perform(put(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\": \"" + savedUserId + "\",\n" +
                        "  \"firstName\": \"Archimedes\",\n" +
                        "  \"lastName\": \"Porter\",\n" +
                        "  \"email\": \"jane.porter@gmail.com\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("User with email jane.porter@gmail.com already exists"));

        Assertions.assertEquals(2, userRepository.count());
    }

    @Test
    void updateUser_EmailInvalidFailure() throws Exception {
        mockMvc.perform(put(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "6671d6cdd518422008b3d9fb",
                                  "firstName": "Jane",
                                  "lastName": "Porter",
                                  "email": "jane.porter@gmail"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.email").value("email is invalid"));
    }

    @Test
    void updateUser_FirstNameMissingFailure() throws Exception {
        mockMvc.perform(put(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "6671d6cdd518422008b3d9fb",
                                  "lastName": "Porter",
                                  "email": "jane.porter@gmail.com"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.firstName").value("firstName is required"));
    }

    @Test
    void updateUser_LastNameMissingFailure() throws Exception {
        mockMvc.perform(put(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "6671d6cdd518422008b3d9fb",
                                  "firstName": "Jane",
                                  "email": "jane.porter@gmail.com"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.lastName").value("lastName is required"));
    }

    @Test
    void updateUser_EmailMissingFailure() throws Exception {
        mockMvc.perform(put(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "6671d6cdd518422008b3d9fb",
                                  "firstName": "Jane",
                                  "lastName": "Porter"
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.email").value("email is required"));
    }

    @Test
    void updateUser_FirstNameSuccess() throws Exception {
        String response = mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@gmail.com",
                          "password": "youcantguessit"
                        }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        String savedUserId = JsonPath.read(response, "$.id");

        mockMvc.perform(put(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\": \"" + savedUserId + "\",\n" +
                        "  \"firstName\": \"Jane\",\n" +
                        "  \"lastName\": \"Doe\",\n" +
                        "  \"email\": \"john.doe@gmail.com\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(savedUserId))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"));

        Assertions.assertEquals(1, userRepository.count());
    }



}
