package Luizrd.Curso_Spring2025.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Value("${cors.originPatterns:default}")
    private String corsOriginPatterns = "";


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = corsOriginPatterns.split(",");
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                //.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") //limitar os métodos
                .allowedMethods("*")
                .allowCredentials(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {


        //Via EXTENSION. _.xml _.JSON Deprecated on Spring Boot 2.6

        //Via QUERY PARAM http://localhost:8080/api/person/v1/1?mediaType=xml
        /*configurer.favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(true) //ignora o comando via header
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);

         */


        //Via HEADER PARAM http://localhost:8080/api/person/v1/1
        //Colocar no Header da Requisição = Accept = application/xml ou application/yaml ou appication/json
        //POST / PUT = colocar Content-Type = application/yaml + o Accept também (Raw - text)
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false) //Não ignora o comando via header
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("xml", MediaType.APPLICATION_YAML)
        ;

    }
}
