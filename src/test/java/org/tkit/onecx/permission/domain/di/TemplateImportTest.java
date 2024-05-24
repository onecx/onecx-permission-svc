package org.tkit.onecx.permission.domain.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of();
            }
        };

        assertThatThrownBy(() -> service.importData(config)).isInstanceOf(TemplateDataImportService.TemplateException.class);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(8);

    }

    @Test
    void importDataNoTenantsTest() {

        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of();
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
                return Map.of();
            }

            @Override
            public byte[] getData() {
                try {
                    return mapper.writeValueAsBytes(request);
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
    void importDataExistSpecialCaseTest() {

        TemplateImportDTO request = new TemplateImportDTO()
                .putRolesItem("n_x", new TemplateRoleValueDTO().assignments(null))
                .putRolesItem("n1", new TemplateRoleValueDTO().description("d1")
                        .putAssignmentsItem("test1", Map.of("app1", Map.of("o1", List.of("a1", "a2", "x1")))))
                .putProductsItem("test_a", null)
                .putProductsItem("test_x", new TemplateProductValueDTO()
                        .putApplicationsItem("app_x", new TemplateApplicationValueDTO()
                                .putPermissionsItem("o1", Map.of("a1", "d1", "x1", "xx1"))));

        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of();
            }

            @Override
            public byte[] getData() {
                try {
                    return mapper.writeValueAsBytes(request);
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        service.importData(config);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(10);

    }
}
