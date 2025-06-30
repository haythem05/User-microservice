package tn.esprit.pokerplaning.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.pokerplaning.Entities.Role;
import tn.esprit.pokerplaning.Entities.User;
import tn.esprit.pokerplaning.Repositories.UserRepository;
import tn.esprit.pokerplaning.Services.JwtService;
import tn.esprit.pokerplaning.Services.UserServices;
import tn.esprit.pokerplaning.Services.twilio.SmsService;
import tn.esprit.pokerplaning.Utils.Utils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServicesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SmsService smsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private Utils utils;

    @InjectMocks
    private UserServices userServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowAllUsers() {
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userServices.ShowAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userServices.DeleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> result = userServices.GetUserById(1L);

        assertEquals(user, result.getBody());
        verify(userRepository).findById(1L);
    }

    @Test
    public void testFindUserByEmail_found() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userServices.findUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void testFindUserByEmail_notFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        User result = userServices.findUserByEmail("missing@example.com");

        assertNull(result);
    }

    @Test
    public void testSendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@example.com");
        message.setText("Hello");

        userServices.sendEmail(message);

        verify(mailSender).send(message);
    }

    @Test
    public void testGetPasswordByEmail_found() {
        User user = new User();
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(user));

        String result = userServices.getPasswordByEmail("email@test.com");

        assertEquals("encodedPassword", result);
    }

    @Test
    public void testGetPasswordByEmail_notFound() {
        when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.empty());

        String result = userServices.getPasswordByEmail("email@test.com");

        assertNull(result);
    }

    @Test
    public void testHandleFailedLogin_withThreeAttempts() {
        User user = new User();
        user.setLoginAttempts(2);

        userServices.handleFailedLogin(user);

        assertTrue(user.isBanned());
        assertEquals(0, user.getLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    public void testHandleSuccessfulLogin() {
        User user = new User();
        user.setLoginAttempts(2);

        userServices.handleSuccessfulLogin(user);

        assertEquals(0, user.getLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    public void testResetBannedStatus() {
        User user = new User();
        user.setBanned(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userServices.resetBannedStatus(1L);

        assertFalse(user.isBanned());
        verify(userRepository).save(user);
    }
}
