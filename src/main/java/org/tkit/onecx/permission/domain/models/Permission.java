package org.tkit.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERMISSION")
@SuppressWarnings("squid:S2160")
public class Permission extends TraceableEntity {

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "PRODUCT_NAME")
    private String productName;

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
