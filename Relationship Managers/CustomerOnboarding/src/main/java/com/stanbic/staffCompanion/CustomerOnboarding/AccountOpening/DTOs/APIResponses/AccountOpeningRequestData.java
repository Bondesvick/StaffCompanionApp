package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.BVNInformationDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.AVCheckOutcomeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.AVCheckStatusEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningOutcomeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningRequestData {
    private String accountOpeningRequestId;
    private String relationshipManagerSAPId;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerBVN;
    private BVNInformationDTO bvnInformation;
    private String accountSchemeCode;
    private SanctionScreeningStatusEnum sanctionScreeningStatus;
    private SanctionScreeningOutcomeEnum sanctionScreeningOutcome;
    private AVCheckStatusEnum AVCheckStatus;
    private AVCheckOutcomeEnum AVCheckOutcome;

    // customer info received
    // nok info received
    // mandate info received

    private Boolean dedupCheckPassed;
    private Boolean onboardingHasBeenTerminated;
    private String reasonForOnboardingTermination;
    private Boolean accountHasBeenOpened;
    private Boolean accountNumberHasBeenSentToCustomerEmail;
    private Boolean accountNumberHasBeenSentToCustomerPhone;
}
