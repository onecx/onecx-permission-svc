package org.tkit.onecx.permission.domain.di.models;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Permission;

public class TemplateCommonData {

    Map<String, Application> applications;

    Map<String, Permission> permissions;

    public TemplateCommonData(List<Application> applications, List<Permission> permissions) {
        this.permissions = permissions.stream()
                .collect(toMap(x -> permId(x.getProductName(), x.getAppId(), x.getResource(), x.getAction()), x -> x));
        this.applications = applications.stream().collect(toMap(x -> appId(x.getProductName(), x.getAppId()), x -> x));
    }

    public Application getApplication(String productName, String appId) {
        return applications.get(appId(productName, appId));
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
