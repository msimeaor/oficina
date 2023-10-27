package io.github.msimeaor.aplicacao.exceptions;

import io.github.msimeaor.aplicacao.controller.ExceptionHandlerController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerControllerTest {

  @InjectMocks
  private ExceptionHandlerController exceptionHandlerController;
  @Mock
  private WebRequest webRequest;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testPessoaConflict() {
    PessoaConflictException exception = new PessoaConflictException("Error message - Pessoa Conflict");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.pessoaConflict(exception, webRequest);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Error message - Pessoa Conflict", response.getBody().getMensagemErro());
  }

  @Test
  public void testPessoaNotFound() {
    PessoaNotFoundException exception = new PessoaNotFoundException("Error message - Pessoa Not Found");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.pessoaNotFound(exception, webRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error message - Pessoa Not Found", response.getBody().getMensagemErro());
  }

  @Test
  public void testEmptyList() {
    EmptyListException exception = new EmptyListException("Error message - Empty List");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.emptyList(exception, webRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error message - Empty List", response.getBody().getMensagemErro());
  }

  @Test
  public void testEnderecoConflict() {
    EnderecoConflictException exception = new EnderecoConflictException("Error message - Endereco Conflict");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.enderecoConflict(exception, webRequest);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Error message - Endereco Conflict", response.getBody().getMensagemErro());
  }

  @Test
  public void testEnderecoNotFound() {
    EnderecoNotFoundException exception = new EnderecoNotFoundException("Error message - Endereco Not Found");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.enderecoNotFound(exception, webRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error message - Endereco Not Found", response.getBody().getMensagemErro());
  }

  @Test
  public void testTelefoneConflict() {
    TelefoneConflictException exception = new TelefoneConflictException("Error message - Telefone Conflict");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.telefoneConflict(exception, webRequest);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Error message - Telefone Conflict", response.getBody().getMensagemErro());
  }

  @Test
  public void testTelefoneNotFound() {
    TelefoneNotFoundException exception = new TelefoneNotFoundException("Error message - Telefone Not Found");
    ResponseEntity<ExceptionResponse> response = exceptionHandlerController.telefoneNotFound(exception, webRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error message - Telefone Not Found", response.getBody().getMensagemErro());
  }

}
