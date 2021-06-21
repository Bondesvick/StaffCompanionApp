package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {

    // Personal Information
    private String gender;
    private String title;
    private String maritalStatus;
    private String mothersMaidenName;
    private String employmentStatus;
    private String occupation;
    private String natureOfBusiness;
    private String sector;
    private String monthlyIncomeRange;
    private String nationality;
    private String localGovtArea;
    private String placeOfBirth;
    private String purposeOfAccountOpening;

    // Contact details - Use these to trigger AVR check
    private String mobileNumber;
    private String emailAddress;
    private String stateOfResidence;
    private String lgaOfResidence;
    private String streetNumber;
    private String nearestLandmark;
    private String otherAddressInfo;
    private String preferredBranchSolID;

    // Next of Kin information
    private String nokLastName;
    private String nokFirstName;
    private String nokOtherNames;
    private String nokMobileNumber;
    private String nokEmailAddress;
    private String nokRelationship;
    private Date nokDateOfBirth;
    private String nokAddressStreetNumber;
    private String nokAddressLandmark;
    private String nokAddressStateOfResidence;
    private String nokCityOfResidence;

    // Other information
    // Confirm the right terms for these fields
    private Boolean hasHeldProminentPosition;
    private Boolean isPoliticallyExposed;
    private String nameOfPoliticalConnection;
    private String positionHeld;
    private String relationshipWithPerson;
}
