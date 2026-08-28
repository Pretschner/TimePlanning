package zhuangyan.timeplanning.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhuangyan.timeplanning.model.Credentials;
import zhuangyan.timeplanning.service.AuthenticationService;


@RestController
public class AuthenticationResource {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<String> login(@RequestBody Credentials credentials) {
        if (credentials == null || credentials.username() == null || credentials.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authenticationService.login(credentials.username(), credentials.password()));
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        authenticationService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts")
    public ResponseEntity<Void> createAccount(@RequestBody Credentials credentials) {
        if (credentials == null || credentials.username() == null || credentials.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        authenticationService.createAccount(credentials.username(), credentials.password());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/accounts")
    public ResponseEntity<Void> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        authenticationService.deleteAccount(authHeader);
        return ResponseEntity.noContent().build();
    }
}
