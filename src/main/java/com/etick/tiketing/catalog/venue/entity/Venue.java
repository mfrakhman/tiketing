package com.etick.tiketing.catalog.venue.entity;

import com.etick.tiketing.catalog.eventsession.entity.EventSession;
import com.etick.tiketing.catalog.organizer.entity.Organizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

// Venues are owned per-organizer for simplicity. If a shared venue
// directory (multiple organizers booking the same physical venue) is
// needed later, split this into a global directory + an
// organizer_venue_bookings join — no other table needs to change.
@Entity
@Getter
@Setter
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", referencedColumnName = "id", nullable = false)
    private Organizer organizer;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 120)
    private String city;

    @Column(name = "region", length = 120)
    private String region; // state/province

    @Column(name = "country", nullable = false, length = 2)
    private String country; // ISO-3166 alpha-2

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "capacity")
    private Integer capacity; // informational total capacity

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone; // IANA tz, e.g. Asia/Jakarta

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "venue")
    private List<EventSession> eventSessions;
}
