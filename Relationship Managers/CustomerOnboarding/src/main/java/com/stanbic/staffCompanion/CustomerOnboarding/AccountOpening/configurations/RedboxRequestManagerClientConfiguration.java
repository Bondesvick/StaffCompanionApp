package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.configurations;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RedboxRequestHeadersDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class RedboxRequestManagerClientConfiguration {

    @Value("${REDBOX_REQUEST_MANAGER_SERVICE_ENDPOINT:None}")
    private String requestManagerServiceEndpoint;

    @Value("${REDBOX_REQUEST_MANAGER_SERVICE_HEADER__SOAP_ACTION:None}")
    private String SOAPAction;

    @Value("${REDBOX_REQUEST_MANAGER_SERVICE_HEADER__AUTHORIZATION:None}")
    private String authorization;

    @Value("${REDBOX_REQUEST_MANAGER_SERVICE_HEADER__MODULE_ID:None}")
    private String moduleId;


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(
                "com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager"
        );

        return marshaller;
    }

    @Bean
    public RedboxRequestManagerClient redboxRequestClient(Jaxb2Marshaller marshaller) {

        // Setup request headers
        RedboxRequestHeadersDTO requestManagerHeadersDTO = new RedboxRequestHeadersDTO(
                SOAPAction, authorization, moduleId);

        // Setup client
        RedboxRequestManagerClient client = new RedboxRequestManagerClient(
                requestManagerServiceEndpoint,
                requestManagerHeadersDTO);

        client.setDefaultUri(requestManagerServiceEndpoint);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);

        return client;
    }

}

