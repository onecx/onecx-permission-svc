package org.tkit.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ROLE", uniqueConstraints = {
        @UniqueConstraint(name = "UC_ROLE_NAME", columnNames = { "NAME", "TENANT_ID" })
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

    /**
     * Flag to protect mandatory data
     */
    @Column(name = "MANDATORY")
    private Boolean mandatory;

    /**
     * Flag to identify role created by an operator
     */
    @Column(name = "OPERATOR")
    private Boolean operator;

}
