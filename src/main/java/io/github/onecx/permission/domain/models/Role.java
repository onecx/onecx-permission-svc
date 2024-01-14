package io.github.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ROLE", uniqueConstraints = {
        @UniqueConstraint(name = "ROLE_NAME", columnNames = { "TENANT_ID", "NAME" })
}, indexes = {
        @Index(name = "ROLE_NAME", columnList = "NAME")
})
@SuppressWarnings("java:S2160")
public class Role extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    /**
     * The role name.
     */
    @Column(name = "NAME")
    private String name;

    /**
     * The role description.
     */
    @Column(name = "DESCRIPTION")
    private String description;

}
