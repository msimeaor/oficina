package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.integration.dto.request.EnderecoRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.EnderecoResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnderecoRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static EnderecoRequestDTOTest enderecoRequestDTOTest;
  private static EnderecoRequestDTOTest enderecoRequestDTOTestWithPersonIdList;
  private static EnderecoResponseDTOTest enderecoResponseDTOTest;

  @BeforeAll
  public static void setup() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    startEntities();
  }

  @Test
  @Order(0)
  public void saveWithoutPessoaIdList() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, "http://localhost:8080")
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given().spec(specification)
            .basePath("/api/enderecos")
            .body(enderecoRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var enderecoResponseDTO = mapper.readValue(content, EnderecoResponseDTOTest.class);

    assertNotNull(enderecoResponseDTO);
    /*
    In this case, was applied a migration in database containing ten address records. When adding a new record,
    then it will have the eleventh ID
    */
    assertEquals(11L, enderecoResponseDTO.getId());
    assertEquals("Area Especial 2A Modulo E Lote 5", enderecoResponseDTO.getLogradouro());
    assertEquals(UFs.DF, enderecoResponseDTO.getUf());
    /*
    Take into account that no person ids were added to the list of person ids in EnderecoRequestDTO.
    */
    assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/enderecos/11\"}}"));
  }

  @Test
  @Order(1)
  public void saveWithPessoaIdList() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/enderecos")
            .body(enderecoRequestDTOTestWithPersonIdList)
            .when()
              .post()
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var enderecoResponseDTO = mapper.readValue(content, EnderecoResponseDTOTest.class);

    assertNotNull(enderecoResponseDTO);
    assertEquals(12L, enderecoResponseDTO.getId());
    assertEquals("QNP 15 Conjunto I", enderecoResponseDTO.getLogradouro());
    assertEquals(UFs.DF, enderecoResponseDTO.getUf());
    /*
    Now, as we passed a person id to request, the response body will return this HATEOAS link containing
    this request path to Pessoa controller.
    */
    assertTrue(content.contains("\"Morador(es)\":{\"href\":\"http://localhost:8888/api/pessoas/8\"}"));
    assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/enderecos/12\"}"));
  }

  @Test
  @Order(2)
  public void findByIdWithAValidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/enderecos")
            .pathParam("id", 12L)
            .when()
              .get("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var enderecoResponseDTO = mapper.readValue(content, EnderecoResponseDTOTest.class);

    assertNotNull(enderecoResponseDTO);
    assertEquals(12L, enderecoResponseDTO.getId());
    assertEquals("QNP 15 Conjunto I", enderecoResponseDTO.getLogradouro());
    assertEquals(UFs.DF, enderecoResponseDTO.getUf());
    /*
    Now, as we passed a person id to request, the response body will return this HATEOAS link containing
    this request path to Pessoa controller.
    */
    assertTrue(content.contains("\"Morador(es)\":{\"href\":\"http://localhost:8888/api/pessoas/8\"}"));
    assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/enderecos/12\"}"));
  }

  @Test
  @Order(3)
  public void findByIdWithAnInvalidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/enderecos")
            .pathParam("id", 13L)
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
    assertEquals("Endereço não encontrado! ID: 13", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/enderecos/13", exceptionResponse.getDetalhesErro());
  }

  public static void startEntities() {
    enderecoRequestDTOTest = EnderecoRequestDTOTest.builder()
            .logradouro("Area Especial 2A Modulo E Lote 5")
            .uf(UFs.DF)
            .pessoasId(null)
            .build();

    enderecoRequestDTOTestWithPersonIdList = EnderecoRequestDTOTest.builder()
            .logradouro("QNP 15 Conjunto I")
            .uf(UFs.DF)
            .pessoasId(Collections.singletonList(8L))
            .build();
  }

}
