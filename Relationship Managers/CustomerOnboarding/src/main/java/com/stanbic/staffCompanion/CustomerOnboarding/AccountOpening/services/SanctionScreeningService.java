package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.salesforce.DigitalOnboardingSalesforceRecordDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.SanctionScreeningBotQueueEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.SanctionScreeningResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.SanctionScreeningException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.SanctionScreeningBotQueueRepository;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.SanctionScreeningResultRepository;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.SalesforceAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;

@Service
public class SanctionScreeningService {
    private Logger log = LoggerFactory.getLogger(SanctionScreeningService.class);

    @Autowired
    private SanctionScreeningBotQueueRepository sanctionScreeningBotQueue;

    @Autowired
    private SanctionScreeningResultRepository sanctionScreeningResultRepo;

    @Autowired
    private SalesforceAPIClient salesforceAPIClient;

    @Value("${SALESFORCE_DIGITAL_ONBOARDING_CUSTOM_OBJECT:None}")
    private String salesforceDigitalOnboardingCustomObject;

    @Value("${SALESFORCE_OBJECT_FIELD_TO_TRIGGER_SANCTION_SCREENING_APPROVAL_PROCESS:None}")
    private String salesforceObjectFieldToTriggerApprovalProcess;

    @Value("${SALESFORCE_OBJECT_EXTERNAL_ID_AKA_ACCOUNT_OPENING_REQUEST_ID_FIELD:None}")
    private String salesforceObjectExternalID;

    public void addSanctionScreeningRequestToBotQueue(SanctionScreeningRequest sanctionScreeningRequest)
            throws SanctionScreeningException {

        log.info("About to log customer for sanction screening ");

        try{
            SanctionScreeningBotQueueEntity sanctionScreeningTask;
            sanctionScreeningTask = new SanctionScreeningBotQueueEntity(
                    sanctionScreeningRequest.getAccountOpeningRequest(),
                    sanctionScreeningRequest.getCustomerFirstName(),
                    sanctionScreeningRequest.getCustomerLastName(),
                    sanctionScreeningRequest.getCustomerFullHomeAddress(),
                    sanctionScreeningRequest.getCustomerBVN()
            );

            sanctionScreeningBotQueue.save(sanctionScreeningTask);

            log.info("Successfully logged for sanction screening");

        } catch (Exception e){
            log.error(e.getMessage());

            throw new SanctionScreeningException("Unable to log sanction screening request");

        }
    }

    public void processSanctionScreeningResult(AccountOpeningRequestEntity AORequestEntity,
                                               SanctionScreeningResult sanctionScreeningResult)
            throws SanctionScreeningException {

        log.info("Processing sanction screening result");

        // Validate base64 screenshot image
        this.validateBase64Image(sanctionScreeningResult
                .getSanctionScreeningResultPDFBase64());

        // Store the results in the Database
        this.storeSanctionScreeningResultInDB(sanctionScreeningResult, AORequestEntity);

        // If there are violations, send the result to compliance to review
        Boolean violationExists = !sanctionScreeningResult.getSanctionScreeningPassed();

        if (violationExists){
            this.requestApprovalFromCompliance(sanctionScreeningResult);
        }

        /**
         *
         * Validate BVN, Sanction Screening, ** Collect Data, ** Collect Docs, ** Open account.
         *
         * **/

        // TODO: Create a worker that tracks if everything is in place.
        //  If it is, place on a queue for the account & CIF to be created
        //  Queues: create CIF, create account number, notify customer, notify rm, notify branch
        //
        //  Worker1:  Monitor to see if everything is in place to open an account
        //              If in place, place item on queue for CIF generation
        //              Then CIF generation guy, does his work and places the item on queue for account number generation

        // TODO: Open account
    }

    private void validateBase64Image(String sanctionScreeningResultPDFBase64)
            throws SanctionScreeningException {

        // Ensure the base 64 is valid
        try{
            log.info("Ensuring the screenshot received is a valid base64 string");
            byte[] resultScreenshotBytes;

            resultScreenshotBytes = Base64.getDecoder().decode(sanctionScreeningResultPDFBase64);
            log.info("Screenshot is a valid base64 string");

            // TODO: Add logic to validate that it's a PNG or sth

        } catch (IllegalArgumentException e){

            String errorMessage = "Sanction Screening result PDF is an invalid base64 string";

            log.error(e.getMessage());
            log.error(errorMessage);

            SanctionScreeningException exception = new SanctionScreeningException(
                    errorMessage);
            exception.setDueToBadResultImage(Boolean.TRUE);

            throw exception;
        }
    }


    private void storeSanctionScreeningResultInDB(
            SanctionScreeningResult sanctionScreeningResult,
            AccountOpeningRequestEntity AORequestEntity) throws SanctionScreeningException {

        try {
            log.info("Storing sanction screening result in database");

            SanctionScreeningResultEntity sanctionScreeningResultEntity;
            sanctionScreeningResultEntity = new SanctionScreeningResultEntity(
                    AORequestEntity,
                    sanctionScreeningResult.getCustomerBVN(),
                    sanctionScreeningResult.getSanctionScreeningResultPDFBase64(),
                    sanctionScreeningResult.getSanctionScreeningPassed()
            );

            sanctionScreeningResultRepo.save(sanctionScreeningResultEntity);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SanctionScreeningException("Unable to store sanction screening results in DB");
        }
    }


    private void requestApprovalFromCompliance(SanctionScreeningResult sanctionScreeningResult) {

        // Request for compliance review on salesforce
        // This request is made by updating the Digital Onboarding Record on Salesforce.
        // The digital onboarding record to be updated is

        HashMap<String, Object> newValuesForFields = new HashMap<>();
        newValuesForFields.put("requiresComplianceReview__c", true);

        DigitalOnboardingSalesforceRecordDTO newRecordData;
        newRecordData = new DigitalOnboardingSalesforceRecordDTO(
                salesforceDigitalOnboardingCustomObject,
                "accountOpeningRequestId__c",
                sanctionScreeningResult.getAccountOpeningRequestID(),
                newValuesForFields
        );

        // TODO: What if this operation fails? Add some retry logic here or in the salesforce api client
        salesforceAPIClient.sendUpdatesToSalesforce(newRecordData);
    }
}
