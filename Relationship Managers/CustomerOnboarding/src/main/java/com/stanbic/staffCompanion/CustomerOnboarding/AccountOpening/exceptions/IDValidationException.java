package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

public class IDValidationException extends Exception{

    private Boolean isDueToInvalidID = Boolean.FALSE;

    public IDValidationException(String message) {
        super(message);
    }

    public Boolean getDueToInvalidID() {
        return isDueToInvalidID;
    }

    public void setDueToInvalidID(Boolean dueToInvalidID) {
        isDueToInvalidID = dueToInvalidID;
    }
}
