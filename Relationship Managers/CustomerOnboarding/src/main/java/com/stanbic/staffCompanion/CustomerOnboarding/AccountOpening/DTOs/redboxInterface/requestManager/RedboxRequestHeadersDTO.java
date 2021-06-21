package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager;

import javax.validation.constraints.NotNull;

public class RedboxRequestHeadersDTO {

    @NotNull
    private final String soapAction;

    @NotNull
    private final String authorization;

    @NotNull
    private final String moduleID;

    public RedboxRequestHeadersDTO(
            @NotNull String soapAction,
            @NotNull String authorization,
            @NotNull String moduleID) {

        this.soapAction = soapAction;
        this.authorization = authorization;
        this.moduleID = moduleID;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getModuleID() {
        return moduleID;
    }
}
