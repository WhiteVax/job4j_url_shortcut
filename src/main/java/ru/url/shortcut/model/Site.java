package ru.url.shortcut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sites")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "sites")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "url", example = "google.com")
    @EqualsAndHashCode.Include
    @NotEmpty
    private String url;

    @Schema(description = "short-url", example = "ZRUfdD2")
    @EqualsAndHashCode.Include
    private String code;

    @Schema(description = "Total number of calls to this site")
    private int total;
}
