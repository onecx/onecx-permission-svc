package org.tkit.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERMISSION", uniqueConstraints = {
        @UniqueConstraint(name = "PERMISSION_KEY", columnNames = { "APP_ID", "RESOURCE", "ACTION" }),
}, indexes = {
        @Index(name = "PERMISSION_APP_ID", columnList = "APP_ID")
})
@SuppressWarnings("squid:S2160")
public class Permission extends TraceableEntity {

    @Column(name = "APP_ID")
    private String appId;

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
