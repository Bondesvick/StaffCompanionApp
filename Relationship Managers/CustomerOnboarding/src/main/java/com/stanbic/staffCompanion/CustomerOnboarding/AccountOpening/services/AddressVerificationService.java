package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload.CustomerContactInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This service manages the address verification process **/
@Service
public class AddressVerificationService {

    private Logger log = LoggerFactory.getLogger(AddressVerificationService.class);

    @Autowired
    private PNDMgmtService pndMgmtService;

    /** Verify a customer's address **/
    public void verifyCustomerAddress(CustomerContactInfo customerContactInfo){}

    /** Sends a verification request to the address verification portal **/
    private void logRequestOnAddressVerificationPortal(){}

    /** Process the result of the address verification
     *
     * This method removes the PND status of an account if
     * the customer's address was successfully verified.
     * **/
    public void processAddressVerificationResult(){
        // If the response is valid, remove PND from the account
    }
}
