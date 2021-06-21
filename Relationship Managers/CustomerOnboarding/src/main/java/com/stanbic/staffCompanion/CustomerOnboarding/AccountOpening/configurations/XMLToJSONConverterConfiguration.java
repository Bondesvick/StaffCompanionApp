package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.configurations;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.XMLToJSONConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XMLToJSONConverterConfiguration {

    @Bean
    public XMLToJSONConverter XMLParser() {
        return new XMLToJSONConverter();
    }
}
