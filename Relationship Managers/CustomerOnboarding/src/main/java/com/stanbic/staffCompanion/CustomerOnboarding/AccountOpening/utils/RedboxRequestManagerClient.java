package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RedboxRequestHeadersDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class RedboxRequestManagerClient extends WebServiceGatewaySupport {

    private final static Logger log = LoggerFactory.getLogger(RedboxRequestManagerClient.class);
    private final String requestManagerServiceEndpoint;
    private final RedboxRequestHeadersDTO requestManagerHeadersDTO;

    @Value("${spring.profiles.active:None}")
    private String currentSpringProfile;

    public RedboxRequestManagerClient(String requestManagerServiceEndpoint, RedboxRequestHeadersDTO requestManagerHeadersDTO) {
        this.requestManagerServiceEndpoint = requestManagerServiceEndpoint;
        this.requestManagerHeadersDTO = requestManagerHeadersDTO;
    }

    public RequestManagerResponse callRequestManagerService(
            RequestManagerRequest requestDTO) {

        log.info("Sending request to Redbox");

        if (currentSpringProfile.equals("dev")){

            log.info("Current Spring Profile is 'dev'. Will print request payload to show what's being sent to Redbox.");

            // Print the request payload so we can see what's being sent to Redbox
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(RequestManagerRequest.class);

                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                StringWriter sw = new StringWriter();

                jaxbMarshaller.marshal(requestDTO, sw);

                String xmlContent = sw.toString();
                log.info("Request payload: \n" + xmlContent);

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        JAXBElement<RequestManagerResponse> response = (JAXBElement<RequestManagerResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(
                        requestManagerServiceEndpoint,
                        requestDTO,
                        new RedboxRequestHeaderCallback(requestManagerHeadersDTO)
                        );

        log.info("Response was received from Redbox and unmarshalled successfully");
        log.info("Response code: "+response.getValue().getResponseCode());
        log.info("Response description: "+response.getValue().getResponseDescription());

        return response.getValue();
    }

}
