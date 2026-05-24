package ru.url.shortcut.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.url.shortcut.dto.RegistrationResponse;
import ru.url.shortcut.model.Role;
import ru.url.shortcut.model.Site;
import ru.url.shortcut.repository.SiteDataRepository;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class SiteService {
    private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
    private static final int LOGIN_LENGTH = 12;
    private static final int PASSWORD_LENGTH = 16;
    private final SiteDataRepository siteData;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public SiteService(SiteDataRepository siteData, PasswordEncoder passwordEncoder) {
        this.siteData = siteData;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponse register(String siteReq) {
        String login = generateCode(LOGIN_LENGTH);
        String rawPassword = generateCode(PASSWORD_LENGTH);
        Site site = Site.builder()
                .domain(siteReq)
                .login(login)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .build();
        siteData.save(site);
        return new RegistrationResponse(siteReq, login, rawPassword);
    }

    private String generateCode(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }
        return result.toString();
    }

    public Optional<Site> findByLogin(String name) {
        return siteData.findByLogin(name);
    }

    public void deleteAll() {
        siteData.deleteAll();
    }
}
