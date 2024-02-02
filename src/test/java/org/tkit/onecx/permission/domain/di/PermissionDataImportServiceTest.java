package org.tkit.onecx.permission.domain.di;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportDTOV1;
import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportTenantWrapperDTOV1;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class PermissionDataImportServiceTest extends AbstractTest {

    @Inject
    PermissionDataImportV1 service;

    @Inject
    ObjectMapper mapper;

    @Inject
    PermissionDAO permissionDAO;

    @Test
    void checkTest() {
        var data = new DataImportDTOV1();
        data.setTenants(null);
        data.setPermissions(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(null);
        data.setPermissions(Map.of());
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of());
        data.setPermissions(Map.of());
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of());
        data.setPermissions(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isTrue();

        data.setTenants(Map.of("2", new DataImportTenantWrapperDTOV1()));
        data.setPermissions(Map.of("2", new HashMap<>()));
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();

        data.setTenants(Map.of("2", new DataImportTenantWrapperDTOV1()));
        data.setPermissions(null);
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();

        data.setTenants(null);
        data.setPermissions(Map.of("2", new HashMap<>()));
        assertThat(PermissionDataImportV1.checkIsEmpty(data)).isFalse();
    }

    @Test
    void importDataNotSupportedActionTest() {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("operation", "CUSTOM_NOT_SUPPORTED");
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }
        };

        service.importData(config);

        List<Permission> data = permissionDAO.findAll().toList();
        assertThat(data).isNotNull().hasSize(7);

    }

    @Test
    void importEmptyDataTest() {
        Assertions.assertDoesNotThrow(() -> {
            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    return new byte[] {};
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        return mapper.writeValueAsBytes(new DataImportDTOV1());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        var data = new DataImportDTOV1();
                        data.setPermissions(null);
                        data.setTenants(null);
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("operation", "CLEAN_INSERT");
                }

                @Override
                public byte[] getData() {
                    try {
                        var data = new DataImportDTOV1();
                        data.setPermissions(null);
                        data.setTenants(Map.of());
                        return mapper.writeValueAsBytes(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        });

        var config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("operation", "CLEAN_INSERT");
            }

            @Override
            public byte[] getData() {
                return new byte[] { 0 };
            }
        };
        Assertions.assertThrows(PermissionDataImportV1.ImportException.class, () -> service.importData(config));

    }
}
