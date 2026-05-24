package ru.url.shortcut.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class ConvertorForSite {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertorForSite.class);

    public String urlConvertor(String url) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(url.getBytes(StandardCharsets.UTF_8));
            String base = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(hash);
            return base.substring(0, 7);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
            throw new NoSuchAlgorithmException();
        }
    }
}
