package Luizrd.Curso_Spring2025.integrationtests.controllers.withxml;

import Luizrd.Curso_Spring2025.config.TestConfigs;
import Luizrd.Curso_Spring2025.integrationtests.dto.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXmlTest {


    private static TokenDTO tokenDTO1;

    private static XmlMapper objectMapper1;

    @BeforeAll
    public static void setUp() {
        objectMapper1 = new XmlMapper();
        objectMapper1.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        tokenDTO1 = new TokenDTO();
    }


    @Test
    @Order(0)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("leandro", "admin123");

        var content = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                        .asString();


        tokenDTO1 = objectMapper1.readValue(content, TokenDTO.class);

        assertNotNull(tokenDTO1.getAccessToken());
        assertNotNull(tokenDTO1.getRefreshToken());
    }

    @Test
    @Order(1)
    void refreshToken() throws JsonProcessingException {
        var content = given()
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("username", tokenDTO1.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO1.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();


        tokenDTO1 = objectMapper1.readValue(content, TokenDTO.class);

        assertNotNull(tokenDTO1.getAccessToken());
        assertNotNull(tokenDTO1.getRefreshToken());
    }
}