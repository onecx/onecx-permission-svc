package io.github.onecx.permission.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "WORKSPACE_PERMISSION", uniqueConstraints = {
        @UniqueConstraint(name = "WORKSPACE_PERMISSION_KEY", columnNames = { "TENANT_ID", "WORKSPACE_ID", "RESOURCE",
                "ACTION" })
})
@SuppressWarnings("squid:S2160")
public class WorkspacePermission extends TraceableEntity {

    @Column(name = "WORKSPACE_ID")
    private String workspaceId;

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    /**
     * The permission action.
     */
    @Column(name = "ACTION")
    private String action;

    /**
     * The permission object.
     */
    @Column(name = "RESOURCE")
    private String resource;

    /**
     * The permission description.
     */
    @Column(name = "DESCRIPTION")
    private String description;

}
