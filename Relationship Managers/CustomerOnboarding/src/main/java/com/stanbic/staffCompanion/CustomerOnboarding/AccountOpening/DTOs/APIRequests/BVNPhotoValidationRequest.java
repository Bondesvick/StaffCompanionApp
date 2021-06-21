package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BVNPhotoValidationRequest {
    private Boolean customerMatchesBVNPhoto;
}
