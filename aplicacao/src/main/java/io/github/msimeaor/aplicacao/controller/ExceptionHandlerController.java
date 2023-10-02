package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
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
