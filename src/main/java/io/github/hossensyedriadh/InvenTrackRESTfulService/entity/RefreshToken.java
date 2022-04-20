package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table
@Entity(name = "refresh_tokens")
public final class RefreshToken implements Serializable {
    @Serial
    private static final long serialVersionUID = -4056316764066757095L;

    @Id
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @NonNull
    @Column(name = "token", unique = true, updatable = false, nullable = false)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "for_user", referencedColumnName = "username", nullable = false)
    private User forUser;
}
