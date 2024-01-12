package io.github.onecx.permission.rs.external.v1;

import io.github.onecx.permission.test.AbstractTest;

//@QuarkusTest
//@TestHTTPEndpoint(PermissionRestController.class)
//@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class PermissionRestControllerConfigIssuerTest extends AbstractTest {
    //
    //    @InjectMock
    //    TokenConfig tokenConfig;
    //
    //    @Inject
    //    Config config;
    //
    //    @BeforeEach
    //    void beforeEach() {
    //        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(TokenConfig.class);
    //        Mockito.when(tokenConfig.tokenClaimSeparator()).thenReturn(tmp.tokenClaimSeparator());
    //        Mockito.when(tokenConfig.tokenClaimPath()).thenReturn("groups");
    //        Mockito.when(tokenConfig.tokenVerified()).thenReturn(true);
    //        Mockito.when(tokenConfig.tokenPublicKeyLocationSuffix()).thenReturn(tmp.tokenPublicKeyLocationSuffix());
    //        Mockito.when(tokenConfig.tokenPublicKeyEnabled()).thenReturn(true);
    //    }
    //
    //    @Test
    //    void skipTokenVerified() {
    //
    //        KeycloakTestClient keycloakClient = new KeycloakTestClient();
    //        var accessToken = keycloakClient.getAccessToken("bob");
    //
    //        var dto = given()
    //                .contentType(APPLICATION_JSON)
    //                .body(new PermissionRequestDTOV1().token(accessToken))
    //                .post("/application/app1")
    //                .then()
    //                .statusCode(OK.getStatusCode())
    //                .extract()
    //                .body().as(ApplicationPermissionsDTOV1.class);
    //
    //        assertThat(dto).isNotNull();
    //        assertThat(dto.getPermissions()).isNotNull().hasSize(1);
    //        assertThat(dto.getPermissions().get("o1")).isNotNull().hasSize(1).containsExactly("a3");
    //    }
}
