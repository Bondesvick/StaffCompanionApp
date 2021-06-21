package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/** Database table for storing the BVN results of BVN validation **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_BVN_Validation_Result")
public class BVNValidationResultEntity extends DateAudit {
    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_BVN_Validation_Result_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_BVN_Validation_Result_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_BVN_Validation_Result_Table_Sequence"
    )
    private Long id;

    @Column(name = "BVN")
    private String BVN;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Middle_Name")
    private String middleName;

    @Column(name = "Last_Name")
    private String lastName;

    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;

    @Column(name = "Watch_Listed")
    private Boolean watchListed;

    @Column(name = "Email")
    private String email;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "Phone_Number")
    private String phoneNumber;

    @Column(name = "NIN")
    private String NIN;

    @Column(name = "Name_On_Card")
    private String nameOnCard;

    @Column(name = "Nationality")
    private String nationality;

    @Column(name = "Title")
    private String title;

    @Lob
    @Column(name = "Customer_Photo_Base_64")
    private String customerPhotoBase64;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;


    public BVNValidationResultEntity(String BVN, String firstName,
                                     String middleName, String lastName,
                                     LocalDate dateOfBirth, Boolean watchListed,
                                     String email, String gender, String phoneNumber,
                                     String NIN, String nameOnCard, String nationality,
                                     String title, String customerPhotoBase64,
                                     AccountOpeningRequestEntity AORequestEntity) {
       this.BVN = BVN;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.watchListed = watchListed;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.NIN = NIN;
        this.nameOnCard = nameOnCard;
        this.nationality = nationality;
        this.title = title;
        this.customerPhotoBase64 = customerPhotoBase64;
        this.accountOpeningRequest = AORequestEntity;
    }
}
