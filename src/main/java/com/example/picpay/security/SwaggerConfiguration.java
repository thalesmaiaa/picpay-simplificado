package com.example.picpay.security;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI publicApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("teste")
                                .summary("aefim")
                                .version("0.0.1")
                                .license(
                                        new License().name("a"))).path("/swagger-ui", null);


    }

}
