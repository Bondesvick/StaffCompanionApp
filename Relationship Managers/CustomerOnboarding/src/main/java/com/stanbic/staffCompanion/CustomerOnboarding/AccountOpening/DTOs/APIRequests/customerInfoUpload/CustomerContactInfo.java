package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContactInfo {
    @NotBlank
    private String mobileNumber;
    @Email
    private String emailAddress;
    @NotBlank
    private String addressLine1;
    @NotBlank
    private String addressLine2;
    @NotBlank
    private String addressLine3;
    @NotBlank
    private String LGAOfResidence;
    @NotBlank
    private String cityOfResidence;
    @NotBlank
    private String stateOfResidence;
    @NotBlank
    private String countryCode;
    @NotBlank
    private String postalCode;
    private LocalDate occupancyStartDate;
    @NotBlank
    private String nearestLandmark;
    @NotBlank
    private String otherAddressInfo;
    @NotBlank
    private String preferredBranchSolID;
}
