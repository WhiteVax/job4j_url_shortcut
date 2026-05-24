package ru.url.shortcut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "sites")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "sites")
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Schema(description = "domain", example = "google.com")
    @EqualsAndHashCode.Include
    @NotEmpty
    private String domain;

    @Schema(description = "login", example = "lfb346")
    @EqualsAndHashCode.Include
    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    Role role;
}
