package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.accountGeneration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailCIFGenRequest {
    private Integer customerTypeCode;
    private Integer salutationCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String emailAddress;
    private String phoneNumber;
    private String addressLine1;
    private String city;
    private String state;
    private String countryCode;
    private String postalCode;
    private String occupancyStartDate;

    private Integer birthDate;
    private Integer birthMonth;
    private Integer birthYear;

    private String Gender;
    private String customerBvn;
    private String isCustomerNonResident;
    private String isCustomerMinor;
    private Integer occupationCode;
    private String segmentationClassCode;
    private String subSegmentCode;
    private String relationshipManagerSAPID;
    private LocalDate relationshipOpeningDate;
    private String maritalStatusCode;
    private String nationality;
    private String employmentStatusCode;
    private String customerBranchSolId;

    private String IDCountryOfIssue;
    private String IDDocumentCode;
    private String IDIssueDate;
    private String IDTypeCode;
    private String IDTypeDescription;
    private String IDPlaceOfIssue;
    private String IDDocumentIssuer;
}
