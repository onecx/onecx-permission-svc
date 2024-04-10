package org.tkit.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ASSIGNMENT")
@SuppressWarnings("java:S2160")
public class Assignment extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "ROLE_ID", insertable = false, updatable = false)
    private String roleId;

    @Column(name = "PERMISSION_ID", insertable = false, updatable = false)
    private String permissionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERMISSION_ID")
    private Permission permission;

    @PostPersist
    void postPersist() {
        roleId = role.getId();
        permissionId = permission.getId();
    }
}
