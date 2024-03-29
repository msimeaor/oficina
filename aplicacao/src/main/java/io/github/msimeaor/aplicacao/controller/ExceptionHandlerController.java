package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServicoConflictException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServicoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.veiculo.VeiculoConflictException;
import io.github.msimeaor.aplicacao.exceptions.veiculo.VeiculoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(PessoaConflictException.class)
  public ResponseEntity<ExceptionResponse> pessoaConflict(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.CONFLICT, ex, request);
  }

  @ExceptionHandler(PessoaNotFoundException.class)
  public ResponseEntity<ExceptionResponse> pessoaNotFound(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  @ExceptionHandler(EmptyListException.class)
  public ResponseEntity<ExceptionResponse> emptyList(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  @ExceptionHandler(EnderecoConflictException.class)
  public ResponseEntity<ExceptionResponse> enderecoConflict(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.CONFLICT, ex, request);
  }

  @ExceptionHandler(EnderecoNotFoundException.class)
  public ResponseEntity<ExceptionResponse> enderecoNotFound(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  @ExceptionHandler(TelefoneConflictException.class)
  public ResponseEntity<ExceptionResponse> telefoneConflict(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.CONFLICT, ex, request);
  }

  @ExceptionHandler(TelefoneNotFoundException.class)
  public ResponseEntity<ExceptionResponse> telefoneNotFound(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  @ExceptionHandler(ServicoConflictException.class)
  public ResponseEntity<ExceptionResponse> servicoConflict(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.CONFLICT, ex, request);
  }

  @ExceptionHandler(ServicoNotFoundException.class)
  public ResponseEntity<ExceptionResponse> servicoNotFound(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  @ExceptionHandler(VeiculoConflictException.class)
  public ResponseEntity<ExceptionResponse> veiculoConflict(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.CONFLICT, ex, request);
  }

  @ExceptionHandler(VeiculoNotFoundException.class)
  public ResponseEntity<ExceptionResponse> veiculoNotFound(Exception ex, WebRequest request) {
    return criarExceptionResponseERetornarResponseEntity(HttpStatus.NOT_FOUND, ex, request);
  }

  private ResponseEntity<ExceptionResponse> criarExceptionResponseERetornarResponseEntity(
          HttpStatus codigoStatus, Exception ex, WebRequest request) {

    ExceptionResponse error = ExceptionResponse.builder()
            .codigoStatus(codigoStatus.value())
            .mensagemErro(ex.getMessage())
            .detalhesErro(request.getDescription(false))
            .timeStamp(new Date())
            .build();

    return new ResponseEntity<>(error, codigoStatus);
  }

}
