package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.PNDUpdateRequestDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.PNDServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Updates the PND status of accounts **/
@Service
public class PNDMgmtService {
    private final Logger log = LoggerFactory.getLogger(PNDMgmtService.class);

    @Autowired
    private RedboxRequestManagerClient redboxRequestManagerClient;

    /** Freeze an account **/
    public void freezeAccount(String accountNumber,
                              String freezeReasonCode)
            throws PNDServiceException {

        log.info("About to freeze account");

        PNDUpdateRequestDTO pndUpdateRequest;
        pndUpdateRequest = new PNDUpdateRequestDTO(
                accountNumber,
                Boolean.TRUE,
                freezeReasonCode, // Use a switch statement to determine this?
                null
        );
    }

    /** Unfreeze an account **/
    public void unfreezeAccount(String accountNumber)
            throws PNDServiceException {

        log.info("About to unfreeze account");

        PNDUpdateRequestDTO pndUpdateRequest;
        pndUpdateRequest = new PNDUpdateRequestDTO(
                accountNumber,
                Boolean.FALSE,
                "",
                null
        );

        this.sendPNDRequestToRedbox(pndUpdateRequest);
    }

    private void sendPNDRequestToRedbox(PNDUpdateRequestDTO pndUpdateRequest)
            throws PNDServiceException {

        // Generate the xml request
        RequestManagerRequest requestManagerRequest = new RequestManagerRequest();

        String freezeCodeTag = "";

        if (pndUpdateRequest.getFreezeCode() != null) {
            freezeCodeTag = "<freezeCode>"+pndUpdateRequest
                    .getFreezeReasonCode()+"</freezeCode>";
        }

        requestManagerRequest.setChannel("BPM");
        requestManagerRequest.setType("UPDATE_PND");
        requestManagerRequest.setBody(
                "<otherRequestDetails>" +
                    "<accountNumber>"+pndUpdateRequest.getAccountNumber()+"</accountNumber>" +
                    "<moduleTranReferenceId>BPM34934343</moduleTranReferenceId>" +
                    "<operationType>"+(pndUpdateRequest
                        .getIsFreezeOperation() == Boolean.TRUE ? 1 : 0)+
                    "</operationType>" + freezeCodeTag +
                    "<freezeReasonCode>"+pndUpdateRequest.getFreezeReasonCode()+"</freezeReasonCode>" +
                "</otherRequestDetails>"
        );

        RequestManagerResponse requestManagerResponse;

        // Send request to redbox
        try{
            log.info("Sending PND update request to Redbox");

            requestManagerResponse = redboxRequestManagerClient.callRequestManagerService(requestManagerRequest);

        }catch (Exception e){
            log.error(e.getMessage());

            throw new PNDServiceException("Unable to update PND status");
        }

        switch (requestManagerResponse.getResponseCode()){
            case "000":
                log.info("PND update was successful");

                return;

            case "999":
            default:
                throw new PNDServiceException("Unable to update PND status");
        }
    }
}
