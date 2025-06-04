package tn.esprit.pokerplaning.Services.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pokerplaning.Entities.User.*;
import tn.esprit.pokerplaning.Repositories.User.UserRepository;
import tn.esprit.pokerplaning.Services.User.twilio.SmsService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserServices userServices;
    private final SmsService smsService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request, MultipartFile file) {
        try {
            byte[] imageBytes = null;
            if (file != null) {
                // Validate file size, type, etc. before processing
                imageBytes = file.getBytes();
            }

            // Process user registration
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .gender(request.getGender())
                    .phone(request.getPhone())
                    .skillRate(request.getSkillRate())
                    .image(imageBytes) // Set image bytes or null if no image provide
                    .build();

            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(user)
                    .build();
        } catch (IOException | IllegalArgumentException e) {
            // Handle errors
            throw new RuntimeException("Error occurred during registration.", e);
        }
    }




  /*  public AuthenticationResponse authenticate (AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return  AuthenticationResponse.builder().token(jwtToken).user(user).build();

    }

*/
  public AuthenticationResponse authenticate(AuthenticationRequest request) throws ChangeSetPersister.NotFoundException {
      User user = repository.findByEmail(request.getEmail())
              .orElseThrow(ChangeSetPersister.NotFoundException::new);

      if (user.isBanned()) {
          // Send SMS to notify the user that their account is banned
          sendBanNotificationSms(user.getPhone());
          return AuthenticationResponse.builder().message("Your account is banned").build();
      }

      try {
          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                          request.getEmail(),
                          request.getPassword()
                  )
          );


          userServices.handleSuccessfulLogin(user);


          var jwtToken = jwtService.generateToken(user);
          return  AuthenticationResponse.builder().token(jwtToken).user(user).build();
      } catch (AuthenticationException e) {

          userServices.handleFailedLogin(user);


          return AuthenticationResponse.builder().message("Authentication failed").build();
      }
  }


    private void sendBanNotificationSms(String phoneNumber) {
        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setNumber(phoneNumber);
        smsRequest.setMessage("Your account has been banned. If you believe this is a mistake, please contact support.");
        smsService.sendSms(smsRequest);
    }






}
