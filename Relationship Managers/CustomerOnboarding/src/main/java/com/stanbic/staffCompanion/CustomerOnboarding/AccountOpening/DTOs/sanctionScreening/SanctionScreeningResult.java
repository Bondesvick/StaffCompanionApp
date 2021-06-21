package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanctionScreeningResult {

    private String sanctionScreeningResultPDFBase64;

    private String customerBVN;

    private String accountOpeningRequestID;

    private Boolean sanctionScreeningPassed;
}
