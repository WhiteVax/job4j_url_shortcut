package ru.url.shortcut.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.url.shortcut.dto.RegistrationResponse;
import ru.url.shortcut.model.Role;
import ru.url.shortcut.model.User;
import ru.url.shortcut.repository.UserDataRepository;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UserService {
    private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
    private static final int LOGIN_LENGTH = 12;
    private static final int PASSWORD_LENGTH = 16;

    private final UserDataRepository userData;
    private final SiteService siteService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public UserService(UserDataRepository userData, SiteService siteService,
                       PasswordEncoder passwordEncoder) {
        this.userData = userData;
        this.siteService = siteService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByLogin(String login) {
        return userData.findByLogin(login);
    }

    public RegistrationResponse register(String site) {
        if (siteService.siteFindByUrl(site).isPresent()) {
            return new RegistrationResponse(false, null, null);
        }
        String login = uniqueLogin();
        String rawPassword = generateCode(PASSWORD_LENGTH);
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        User saved = userData.save(user);
        siteService.createSite(site, saved);
        return new RegistrationResponse(true, login, rawPassword);
    }

    public void delete(User user) {
        userData.delete(user);
    }

    public void deleteByLoginAndPassword(String login, String password) {
        userData.deleteByLoginAndPassword(login, password);
    }

    private String uniqueLogin() {
        String login = generateCode(LOGIN_LENGTH);
        while (userData.findByLogin(login).isPresent()) {
            login = generateCode(LOGIN_LENGTH);
        }
        return login;
    }

    private String generateCode(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }
        return result.toString();
    }
}
