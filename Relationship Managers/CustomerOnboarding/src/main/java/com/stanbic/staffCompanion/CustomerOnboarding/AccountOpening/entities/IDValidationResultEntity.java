package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


/** Database table to store the results of ID validation **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_ID_Validation_Result")
public class IDValidationResultEntity extends DateAudit {

    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_ID_Validation_Result_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_ID_Validation_Result_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_ID_Validation_Result_Table_Sequence"
    )
    private Long id;

    @Column(name = "ID_Type")
    private String idType;

    @Column(name = "ID_number")
    private String idNumber;

    @Column(name = "Expiration_Date")
    private LocalDate expirationDate;

    @Column(name = "Full_Name")
    private String fullName;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Middle_Name")
    private String middleName;

    @Column(name = "Last_Name")
    private String lastName;

    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "Smile_Identity_Job_ID")
    private String smileIdentityJobId;

    @Lob
    @Column(name = "User_Photo")
    private String userPhoto;

    @Column(name = "Matches_BVN_Information")
    private Boolean idDetailsMatchBVNInformation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;


    public IDValidationResultEntity(String idType, String idNumber,
                                    LocalDate expirationDate, String fullName,
                                    String firstName, String middleName,
                                    String lastName, LocalDate dateOfBirth,
                                    String gender, String userPhoto,
                                    String smileIdentityJobId,
                                    Boolean idDetailsMatchBVNInformation,
                                    AccountOpeningRequestEntity AORequestEntity) {
        this.idType = idType;
        this.idNumber = idNumber;
        this.expirationDate = expirationDate;
        this.fullName = fullName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.userPhoto = userPhoto;
        this.idDetailsMatchBVNInformation = idDetailsMatchBVNInformation;
        this.smileIdentityJobId = smileIdentityJobId;
        this.accountOpeningRequest = AORequestEntity;
    }

}
