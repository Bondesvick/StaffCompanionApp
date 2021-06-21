package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.salesforce.SalesforceBearerTokenInfoDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.salesforce.DigitalOnboardingSalesforceRecordDTO;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;

@Component
public class SalesforceAPIClient {

    private final Logger log = LoggerFactory.getLogger(SalesforceAPIClient.class);

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Value("${SALESFORCE_INSTANCE_URL:None}")
    private String salesforceInstanceUrl;

    private final String authenticationEndpoint = "https://login.salesforce.com/services/oauth2/token";

    // Setup details for keystore
    @Value("${PATH_TO_JAVA_KEYSTORE_FILE:None}")
    private String pathToKeystoreFile;

    @Value("${JAVA_KEYSTORE_NAME:None}")
    private String keystoreName;

    @Value("${JAVA_KEYSTORE_PASSWORD:None}")
    private String keystorePassword;

    @Value("${SALESFORCE_PRIVATE_KEY_ALIAS_IN_JAVA_KEYSTORE:None}")
    private String salesforcePrivateKeyAlias;

    // Setup the details for the JWT
    @Value("${SALESFORCE_CONNECTED_APP_CONSUMER_KEY:None}")
    private String connectedAppConsumerKey;

    @Value("${SALESFORCE_USERNAME:None}")
    private String salesforceUsername;

    @Value("${SALESFORCE_OAUTH_AUDIENCE:None}")
    private String oAuthAudience;


    public Boolean sendUpdatesToSalesforce(DigitalOnboardingSalesforceRecordDTO digitalOnboardingSalesforceRecordDTO) {

        // TODO: Maybe implement caching?
        //  So we can reuse the same token for closely spaced request. Refactor this.
        //  Figure out a way to structure the code so we fetch a new token only if
        //  the previous token has expired. Figure out the necessary exceptions to watch for and catch

        // Generate OAuth JWT
        log.info("Generating OAuth JWT");
        String oAuthJWT = this.generateSalesforceOAuthJWT();

        // Get API key/bearer token from salesforce
        log.info("Using OAuth JWT to fetch API Bearer token from Salesforce");
        String apiBearerToken = getSalesforceBearerToken(oAuthJWT).getAccess_token();

        // Send update to salesforce
        log.info("Sending updates to Salesforce");
        // if an error occurs here, maybe due to a bad token, make sure to fetch a new token and retry the request

        Boolean updateWasSentSuccessfully = sendPatchRequestToSalesforce(
                digitalOnboardingSalesforceRecordDTO,
                apiBearerToken
        );

        return Boolean.TRUE ? updateWasSentSuccessfully : Boolean.FALSE;

    }

    /**
     * This method assumes that you currently have a keystore (.jks) file present in
     * the location specified by the pathToKeystoreFile variable. Also, the keystore file should
     * contain a private key and its corresponding certificate (public key) should have been
     * uploaded to the connected app on salesforce.
     **/
    private String generateSalesforceOAuthJWT() throws IllegalStateException  {


        try {
            // Create a keystore variable
            KeyStore keystore = KeyStore.getInstance("JKS");

            String fullPathToJavaKeystore = pathToKeystoreFile + keystoreName;

            // Load the keystore
            keystore.load(new FileInputStream(fullPathToJavaKeystore), keystorePassword.toCharArray());

            if (keystore.containsAlias(salesforcePrivateKeyAlias)) {
                log.info("The salesforce private key is contained in the keystore");
                log.info("Proceeding to create the JWT and sign it with the private key");
            } else {
                throw new IllegalStateException("The private key is missing from the key store");
            }

            // Get the private key from the keystore
            PrivateKey privateKey = (PrivateKey) keystore.getKey(salesforcePrivateKeyAlias,
                    keystorePassword.toCharArray());


            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + 300;
            Date exp = new Date(expMillis);

            // Create the JWT
            JwtBuilder builder;
            builder = Jwts.builder()
                    .setIssuer(connectedAppConsumerKey)
                    .setSubject(salesforceUsername)
                    .setAudience(oAuthAudience)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.RS256, privateKey);

            String jwtString = builder.compact();

            return jwtString;

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | UnrecoverableKeyException
                | IllegalStateException e) {

            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());

            throw new IllegalStateException("Unable generate JWT at this point. Please try again later");
        }
    }

    private SalesforceBearerTokenInfoDTO getSalesforceBearerToken(String oAuthJWT)
            throws IllegalStateException {

        // Setup request URL and body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        requestBody.add("assertion", oAuthJWT);

        // Setup request header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // Get Bearer token from salesforce
        ResponseEntity<SalesforceBearerTokenInfoDTO> response;

        try {
            response = restTemplate.postForEntity(
                    authenticationEndpoint, request, SalesforceBearerTokenInfoDTO.class);

        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Unable to get Bearer token from Salesforce. Please try again later");
        }

        // Check that the response was valid and return the token information
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("API Bearer token successfully received from Salesforce OAuth authentication endpoint");

            SalesforceBearerTokenInfoDTO bearerTokenInfo = response.getBody();

            return bearerTokenInfo;

        } else {
            log.error("Unable to get Bearer token from Salesforce. Please try again later");
            log.error("Response code: " + response.getStatusCode().toString());

            throw new IllegalStateException("Unable to get Bearer token from Salesforce. Please try again later");
        }
    }

    private Boolean sendPatchRequestToSalesforce(
            DigitalOnboardingSalesforceRecordDTO digitalOnboardingSalesforceRecordDTO,
            @NotNull String apiKey) {

        String baseURL = String.format("%s/services/data/v51.0/sobjects/",
                salesforceInstanceUrl);

        String salesforceRecordURI = baseURL + digitalOnboardingSalesforceRecordDTO.getRecordEndpoint();

        // Setup request Body
        HashMap<String, Object> requestBody = digitalOnboardingSalesforceRecordDTO.getNewValuesForFields();

        // Setup request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final String bearerToken = "Bearer " + apiKey;

        headers.add("Authorization", bearerToken);

        // Setup request entity
        final HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try{
            // Send the request to salesforce
            ResponseEntity<String> response = restTemplate.exchange(
                    salesforceRecordURI,
                    HttpMethod.PATCH,
                    requestEntity,
                    String.class);

            // If the sync was successful, return true
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // The salesforce v51 API returns 204 (No content) after a successful patch request

                String message = "Updates were successfully sent to Salesforce";

                log.info(message);

                return Boolean.TRUE;
            }

        }catch (Exception e) {
            log.error(e.getMessage());
        }

        String message = "Could not send updates to salesforce";
        log.error(message);

        return Boolean.FALSE;
    }
}
