package org.tkit.onecx.permission.domain.di;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.permission.domain.di.mappers.DataImportV1Mapper;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportApplicationValueDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportProductValueDTOV1;

@DataImport("permission")
public class PermissionDataImportV1 implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(PermissionDataImportV1.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PermissionImportService service;

    @Inject
    DataImportV1Mapper mapper;

    @Override
    public void importData(DataImportConfig config) {
        log.info("Import permissions from configuration {}", config);
        try {
            var operation = config.getMetadata().getOrDefault("operation", "NONE");

            Consumer<DataImportDTOV1> action = switch (operation) {
                case "CLEAN_INSERT" -> this::cleanInsert;
                case "UPDATE" -> this::update;
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

        for (Map.Entry<String, DataImportProductValueDTOV1> p : data.getProducts().entrySet()) {
            String productName = p.getKey();

            for (Map.Entry<String, DataImportApplicationValueDTOV1> a : p.getValue().getApplications().entrySet()) {
                var appId = a.getKey();
                var app = a.getValue();
                applications.add(mapper.createApp(appId, app.getName(), app.getDescription(), productName));

                for (Map.Entry<String, Map<String, String>> perm : app.getPermissions().entrySet()) {
                    var resource = perm.getKey();
                    for (Map.Entry<String, String> action : perm.getValue().entrySet()) {
                        var permission = mapper.createPermission(appId, resource, action.getKey(), productName);
                        permission.setDescription(action.getValue());
                        permissions.add(permission);
                    }
                }

            }
        }

        // create all products, start Tx
        Map<String, Permission> permissionMap = service.createAllProducts(applications, permissions);

        // create tenant data
        data.getTenants().forEach((tenantId, dto) -> {

            // mapping
            var roles = mapper.createRoles(dto.getRoles());

            var rolesMap = roles.stream().collect(Collectors.toMap(Role::getName, r -> r));
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
        // update applications
        var permissionMap = service.updateApplicationsAndPermissions(data.getProducts());

        //update tenant data
        //        data.getTenants().forEach((tenantId, dto) -> service.createTenantData(tenantId, dto, permissionMap));
    }
}
