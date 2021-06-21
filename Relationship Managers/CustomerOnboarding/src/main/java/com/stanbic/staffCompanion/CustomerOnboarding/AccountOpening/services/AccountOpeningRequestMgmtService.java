package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.BVNPhotoValidationRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.DedupCheckRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.IDValidationRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.NewBankAccountRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload.*;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.AccountOpeningRequestData;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.CustomerInfo;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.BVNInformationDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.DedupCheckResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.IDValidationResultDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.SanctionScreeningComplianceFeedback;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.BVNValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.CustomerInformationEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.IDValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningOutcomeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningStatusEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.*;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.mappers.AccountOpeningRequestMapper;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.mappers.CustomerInfoMapper;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.AccountOpeningRequestRepository;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.CustomerInformationRepository;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

/** This service manages the account opening process **/
@Service
public class AccountOpeningRequestMgmtService {

    private final Logger log = LoggerFactory.getLogger(AccountOpeningRequestMgmtService.class);

    @Autowired
    private RedboxRequestManagerClient redboxRequestManagerClient;

    @Autowired
    private BvnValidationService bvnValidationService;

    @Autowired
    private IDValidationService idValidationService;

    @Autowired
    private DedupCheckService dedupCheckService;

    @Autowired
    private AddressVerificationService addressVerificationService;

    @Autowired
    private CustomerMandateStorageService mandateStorageService;

    @Autowired
    private SanctionScreeningService sanctionScreeningService;

    @Autowired
    private AccountOpeningRequestRepository accountOpeningRequestRepository;

    @Autowired
    private CustomerInformationRepository customerInformationRepository;

    @Autowired
    private AccountOpeningRequestMapper AORequestMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    private AccountOpeningRequestEntity getAccountOpeningRequestEntity(String requestID)
            throws AccountOpeningServiceException {

        // Fetch request entity from DB
        Optional<AccountOpeningRequestEntity> AORequestEntity;
        AORequestEntity = this.accountOpeningRequestRepository
                .findByAccountOpeningRequestIdAndOnboardingHasBeenTerminated(requestID, Boolean.FALSE);

        if (!AORequestEntity.isPresent()){
            AccountOpeningServiceException exception = new AccountOpeningServiceException(
                    "No account opening request was found for this ID. " +
                            "Either the ID is invalid, or the account opening request " +
                            "associated with the provided ID has been terminated.");
            exception.setIsDueToInvalidRequestID(Boolean.TRUE);
            throw exception;
        }

        return AORequestEntity.get();
    }

//    private AccountOpeningRequestEntity getNonTerminatedAccountOpeningRequest(
//            String requestID) throws AccountOpeningServiceException {
//
//        try {
//            AccountOpeningRequestEntity AORequestEntity;
//            AORequestEntity = this.accountOpeningRequestRepository
//                    .findByRequestIdAndOnboardingTerminationStatus(
//                            requestID,
//                            Boolean.FALSE
//                    ).get(0);
//            return AORequestEntity;
//
//        } catch (Exception e) {
//            AccountOpeningServiceException exception = new AccountOpeningServiceException("Invalid Request ID");
//            exception.setIsDueToInvalidRequestID(Boolean.TRUE);
//            throw exception;
//        }
//
//
//    }

    /**
     * Create a new database entity to track a new request for a savings account.
     * This is done after the customer's BVN is validated,
     **/
    public AccountOpeningRequestData startAccountOpeningFlow(NewBankAccountRequest newBankAccountRequest)
            throws AccountOpeningServiceException {

        LocalDate customerDOB = newBankAccountRequest.getCustomerDOB().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        log.info("customerDOB: "+customerDOB);

        try {
            // validate BVN
            BVNInformationDTO bvnInformationDTO;
            bvnInformationDTO = bvnValidationService.validateBVN(
                    newBankAccountRequest.getCustomerBVN(),
                    newBankAccountRequest.getCustomerDOB().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate());
            log.info("BVN Validation complete");

            // Ensure the customer does not have a duplicate account
            DedupCheckRequest dedupCheckRequest;
            dedupCheckRequest = new DedupCheckRequest(
                    bvnInformationDTO.getFirstName(),
                    bvnInformationDTO.getMiddleName(),
                    bvnInformationDTO.getLastName(),
                    newBankAccountRequest.getCustomerPhoneNumber(),
                    bvnInformationDTO.getDateOfBirth()
            );

            DedupCheckResult dedupCheckResult = this.dedupCheckService
                    .checkForDuplicateAccount(dedupCheckRequest);

            // Process dedup check results
            if (dedupCheckResult.getMatchExists()){
                AccountOpeningServiceException exception;
                exception = new AccountOpeningServiceException(
                        "Unable to open an account. Customer already has an account with the bank. \n" +
                                "Customer ID: "+dedupCheckResult.getFinacleCustomerId()
                );
                exception.setCustomerAlreadyExists(Boolean.TRUE);
                throw exception;
            }

            log.info("Dedup check passed. Proceeding to next step");

            // create an entity to track this account opening request
            AccountOpeningRequestEntity AORequestEntity;
            AORequestEntity = new AccountOpeningRequestEntity(
                    newBankAccountRequest.getCustomerBVN(),
                    newBankAccountRequest.getCustomerEmail(),
                    newBankAccountRequest.getCustomerPhoneNumber(),
                    newBankAccountRequest.getRelationshipManagerSAPId(),
                    newBankAccountRequest.getAccountSchemeCode(),
                    newBankAccountRequest.getAccountType()
            );

            AORequestEntity.setDedupCheckPassed(Boolean.TRUE);

            accountOpeningRequestRepository.save(AORequestEntity);

            // save result of BVN validation
            BVNValidationResultEntity  savedBVNValidationResult;
            savedBVNValidationResult = bvnValidationService.saveBVNValidationResult(
                    bvnInformationDTO,
                    AORequestEntity);

            // kick off sanction screening
            SanctionScreeningRequest sanctionScreeningRequest;
            sanctionScreeningRequest = new SanctionScreeningRequest(
                    bvnInformationDTO.getFirstName(),
                    bvnInformationDTO.getLastName(),
                    "",
                    bvnInformationDTO.getBVN(),
                    AORequestEntity
            );

            sanctionScreeningService.addSanctionScreeningRequestToBotQueue(sanctionScreeningRequest);

            // update the sanction screening status
            AORequestEntity.setSanctionScreeningStatus(SanctionScreeningStatusEnum.IN_PROGRESS);

            accountOpeningRequestRepository.save(AORequestEntity);

            log.info("Sanction screening has been kick-started.");

            // Map account opening request entity to DTO and return it
            AccountOpeningRequestData AORequestDTO;
            AORequestDTO = AORequestMapper.getAORequestData(AORequestEntity);
            AORequestDTO.setBvnInformation(bvnInformationDTO);

            return AORequestDTO;

        } catch (BVNValidationServiceException bvnException) {

            AccountOpeningServiceException exception = new AccountOpeningServiceException(bvnException.getMessage());

            if (bvnException.getIsDueToBadRequest()) {
                exception.setIsDueToBadRequest(Boolean.TRUE);
            } else if (bvnException.getIsDueToServiceOutage()){
                exception.setIsDueToServiceOutage(Boolean.TRUE);
            }

            throw exception;

        } catch (SanctionScreeningException exception){

            // TODO: Besides throwing this exception, what else should be done here?
            //  Consider rolling back all transactions made by this service

            throw new AccountOpeningServiceException("Unable to kickoff sanction screening");


        } catch (DedupCheckServiceException e) {

            AccountOpeningServiceException exception;
            exception = new AccountOpeningServiceException("Unable to complete dedup check");
            exception.setIsDueToServiceOutage(Boolean.TRUE);
            throw exception;
        }
    }


    /** Confirm that the customer matches the picture in the BVN **/
    public String confirmCustomerMatchesBVNProfile(
            String accountOpeningRequestID,
            BVNPhotoValidationRequest bvnPhotoValidationRequest
    ) throws AccountOpeningServiceException {

        AccountOpeningRequestEntity AORequestEntity;
        AORequestEntity = this.getAccountOpeningRequestEntity(accountOpeningRequestID);

        String message;

        // If customer does not match photo on BVN, terminate onboarding
        if (!bvnPhotoValidationRequest.getCustomerMatchesBVNPhoto()){

            message = "Onboarding terminated. Customer does not match the photo in the BVN validation results";

            AORequestEntity.setOnboardingHasBeenTerminated(Boolean.TRUE);
            AORequestEntity.setReasonForOnboardingTermination(
                    "Customer does not match the photo in the BVN validation results");
            this.accountOpeningRequestRepository.save(AORequestEntity);

            log.info(message);
            return message;
        }

        message = "Thank you for confirming that the customer matches the photo in the BVN";
        return message;
    }


    /** Validate the identity of the customer whom the account is being opened for
     * @return**/
    public IDValidationResultDTO validateCustomerIdentity(
            String accountOpeningRequestID,
            IDValidationRequest idValidationRequest) throws AccountOpeningServiceException {

        AccountOpeningRequestEntity AORequestEntity;
        AORequestEntity = getAccountOpeningRequestEntity(accountOpeningRequestID);

        // Get the customer's ID info
        IDValidationResultDTO IDValidationResultDTO;

        try {
            IDValidationResultDTO = this.idValidationService
                    .validateCustomerIdentity(idValidationRequest);

        } catch (IDValidationException e) {
            AccountOpeningServiceException exception;
            exception = new AccountOpeningServiceException(e.getMessage());

            if (e.getDueToInvalidID()){
                exception.setIsDueToIdentityValidationFailure(Boolean.TRUE);
            }

            throw exception;
        }

        // TODO: Compare the data on the BVN to the data on the ID
        //   Was unable to implement this because I couldn't get test data for it

        // Store the ID validation results in a database
        IDValidationResultEntity idValidationResultEntity;
        idValidationResultEntity = idValidationService.saveIDValidationResult(
                IDValidationResultDTO,
                null,
                AORequestEntity);


        return IDValidationResultDTO;
    }


    /** Fetch a list of account opening requests made by an Relationship Manager
     * @return**/
    public ArrayList<AccountOpeningRequestData> getAccountOpeningRequests(
            String relationshipManagerSAPId) throws AccountOpeningServiceException {

        try {
            // Get the list of requests
            ArrayList<AccountOpeningRequestEntity> listOfRequests;
            listOfRequests = accountOpeningRequestRepository
                    .findByRelationshipManagerSAPIdEquals(relationshipManagerSAPId);

            ArrayList<AccountOpeningRequestData> listOfRequestDTOs;
            listOfRequestDTOs = new ArrayList<>();


            // Map them to DTOs
            for (AccountOpeningRequestEntity AORequestEntity: listOfRequests) {
                AccountOpeningRequestData AORequestDTO;
                AORequestDTO = AORequestMapper.getAORequestData(AORequestEntity);
                listOfRequestDTOs.add(AORequestDTO);
            }

            return listOfRequestDTOs;

        }catch (Exception e){
            log.error(e.getMessage());
            throw new AccountOpeningServiceException("Unable to fetch list of account opening requests");
        }
    }


    /** Store a the customer's personal info required for the AO process **/
    public void storeCustomerPersonalInfo(String accountOpeningRequestID,
                                          CustomerPersonalInfo customerPersonalInfo)
            throws AccountOpeningServiceException {

        AccountOpeningRequestEntity AORequestEntity;
        AORequestEntity = this.getAccountOpeningRequestEntity(
                accountOpeningRequestID);

        try {
            CustomerInformationEntity customerInformationEntity;
            customerInformationEntity = new CustomerInformationEntity(
                    AORequestEntity,
                    customerPersonalInfo.getGender(),
                    customerPersonalInfo.getTitle(),
                    customerPersonalInfo.getMaritalStatus(),
                    customerPersonalInfo.getMothersMaidenName(),
                    customerPersonalInfo.getEmploymentStatus(),
                    customerPersonalInfo.getOccupation(),
                    customerPersonalInfo.getNatureOfBusiness(),
                    customerPersonalInfo.getSector(),
                    customerPersonalInfo.getMonthlyIncomeRange(),
                    customerPersonalInfo.getNationality(),
                    customerPersonalInfo.getLocalGovtArea(),
                    customerPersonalInfo.getPlaceOfBirth(),
                    customerPersonalInfo.getPurposeOfAccountOpening()
            );

            customerInformationRepository.save(customerInformationEntity);

            log.info("Stored customer info: "+customerInformationEntity.toString());

        }catch (Exception e){
            log.error(e.getMessage());
            throw new AccountOpeningServiceException(
                    "Unable to Store customer information. Please try again");
        }

    }


    /** Get the customer's information for a given AO request
     * @return**/
    public CustomerInfo getCustomerInfo(String accountOpeningRequestID)
            throws AccountOpeningServiceException {

        AccountOpeningRequestEntity AORequestEntity;
        AORequestEntity = this.getAccountOpeningRequestEntity(accountOpeningRequestID);
        
        Optional<CustomerInformationEntity> customerInformationEntity;
        customerInformationEntity = this.customerInformationRepository
                .findByAccountOpeningRequestEntityEquals(AORequestEntity);

        if (customerInformationEntity.isPresent()){
            log.info("Customer information: "+customerInformationEntity.get().toString());
            return this.customerInfoMapper.getCustomerInfo(customerInformationEntity.get());

        }else{
            AccountOpeningServiceException exception =  new AccountOpeningServiceException(
                    "No customer information found for this account opening request");

            exception.setCustomerInformationNotFound(Boolean.TRUE);
            throw exception;
        }

    }


    /** Store the customer's contact information required for the AO process **/
    @Transactional
    public void storeCustomerContactInfo(String accountOpeningRequestID,
                                         CustomerContactInfo customerContactInfo)
            throws AccountOpeningServiceException {

        Integer updateHappened = customerInformationRepository.updateContactInfo(
                customerContactInfo.getMobileNumber(),
                customerContactInfo.getEmailAddress(),
                customerContactInfo.getAddressLine1(),
                customerContactInfo.getAddressLine2(),
                customerContactInfo.getAddressLine3(),
                customerContactInfo.getLGAOfResidence(),
                customerContactInfo.getCityOfResidence(),
                customerContactInfo.getStateOfResidence(),
                customerContactInfo.getCountryCode(),
                customerContactInfo.getPostalCode(),
                customerContactInfo.getOccupancyStartDate(),
                customerContactInfo.getNearestLandmark(),
                customerContactInfo.getOtherAddressInfo(),
                customerContactInfo.getPreferredBranchSolID(),
                this.getAccountOpeningRequestEntity(accountOpeningRequestID)
        );

        // Tell the AOEntity that the customer's contact info has been received
    }


    /** Store the customer's NOK information required for the AO process **/
    @Transactional
    public void storeCustomerNOKInfo(String accountOpeningRequestID,
                                     CustomerNOKInfo customerNOKInfo)
            throws AccountOpeningServiceException {

        Integer updateHappened = customerInformationRepository.updateNOKInfo(
                customerNOKInfo.getNokLastName(),
                customerNOKInfo.getNokFirstName(),
                customerNOKInfo.getNokOtherNames(),
                customerNOKInfo.getNokMobileNumber(),
                customerNOKInfo.getNokEmailAddress(),
                customerNOKInfo.getNokRelationship(),
                customerNOKInfo.getNokDateOfBirth(),
                customerNOKInfo.getNokAddressStreetNumber(),
                customerNOKInfo.getNokAddressLandmark(),
                customerNOKInfo.getNokAddressStateOfResidence(),
                customerNOKInfo.getNokCityOfResidence(),
                this.getAccountOpeningRequestEntity(accountOpeningRequestID)
        );
    }


    /** Store other information needed, from the customer, for the AO process **/
    @Transactional
    public void storeOtherCustomerInfo(String accountOpeningRequestID,
                                       CustomerOtherInfo customerOtherInfo)
            throws AccountOpeningServiceException {

        Integer updateHappened = customerInformationRepository
                .updateCustomerOtherInfo(
                        customerOtherInfo.getHasHeldProminentPosition(),
                        customerOtherInfo.getIsPoliticallyExposed(),
                        customerOtherInfo.getNameOfPoliticalConnection(),
                        customerOtherInfo.getPositionHeld(),
                        customerOtherInfo.getRelationshipWithPerson(),
                        this.getAccountOpeningRequestEntity(accountOpeningRequestID)
                );
    }


    /** Store the customer's mandate information **/
    public void storeCustomerMandateInfo(
            String accountOpeningRequestID,
            CustomerMandateInfo customerMandateInfo)
            throws AccountOpeningServiceException {

        try {
            this.mandateStorageService.storeMandateFile(
                    customerMandateInfo,
                    this.getAccountOpeningRequestEntity(accountOpeningRequestID)
            );

        } catch (CustomerMandateStorageException e) {
            throw new AccountOpeningServiceException(e.getMessage());

        }
    }


    public void getCustomerMandateDocuments(String accountOpeningRequestID) throws AccountOpeningServiceException {
        this.mandateStorageService.getMandateDocuments(
                this.getAccountOpeningRequestEntity(accountOpeningRequestID)
        );
    }

    public void processSanctionScreeningResult(SanctionScreeningResult sanctionScreeningResult)
            throws SanctionScreeningException, AccountOpeningServiceException {

        log.info("Processing sanction screening result");

        AccountOpeningRequestEntity AORequestEntity;
        AORequestEntity = this.getAccountOpeningRequestEntity(
                sanctionScreeningResult.getAccountOpeningRequestID()
        );

        this.sanctionScreeningService.processSanctionScreeningResult(
                AORequestEntity,
                sanctionScreeningResult);

        if (sanctionScreeningResult.getSanctionScreeningPassed()){
            AORequestEntity.setSanctionScreeningStatus(SanctionScreeningStatusEnum.COMPLETED);
            AORequestEntity.setSanctionScreeningOutcome(SanctionScreeningOutcomeEnum.PASSED);
        } else {
            AORequestEntity.setSanctionScreeningStatus(
                    SanctionScreeningStatusEnum.IN_PROGRESS_AWAITING_COMPLIANCE_REVIEW);
        }

        accountOpeningRequestRepository.save(AORequestEntity);
    }

    @Transactional
    public void processSanctionScreeningReview(SanctionScreeningComplianceFeedback complianceFeedback)
            throws AccountOpeningServiceException {

        log.info("Processing Compliance Feedback");

        Boolean terminateOnboarding = !complianceFeedback.getContinueOnboarding();
        String reasonForTermination = complianceFeedback.getReasonForOnboardingTermination();

        SanctionScreeningStatusEnum sanctionScreeningStatus;
        SanctionScreeningOutcomeEnum sanctionScreeningOutcome;

        sanctionScreeningStatus = SanctionScreeningStatusEnum.COMPLETED;

        if (terminateOnboarding) {
            sanctionScreeningOutcome = SanctionScreeningOutcomeEnum.FAILED;
        } else {
            sanctionScreeningOutcome = SanctionScreeningOutcomeEnum.PASSED;
        }

        // Update the AORequest entity
        int updateWasSuccessful =  this.accountOpeningRequestRepository
                .updateSanctionScreeningInformation(
                        sanctionScreeningOutcome,
                        sanctionScreeningStatus,
                        terminateOnboarding,
                        reasonForTermination,
                        complianceFeedback.getAccountOpeningRequestID()
                );

        if (updateWasSuccessful != 1){
            AccountOpeningServiceException exception;
            exception = new AccountOpeningServiceException(
                    "Unable to save compliance feedback. " +
                            "It could be due to an incorrect account opening request ID. " +
                            "Please confirm your payload and try again");
            exception.setIsDueToBadRequest(Boolean.TRUE);
            throw exception;
        }

    }
}
