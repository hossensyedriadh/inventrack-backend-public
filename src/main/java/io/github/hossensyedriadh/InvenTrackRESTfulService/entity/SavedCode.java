package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "saved_codes")
public final class SavedCode implements Serializable {
    @Serial
    private static final long serialVersionUID = -2509494154077804096L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @NotNull(message = "Code can not be null")
    @Column(name = "code", updatable = false, nullable = false)
    private String code;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "for_user", referencedColumnName = "username", nullable = false)
    private User forUser;

    @PrePersist
    private void init() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[3].concat(uuid[4]);
    }
}
