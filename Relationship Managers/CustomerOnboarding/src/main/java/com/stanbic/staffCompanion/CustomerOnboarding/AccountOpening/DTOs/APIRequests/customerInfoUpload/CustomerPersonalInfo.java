package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPersonalInfo {
    // Personal Information
    private String gender;
    private String title;
    private String maritalStatus;
    private String mothersMaidenName;
    private String employmentStatus;
    private String occupation;
    private String natureOfBusiness;
    private String sector;
    private String subSector;
    private String monthlyIncomeRange;
    private String nationality;
    private String localGovtArea;
    private String placeOfBirth;
    private String purposeOfAccountOpening;
}
