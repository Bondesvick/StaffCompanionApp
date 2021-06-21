package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.DedupCheckRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.DedupCheckResult;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.DedupCheckServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.XMLToJSONConverter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DedupCheckService {

    private final Logger log = LoggerFactory.getLogger(DedupCheckService.class);

    @Autowired
    private RedboxRequestManagerClient redboxRequestManagerClient;

    @Autowired
    private XMLToJSONConverter xmlToJSONConverter;

    public DedupCheckResult checkForDuplicateAccount(DedupCheckRequest dedupCheckRequest)
            throws DedupCheckServiceException {

        // Generate the xml request
        RequestManagerRequest requestManagerRequest = new RequestManagerRequest();

        // Format the date of birth
        LocalDate dob = dedupCheckRequest.getDateOfBirth();
        String formattedDate = dob+"T00:00:00.000";

        requestManagerRequest.setChannel("BPM");
        requestManagerRequest.setType("DUPE_CHECK");
        requestManagerRequest.setBody(
                "<otherRequestDetails>" +
                " <customerType></customerType>" +
                " <firstName>"+dedupCheckRequest.getFirstName()+"</firstName>" +
                " <middleName>"+dedupCheckRequest.getMiddleName()+"</middleName>" +
                " <lastName>"+dedupCheckRequest.getLastName()+"</lastName>" +
                " <dOB>"+formattedDate+"</dOB>" +
            " <phoneNumber>"+dedupCheckRequest.getPhoneNumber()+"</phoneNumber>" +
            " <corporateName></corporateName>" +
            " <rCNumber></rCNumber>" +
            "</otherRequestDetails>"
            );

        RequestManagerResponse requestManagerResponse;

        // Send request to redbox
        try{
            log.info("Sending customer details to Redbox for a Dedup check");

            requestManagerResponse = redboxRequestManagerClient.callRequestManagerService(requestManagerRequest);

        }catch (Exception e){
            log.error(e.getMessage());

            throw new DedupCheckServiceException(
                    "Unable to connect to redbox for dedup check"
            );
        }

        switch (requestManagerResponse.getResponseCode()){
        case "202":
            // Possibly a successful response. Process it to confirm
            JSONObject redboxDedupCheckResponse;
            redboxDedupCheckResponse = this.xmlToJSONConverter.parse(
                    requestManagerResponse.getDetail());

            DedupCheckResult dedupCheckResult = this.processDedupCheckResponse(redboxDedupCheckResponse);

            return dedupCheckResult;

        case "999":
        default:
            throw new DedupCheckServiceException(
                    "Unable to complete Dedup Check"
            );
        }
    }

    /** Process the response gotten from the dedup check request to redbox **/
    private DedupCheckResult processDedupCheckResponse(JSONObject redboxDedupCheckResponse)
            throws DedupCheckServiceException {

        JSONObject responseDetail = redboxDedupCheckResponse.getJSONObject("ns2:getEntityInformationDetailsResponse");

        String responseCode = responseDetail.get("ResponseCode").toString();
        if (!responseCode.equals("000")){
            log.error("Unable to complete dedup check");
            throw new DedupCheckServiceException("Unable to complete dedup check");
        }

        Boolean matchExists = responseDetail.getString("MatchExists")
                .equalsIgnoreCase("Y");

        DedupCheckResult dedupCheckResult;
        dedupCheckResult = new DedupCheckResult(matchExists);

        if (matchExists) {
            JSONObject customerInformation = responseDetail.getJSONObject("CustomerInformationRecords")
                    .getJSONObject("CustomerInformation");

            dedupCheckResult.setFinacleCustomerId(
                    customerInformation.get("CustomerId").toString()
            );
            dedupCheckResult.setFinacleCustomerType(
                    customerInformation.get("CustomerType").toString()
            );

            JSONObject demographyInfo = customerInformation.getJSONObject("Demography");
            dedupCheckResult.setFinacleCustomerFirstName(
                    demographyInfo.getString("FirstName")
            );
            dedupCheckResult.setFinacleCustomerMiddleName(
                    demographyInfo.getString("MiddleName")
            );
            dedupCheckResult.setFinacleCustomerLastName(
                    demographyInfo.getString("Surname")
            );
        }

        return dedupCheckResult;
    }

}
