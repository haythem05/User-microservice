package tn.esprit.pokerplaning.Services.User;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pokerplaning.Entities.User.User;
import tn.esprit.pokerplaning.Repositories.User.UserRepository;
import tn.esprit.pokerplaning.Services.User.twilio.SmsService;
import tn.esprit.pokerplaning.Utils.Utils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor



public class UserServices {

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 60;

    @Autowired
    private UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;

 
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SmsService smsService;
    @Autowired
    Utils utils;
    @Autowired
    private JwtService jwtService;

    public List<User> ShowAllUsers()
    {
        List<User> users = userRepository.findAll();
        return users;
    }

    public void DeleteUser(Long id)
    {

        User user = userRepository.findById(id).get();
        this.userRepository.delete(user);
    }
    public ResponseEntity<User> GetUserById(@PathVariable Long id)
    {
        User user  = userRepository.findById(id).get();
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> UpdateUser(@PathVariable Long UserId, @ModelAttribute User userDetails, @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            byte[] imageBytes = null;
            if (file != null && !file.isEmpty()) {
                // Validate file size, type, etc. before processing
                imageBytes = file.getBytes();
            }

            User user = userRepository.findById(UserId).get();

            // Update user details
            user.setEmail(userDetails.getEmail());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            user.setSkillRate(userDetails.getSkillRate());
            user.setRole(userDetails.getRole());
            user.setGender(userDetails.getGender());

            // Update image if provided
            if (imageBytes != null) {
                user.setImage(imageBytes);
            }

            // Save updated user
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            // Handle errors
            throw new RuntimeException("Error occurred during user update.", e);
        }
    }


    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email).orElse(null);

    }
    public User getUserByToken(String token) {
        try {
            String email = this.jwtService.extractUsername(token);
            if (email != null) {
                User user = this.findUserByEmail(email);
                if (user != null)

                    return user;

            }
        } catch (ExpiredJwtException expiredJwtException) {
            return null;
        }
        return null;

    }




    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }

    public String getPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user.getPassword();
        } else {
            return null; // or throw an exception indicating user not found
        }
    }



    public void handleFailedLogin(User user) {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
        if (user.getLoginAttempts() >= 3) {
            user.setBanned(true);
            user.setLoginAttempts(0);
        }
        userRepository.save(user);
    }

    public void handleSuccessfulLogin(User user) {
        // Reset login attempts on successful login
        user.setLoginAttempts(0);
        userRepository.save(user);
    }

    public void resetBannedStatus(Long userId) {
        User user = userRepository.findById(userId).get();

        user.setBanned(false);
        userRepository.save(user);
    }


}