package org.tkit.onecx.permission.domain.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.template.model.TemplateApplicationValueDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateImportDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateProductValueDTO;
import gen.org.tkit.onecx.permission.domain.template.model.TemplateRoleValueDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/test-template.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class TemplateImportTest extends AbstractTest {

    @Inject
    TemplateDataImportService service;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    ObjectMapper mapper;

    @Test
    void importDataNoDataTest() {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("tenants", null);
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }
        };

        assertThatThrownBy(() -> service.importData(config)).isInstanceOf(TemplateDataImportService.TemplateException.class);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(8);

    }

    @Test
    void importDataNoTenantsTest() {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("tenants", null);
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }

            @Override
            public byte[] getData() {
                try {
                    return mapper.writeValueAsBytes(new TemplateImportDTO());
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        service.importData(config);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(8);

    }

    @Test
    void importDataExistTest() {

        TemplateImportDTO request = new TemplateImportDTO()
                .putRolesItem("n1", new TemplateRoleValueDTO().description("d1")
                        .putAssignmentsItem("test1", Map.of("app1", Map.of("o1", List.of("a1", "a2")))))
                .putProductsItem("test1", new TemplateProductValueDTO()
                        .putApplicationsItem("app1", new TemplateApplicationValueDTO()
                                .putPermissionsItem("o1", Map.of("a1", "d1"))));

        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("tenants", "default");
            }

            @Override
            public byte[] getData() {
                try {
                    return mapper.writeValueAsBytes(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };

        service.importData(config);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(8);

    }
}
