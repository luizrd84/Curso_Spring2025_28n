package Luizrd.Curso_Spring2025.integrationtests.controllers.withjson;

import Luizrd.Curso_Spring2025.config.TestConfigs;
import Luizrd.Curso_Spring2025.integrationtests.dto.AccountCredentialsDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.PersonDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.TokenDTO;
import Luizrd.Curso_Spring2025.integrationtests.dto.wrappers.json.WrapperPersonDTO;
import Luizrd.Curso_Spring2025.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonDTO person;

    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

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

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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


        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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

        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("page", 3, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperPersonDTO wraper = objectMapper.readValue(content, WrapperPersonDTO.class);
        List<PersonDTO> people = wraper.getEmbedded().getPeople();

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
    void findByNameTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("firstName", "and")
                .queryParam("page", 0, "size", 12, "direction", "asc")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperPersonDTO wraper = objectMapper.readValue(content, WrapperPersonDTO.class);
        List<PersonDTO> people = wraper.getEmbedded().getPeople();

        PersonDTO personOne = people.get(0);
        person = personOne;

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId()>0);

        assertEquals("Alessandro", personOne.getFirstName());
        assertEquals("McFaul", personOne.getLastName());
        assertEquals("5 Lukken Plaza", personOne.getAddress());
        assertEquals("Male", personOne.getGender());

        assertTrue(personOne.getEnabled());


        //Person 4
        PersonDTO personFour = people.get(4);
        person = personFour;

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId()>0);

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