package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BVNValidationServiceException extends Exception {

    private Boolean isDueToBadRequest = Boolean.FALSE;
    private Boolean isDueToServiceOutage = Boolean.FALSE;

    public BVNValidationServiceException(
            String message){
        super(message);
    }

    public BVNValidationServiceException(
            String message, Boolean isDueToBadRequest){
        super(message);
        this.isDueToBadRequest = isDueToBadRequest;
    }

}
