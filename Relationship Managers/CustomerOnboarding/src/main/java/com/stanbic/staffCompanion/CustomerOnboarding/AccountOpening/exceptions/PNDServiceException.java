package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

public class PNDServiceException extends Exception {

    private Boolean isDueToInvalidAccount = Boolean.FALSE;

    public PNDServiceException(String message) {
        super(message);
    }

    public Boolean getDueToInvalidAccount() {
        return isDueToInvalidAccount;
    }

    public void setDueToInvalidAccount(Boolean dueToInvalidAccount) {
        isDueToInvalidAccount = dueToInvalidAccount;
    }
}
