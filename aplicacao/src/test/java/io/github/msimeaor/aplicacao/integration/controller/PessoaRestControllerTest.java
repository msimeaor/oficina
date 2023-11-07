package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.integration.dto.request.PessoaRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.PessoaResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.helper.pessoa.PessoaWrapper;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PessoaRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static PessoaRequestDTOTest pessoaRequestDTOTest;
  private static PessoaRequestDTOTest pessoaRequestDTOTestWithPessoaId;
  private static PessoaRequestDTOTest pessoaRequestDTOTestUpdated;

  @BeforeAll
  public static void setup() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.findAndRegisterModules();

    startTestEntities();
  }

  @Test
  @Order(0)
  public void savePersonWithoutEnderecoId() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, "http://localhost:8080")
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .build();

    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .body(pessoaRequestDTOTest)
            .pathParam("placa", "JJJ1111")
            .when()
              .post("{placa}")
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var pessoaResponseDTO = mapper.readValue(content, PessoaResponseDTOTest.class);

    assertNotNull(pessoaResponseDTO);
    // A migration containing 50 records was applied. This record is number 51.
    assertEquals(51L, pessoaResponseDTO.getId());
    assertEquals(pessoaRequestDTOTest.getNome(), pessoaResponseDTO.getNome());
    assertEquals(pessoaRequestDTOTest.getCpf(), pessoaResponseDTO.getCpf());
    assertEquals(pessoaRequestDTOTest.getEmail(), pessoaResponseDTO.getEmail());
    assertEquals(pessoaRequestDTOTest.getSexo(), pessoaResponseDTO.getSexo());
    assertEquals(pessoaRequestDTOTest.getDataNascimento(), pessoaResponseDTO.getDataNascimento());
    assertNull(pessoaResponseDTO.getEnderecoResponse());

    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/51\"}}"));
  }

  @Test
  @Order(1)
  public void savePersonWithPessoaId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .body(pessoaRequestDTOTestWithPessoaId)
            .pathParam("placa", "JJJ1111")
            .when()
              .post("{placa}")
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var pessoaResponseDTO = mapper.readValue(content, PessoaResponseDTOTest.class);

    assertNotNull(pessoaResponseDTO);
    // Take into account that the 51 ID was filled in the first test
    assertEquals(52L, pessoaResponseDTO.getId());
    assertEquals(pessoaRequestDTOTestWithPessoaId.getNome(), pessoaResponseDTO.getNome());
    assertEquals(pessoaRequestDTOTestWithPessoaId.getCpf(), pessoaResponseDTO.getCpf());
    assertEquals(pessoaRequestDTOTestWithPessoaId.getEmail(), pessoaResponseDTO.getEmail());
    assertEquals(pessoaRequestDTOTestWithPessoaId.getSexo(), pessoaResponseDTO.getSexo());
    assertEquals(pessoaRequestDTOTestWithPessoaId.getDataNascimento(), pessoaResponseDTO.getDataNascimento());

    assertNotNull(pessoaResponseDTO.getEnderecoResponse());
    assertEquals(EnderecoResponseDTO.class, pessoaResponseDTO.getEnderecoResponse().getClass());
    assertEquals(1L, pessoaResponseDTO.getEnderecoResponse().getId());
    assertEquals("68643 Shopko Hill", pessoaResponseDTO.getEnderecoResponse().getLogradouro());
    assertEquals(UFs.DF, pessoaResponseDTO.getEnderecoResponse().getUf());

    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/52\"}}"));
  }

  @Test
  @Order(2)
  public void findByIdWithAValidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            // The first id found in database is 8L. This was a change I made in the migration.
            .pathParam("id", 8L)
            .when()
              .get("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var pessoaResponseDTO = mapper.readValue(content, PessoaResponseDTOTest.class);

    /*
    During migration, the database does not contain registered CPFs.
    Therefore, the assertions do not contain validation of the CPF variable, but records that contain CPF,
    must return an object with the CPF variable filled in
    */
    assertNotNull(pessoaResponseDTO);
    assertEquals(8L, pessoaResponseDTO.getId());
    assertEquals("Curtis", pessoaResponseDTO.getNome());
    assertEquals("cattew7@discovery.com", pessoaResponseDTO.getEmail());
    assertEquals("Male", pessoaResponseDTO.getSexo());
    assertEquals(LocalDate.of(2022, 11, 9), pessoaResponseDTO.getDataNascimento());

    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/8\"}}"));
  }

  // Validation test of the findById method by passing an ID that is not present in database
  @Test
  @Order(3)
  public void findByIdWithAnInvalidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .pathParam("id", 100L)
            .when()
              .get("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(ExceptionResponse.class, exceptionResponse.getClass());
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Cliente não encontrado! ID: 100", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/pessoas/100", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(4)
  public void findAll() throws JsonProcessingException {
    /*
    Have not been added any request param on this request. Therefore, the response returns a paginate list containig
    ten ocurrences per page, being the first page the page zero.
    In this case, as the database has 45 records, the paginate list contain 5 pages.
    */
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    PessoaWrapper response = mapper.readValue(content, PessoaWrapper.class);
    List<PessoaResponseDTOTest> pessoaResponseDTOTestList = response.getPessoaEmbedded().getPessoaResponseDTOList();

    assertNotNull(pessoaResponseDTOTestList);
    assertEquals(10, pessoaResponseDTOTestList.size());
    assertEquals(35, pessoaResponseDTOTestList.get(0).getId());
    assertEquals("Male", pessoaResponseDTOTestList.get(0).getSexo());
    assertEquals("Adolphus", pessoaResponseDTOTestList.get(0).getNome());
    assertEquals(LocalDate.of(2023, 7, 30), pessoaResponseDTOTestList.get(0)
            .getDataNascimento());
    assertNull(pessoaResponseDTOTestList.get(0).getCpf());

    assertTrue(content.contains(
      "\"_links\":{\"first\":{\"href\":\"http://localhost:8888/api/pessoas?direction=ASC&page=0&size=10&sort=nome,asc\"}," +
      "\"self\":{\"href\":\"http://localhost:8888/api/pessoas?page=0&size=10&direction=ASC\"}," +
      "\"next\":{\"href\":\"http://localhost:8888/api/pessoas?direction=ASC&page=1&size=10&sort=nome,asc\"}," +
      "\"last\":{\"href\":\"http://localhost:8888/api/pessoas?direction=ASC&page=4&size=10&sort=nome,asc\"}}"));
    assertTrue(content.contains(
            "\"page\":{\"size\":10,\"totalElements\":45,\"totalPages\":5,\"number\":0}"));
  }

  @Test
  @Order(5)
  public void updateWithoutEnderecoId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .body(pessoaRequestDTOTestUpdated)
            .pathParam("id", 51L)
            .when()
              .put("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var pessoaResponseDTO = mapper.readValue(content, PessoaResponseDTOTest.class);

    assertNotNull(pessoaResponseDTO);
    assertEquals(51L, pessoaResponseDTO.getId());
    assertEquals(pessoaRequestDTOTestUpdated.getNome(), pessoaResponseDTO.getNome());
    assertEquals(pessoaRequestDTOTestUpdated.getCpf(), pessoaResponseDTO.getCpf());
    assertEquals(pessoaRequestDTOTestUpdated.getEmail(), pessoaResponseDTO.getEmail());
    assertEquals(pessoaRequestDTOTestUpdated.getSexo(), pessoaResponseDTO.getSexo());
    assertEquals(pessoaRequestDTOTestUpdated.getDataNascimento(), pessoaResponseDTO.getDataNascimento());
    /*
    Take into account that the updated person did not have any registered address
    If the person had an address, he would remain in object of response body
    */
    assertNull(pessoaResponseDTO.getEnderecoResponse());

    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/51\"}}"));
  }

  @Test
  @Order(6)
  public void updateWithEnderecoId() throws JsonProcessingException {
    pessoaRequestDTOTestUpdated.setNome("FERDINANDO CORREIO");
    pessoaRequestDTOTestUpdated.setEnderecoId(1L);

    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .body(pessoaRequestDTOTestUpdated)
            .pathParam("id", 51L)
            .when()
              .put("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var pessoaResponseDTO = mapper.readValue(content, PessoaResponseDTOTest.class);

    assertNotNull(pessoaResponseDTO);
    assertEquals(51L, pessoaResponseDTO.getId());
    assertEquals("FERDINANDO CORREIO", pessoaResponseDTO.getNome());
    assertNotNull(pessoaResponseDTO.getEnderecoResponse());
    assertEquals(1L, pessoaResponseDTO.getEnderecoResponse().getId());
    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/51\"}}"));
  }

  @Test
  @Order(7)
  public void updateWithInvalidPessoaId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas")
            .body(pessoaRequestDTOTestUpdated)
            .pathParam("id", 100L)
            .when()
              .put("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(ExceptionResponse.class, exceptionResponse.getClass());
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Cliente não encontrado! ID: 100", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/pessoas/100", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(8)
  public void findByNome() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/pessoas/findByNome")
            .pathParam("nome", "adol")
            .when()
              .get("{nome}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    PessoaWrapper response = mapper.readValue(content, PessoaWrapper.class);
    List<PessoaResponseDTOTest> pessoaResponseDTOTestList = response.getPessoaEmbedded().getPessoaResponseDTOList();

    assertNotNull(pessoaResponseDTOTestList);
    assertEquals(1, pessoaResponseDTOTestList.size());
    assertEquals(35, pessoaResponseDTOTestList.get(0).getId());
    assertEquals("Male", pessoaResponseDTOTestList.get(0).getSexo());
    assertEquals("Adolphus", pessoaResponseDTOTestList.get(0).getNome());
    assertEquals(LocalDate.of(2023, 7, 30), pessoaResponseDTOTestList.get(0)
            .getDataNascimento());
    assertNull(pessoaResponseDTOTestList.get(0).getCpf());

    assertTrue(content.contains(
            "\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/pessoas?page=0&size=5&direction=ASC\"}}"));
    assertTrue(content.contains(
            "\"page\":{\"size\":5,\"totalElements\":1,\"totalPages\":1,\"number\":0}"));
  }

  private static void startTestEntities() {
    pessoaRequestDTOTest = PessoaRequestDTOTest.builder()
            .nome("MATHEUS SIMEAO")
            .cpf("000.000.000-00")
            .sexo("MASCULINO")
            .email("maatsimeao@gmail.com")
            .dataNascimento(LocalDate.of(2002, 5, 11))
            .build();

    pessoaRequestDTOTestWithPessoaId = PessoaRequestDTOTest.builder()
            .nome("ROGERIO MADURO")
            .cpf("999.999.999-99")
            .sexo("FEMININO")
            .email("rogeriomaduro@gmail.com")
            .dataNascimento(LocalDate.of(2002, 5, 11))
            .enderecoId(1L)
            .build();

    pessoaRequestDTOTestUpdated = PessoaRequestDTOTest.builder()
            .nome("FRANCISCO VIEIRA")
            .cpf("123.456.789-10")
            .sexo("FEMININO")
            .email("franciscovieira@gmail.com")
            .dataNascimento(LocalDate.of(2004, 3, 12))
            .build();
  }

}
