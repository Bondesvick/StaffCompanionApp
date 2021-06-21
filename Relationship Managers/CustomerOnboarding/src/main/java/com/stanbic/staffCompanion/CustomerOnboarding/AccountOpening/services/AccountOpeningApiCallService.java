package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.DedupCheckResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.BVNValidationResultEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.AccountOpeningApiCallServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.DedupCheckServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.XMLToJSONConverter;
import lombok.var;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@Service
public class AccountOpeningApiCallService {

    private final Logger log = LoggerFactory.getLogger(DedupCheckService.class);

    @Autowired
    private RedboxRequestManagerClient redboxRequestManagerClient;

    @Autowired
    private XMLToJSONConverter xmlToJSONConverter;

    public void CallAccountOpeningAPi(BVNValidationResultEntity bvnValidationResultEntity)
        throws AccountOpeningApiCallServiceException {

        // Generate the xml request
        RequestManagerRequest requestManagerRequest = new RequestManagerRequest();


        var name =  bvnValidationResultEntity.getFirstName() + " " +
                bvnValidationResultEntity.getMiddleName() + " " +
                bvnValidationResultEntity.getFirstName();
        requestManagerRequest.setChannel("BPM");
        requestManagerRequest.setType("OD_ACCOUNT_GENERATION");
        requestManagerRequest.setReqTranId("123456");
        //requestManagerRequest.setSubmissionTime(new XMLGregorianCalendar());

        requestManagerRequest.setBody(
                        "<ModuleTranReferenceId>NG020620211218420269</ModuleTranReferenceId>"+
                        "<CustomerId>101152673</CustomerId>"+
                        "<AccountNumber></AccountNumber>"+
                        "<IsNewCustomer>0</IsNewCustomer>"+
                        "<IsKYCCompliant>N</IsKYCCompliant>"+
                        "<AccountTypeCode>3</AccountTypeCode>"+
                        " <AccountSchemeCode>ODVIS</AccountSchemeCode>"+
                        "<AccountSchemeType>ODA</AccountSchemeType>"+
                        "<AccountCurrency>NGN</AccountCurrency>"+
                        "<AccountOpeningDate></AccountOpeningDate>"+
                        "<AdvanceType></AdvanceType>"+
                        "<GeneralLedgerSubHeadCode>2823</GeneralLedgerSubHeadCode>"+
                        "<GeneralLedgerSubHeadCurrencyCode>NGN</GeneralLedgerSubHeadCurrencyCode>"+
                        "<AccountName>"+name+"</AccountName>"+
                        "<AccountShortName>"+bvnValidationResultEntity.getFirstName()+"</AccountShortName>"+
                        "<AccountManager>A212005</AccountManager>"+
                        "<AccountStatementMode>N</AccountStatementMode>"+
                        "<AccountStatementDispatchMode>C</AccountStatementDispatchMode>"+
                        "<InterestCreditAccountFlag>S</InterestCreditAccountFlag>"+
                        "<InterestDebitAccountFlag>S</InterestDebitAccountFlag>"+
                        "<AccountBranchId>000046</AccountBranchId>"+
                        "<WithholdingTaxCategoryCode>W</WithholdingTaxCategoryCode>"+
                        " <WithholdingTaxLevelFlag>A</WithholdingTaxLevelFlag>"+
                        "<WithholdingTaxBorneBy>P</WithholdingTaxBorneBy>"+
                        "<WithholdingTaxFloorLimit>0</WithholdingTaxFloorLimit>"+
                        "<WithholdingTaxPercent>10</WithholdingTaxPercent>"+
                        "<AccountTypeOtherDetails>"+
                                "<OverDraftAccountDetails>"+
                                    "<SanctionDate>2021-06-02T12:18:42.269</SanctionDate>"+
                                    "<ExpiryDate>2099-11-24T00:00:00.000</ExpiryDate>"+
                                    "<DocumentDate>2021-06-02T12:18:42.269</DocumentDate>"+
                                    "<SanctionLevel>001</SanctionLevel>"+
                                    "<SanctionAuthority>001</SanctionAuthority>"+
                                    "<DrawingPowerIndicator>E</DrawingPowerIndicator>"+
                                    "<AccountRecallFlag>N</AccountRecallFlag>"+
                                    " <SanctionReferenceNumber>1</SanctionReferenceNumber>"+
                                    "<IsLocalFlag>Y</IsLocalFlag>"+
                                    "<HealthCode>003</HealthCode>"+
                                    " <WithHoldingTaxLevelFlag>A</WithHoldingTaxLevelFlag>"+
                                "</OverDraftAccountDetails>"+
                        "</AccountTypeOtherDetails>"+
                        "<ChannelIdCode>0</ChannelIdCode>"
        );

        RequestManagerResponse requestManagerResponse;

        // Send request to redbox
        try{
            log.info("Sending customer details to Redbox for Account Opening");

            requestManagerResponse = redboxRequestManagerClient.callRequestManagerService(requestManagerRequest);

        }catch (Exception e){
            log.error(e.getMessage());

            throw new AccountOpeningApiCallServiceException(
                    "Unable to connect to redbox for Account Opening"
            );
        }

        switch (requestManagerResponse.getResponseCode()){
            case "202":
                // Possibly a successful response. Process it to confirm
                JSONObject redboxAccountOpeningResponse;
                redboxAccountOpeningResponse = this.xmlToJSONConverter.parse(
                        requestManagerResponse.getDetail());

                //DedupCheckResult dedupCheckResult = this.processDedupCheckResponse(redboxDedupCheckResponse);
                this.processAccountOpeningCheckResponse(redboxAccountOpeningResponse);

                //return dedupCheckResult;

            case "999":
            default:
                throw new AccountOpeningApiCallServiceException(
                        "Unable to complete Dedup Check"
                );
        }
    }

    /** Process the response gotten from the dedup check request to redbox **/
    private void processAccountOpeningCheckResponse(JSONObject redboxAccountOpeningResponse)
            throws AccountOpeningApiCallServiceException {
        //return null;
    }
}
