package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.salesforce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class DigitalOnboardingSalesforceRecordDTO {

    @NotNull
    private final String objectType;

    @NotNull
    private final String externalIDField;

    @NotNull
    private final String externalID;

    @NotNull
    private final HashMap<String, Object> newValuesForFields;

    public String getRecordEndpoint() {
        return String.format("%s/%s/%s", objectType, externalIDField, externalID);
    }

}
