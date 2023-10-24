package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.integration.dto.request.PessoaRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.PessoaResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PessoaRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static PessoaRequestDTOTest pessoaRequestDTOTest;
  private static PessoaResponseDTOTest pessoaResponseDTOTest;

  @BeforeAll
  public static void setup() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.findAndRegisterModules();

    pessoaRequestDTOTest = new PessoaRequestDTOTest();
    pessoaResponseDTOTest = new PessoaResponseDTOTest();
  }

  @Test
  @Order(0)
  public void savePersonWithoutEnderecoIdWithSucces() throws JsonProcessingException {
    mockPessoa();

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

    pessoaResponseDTOTest = mapper.readValue(content, PessoaResponseDTOTest.class);

    assertNotNull(pessoaResponseDTOTest);
    // A migration containing 50 records was applied. This record is number 51.
    assertEquals(51L, pessoaResponseDTOTest.getId());
    assertEquals(pessoaRequestDTOTest.getNome(), pessoaResponseDTOTest.getNome());
    assertEquals(pessoaRequestDTOTest.getCpf(), pessoaResponseDTOTest.getCpf());
    assertEquals(pessoaRequestDTOTest.getEmail(), pessoaResponseDTOTest.getEmail());
    assertEquals(pessoaRequestDTOTest.getSexo(), pessoaResponseDTOTest.getSexo());
    assertEquals(pessoaRequestDTOTest.getDataNascimento(), pessoaResponseDTOTest.getDataNascimento());

    assertTrue(content.contains("{\"self\":{\"href\":\"http://localhost:8888/api/pessoas/51\"}}"));
  }

  public void mockPessoa() {
    pessoaRequestDTOTest.setNome("MATHEUS SIMEAO");
    pessoaRequestDTOTest.setCpf("000.000.000-00");
    pessoaRequestDTOTest.setSexo("MASCULINO");
    pessoaRequestDTOTest.setEmail("maatsimeao@gmail.com");
    pessoaRequestDTOTest.setDataNascimento(LocalDate.of(2002, 05, 11));
  }

}
