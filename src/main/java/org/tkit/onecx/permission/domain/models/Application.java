package org.tkit.onecx.permission.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "APPLICATION", uniqueConstraints = {
        @UniqueConstraint(name = "APPLICATION_KEY", columnNames = { "PRODUCT_NAME", "APP_ID" }),
}, indexes = {
        @Index(name = "APPLICATION_APP_ID", columnList = "PRODUCT_NAME,APP_ID")
})
@SuppressWarnings("squid:S2160")
public class Application extends TraceableEntity {

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRODUCT_NAME")
    private String productName;
}
