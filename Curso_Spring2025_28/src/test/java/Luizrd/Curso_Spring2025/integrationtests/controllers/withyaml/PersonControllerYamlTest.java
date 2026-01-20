package Luizrd.Curso_Spring2025.integrationtests.controllers.withyaml;

import Luizrd.Curso_Spring2025.config.TestConfigs;
import Luizrd.Curso_Spring2025.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import Luizrd.Curso_Spring2025.integrationtests.dto.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.PersonDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.TokenDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.wrappers.xml.PagedModelPerson;
import Luizrd.Curso_Spring2025.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static PersonDTO person;

    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();

        person = new PersonDTO();

        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(0)
    void signin() {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("leandro", "admin123");

        tokenDTO = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();

        var createdPerson = given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(
                            EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId()>0);

        assertEquals("Lucas", createdPerson.getFirstName());
        assertEquals("Bolmann", createdPerson.getLastName());
        assertEquals("New Jersei", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());

        assertTrue(createdPerson.getEnabled());

    }


    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("Torvalds");


        var createdPerson = given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);


        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId()>0);

        assertEquals("Lucas", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("New Jersei", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());

        assertTrue(createdPerson.getEnabled());

    }




    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var createdPerson = given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParams("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);


        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId()>0);

        assertEquals("Lucas", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("New Jersei", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());

        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var createdPerson = given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParams("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objectMapper);


        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId()>0);

        assertEquals("Lucas", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("New Jersei", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());

        assertFalse(createdPerson.getEnabled());

    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParams("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .queryParam("page", 3, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();


        PersonDTO personOne = people.get(0);
        person = personOne;

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId()>0);

        assertEquals("Allin", personOne.getFirstName());
        assertEquals("Emmot", personOne.getLastName());
        assertEquals("7913 Lindbergh Way", personOne.getAddress());
        assertEquals("Male", personOne.getGender());

        assertFalse(personOne.getEnabled());


        //Person 4
        PersonDTO personFour = people.get(2);
        person = personFour;

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId()>0);

        assertEquals("Allyn", personFour.getFirstName());
        assertEquals("Josh", personFour.getLastName());
        assertEquals("119 Declaration Lane", personFour.getAddress());
        assertEquals("Female", personFour.getGender());

        assertFalse(personFour.getEnabled());
    }


    @Test
    @Order(7)
    void findByNameTestTest() throws JsonProcessingException {

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("firstName", "and")
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alessandro", personOne.getFirstName());
        assertEquals("McFaul", personOne.getLastName());
        assertEquals("5 Lukken Plaza", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Brandyn", personFour.getFirstName());
        assertEquals("Grasha", personFour.getLastName());
        assertEquals("96 Mosinee Parkway", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertTrue(personFour.getEnabled());
    }






    private void mockPerson() {
        person.setFirstName("Lucas");
        person.setLastName("Bolmann");
        person.setAddress("New Jersei");
        person.setGender("Male");
        person.setEnabled(true);
        person.setPhotoUrl("https://pub.erudio.com.br/meus-cursos");
        person.setPhotoUrl("https://pub.erudio.com.br/meus-cursos");
    }
}