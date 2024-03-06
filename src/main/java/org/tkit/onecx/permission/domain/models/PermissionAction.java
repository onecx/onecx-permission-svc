package org.tkit.onecx.permission.domain.models;

public record PermissionAction(String roleName, String productName, String applicationId, String resource, String action) {
}
