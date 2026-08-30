package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.ChangePasswordDto;
import com.sanjayrisbud.starzzboot.dtos.UserDto;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StarzzBootApplication.class)
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.security.password-reset-sentinel}")
    private String passwordResetSentinel;

    @Test
    @WithMockUser
    void getUserListReturns200AndList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(101));
    }

    @Test
    @WithMockUser
    void getUserGivenExistingIdReturns200AndUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("ebalducci29128"));
    }

    @Test
    @WithMockUser
    void getUserGivenNonExistentIdReturns404AndError() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerUserWithoutAuthReturns401() throws Exception {
        UserDto request = UserDto.builder()
                .username("testuser12345")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void registerUserWithoutAdminRoleReturns403() throws Exception {
        UserDto request = UserDto.builder()
                .username("testuser12345")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void registerUserGivenValidDataReturns201AndUser() throws Exception {
        String newUsername = "testuser12345";
        UserDto request = UserDto.builder()
                .username(newUsername)
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").value(newUsername));

        User u = userRepository.findByName(newUsername);
        assertTrue(passwordEncoder.matches(
                passwordResetSentinel, u.getPassword()));
    }

    @Test
    @Transactional
    @WithMockUser
    void updateUserGivenExistingIdReturns200AndUpdatedUser() throws Exception {
        UserDto request = UserDto.builder()
                .username("updateduser12345")
                .email("updated@email.com")
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("updateduser12345"));
    }

    @Test
    @Transactional
    void changePasswordGivenMatchingPasswordReturns204() throws Exception {
        String newUserPassword = "passwordsMatch";
        String updatedUserPassword = "passwordUpdated";
        User u = User.builder()
                .name("testuser12345").email("testuser12345@email.com")
                .password(passwordEncoder.encode(newUserPassword))
                .build();
        userRepository.save(u);

        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword(newUserPassword)
                .newPassword(updatedUserPassword)
                .build();

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertTrue(passwordEncoder.matches(updatedUserPassword, u.getPassword()));
    }

    @Test
    @Transactional
    void changePasswordGivenNonMatchingPasswordReturns400() throws Exception {
        String newUserPassword = "passwordsDontMatch";
        User u = User.builder()
                .name("testuser12345").email("testuser12345@email.com")
                .password(passwordEncoder.encode(newUserPassword))
                .build();
        userRepository.save(u);

        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("wrongPassword")
                .newPassword("newPassword")
                .build();

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertTrue(passwordEncoder.matches(newUserPassword, u.getPassword()));
    }

    @Test
    void changePasswordGivenNonExistentIdReturns404() throws Exception {
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        mockMvc.perform(patch("/users/9999/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}
