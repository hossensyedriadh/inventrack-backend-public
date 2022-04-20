package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suppliers")
public final class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = -2467982657716738603L;

    @NotNull(message = "Supplier name can not be null")
    @Pattern(message = "Invalid supplier name", regexp = "^[\\w\s.]{4,}$")
    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Id
    @NotNull(message = "Supplier phone no. can not be null")
    @Pattern(message = "Invalid phone number", regexp = "^[0-9+]{8,20}$")
    @Column(name = "phone_no", unique = true, updatable = false, nullable = false)
    private String phoneNo;

    @Email(message = "Invalid email", regexp = "\\S+@\\S+\\.\\S+")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Supplier address can not be null")
    @Pattern(regexp = "^[a-zA-Z0-9:;().,\\-\\s\\\\/]{5,}$", message = "Invalid address")
    @Column(name = "address", nullable = false)
    private String address;

    @URL(regexp = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            message = "Invalid website URL")
    @Column(name = "website")
    private String website;

    @Column(name = "notes")
    private String notes;

    @Setter(AccessLevel.NONE)
    @Column(name = "added_on", updatable = false, nullable = false)
    private LocalDateTime addedOn;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = false)
    private User addedBy;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @PrePersist
    private void initialize() {
        this.addedOn = LocalDateTime.now();
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now();
    }
}
