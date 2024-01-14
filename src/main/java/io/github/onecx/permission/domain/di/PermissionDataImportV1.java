package io.github.onecx.permission.domain.di;

import java.util.function.Consumer;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.permission.domain.di.v1.model.DataImportDTOV1;

@DataImport("permission")
public class PermissionDataImportV1 implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(PermissionDataImportV1.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PermissionImportService service;

    @Override
    public void importData(DataImportConfig config) {
        log.info("Import permissions from configuration {}", config);
        try {
            var operation = config.getMetadata().getOrDefault("operation", "NONE");

            Consumer<DataImportDTOV1> action = null;
            if ("CLEAN_INSERT".equals(operation)) {
                action = this::cleanInsert;
            }

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
        return (data.getPermissions() == null || data.getPermissions().isEmpty())
                && (data.getTenants() == null || data.getTenants().isEmpty());
    }

    public void cleanInsert(DataImportDTOV1 data) {

        // delete all tenant data
        var tenants = data.getTenants().keySet();
        tenants.forEach(tenant -> service.deleteAllData(tenant));

        // delete all permission
        service.deleteAllPermissions();

        // create permissions
        var permissionMap = service.createAllPermissions(data.getPermissions());

        // create tenant data
        data.getTenants().forEach((tenantId, dto) -> service.createTenantData(tenantId, dto, permissionMap));

    }

    public static class ImportException extends RuntimeException {

        public ImportException(String message, Throwable ex) {
            super(message, ex);
        }
    }
}
