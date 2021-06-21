package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountOpeningServiceException extends Exception {
    private Boolean isDueToBadRequest = Boolean.FALSE;
    private Boolean isDueToInvalidRequestID = Boolean.FALSE;
    private Boolean customerAlreadyExists = Boolean.FALSE;
    private Boolean customerInformationNotFound = Boolean.FALSE;

    private Boolean isDueToDedupCheckFailure = Boolean.FALSE;
    private Boolean isDueToIdentityValidationFailure = Boolean.FALSE;

    // For when a required redbox service is down
    private Boolean isDueToServiceOutage = Boolean.FALSE;

    public AccountOpeningServiceException(
            String message){
        super(message);
    }
}
