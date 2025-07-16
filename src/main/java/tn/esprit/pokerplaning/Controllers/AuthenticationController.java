package tn.esprit.pokerplaning.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pokerplaning.Entities.AuthenticationRequest;
import tn.esprit.pokerplaning.Entities.AuthenticationResponse;
import tn.esprit.pokerplaning.Entities.RegisterRequest;
import tn.esprit.pokerplaning.Services.AuthenticationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private  final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register (@ModelAttribute RegisterRequest request, @RequestParam(value= "file" ,required = false) MultipartFile file) throws IOException {

        return ResponseEntity.ok(service.register(request,file));

    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws ChangeSetPersister.NotFoundException {
        AuthenticationResponse response = service.authenticate(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return ResponseEntity.ok(response);
    }


}