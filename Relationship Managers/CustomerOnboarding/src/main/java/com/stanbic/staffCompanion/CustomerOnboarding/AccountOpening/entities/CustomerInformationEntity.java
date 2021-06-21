package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;


/** Database table to store information received from the customer during the AO process **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_Request_Customer_Info")
public class CustomerInformationEntity extends DateAudit {
    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_Request_Customer_Info_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_Request_Customer_Info_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_Request_Customer_Info_Sequence"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequestEntity;

    // Personal Information
    @Column(name = "Gender")
    private String gender;
    @Column(name = "Title")
    private String title;
    @Column(name = "Marital_Status")
    private String maritalStatus;
    @Column(name = "Mothers_Maiden_Name")
    private String mothersMaidenName;
    @Column(name = "Employment_Status")
    private String employmentStatus;
    @Column(name = "Occupation")
    private String occupation;
    @Column(name = "Nature_Of_Business")
    private String natureOfBusiness;
    @Column(name = "Sector")
    private String sector;
    @Column(name = "Sub_Sector")
    private String subSector;
    @Column(name = "Monthly_Income_Range")
    private String monthlyIncomeRange;
    @Column(name = "Nationality")
    private String nationality;
    @Column(name = "Local_Government_Area")
    private String localGovtArea;
    @Column(name = "Place_Of_Birth")
    private String placeOfBirth;
    @Column(name = "Purpose_Of_Account_Opening")
    private String purposeOfAccountOpening;

    // Contact details - Use these to trigger AVR check
    @Column(name = "Mobile_Number")
    private String mobileNumber;
    @Column(name = "Email_Address")
    private String emailAddress;
    @Column(name = "Address_Line1")
    private String addressLine1;
    @Column(name = "Address_Line2")
    private String addressLine2;
    @Column(name = "Address_Line3")
    private String addressLine3;
    @Column(name = "LGA_Of_Residence")
    private String lgaOfResidence;
    @Column(name = "City_Of_Residence")
    private String cityOfResidence;
    @Column(name = "State_Of_Residence")
    private String stateOfResidence;
    @Column(name = "Country_Code")
    private String countryCode;
    @Column(name = "Postal_Code")
    private String postalCode;
    @Column(name = "Occupancy_Start_Date")
    private LocalDate occupancyStartDate;
    @Column(name = "Nearest_Landmark")
    private String nearestLandmark;
    @Column(name = "Other_Address_Information")
    private String otherAddressInfo;
    @Column(name = "Preferred_Branch_Sol_ID")
    private String preferredBranchSolID;

    // Next of Kin information
    @Column(name = "NOK_Last_Name")
    private String nokLastName;
    @Column(name = "NOK_First_Name")
    private String nokFirstName;
    @Column(name = "NOK_Other_Names")
    private String nokOtherNames;
    @Column(name = "NOK_Mobile_Number")
    private String nokMobileNumber;
    @Column(name = "NOK_Email_Address")
    private String nokEmailAddress;
    @Column(name = "NOK_Relationship")
    private String nokRelationship;
    @Column(name = "NOK_Date_Of_Birth")
    private Date nokDateOfBirth;
    @Column(name = "NOK_Address_Street_Number")
    private String nokAddressStreetNumber;
    @Column(name = "NOK_Address_Landmark")
    private String nokAddressLandmark;
    @Column(name = "NOK_Address_State_Of_Residence")
    private String nokAddressStateOfResidence;
    @Column(name = "NOK_City_Of_Residence")
    private String nokCityOfResidence;

    // Other information
    // Confirm the right terms for these fields
    @Column(name = "Has_Held_Prominent_Position")
    private Boolean hasHeldProminentPosition;
    @Column(name = "Is_Politically_Exposed")
    private Boolean isPoliticallyExposed;
    @Column(name = "Name_Of_Political_Connection")
    private String nameOfPoliticalConnection;
    @Column(name = "Position_Held")
    private String positionHeld;
    @Column(name = "Relationship_With_Person")
    private String relationshipWithPerson;


    public CustomerInformationEntity(
            AccountOpeningRequestEntity accountOpeningRequestEntity,
            String gender, String title, String maritalStatus,
            String mothersMaidenName, String employmentStatus,
            String occupation, String natureOfBusiness,
            String sector, String monthlyIncomeRange,
            String nationality, String localGovtArea,
            String placeOfBirth, String purposeOfAccountOpening
            ) {
        this.accountOpeningRequestEntity = accountOpeningRequestEntity;
        this.gender = gender;
        this.title = title;
        this.maritalStatus = maritalStatus;
        this.mothersMaidenName = mothersMaidenName;
        this.employmentStatus = employmentStatus;
        this.occupation = occupation;
        this.natureOfBusiness = natureOfBusiness;
        this.sector = sector;
        this.monthlyIncomeRange = monthlyIncomeRange;
        this.nationality = nationality;
        this.localGovtArea = localGovtArea;
        this.placeOfBirth = placeOfBirth;
        this.purposeOfAccountOpening = purposeOfAccountOpening;
    }
}
