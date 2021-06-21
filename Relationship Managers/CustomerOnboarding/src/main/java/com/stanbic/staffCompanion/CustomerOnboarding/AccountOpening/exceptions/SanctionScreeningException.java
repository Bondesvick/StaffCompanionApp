package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

public class SanctionScreeningException extends Exception {

    private Boolean isDueToBadResultImage = Boolean.FALSE;

    public SanctionScreeningException(String message) {
        super(message);
    }

    public Boolean getDueToBadResultImage() {
        return isDueToBadResultImage;
    }

    public void setDueToBadResultImage(Boolean dueToBadResultImage) {
        isDueToBadResultImage = dueToBadResultImage;
    }
}
