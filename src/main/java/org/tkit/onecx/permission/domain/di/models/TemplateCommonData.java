package org.tkit.onecx.permission.domain.di.models;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tkit.onecx.permission.domain.models.ApplicationRef;
import org.tkit.onecx.permission.domain.models.Permission;

public class TemplateCommonData {

    Set<String> applications;

    Map<String, Permission> permissions;

    public TemplateCommonData(List<ApplicationRef> applications, List<Permission> permissions) {
        this.permissions = permissions.stream()
                .collect(toMap(x -> permId(x.getProductName(), x.getAppId(), x.getResource(), x.getAction()), x -> x));
        this.applications = applications.stream().map(x -> appId(x.productName(), x.appId())).collect(toSet());
    }

    public boolean getApplication(String productName, String appId) {
        return applications.contains(appId(productName, appId));
    }

    public Permission getPermission(String productName, String appId, String resource, String action) {
        return permissions.get(permId(productName, appId, resource, action));
    }

    public void addPermission(Permission permission) {
        permissions.put(
                permId(permission.getProductName(), permission.getAppId(), permission.getResource(), permission.getAction()),
                permission);
    }

    private static String appId(String productName, String appId) {
        return productName + "#" + appId;
    }

    private static String permId(String productName, String appId, String resource, String action) {
        return productName + "#" + appId + "#" + resource + "#" + action;
    }
}
