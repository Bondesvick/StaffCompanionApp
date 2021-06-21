package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RedboxRequestHeadersDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.MimeHeaders;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class RedboxRequestHeaderCallback implements WebServiceMessageCallback {

    private final static Logger log = LoggerFactory.getLogger(RedboxRequestHeaderCallback.class);
    private final RedboxRequestHeadersDTO redboxRequestHeadersDTO;

    public RedboxRequestHeaderCallback(RedboxRequestHeadersDTO redboxRequestHeadersDTO) {
        this.redboxRequestHeadersDTO = redboxRequestHeadersDTO;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        /*
         * This intercepts the request and adds
         * the soap header before the request is sent
         *
         * Source: https://stackoverflow.com/a/37857351
         * */

        if (message instanceof SaajSoapMessage) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) message;
            MimeHeaders mimeHeader = soapMessage.getSaajMessage().getMimeHeaders();
            mimeHeader.setHeader("SOAPAction", redboxRequestHeadersDTO.getSoapAction());
            mimeHeader.setHeader("authorization", redboxRequestHeadersDTO.getAuthorization());
            mimeHeader.setHeader("module_id", redboxRequestHeadersDTO.getModuleID());
        }
    }
}