package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.IDValidationRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.IDValidationResultDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.BVNValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.IDValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.IDValidationException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.IDValidationResultRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class IDValidationService {

    @Value("${SMILE_IDENTITY_ID_VALIDATION_SERVICE_ENDPOINT:None}")
    private String IDValidationServiceEndpoint;

    @Value("${SMILE_IDENTITY_ID_VALIDATION_SERVICE_MODULE_ID:None}")
    private String smileIdentityModuleID;

    @Value("${SMILE_IDENTITY_ID_VALIDATION_SERVICE_CHANNEL:None}")
    private String smileIdentityChannel;

    private final Logger log = LoggerFactory.getLogger(IDValidationService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private IDValidationResultRepository idValidationResultRepository;

    /** Validate a customer's identity **/
    public IDValidationResultDTO validateCustomerIdentity(IDValidationRequest idValidationRequest)
            throws IDValidationException {

        JSONObject smileIdentityResponse;
        smileIdentityResponse = this.callSmileIdentityService(
                idValidationRequest);

        // if ID is not valid, raise an exception
        JSONObject idValidationActions = smileIdentityResponse.getJSONObject("Actions");
        Boolean idIsValid = idValidationActions.
                getString("Return_Personal_Info")
                .equalsIgnoreCase("Not Done") ? Boolean.FALSE : Boolean.TRUE;

        if (!idIsValid) {
            IDValidationException exception;
            exception = new IDValidationException(
                    "Unable to validate customer identity. ID number is invalid");

            throw exception;
        }

        // if ID is valid extract and return the validated ID info
        IDValidationResultDTO IDValidationResultDTO;
        IDValidationResultDTO = this.extractCustomerIDInfo(smileIdentityResponse);

        return IDValidationResultDTO;
    }

    /** Send an ID validation request to the Smile Identity Service **/
    private JSONObject callSmileIdentityService(
            IDValidationRequest idValidationRequest){

        // Add channel and Module ID to request
        idValidationRequest.setChannel(smileIdentityChannel);
        idValidationRequest.setModuleId(smileIdentityModuleID);

        // Setup request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send post request to Smile ID
        final HttpEntity<IDValidationRequest> requestEntity = new HttpEntity<>(idValidationRequest, headers);
        ResponseEntity<String> responseEntity;

        try{
            log.info("Sending ID validation request to Smile Identity");

            responseEntity = restTemplate.postForEntity(
                    IDValidationServiceEndpoint,
                    requestEntity,
                    String.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                // TODO: FIX THE LOGGING FORMAT
                //  Log error and raise an exception
                throw new IDValidationException("Unable to validate customer Identity");
            }

            JSONObject responseBody = new JSONObject(responseEntity.getBody());

            return responseBody;

        }catch (Exception e){
            log.error(e.getMessage());

            // TODO: Update this exception
            throw new IllegalStateException("Could not make http request to validate customer ID");
        }
    }

    /** Extract the customer information from the ID validation response **/
    private IDValidationResultDTO extractCustomerIDInfo(
            JSONObject smileIdentityResponse) throws IDValidationException {

        IDValidationResultDTO IDValidationResultDTO;

        LocalDate customerDOB;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd");
            customerDOB = LocalDate.parse(
                    smileIdentityResponse.getString("DOB"), dateTimeFormatter);

        } catch (DateTimeParseException e) {
            throw new IDValidationException(
                    "Unable to parse customer's date of birth. Received '"
                            +smileIdentityResponse.getString("DOB")+"' from ID validation API");
        }

        LocalDate idExpiryDate = null;
        String expiryDateFromAPIResponse = smileIdentityResponse.getString("ExpirationDate");

        if (!expiryDateFromAPIResponse.equalsIgnoreCase("Not Available")){
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd");
                idExpiryDate = LocalDate.parse(
                        smileIdentityResponse.getString("ExpirationDate"), dateTimeFormatter);

            } catch (DateTimeParseException e) {
                throw new IDValidationException(
                        "Unable to parse the expiration date of the customer's ID. Received '"
                                +smileIdentityResponse.getString("ExpirationDate")+"' from ID validation API");
            }

            // TODO: Check that the ID will still be valid X days from now
        }

        JSONObject customerIDFullData = smileIdentityResponse.getJSONObject("FullData");

        IDValidationResultDTO = new IDValidationResultDTO(
                smileIdentityResponse.getString("Country"),
                smileIdentityResponse.getString("IDType"),
                smileIdentityResponse.getString("IDNumber"),
                idExpiryDate,
                customerDOB,
                customerIDFullData.getString("firstname"),
                customerIDFullData.getString("pmiddlename"),
                customerIDFullData.getString("surname"),
                smileIdentityResponse.getString("FullName"),
                customerIDFullData.getString("title"),
                smileIdentityResponse.getString("PhoneNumber"),
                smileIdentityResponse.getString("Photo"),
                customerIDFullData.getString("gender"),
                smileIdentityResponse.getString("SmileJobID")
                );

        IDValidationResultDTO.setExpiryDate(idExpiryDate);

        return IDValidationResultDTO;
    }

    /** Save the ID Validation result to the database **/
    public IDValidationResultEntity saveIDValidationResult(
            IDValidationResultDTO customerIdentityInformation,
            Boolean idDetailsMatchBVNInformation,
            AccountOpeningRequestEntity AORequestEntity){

        IDValidationResultEntity idValidationResultEntity;
        idValidationResultEntity = new IDValidationResultEntity(
                customerIdentityInformation.getIdType(),
                customerIdentityInformation.getIdNumber(),
                customerIdentityInformation.getExpiryDate(),
                customerIdentityInformation.getFullName(),
                customerIdentityInformation.getFirstName(),
                customerIdentityInformation.getMiddleName(),
                customerIdentityInformation.getLastName(),
                customerIdentityInformation.getDob(),
                customerIdentityInformation.getGender(),
                customerIdentityInformation.getCustomerPhoto(),
                customerIdentityInformation.getSmileIdentityJobId(),
                idDetailsMatchBVNInformation,
                AORequestEntity
        );

        idValidationResultRepository.save(idValidationResultEntity);

        return idValidationResultEntity;
    }

    /** Confirm that the ID Validation result matches the customer's BVN information **/
    public Boolean idDetailsMatchBVNDetails(
            IDValidationResultDTO IDValidationResultDTO,
            BVNValidationResultEntity bvnValidationResultEntity){

        Boolean firstNamesMatch = IDValidationResultDTO
                .getFirstName().equalsIgnoreCase(bvnValidationResultEntity.getFirstName());

        Boolean lastNamesMatch = IDValidationResultDTO
                .getLastName().equalsIgnoreCase(bvnValidationResultEntity.getLastName());

        // TODO: Validate that the middle names match. If no middle name, that can be skipped

        // TODO: Update this to match only on the dates & not the full datetime
        Integer datesOfBirthMatch = IDValidationResultDTO
                .getDob().compareTo(bvnValidationResultEntity.getDateOfBirth());

        return firstNamesMatch && lastNamesMatch && (datesOfBirthMatch == 0);
    }
}
