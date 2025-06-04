package tn.esprit.pokerplaning.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pokerplaning.Entities.User.User;
import tn.esprit.pokerplaning.Repositories.User.UserRepository;
import tn.esprit.pokerplaning.Services.User.UserServices;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@AllArgsConstructor

@RequestMapping("/api/auth")
@CrossOrigin

public class UserController {

    @Autowired
    private UserServices userServices;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private   PasswordEncoder passwordEncoder;

    @GetMapping("/ShowallUsers")
    public List<User> ShowAllUsers(){
        return  userServices.ShowAllUsers();
    }

    @GetMapping("/GetUserById/{userid}")
    public ResponseEntity<User> GetUserById(@PathVariable Long userid){
        return userServices.GetUserById(userid);
    }


    @PutMapping("/UpdateUser/{UserId}")
    public ResponseEntity<User> UpdateUser(@PathVariable Long UserId, @ModelAttribute User userDetails, @RequestParam(value = "file", required = false) MultipartFile file) {
        return userServices.UpdateUser(UserId, userDetails, file);
    }

    @DeleteMapping("/DeleteUser/{userId}")
    public void DeleteUser(@PathVariable Long userId){
        this.userServices.DeleteUser(userId);
    }

/*
    @GetMapping("/image/{userId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            byte[] imageData = userOptional.get().getImage();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type (e.g., MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
*/




    @GetMapping("/users/verif/{email}")
    public String findUserByEmail(@PathVariable String email, HttpServletRequest request) {
        Optional<User> user = userRepository.findUserByEmail(email);
        String appUrl = request.getScheme() + "://" + request.getServerName()+":8083";
        if (!user.isPresent()) {
            System.out.println( "We didn't find an account for that e-mail address.");
            return "0";
        } else {
            User userr = user.get();
            if (userr.getEmail().equals(email))
            {
                userr.setDateToken(LocalDateTime.now());
                userr.setResetToken(UUID.randomUUID().toString());
                userRepository.save(userr);
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setFrom("Haythemloueti0@gmail.com");
                simpleMailMessage.setTo(userr.getEmail());
                simpleMailMessage.setSubject("Password Reset Request");
                simpleMailMessage.setText("To reset your password, click on the following link: \n" + appUrl
                        + "/resetpassword/" + userr.getResetToken());
                System.out.println(userr.getResetToken());
                userServices.sendEmail(simpleMailMessage);
                return "1";
            }
            else
            {
                System.out.println( "Email Incorrecte");
                return ("2");
            }
        }



    }


    @PostMapping("/users/reset-password/{token}/{newPassword}")
    public ResponseEntity<Object> resetPassword(@PathVariable("token") String resetToken, @PathVariable("newPassword") String newPassword) {
        User user = userRepository.getUserByResetToken(resetToken);
        if (user != null) {
            // Update the user's password with the new password
            user.setPassword(passwordEncoder.encode(newPassword));

            // Generate a new reset token
            String newResetToken = UUID.randomUUID().toString();
            user.setResetToken(newResetToken);

            userRepository.save(user);

            // Return a JSON response with a success message
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Password reset successfully."));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid or expired reset token."));
        }
    }


    @GetMapping("/getpassword/{email}/{token}")

        public ResponseEntity<String> getPasswordByEmail(@PathVariable String email,@PathVariable String token) {
        String t = this.userServices.getPasswordByEmail(email);
        if (t != null) {
            return ResponseEntity.ok(t);
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @PutMapping("/reset-banned-status/{userId}")
    public ResponseEntity<String> resetBannedStatus(@PathVariable Long userId) {
        userServices.resetBannedStatus(userId);
        return ResponseEntity.ok("User banned status reset successfully");
    }





    }
