package io.github.onecx.permission.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Getter
@Setter
@Entity
@Table(name = "PERMISSION_ASSIGNMENT")
public class PermissionAssignment {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERMISSION_ID")
    private Permission permission;
}
