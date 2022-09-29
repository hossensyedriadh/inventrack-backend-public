package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "PersistedOtp")
@Table(name = "saved_codes", schema = "inventrack")
public final class PersistedOtp implements Serializable {
    @Serial
    private static final long serialVersionUID = -6401828414418468763L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @NotNull
    @Column(name = "code", updatable = false, nullable = false)
    private String code;

    @NotNull
    @Column(name = "expires_on", updatable = false, nullable = false)
    private LocalDateTime expiresOn;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "for_user", referencedColumnName = "username", nullable = false)
    private User forUser;

    @PrePersist
    private void init() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[3].concat(uuid[4]);
        this.expiresOn = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(10);
    }
}
