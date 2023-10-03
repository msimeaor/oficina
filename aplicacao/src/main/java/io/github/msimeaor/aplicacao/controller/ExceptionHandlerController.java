package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
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
    return criarExceptionResponseERetornarResponseEntity(
            HttpStatus.CONFLICT, ex, request);
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
