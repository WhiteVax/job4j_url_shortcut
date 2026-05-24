package ru.url.shortcut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Table(name = "urls")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
@Schema(description = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private Site site;

    @Column(name = "original_url")
    @EqualsAndHashCode.Include
    private String originalUrl;

    private String code;

    @Schema(description = "Total number of calls to this site")
    private int total;
}
