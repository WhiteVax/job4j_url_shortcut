package ru.url.shortcut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
@Schema(description = "User model")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Schema(description = "login", example = "Losdvn2")
    @EqualsAndHashCode.Include
    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
