package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.controllers;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.BVNPhotoValidationRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.IDValidationRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.NewBankAccountRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload.*;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.AccountOpeningRequestData;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.CustomerInfo;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.IDValidationResultDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.SanctionScreeningComplianceFeedback;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.StandardAPIResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.CustomerMandateDocTypeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.AccountOpeningServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.SanctionScreeningException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services.AccountOpeningRequestMgmtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@CrossOrigin
@RestController
@RequestMapping(path = "/account-opening-requests")
@Api(value = "Account Opening (Digital Onboarding)", tags = "Account Opening (Digital Onboarding)")
@ApiResponses(value = {
        @ApiResponse(response = StandardAPIResponse.class,
                code = 400, message = "Bad request. Please double check your request payload"),
        @ApiResponse(response = StandardAPIResponse.class,
                code = 404, message = "Not found. Whatever you're trying to access does not exist on the server"),
        @ApiResponse(response = StandardAPIResponse.class,
                code = 409, message = "Conflict/Duplicate entity"),
        @ApiResponse(response = StandardAPIResponse.class,
                code = 500, message = "Internal Server Error. Something is broken on the backend"),
        @ApiResponse(response = StandardAPIResponse.class,
                code = 503, message = "One of the backend services is currently unavailable")
})
public class AccountOpeningRequestController {

    @Autowired
    private AccountOpeningRequestMgmtService accountOpeningRequestMgmtService;

    private final Logger log = LoggerFactory.getLogger(AccountOpeningRequestController.class);


    @GetMapping
    @ApiOperation("Get list of account opening requests initiated by a Relationship Manager")
    public ResponseEntity<ArrayList<AccountOpeningRequestData>> getListOfAccounts(
            HttpServletRequest request,
            @NotBlank @RequestParam(name = "relationship-manager-SAP-Id") String RMSapId
    ) throws AccountOpeningServiceException {
        log.info("Received a request to fetch all accounts opened by an RM");

        ArrayList<AccountOpeningRequestData> results = accountOpeningRequestMgmtService
                .getAccountOpeningRequests(RMSapId);

        log.info("Returning API response to client");

        return new ResponseEntity(results, HttpStatus.OK);
    }


    // TODO: Use the BVN to check that a similar process isn't in progress for the same BVN
    // TODO: Match based on BVN & Account Type, if yes, ask them to continue the AO process
    @ApiOperation("Initiate an Account Opening Request")
    @PostMapping
    public ResponseEntity<AccountOpeningRequestData> newAccountRequest(
            HttpServletRequest request,
            @Valid @RequestBody NewBankAccountRequest newBankAccountRequest)
            throws AccountOpeningServiceException {

        log.info("Received a request to open a new Savings account");

        AccountOpeningRequestData result = accountOpeningRequestMgmtService
                .startAccountOpeningFlow(newBankAccountRequest);

        log.info("Returning API response to client");

        return new ResponseEntity(result, HttpStatus.OK);
    }

    /** Confirm that the customer matches the picture in the BVN **/
    @PostMapping(path = "/{request-id}/verify-customer-bvn-photo")
    @ApiOperation("Confirm that customer matches the photo on their BVN profile")
    public ResponseEntity<StandardAPIResponse> test(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody BVNPhotoValidationRequest bvnPhotoValidationRequest
            ) throws AccountOpeningServiceException {

        log.info("Received a request to confirm that the customer matches the photo on their BVN profile");

        String message = this.accountOpeningRequestMgmtService
                .confirmCustomerMatchesBVNProfile(
                        accountOpeningRequestID,
                        bvnPhotoValidationRequest);

        StandardAPIResponse response = new StandardAPIResponse(
                message,
                HttpStatus.OK,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity(response, HttpStatus.OK);
    }


    @PostMapping(path = "/{request-id}/verify-customer-identity")
    @ApiOperation("Validate a customer's identity")
    public ResponseEntity<IDValidationResultDTO> idValidation(
            HttpServletRequest request,
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody IDValidationRequest idValidationRequest) throws AccountOpeningServiceException {

        log.info("Received a request to validate a customer's identity");

        IDValidationResultDTO idValidationResult;
        idValidationResult = accountOpeningRequestMgmtService.validateCustomerIdentity(
                accountOpeningRequestID,
                idValidationRequest
        );

        log.info("Returning API response to client");

        return new ResponseEntity(idValidationResult, HttpStatus.OK);
    }

    
    @GetMapping(path = "/{request-id}/customer-data")
    @ApiOperation("Retrieve the customer data associated with an account opening request")
    public ResponseEntity<CustomerInfo> getCustomerInformation(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID
    ) throws AccountOpeningServiceException {

        log.info("Received a request to fetch customer information associated" +
                " with and account opening request");

        CustomerInfo customerInformation;
        customerInformation = this.accountOpeningRequestMgmtService
                .getCustomerInfo(accountOpeningRequestID);

        log.info("Returning API response to client");

        return new ResponseEntity(customerInformation, HttpStatus.OK);
    }

    
    @PostMapping(path = "/{request-id}/customer-data/personal-info")
    @ApiOperation("Upload the customer's personal information")
    public ResponseEntity<StandardAPIResponse> uploadCustomerPersonalInfo(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerPersonalInfo customerPersonalInfo
    ) throws AccountOpeningServiceException {

        log.info("Received a request to store a customer's personal information");

        this.accountOpeningRequestMgmtService.storeCustomerPersonalInfo(
                    accountOpeningRequestID,customerPersonalInfo);

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's personal information was saved successfully", 
                HttpStatus.OK, 
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    
    @PostMapping(path = "/{request-id}/customer-data/contact-info")
    @ApiOperation("Upload the customer's contact information")
    public ResponseEntity<StandardAPIResponse> uploadCustomerContactInfo(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerContactInfo customerContactInfo
    ) throws AccountOpeningServiceException {
        log.info("Received a request to store a customer's contact information");

        this.accountOpeningRequestMgmtService.storeCustomerContactInfo(
                    accountOpeningRequestID, customerContactInfo);

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's contact information was saved successfully", 
                HttpStatus.OK, 
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    
    @PostMapping(path = "/{request-id}/customer-data/next-of-kin-info")
    @ApiOperation("Upload the customer's next of kin information")
    public ResponseEntity<StandardAPIResponse> uploadCustomerNOKInfo(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerNOKInfo customerNOKInfo
    ) throws AccountOpeningServiceException {
        log.info("Received a request to store a customer's next of kin information");

        this.accountOpeningRequestMgmtService.storeCustomerNOKInfo(
                accountOpeningRequestID, customerNOKInfo);

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's next of kin information was saved successfully", 
                HttpStatus.OK, 
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    @PostMapping(path = "/{request-id}/customer-data/other-info")
    @ApiOperation("Upload other information about the customer")
    public ResponseEntity<StandardAPIResponse> uploadCustomerOtherInfo(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerOtherInfo customerOtherInfo
            ) throws AccountOpeningServiceException {
        log.info("Received a request to store a customer's other information");

        this.accountOpeningRequestMgmtService.storeOtherCustomerInfo(accountOpeningRequestID, customerOtherInfo);

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's other information have been saved successfully",
                HttpStatus.OK,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    
    @PostMapping(path = "/{request-id}/customer-mandate/passport-photo")
    @ApiOperation("Upload the customer's passport photograph")
    public ResponseEntity<StandardAPIResponse> customerMandatePhotoUpload(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerMandateInfo customerMandateInfo) throws AccountOpeningServiceException {
        log.info("Received a request to store a customer's passport photo");

        customerMandateInfo.setDocumentType(
                CustomerMandateDocTypeEnum.CUSTOMER_PHOTO);

        this.accountOpeningRequestMgmtService.storeCustomerMandateInfo(
                accountOpeningRequestID,
                customerMandateInfo
        );

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's passport photograph was saved successfully",
                HttpStatus.OK, 
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");
        
        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    
    @PostMapping(path = "/{request-id}/customer-mandate/signature")
    @ApiOperation("Upload the customer's signature")
    public ResponseEntity<StandardAPIResponse> customerMandateSignatureUpload(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerMandateInfo customerMandateInfo) throws AccountOpeningServiceException {

        log.info("Received a request to store a customer's signature");

        customerMandateInfo.setDocumentType(
                CustomerMandateDocTypeEnum.CUSTOMER_SIGNATURE);

        this.accountOpeningRequestMgmtService.storeCustomerMandateInfo(
                accountOpeningRequestID,
                customerMandateInfo
        );

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's signature was saved successfully",
                HttpStatus.OK, 
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    @PostMapping(path = "/{request-id}/customer-mandate/other-document")
    @ApiOperation("Upload the customer's other document")
    public ResponseEntity<StandardAPIResponse> customerMandateAdditionalUpload(
            @Valid @PathVariable(name = "request-id") String accountOpeningRequestID,
            @Valid @RequestBody CustomerMandateInfo customerMandateInfo) throws AccountOpeningServiceException {

        log.info("Received a request to store a customer's signature");

        customerMandateInfo.setDocumentType(
                CustomerMandateDocTypeEnum.CUSTOMER_OTHER_DOCUMENT);

        this.accountOpeningRequestMgmtService.storeCustomerMandateInfo(
                accountOpeningRequestID,
                customerMandateInfo
        );

        StandardAPIResponse response = new StandardAPIResponse(
                "Customer's signature was saved successfully",
                HttpStatus.OK,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<StandardAPIResponse>(
                response, HttpStatus.OK);
    }

    
    /* How do we setup authentication so Salesforce & the BOT can use the API? */
    // This endpoint should be accessible only by the sanction screening bot
    @PostMapping(path = "/sanction-screening/result-data")
    @ApiOperation("Upload the customer's sanction screening results. (To be used by the sanction screening bot)")
    public ResponseEntity<StandardAPIResponse> storeSanctionScreeningResults(
            @Valid @RequestBody SanctionScreeningResult sanctionScreeningResult)
            throws SanctionScreeningException, AccountOpeningServiceException {

        log.info("Received sanction screening result");

        this.accountOpeningRequestMgmtService.processSanctionScreeningResult(sanctionScreeningResult);

        StandardAPIResponse response = new StandardAPIResponse(
                "Sanction screening results have been received. Thank you.",
                HttpStatus.OK,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<>(
                response, HttpStatus.OK);
    }

    
    @PostMapping(path = "/sanction-screening/compliance-review-feedback")
    @ApiOperation("Upload the feedback from the sanction screening review done by Compliance")
    public ResponseEntity<StandardAPIResponse> sendComplianceReviewFeedback(
            @Valid @RequestBody SanctionScreeningComplianceFeedback complianceFeedback)
            throws AccountOpeningServiceException {

        log.info("Received review of sanction screening results from compliance");

        this.accountOpeningRequestMgmtService.processSanctionScreeningReview(complianceFeedback);

        StandardAPIResponse response = new StandardAPIResponse(
                "Compliance review of the sanction screening results have been received. Thank you.",
                HttpStatus.OK,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        log.info("Returning API response to client");

        return new ResponseEntity<>(
                response, HttpStatus.OK);

    }
}
