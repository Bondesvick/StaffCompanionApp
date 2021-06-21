package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.BVNInformationDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.bvnValidationService.RedboxBVNValidationResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.BVNValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.BVNValidationServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.BVNValidationResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Service
public class BvnValidationService {

    private final Logger log = LoggerFactory.getLogger(BvnValidationService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    BVNValidationResultRepository bvnValidationResultRepository;

    @Value("${REDBOX_BVN_VALIDATION_SERVICE_ENDPOINT:None}")
    private String bvnValidationEndpoint;

    @Value("${REDBOX_REQUEST_MANAGER_SERVICE_HEADER__MODULE_ID:None}")
    private String redboxModuleID;

    /** Validate a customer's BVN **/
    public BVNInformationDTO validateBVN(String bvn, LocalDate customerDateOfBirth)
            throws BVNValidationServiceException {

        RedboxBVNValidationResponse bvnValidationResponse = this.callRedboxBVNValidationService(bvn);

        String responseCode = bvnValidationResponse.getResponseCode();

        switch (responseCode){
            case "00":
                // Validation successful
                // Map the response to a BVN information object and return it
                LocalDate bvnDOB;

                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                    bvnDOB = LocalDate.parse(
                            bvnValidationResponse.getDateOfBirth(), dateTimeFormatter);

                } catch (DateTimeParseException e) {
                    throw new BVNValidationServiceException("Unable to parse customer's date of birth. Received '"
                            +bvnValidationResponse.getDateOfBirth()+"' from BVN validation API");
                }

                if (!customerDateOfBirth.isEqual(bvnDOB)){
                    BVNValidationServiceException exception;
                    exception = new BVNValidationServiceException("Customer DOB does not match DOB on BVN");
                    exception.setIsDueToBadRequest(Boolean.TRUE);
                    throw exception;
                }

                BVNInformationDTO bvnInformationDTO;
                bvnInformationDTO = new BVNInformationDTO(
                        bvnValidationResponse.getBVN(),
                        bvnValidationResponse.getFirstName(),
                        bvnValidationResponse.getMiddleName(),
                        bvnValidationResponse.getLastName(),
                        bvnDOB,
                        bvnValidationResponse.getWatchListed()
                                .equalsIgnoreCase("NO") ? Boolean.FALSE : Boolean.TRUE,
                        bvnValidationResponse.getEmail(),
                        bvnValidationResponse.getGender(),
                        bvnValidationResponse.getPhoneNumber2(),
                        bvnValidationResponse.getNIN(),
                        bvnValidationResponse.getNameOnCard(),
                        bvnValidationResponse.getNationality(),
                        bvnValidationResponse.getTitle(),
                        bvnValidationResponse.getCustomerPhotoBase64()
                );
                return bvnInformationDTO;

            case "01":
            case "02":
                // Searched BVN is invalid (01) or
                // BVN not found (02)
                log.info("Bad Request - "+bvnValidationResponse.getResponseMessage());
                throw new BVNValidationServiceException(
                        bvnValidationResponse.getResponseMessage(),
                        Boolean.TRUE
                );

            default:
                // Error
                throw new BVNValidationServiceException(
                        "Something went wrong. Could not validate BVN"
                );

        }
    }

    /** Send BVN validation request to Redbox **/
    private RedboxBVNValidationResponse callRedboxBVNValidationService(
            @NotNull @NotBlank @NotEmpty String bvn)
            throws BVNValidationServiceException {
        // Setup request body
        ArrayList<String> bvns = new ArrayList<>();
        bvns.add(bvn);

        HashMap<String, ArrayList> bvnRequest = new HashMap<>();
        bvnRequest.put("bvns", bvns);

        HashMap<String, HashMap> requestBody = new HashMap<>();
        requestBody.put("bvnRequest", bvnRequest);

        // Setup request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("module_id", redboxModuleID);

        // Send request
        final HttpEntity<HashMap<String, HashMap>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<RedboxBVNValidationResponse> responseEntity;

        try{
            log.info("Sending BVN validation request to Redbox");

            responseEntity = restTemplate.postForEntity(
                    bvnValidationEndpoint,
                    requestEntity,
                    RedboxBVNValidationResponse.class);

            RedboxBVNValidationResponse responseBody = responseEntity.getBody();
            return responseBody;

        }catch (Exception e){
            log.error(e.getMessage());

            BVNValidationServiceException exception;
            exception = new BVNValidationServiceException("Could not make HTTP request to validate BVN");
            exception.setIsDueToServiceOutage(Boolean.TRUE);
            throw exception;
        }
    }

    /** Save BVN validation result in the database **/
    public BVNValidationResultEntity saveBVNValidationResult(
            BVNInformationDTO bvnInformationDTO,
            AccountOpeningRequestEntity AORequestEntity)
            throws BVNValidationServiceException {

        BVNValidationResultEntity bvnValidationResultEntity;

        bvnValidationResultEntity = new BVNValidationResultEntity(
                bvnInformationDTO.getBVN(),
                bvnInformationDTO.getFirstName(),
                bvnInformationDTO.getMiddleName(),
                bvnInformationDTO.getLastName(),
                bvnInformationDTO.getDateOfBirth(),
                bvnInformationDTO.getWatchListed(),
                bvnInformationDTO.getEmail(),
                bvnInformationDTO.getGender(),
                bvnInformationDTO.getPhoneNumber(),
                bvnInformationDTO.getNIN(),
                bvnInformationDTO.getNameOnCard(),
                bvnInformationDTO.getNationality(),
                bvnInformationDTO.getTitle(),
                bvnInformationDTO.getCustomerPhotoBase64(),
                AORequestEntity
        );

        try{
            bvnValidationResultRepository.save(bvnValidationResultEntity);

            return bvnValidationResultEntity;

        }catch (Exception e){
            throw new BVNValidationServiceException("Unable to save BVN validation results to database");
        }
    }

    /** Get BVN validation result from the database **/
    public BVNValidationResultEntity getBVNValidationResults(
            AccountOpeningRequestEntity aoRequestEntity)
            throws BVNValidationServiceException {

        Optional<BVNValidationResultEntity> bvnValidationResult;
        bvnValidationResult = this.bvnValidationResultRepository
                .findByAccountOpeningRequestEquals(aoRequestEntity);

        if (bvnValidationResult.isPresent()){
            return bvnValidationResult.get();
        } else {
            BVNValidationServiceException exception;
            exception = new BVNValidationServiceException(
                    "Unable to fetch BVN validation results. Please try again");
            throw exception;
        }
    }


    public void validateBVN2FA(String requestID, String twoFactorToken){
        // Get the request ID
        // Query DB based on 2FA & requestID
        // If no result, return null
    }
}
