package com.drmarkdown.doc.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();

        // GET , POST, HEAD
        config.applyPermitDefaultValues();

        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.OPTIONS);

        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }


}
