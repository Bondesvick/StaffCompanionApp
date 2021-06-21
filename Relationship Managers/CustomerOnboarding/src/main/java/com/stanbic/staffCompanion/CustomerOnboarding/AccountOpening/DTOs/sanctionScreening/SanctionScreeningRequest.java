package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SanctionScreeningRequest {
    private final String customerFirstName;
    private final String customerLastName;
    private final String customerFullHomeAddress;
    private final String customerBVN;
    private final AccountOpeningRequestEntity accountOpeningRequest;
}
