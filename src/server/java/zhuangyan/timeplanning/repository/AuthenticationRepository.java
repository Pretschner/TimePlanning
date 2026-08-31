package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AuthenticationRepository {
    private final AtomicLong nextUserId = new AtomicLong(1);
    // Login
    private final Map<String, Integer> passwords; // username -> hash(password)
    // Create/Delete Account
    private final Map<String, Long> userIds; // username -> userId
    // Login and Access
    private final Map<String, Long> tokens; // token -> userId

    public AuthenticationRepository() {
        passwords = new ConcurrentHashMap<>();
        userIds = new ConcurrentHashMap<>();
        tokens = new ConcurrentHashMap<>();
        // Admin Login
        saveUser("admin", "pass");
    }

    public boolean authenticate(String username, String password) {
        Integer storedPasswordHash = passwords.get(username);
        if (storedPasswordHash == null) return false;
        return storedPasswordHash.equals(password.hashCode());
    }

    public String storeToken(String token, String username) {
        long userId = userIds.get(username);
        tokens.put(token, userId);
        return token;
    }

    public void invalidateToken(String token) {
        tokens.remove(token);
    }

    public boolean saveUser(String username, String password) {
        Integer existing = passwords.putIfAbsent(username, password.hashCode());
        if (existing != null) return false; // already exists
        userIds.put(username, nextUserId.getAndIncrement());
        return true;
    }

    public void deleteUser(long userId) {
        passwords.entrySet().removeIf(entry -> userIds.get(entry.getKey()).equals(userId));
        userIds.entrySet().removeIf(entry -> entry.getValue().equals(userId));
        tokens.entrySet().removeIf(entry -> entry.getValue().equals(userId));
    }

    public long getUserId(String token) {
        Long userId = tokens.get(token);
        return userId != null ? userId : -1;
    }
}
