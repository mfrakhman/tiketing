package com.etick.tiketing.auth.user.entity;

public enum PlatformRole {
    // Everyone else — including organizer owners/staff, which is tracked
    // separately via Organizer.ownerUser / OrganizerMember, not here.
    CUSTOMER,

    // The platform's own admin/dev team — full control over the whole app.
    PLATFORM_ADMIN
}
