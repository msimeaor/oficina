package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.integration.dto.request.TelefoneRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.TelefoneResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.helper.telefone.TelefoneWrapper;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TelefoneRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static TelefoneRequestDTOTest telefoneRequestDTOTest;
  private static TelefoneRequestDTOTest telefoneRequestDTOTestWithAnInvalidId;
  private static TelefoneRequestDTOTest telefoneRequestDTOTestUpdated;

  @BeforeAll
  public static void setup() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    startTestEntities();
  }

  @Test
  @Order(0)
  public void saveWithAValidPersonId() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, "http://localhost:8080")
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given().spec(specification)
            .basePath("/api/telefones")
            .body(telefoneRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var telefoneResponseDTO = mapper.readValue(content, TelefoneResponseDTOTest.class);

    assertNotNull(telefoneResponseDTO);
    /*
    How I applied a migration populating the telefone table with ten records, the ID of saved record it will be eleven
    */
    assertEquals(11L, telefoneResponseDTO.getId());
    assertEquals("61991979110", telefoneResponseDTO.getNumero());

    assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/telefones/11\"}"));
    assertTrue(content.contains("\"Proprietário\":{\"href\":\"http://localhost:8888/api/pessoas/40\"}"));
  }

  @Test
  @Order(1)
  public void saveWithAnInvalidPersonId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .body(telefoneRequestDTOTestWithAnInvalidId)
            .when()
              .post()
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    ExceptionResponse exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Cliente não encontrado! ID: 100", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/telefones", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(2)
  public void findByIdWithAValidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .pathParam("id", 11L)
            .when()
              .get("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var telefoneResponseDTO = mapper.readValue(content, TelefoneResponseDTOTest.class);

    assertNotNull(telefoneResponseDTO);
    assertEquals(11L, telefoneResponseDTO.getId());
    assertEquals("61991979110", telefoneResponseDTO.getNumero());

    assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/telefones/11\"}"));
    assertTrue(content.contains("\"Proprietário\":{\"href\":\"http://localhost:8888/api/pessoas/40\"}"));
  }

  @Test
  @Order(3)
  public void saveWithAnInvalidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .pathParam("id", 12L)
            .when()
              .get("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    ExceptionResponse exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Telefone não encontrado! ID: 12", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/telefones/12", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(4)
  public void findAll() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    TelefoneWrapper response = mapper.readValue(content, TelefoneWrapper.class);
    List<TelefoneResponseDTOTest> telefoneResponseDTOTestList = response.getTelefoneEmbedded().getTelefoneResponseDTOList();

    assertNotNull(telefoneResponseDTOTestList);
    assertEquals(10, telefoneResponseDTOTestList.size());
    assertEquals(1L, telefoneResponseDTOTestList.get(0).getId());
    assertEquals("1351417529", telefoneResponseDTOTestList.get(0).getNumero());

    System.out.println(content);
    assertTrue(content.contains(
      "\"_links\":{\"first\":{\"href\":\"http://localhost:8888/api/telefones?page=0&size=10&sort=numero,asc\"}," +
      "\"self\":{\"href\":\"http://localhost:8888/api/telefones{?page,size,direction}\",\"templated\":true}," +
      "\"next\":{\"href\":\"http://localhost:8888/api/telefones?page=1&size=10&sort=numero,asc\"}," +
      "\"last\":{\"href\":\"http://localhost:8888/api/telefones?page=1&size=10&sort=numero,asc\"}}"
    ));
    assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":11,\"totalPages\":2,\"number\":0}"));
  }

  @Test
  @Order(5)
  public void updateWithAValidPersonIDAndValidPhoneID() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .body(telefoneRequestDTOTestUpdated)
            .pathParam("id", 11L)
            .when()
              .put("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var telefoneResponseDTO = mapper.readValue(content, TelefoneResponseDTOTest.class);

    assertNotNull(telefoneResponseDTO);
    assertEquals(11L, telefoneResponseDTO.getId());
    assertEquals("99999999999", telefoneResponseDTO.getNumero());

    assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/telefones/11\"}"));
    /*
    When passing the object in request, we changed the person who owned of this phone. Now, the new owner is referenced
    in this HATEOAS link.
    */
    assertTrue(content.contains("Proprietário\":{\"href\":\"http://localhost:8888/api/pessoas/41\"}"));
  }

  @Test
  @Order(6)
  public void updateWithAnInvalidTelefoneId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones")
            .body(telefoneRequestDTOTest)
            .pathParam("id", 100L)
            .when()
              .put("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    ExceptionResponse exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Telefone não encontrado! ID: 100", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/telefones/100", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(7)
  public void findByNumeroWithAValidNumero() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones/findByNumero")
            .pathParam("numero", "1351417529")
            .when()
              .get("{numero}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var telefoneResponseDTO = mapper.readValue(content, TelefoneResponseDTO.class);

    assertNotNull(telefoneResponseDTO);
    assertEquals(TelefoneResponseDTO.class, telefoneResponseDTO.getClass());
    assertEquals(1L, telefoneResponseDTO.getId());
    assertEquals("1351417529", telefoneResponseDTO.getNumero());

    assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/telefones/1\"}"));
    // In migration, this record have a relationship with the person that contains the ID 8
    assertTrue(content.contains("\"Proprietário\":{\"href\":\"http://localhost:8888/api/pessoas/8\"}"));
  }

  @Test
  @Order(8)
  public void findByNumeroWithAnInvalidNumero() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/telefones/findByNumero")
            // Wrong number
            .pathParam("numero", "1092889201")
            .when()
              .get("{numero}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    ExceptionResponse exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Telefone não encontrado! Numero: 1092889201", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/telefones/findByNumero/1092889201", exceptionResponse.getDetalhesErro());
  }

  private static void startTestEntities() {
    telefoneRequestDTOTest = TelefoneRequestDTOTest.builder()
            .numero("61991979110")
            // Random person ID
            .pessoaId(40L)
            .build();

    telefoneRequestDTOTestWithAnInvalidId = TelefoneRequestDTOTest.builder()
            .numero("00000000000")
            // Invalid ID in database
            .pessoaId(100L)
            .build();

    telefoneRequestDTOTestUpdated = TelefoneRequestDTOTest.builder()
            .numero("99999999999")
            .pessoaId(41L)
            .build();
  }

}
