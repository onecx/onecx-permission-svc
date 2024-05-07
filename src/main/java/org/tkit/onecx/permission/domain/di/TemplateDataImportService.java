package org.tkit.onecx.permission.domain.di;

import java.util.*;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.permission.domain.di.mappers.TemplateMapper;
import org.tkit.onecx.permission.domain.di.models.TemplateCommonData;
import org.tkit.onecx.permission.domain.di.models.TemplateTenantData;
import org.tkit.onecx.permission.domain.models.Application;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.template.model.TemplateApplicationValueDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateImportDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateProductValueDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateRoleValueDTO;

@DataImport("template")
public class TemplateDataImportService implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(TemplateDataImportService.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PermissionTemplateService service;

    @Inject
    TemplateMapper mapper;

    @Override
    public void importData(DataImportConfig config) {
        log.info("Import permissions from configuration {}", config);
        try {

            TemplateImportDTO data = objectMapper.readValue(config.getData(), TemplateImportDTO.class);

            var existingData = importProducts(data.getProducts());

            List<String> tenants = List.of();
            var tmp = config.getMetadata().get("tenants");
            if (tmp != null) {
                tenants = List.of(tmp.split(","));
            }

            if (tenants.isEmpty()) {
                log.warn("No tenants defined for the templates");
                return;
            }

            importRoles(tenants, data.getRoles(), existingData);

        } catch (Exception ex) {
            throw new TemplateException(ex.getMessage(), ex);
        }
    }

    private TemplateCommonData importProducts(Map<String, TemplateProductValueDTO> products) {

        var data = service.getCommonData(products.keySet());

        // create or update apps
        var applications = new ArrayList<Application>();

        // create or update permission
        var permissions = new ArrayList<Permission>();

        // loop over products and create applications and permissions create/update request
        for (Map.Entry<String, TemplateProductValueDTO> item : products.entrySet()) {
            String productName = item.getKey();

            for (Map.Entry<String, TemplateApplicationValueDTO> entry : item.getValue().getApplications().entrySet()) {
                var appId = entry.getKey();
                var app = entry.getValue();

                // check the application of the product
                var appDb = data.getApplication(productName, appId);
                if (appDb == null) {
                    var a = mapper.createApplication(productName, appId, app.getName(), app.getDescription());
                    applications.add(a);
                }

                // check application permission
                createPermission(productName, appId, app.getPermissions(), permissions, data);
            }
        }

        // create or update all applications and permissions in request, start Tx
        service.createApplicationsAndPermissions(applications, permissions);

        return data;
    }

    private void importRoles(List<String> tenants, Map<String, TemplateRoleValueDTO> dto, TemplateCommonData commonData) {

        var roleNames = dto.keySet();
        var productNames = dto.values().stream()
                .map(x -> x.getAssignments().keySet()).collect(HashSet::new, Set<String>::addAll, Set<String>::addAll);

        for (String tenant : tenants) {

            var data = service.getTenantData(tenant, roleNames, productNames);

            var roles = new ArrayList<Role>();
            var assignments = new ArrayList<Assignment>();

            for (var dr : dto.entrySet()) {
                var role = data.getRole(dr.getKey());
                if (role == null) {
                    role = mapper.createRole(dr.getKey(), dr.getValue().getDescription());
                    roles.add(role);
                }

                assignments.addAll(createAssignments(role, dr.getValue(), data, commonData));
            }

            service.createTenantData(tenant, roles, assignments);
        }

    }

    private List<Assignment> createAssignments(Role role, TemplateRoleValueDTO dto, TemplateTenantData data,
            TemplateCommonData commonData) {
        List<Assignment> assignments = new ArrayList<>();
        for (var aProduct : dto.getAssignments().entrySet()) {
            for (var aApp : aProduct.getValue().entrySet()) {
                for (var aResource : aApp.getValue().entrySet()) {
                    for (var action : aResource.getValue()) {
                        if (!data.hasPermissionAction(role.getName(), aProduct.getKey(), aApp.getKey(), aResource.getKey(),
                                action)) {
                            var p = commonData.getPermission(aProduct.getKey(), aApp.getKey(), aResource.getKey(), action);
                            assignments.add(mapper.createAssignment(role, p));
                        }
                    }
                }
            }
        }
        return assignments;
    }

    private void createPermission(String productName, String appId, Map<String, Map<String, String>> dto,
            List<Permission> permissions, TemplateCommonData data) {
        for (Map.Entry<String, Map<String, String>> perm : dto.entrySet()) {
            var resource = perm.getKey();
            for (Map.Entry<String, String> action : perm.getValue().entrySet()) {
                var permDb = data.getPermission(productName, appId, resource, action.getKey());
                if (permDb == null) {
                    var permCreate = mapper.createPermission(productName, appId, resource, action.getKey(), action.getValue());
                    permissions.add(permCreate);
                    data.addPermission(permCreate);
                }
            }
        }
    }

    public static class TemplateException extends RuntimeException {

        public TemplateException(String message, Throwable ex) {
            super(message, ex);
        }
    }

}
