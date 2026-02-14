package com.organizadorfinancas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Organizador de Finanças API")
                        .description("API REST para gerenciamento de finanças pessoais. " +
                                "Permite categorizar receitas e despesas, identificar gastos recorrentes, " +
                                "separar essenciais de supérfluos, e visualizar o panorama financeiro completo.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Organizador Finanças")
                                .email("contato@organizadorfinancas.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
