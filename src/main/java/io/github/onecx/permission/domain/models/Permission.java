package io.github.onecx.permission.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERMISSION", uniqueConstraints = {
        @UniqueConstraint(name = "PERMISSION_KEY", columnNames = { "APP_ID", "OBJECT", "ACTION" })
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
    @Column(name = "OBJECT")
    private String object;

    /**
     * The permission name.
     */
    @Column(name = "NAME")
    private String name;

    /**
     * The permission description.
     */
    @Column(name = "DESCRIPTION")
    private String description;

}
