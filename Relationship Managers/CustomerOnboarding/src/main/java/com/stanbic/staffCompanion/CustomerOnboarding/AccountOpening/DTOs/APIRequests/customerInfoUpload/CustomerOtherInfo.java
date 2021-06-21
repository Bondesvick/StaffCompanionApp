package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOtherInfo {
    private Boolean hasHeldProminentPosition;
    private Boolean isPoliticallyExposed;
    private String nameOfPoliticalConnection;
    private String positionHeld;
    private String relationshipWithPerson;
}
