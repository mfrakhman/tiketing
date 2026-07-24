package com.etick.tiketing.auth.user.entity;

import com.etick.tiketing.auth.token.entity.RefreshToken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_role", nullable = false, length = 20)
    private PlatformRole platformRole = PlatformRole.CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Null = plain buyer, no organizer associated. Set = this user belongs
    // to that one organizer, with orgRole saying what they do there
    // (OWNER/MANAGER/STAFF). A user can only be affiliated with one
    // organizer at a time (mirrors KittoPOS's Staff: one user, one merchant).
    //
    // Plain ID, not a @ManyToOne — organizer lives in the catalog module,
    // and auth must not hold a mapped relationship across that boundary
    // (keeps this split-ready: no cross-module join to unwind later).
    @Column(name = "organizer_id")
    private Long organizerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_role", length = 20)
    private OrganizerRole orgRole;

    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshTokens;
}
