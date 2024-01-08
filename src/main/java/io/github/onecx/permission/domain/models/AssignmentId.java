package io.github.onecx.permission.domain.models;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class AssignmentId implements Serializable {

    @Serial
    private static final long serialVersionUID = -2387354805510410784L;

    /**
     * The role id.
     */
    private String roleId;

    /**
     * The permission id.
     */
    private String permissionId;

    @Override
    public String toString() {
        return "{role='" + roleId + ",perm='" + permissionId + '}';
    }
}
