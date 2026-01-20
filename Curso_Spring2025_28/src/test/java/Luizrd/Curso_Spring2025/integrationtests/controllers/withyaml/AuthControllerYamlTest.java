package Luizrd.Curso_Spring2025.integrationtests.controllers.withyaml;

import Luizrd.Curso_Spring2025.config.TestConfigs;
import Luizrd.Curso_Spring2025.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import Luizrd.Curso_Spring2025.integrationtests.dto.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_YAML_VALUE;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYamlTest {


    private static TokenDTO tokenDTO;

    private static YAMLMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();

        tokenDTO = new TokenDTO();
    }


    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("leandro", "admin123");

        tokenDTO = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                APPLICATION_YAML_VALUE, ContentType.TEXT
                                        )
                                )
                )
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(APPLICATION_YAML_VALUE)
                .accept(APPLICATION_YAML_VALUE)
                .body(credentials, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                        .as(TokenDTO.class, objectMapper);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {

        tokenDTO = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig().encodeContentTypeAs(
                                        APPLICATION_YAML_VALUE, ContentType.TEXT
                                )
                        )
                )
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(APPLICATION_YAML_VALUE)
                .accept(APPLICATION_YAML_VALUE)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class, objectMapper);


        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }
}