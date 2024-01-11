package io.github.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "WORKSPACE_ASSIGNMENT", uniqueConstraints = {
        @UniqueConstraint(name = "WORKSPACE_ASSIGNMENT_KEY", columnNames = { "TENANT_ID", "ROLE_ID", "PERMISSION_ID" })
})
public class WorkspaceAssignment extends TraceableEntity {

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
    private WorkspacePermission permission;

    @PostPersist
    void postPersist() {
        roleId = role.getId();
        permissionId = permission.getId();
    }
}
