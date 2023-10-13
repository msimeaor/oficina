package io.github.msimeaor.aplicacao.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customerOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Sistema Oficina")
        .description("Sistema de controle de clientes e serviços")
        .version("1.0.0")
        .contact(new Contact()
          .name("Matheus Simeão dos Reis")
          .email("maatsimeao@gmail.com")
          .url("https://github.com/msimeaor")
        )
      );
  }

}
