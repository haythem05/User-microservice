package tn.esprit.pokerplaning.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pokerplaning.Entities.User;
import tn.esprit.pokerplaning.Repositories.UserRepository;
import tn.esprit.pokerplaning.Services.JwtService;
import tn.esprit.pokerplaning.Services.UserServices;
import tn.esprit.pokerplaning.Services.twilio.SmsService;
import tn.esprit.pokerplaning.Utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServicesTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JavaMailSender mailSender;
    @Mock private SmsService smsService;
    @Mock private JwtService jwtService;
    @Mock private Utils utils;

    @InjectMocks
    private UserServices userServices;

    @Test
    void testShowAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userServices.ShowAllUsers().size());
    }

    @Test
    void testSendEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        userServices.sendEmail(msg);
        verify(mailSender).send(msg);
    }

    @Test
    void testGetPasswordByEmail_found() {
        User u = new User();
        u.setPassword("pass");
        when(userRepository.findByEmail("x@y.com")).thenReturn(Optional.of(u));
        assertEquals("pass", userServices.getPasswordByEmail("x@y.com"));
    }

    @Test
    void testFindUserByEmail_found() {
        User user = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userServices.findUserByEmail("test@example.com");
        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindUserByEmail_notFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        User result = userServices.findUserByEmail("notfound@example.com");
        assertNull(result);
    }

    @Test
    void testDeleteUser_success() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userServices.DeleteUser(1L);

        verify(userRepository).delete(user);
    }
    @Test
    void testGetUserById_success() {
        User user = new User();
        user.setUserId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        var response = userServices.GetUserById(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5L, response.getBody().getUserId());
    }

    @Test
    void testResetBannedStatus_success() {
        User user = new User();
        user.setUserId(99L);
        user.setBanned(true);
        when(userRepository.findById(99L)).thenReturn(Optional.of(user));

        userServices.resetBannedStatus(99L);

        assertFalse(user.isBanned());
        verify(userRepository).save(user);
    }
    @Test
    void testUpdateUser() throws IOException {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);

        User updatedDetails = new User();
        updatedDetails.setEmail("email@example.com");
        updatedDetails.setPassword("pass");

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<User> response = userServices.UpdateUser(userId, updatedDetails, file);
        assertEquals("email@example.com", response.getBody().getEmail());
        assertEquals("encoded", response.getBody().getPassword());
    }


}
