package zhuangyan.timeplanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuangyan.timeplanning.exception.ForbiddenException;
import zhuangyan.timeplanning.repository.AuthenticationRepository;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    public String login(String username, String password) {
        if (!authenticationRepository.authenticate(username, password)) {
            throw new ForbiddenException("Authentication Failed.");
        }
        return authenticationRepository.storeToken(UUID.randomUUID().toString(), username);
    }

    public void logout(String authHeader) {
        String token = toToken(authHeader);
        authenticationRepository.invalidateToken(token);
    }

    public void createAccount(String username, String password) {
        if (!authenticationRepository.saveUser(username, password)) {
            throw new ForbiddenException("Credentials are already in use.");
        }
    }

    public void deleteAccount(String authHeader) {
        long userId = getUserId(authHeader);
        authenticationRepository.deleteUser(userId);
    }

    public long getUserId(String authHeader) {
        String token = toToken(authHeader);
        long id = authenticationRepository.getUserId(token);
        if (id == -1) {
            throw new ForbiddenException("No authentication!");
        }
        return id;
    }

    private String toToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ForbiddenException("Missing or malformed Authorization header.");
        }
        return authHeader.substring(7);
    }
}
