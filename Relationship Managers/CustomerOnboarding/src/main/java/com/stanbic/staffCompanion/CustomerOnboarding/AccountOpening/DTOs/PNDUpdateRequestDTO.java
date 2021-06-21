package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PNDUpdateRequestDTO {
    private final String accountNumber;
    private final Boolean isFreezeOperation;
    private final String freezeReasonCode;
    private final String freezeCode;
}
