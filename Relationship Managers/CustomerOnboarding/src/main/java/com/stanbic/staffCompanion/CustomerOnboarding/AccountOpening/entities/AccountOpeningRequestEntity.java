package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/** Database table for storing & tracking requests to open accounts **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_Request")
public class AccountOpeningRequestEntity extends DateAudit {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private String accountOpeningRequestId;

    @Column(nullable = false, updatable = false, name = "Relationship_Manager_SAP_ID")
    private String relationshipManagerSAPId;

    @Column(nullable = false, name = "Customer_Email")
    private String customerEmail;

    @Column(nullable = false, name = "Customer_Phone_Number")
    private String customerPhoneNumber;

    @Column(nullable = false, updatable = false, name = "Customer_BVN")
    private String customerBVN;

    @Column(nullable = false, name = "Account_Type")
    @Enumerated(EnumType.STRING)
    private BankAccountType accountType;

    @Column(nullable = false, name = "Account_Scheme_Code")
    private String accountSchemeCode;

    @Column(nullable = false, name = "Sanction_Screening_Status")
    @Enumerated(EnumType.STRING)
    private SanctionScreeningStatusEnum sanctionScreeningStatus = SanctionScreeningStatusEnum.NOT_STARTED;

    @Column(nullable = false, name = "Sanction_Screening_Outcome")
    @Enumerated(EnumType.STRING)
    private SanctionScreeningOutcomeEnum sanctionScreeningOutcome = SanctionScreeningOutcomeEnum.PENDING;

    @Column(nullable = false, name = "AV_Check_Status")
    @Enumerated(EnumType.STRING)
    private AVCheckStatusEnum AVCheckStatus = AVCheckStatusEnum.NOT_STARTED;

    @Column(nullable = false, name = "AV_Check_Outcome")
    @Enumerated(EnumType.STRING)
    private AVCheckOutcomeEnum AVCheckOutcome = AVCheckOutcomeEnum.PENDING;

    // customer info received
    // nok info received
    // mandate info received

    @Column(nullable = false, name = "Dedup_Check_Passed")
    private Boolean dedupCheckPassed = Boolean.FALSE;

    @Column(nullable = false, name = "Onboarding_Has_Been_Terminated")
    private Boolean onboardingHasBeenTerminated = Boolean.FALSE;

    @Column(length = 480, name = "Reason_For_Onboarding_Termination")
    private String reasonForOnboardingTermination;

    @Column(nullable = false, name = "Account_Has_Been_Opened")
    private Boolean accountHasBeenOpened = Boolean.FALSE;

    @Column(nullable = false, name = "Account_Number_Has_Been_Sent_To_Customer_Email")
    private Boolean accountNumberHasBeenSentToCustomerEmail = Boolean.FALSE;

    @Column(nullable = false, name = "Account_Number_Has_Been_Sent_To_Customer_Phone")
    private Boolean accountNumberHasBeenSentToCustomerPhone = Boolean.FALSE;

    // Service access - Internet banking & Cards

    public AccountOpeningRequestEntity(String customerBVN, String customerEmail,
                                       String customerPhoneNumber, String relationshipManagerSAPId,
                                       String accountSchemeCode, BankAccountType accountType) {
        this.customerBVN = customerBVN;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.relationshipManagerSAPId = relationshipManagerSAPId;
        this.accountSchemeCode = accountSchemeCode;
        this.accountType = accountType;
    }
}
