package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SanctionScreeningResultUploadResponse {
    private String message;
    private String error;
}
