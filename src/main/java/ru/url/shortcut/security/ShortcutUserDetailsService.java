package ru.url.shortcut.security;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.url.shortcut.repository.SiteDataRepository;

import java.util.List;

@Service
public class ShortcutUserDetailsService implements UserDetailsService {
    private final SiteDataRepository siteDataRepository;

    public ShortcutUserDetailsService(SiteDataRepository userDataRepository) {
        this.siteDataRepository = userDataRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return siteDataRepository.findByLogin(username)
                .map(user -> new User(
                        user.getLogin(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
