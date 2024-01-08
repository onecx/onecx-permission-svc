package io.github.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@Table(name = "ASSIGNMENT")
public class Assignment extends AbstractTraceableEntity<AssignmentId> {

    @EmbeddedId
    private AssignmentId id = new AssignmentId();

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLE_ID")
    @MapsId("roleId")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERMISSION_ID")
    @MapsId("permissionId")
    private Permission permission;

    @Override
    public String toString() {
        return "Assignment:" + id.toString();
    }
}
