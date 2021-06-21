package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SanctionScreeningComplianceFeedback {
    @Length(max = 36, min = 36)
    private String accountOpeningRequestID;
    private Boolean continueOnboarding;
    @Length(max = 480)
    private String reasonForOnboardingTermination;
}
