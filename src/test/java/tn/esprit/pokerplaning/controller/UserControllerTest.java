package tn.esprit.pokerplaning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import static org.junit.jupiter.api.Assertions.*;
import tn.esprit.pokerplaning.Entities.Role;
import tn.esprit.pokerplaning.Entities.User;
import tn.esprit.pokerplaning.Entities.Gender;
import tn.esprit.pokerplaning.UserMicroserviceApplication;
import tn.esprit.pokerplaning.Repositories.UserRepository;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = UserMicroserviceApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@EntityScan(basePackages = "tn.esprit.pokerplaning.Entities")
@TestPropertySource("classpath:application-test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.Developpeur);
        user.setGender(Gender.Male);
        savedUser = userRepository.save(user);
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/auth/ShowallUsers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/auth/GetUserById/" + savedUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updated = new User();
        updated.setEmail("updated@example.com");
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        updated.setPassword("newpass");
        updated.setSkillRate(5);
        updated.setRole(Role.ScrumMaster);
        updated.setGender(Gender.Female);

        MockMultipartFile userFile = new MockMultipartFile("file", "filename.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart("/api/auth/UpdateUser/" + savedUser.getUserId())
                        .file(userFile)
                        .param("email", updated.getEmail())
                        .param("firstName", updated.getFirstName())
                        .param("lastName", updated.getLastName())
                        .param("password", updated.getPassword())
                        .param("role", updated.getRole().name())
                        .param("gender", String.valueOf(updated.getGender()))
                        .param("skillRate", String.valueOf(updated.getSkillRate()))
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/auth/DeleteUser/" + savedUser.getUserId()))
                .andExpect(status().isOk());

        Optional<User> deleted = userRepository.findById(savedUser.getUserId());
        assertFalse(deleted.isPresent());
    }
}
