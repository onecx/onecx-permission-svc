package org.tkit.onecx.permission.domain.di;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.tkit.onecx.permission.domain.di.PermissionDataImportV1.METADATA_OPERATION;

import java.util.Map;

import jakarta.inject.Inject;

import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.dataimport.DataImportConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportDTOV1;

@SuppressWarnings("java:S2187")
public abstract class AbstractPermissionDataImportV1Test extends AbstractTest {

    @Inject
    PermissionDataImportV1 service;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    ApplicationDAO applicationDAO;

    protected DataImportConfig createConfig(String key, PermissionDataImportV1.Operation operation, DataImportDTOV1 dto) {

        byte[] data;
        try {
            data = objectMapper.writerFor(DataImportDTOV1.class).writeValueAsBytes(dto);
        } catch (Exception ex) {
            throw new RuntimeException("Error serialize data import data", ex);
        }

        return new DataImportConfig() {

            @Override
            public Map<String, String> getMetadata() {
                return Map.of(METADATA_OPERATION, operation.name());
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public byte[] getData() {
                return data;
            }
        };
    }

    protected void checkResult(String tenantId, int applicationCount, int permissionCount, int roleCount, int assignmentCount) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            var applications = applicationDAO.findAll().toList();
            assertThat(applications).hasSize(applicationCount);

            var permissions = permissionDAO.findAll().toList();
            assertThat(permissions).hasSize(permissionCount);

            var roles = roleDAO.findAll().toList();
            assertThat(roles).hasSize(roleCount);

            var assignments = assignmentDAO.findAll();
            assertThat(assignments).hasSize(assignmentCount);

        } finally {
            ApplicationContext.close();
        }
    }
}
