package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNOKInfo {
    private String nokLastName;
    private String nokFirstName;
    private String nokOtherNames;
    private String nokMobileNumber;
    private String nokEmailAddress;
    private String nokRelationship;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date nokDateOfBirth;
    private String nokAddressStreetNumber;
    private String nokAddressLandmark;
    private String nokAddressStateOfResidence;
    private String nokCityOfResidence;
}
