package org.tkit.onecx.permission.domain.di;

import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.function.Consumer;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.di.mappers.DataImportV1Mapper;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.di.v1.model.*;

@DataImport("permission")
public class PermissionDataImportV1 implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(PermissionDataImportV1.class);

    public static final String METADATA_OPERATION = "operation";

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PermissionImportService service;

    @Inject
    DataImportV1Mapper mapper;

    @Inject
    ApplicationDAO applicationDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Override
    public void importData(DataImportConfig config) {
        log.info("Import permissions from configuration {}", config);
        try {
            var operation = config.getMetadata().get(METADATA_OPERATION);
            var op = Operation.valueOfMetadata(operation);

            Consumer<DataImportDTOV1> action = switch (op) {
                case CLEAN_INSERT -> this::cleanInsert;
                case UPDATE -> this::update;
                default -> null;
            };

            if (action == null) {
                log.warn("Not supported operation '{}' for the import configuration key '{}'", operation, config.getKey());
                return;
            }

            if (config.getData() == null || config.getData().length == 0) {
                log.warn("Import configuration key {} does not contains any data to import", config.getKey());
                return;
            }

            DataImportDTOV1 data = objectMapper.readValue(config.getData(), DataImportDTOV1.class);

            if (checkIsEmpty(data)) {
                log.warn("Import configuration key {} does not contains any JSON data to import", config.getKey());
                return;
            }

            // execute the import
            action.accept(data);
        } catch (Exception ex) {
            throw new ImportException(ex.getMessage(), ex);
        }
    }

    static boolean checkIsEmpty(DataImportDTOV1 data) {
        return (data.getProducts() == null || data.getProducts().isEmpty())
                && (data.getTenants() == null || data.getTenants().isEmpty());
    }

    public void cleanInsert(DataImportDTOV1 data) {

        // delete all tenant data
        var tenants = data.getTenants().keySet();
        tenants.forEach(tenant -> service.deleteAllData(tenant));

        // create application and permissions
        var applications = new ArrayList<Application>();
        var permissions = new ArrayList<Permission>();
        Map<String, Permission> permissionMap = new HashMap<>();

        for (Map.Entry<String, DataImportProductValueDTOV1> product : data.getProducts().entrySet()) {
            String productName = product.getKey();

            for (Map.Entry<String, DataImportApplicationValueDTOV1> a : product.getValue().getApplications().entrySet()) {
                var appId = a.getKey();
                applications.add(mapper.createApp(productName, appId, a.getValue().getName(), a.getValue().getDescription()));

                for (Map.Entry<String, Map<String, String>> perm : a.getValue().getPermissions().entrySet()) {
                    var resource = perm.getKey();
                    for (Map.Entry<String, String> action : perm.getValue().entrySet()) {
                        var p = mapper.createPermission(productName, appId, resource, action.getKey(), action.getValue());
                        permissions.add(p);
                        permissionMap.put(p.getProductName() + p.getAppId() + p.getResource() + p.getAction(), p);
                    }
                }

            }
        }

        // create all products, start Tx
        service.createAllProducts(applications, permissions);

        // create tenant data
        data.getTenants().forEach((tenantId, dto) -> {

            // mapping
            var roles = mapper.createRoles(dto.getRoles());

            var rolesMap = roles.stream().collect(toMap(Role::getName, r -> r));
            var mapping = mapper.createMapping(dto.getRoles());
            var assignments = mapper.createAssignments(mapping, rolesMap, permissionMap);

            // create data, start tenant Tx
            service.createTenantData(tenantId, roles, assignments);
        });

    }

    public static class ImportException extends RuntimeException {

        public ImportException(String message, Throwable ex) {
            super(message, ex);
        }
    }

    public void update(DataImportDTOV1 data) {

        // find apps in DB
        var appList = applicationDAO.findByProductNames(data.getProducts().keySet());
        var appMap = appList.stream().collect(toMap(x -> x.getProductName() + x.getAppId(), x -> x));

        // find permission in DB
        var loadedProductPermissions = new HashSet<>(data.getProducts().keySet());
        var permList = permissionDAO.findByProductNames(data.getProducts().keySet());
        var permMap = permList.stream()
                .collect(toMap(x -> x.getProductName() + x.getAppId() + x.getResource() + x.getAction(), x -> x));

        // create or update apps
        var createApplications = new ArrayList<Application>();
        var updateApplications = new ArrayList<Application>();
        // create or update permission
        var createPermissions = new ArrayList<Permission>();
        var updatePermissions = new ArrayList<Permission>();

        for (Map.Entry<String, DataImportProductValueDTOV1> p : data.getProducts().entrySet()) {
            String productName = p.getKey();

            for (Map.Entry<String, DataImportApplicationValueDTOV1> a : p.getValue().getApplications().entrySet()) {
                var appId = a.getKey();
                var app = a.getValue();

                var appDb = appMap.get(productName + appId);
                if (appDb != null) {
                    updateApplications.add(mapper.updateApp(app.getName(), app.getDescription(), appDb));
                } else {
                    createApplications.add(mapper.createApp(productName, appId, app.getName(), app.getDescription()));
                }

                createPermission(productName, appId, app.getPermissions(), createPermissions, updatePermissions, permMap);

            }
        }

        // create or update all products, start Tx
        service.createAndUpdateAllProducts(createApplications, createPermissions, updateApplications, updatePermissions);

        // tenant data
        data.getTenants().forEach((tenantId, dto) -> updateTenantData(tenantId, dto, permMap, loadedProductPermissions));
    }

    private void createPermission(String productName, String appId, Map<String, Map<String, String>> permissions,
            List<Permission> createPermissions, List<Permission> updatePermissions,
            Map<String, Permission> permMap) {
        for (Map.Entry<String, Map<String, String>> perm : permissions.entrySet()) {
            var resource = perm.getKey();
            for (Map.Entry<String, String> action : perm.getValue().entrySet()) {

                var id = productName + appId + resource + action.getKey();
                var permDb = permMap.get(id);
                if (permDb != null) {
                    updatePermissions.add(mapper.updatePermission(action.getValue(), permDb));
                } else {
                    var x = mapper.createPermission(productName, appId, resource, action.getKey(), action.getValue());
                    createPermissions.add(x);
                    permMap.put(x.getProductName() + x.getAppId() + x.getResource() + x.getAction(), x);
                }
            }
        }
    }

    private void updateTenantData(String tenantId, DataImportTenantValueDTOV1 dto, Map<String, Permission> permMap,
            Set<String> loadedProductPermissions) {
        // check roles in DB
        var roleList = roleDAO.findByNames(dto.getRoles().keySet());
        var roleMap = roleList.stream().collect(toMap(Role::getName, x -> x));

        var productNames = new HashSet<String>();
        for (var dr : dto.getRoles().entrySet()) {
            productNames.addAll(dr.getValue().getAssignments().keySet());
        }

        // check assignments in DB
        var assignmentList = assignmentDAO.findPermissionActionForProducts(productNames);
        var assignmentMap = assignmentList.stream().collect(
                toMap(x -> x.roleName() + x.productName() + x.applicationId() + x.resource() + x.action(), x -> x)).keySet();

        // check if we have all permission
        productNames.removeAll(loadedProductPermissions);
        if (!productNames.isEmpty()) {
            var tmp = permissionDAO.findByProductNames(productNames);
            tmp.forEach(x -> permMap.put(x.getProductName() + x.getAppId() + x.getResource() + x.getAction(), x));
        }

        // create or update roles
        var createRoles = new ArrayList<Role>();
        var updateRoles = new ArrayList<Role>();
        var assignments = new ArrayList<Assignment>();

        for (var dr : dto.getRoles().entrySet()) {
            var role = roleMap.get(dr.getKey());
            if (role != null) {
                updateRoles.add(mapper.updateRole(dr.getValue().getDescription(), role));
            } else {
                role = mapper.createRole(dr.getKey(), dr.getValue().getDescription());
                createRoles.add(role);
                roleMap.put(role.getName(), role);
            }

            // check the assignments
            assignments.addAll(createAssignments(role, dr.getValue(), assignmentMap, permMap));
        }

        // create data, start tenant Tx
        service.createAndUpdateTenantData(tenantId, createRoles, assignments, updateRoles);
    }

    private List<Assignment> createAssignments(Role role, DataImportTenantRoleValueDTOV1 dto, Set<String> assignmentMap,
            Map<String, Permission> permMap) {
        List<Assignment> assignments = new ArrayList<>();
        for (var aProduct : dto.getAssignments().entrySet()) {
            for (var aApp : aProduct.getValue().entrySet()) {
                for (var aResource : aApp.getValue().entrySet()) {
                    for (var action : aResource.getValue()) {
                        var permId = aProduct.getKey() + aApp.getKey() + aResource.getKey() + action;
                        if (!assignmentMap.contains(role.getName() + permId)) {
                            var p = permMap.get(permId);
                            assignments.add(mapper.createAssignment(role, p));
                        }
                    }
                }
            }
        }
        return assignments;
    }

    public enum Operation {
        NONE,
        CLEAN_INSERT,

        UPDATE;

        public static Operation valueOfMetadata(String operation) {
            for (Operation value : values()) {
                if (value.name().equalsIgnoreCase(operation)) {
                    return value;
                }
            }
            return NONE;
        }
    }
}
