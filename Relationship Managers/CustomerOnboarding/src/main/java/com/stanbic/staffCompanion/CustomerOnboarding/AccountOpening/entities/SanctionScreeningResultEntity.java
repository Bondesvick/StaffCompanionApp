package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/** Database table to store the results of sanction screening **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Result")
public class SanctionScreeningResultEntity extends DateAudit {

    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Result_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Result_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Result_Table_Sequence"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;

    @Column(name = "Customer_BVN")
    private String customerBVN;

    @Lob
    @Column(name = "Result_Screenshot_PDF_Base64")
    private String sanctionScreeningScreenshot;

    @Column(name = "Sanction_Screening_Passed")
    private Boolean sanctionScreeningPassed;

    public SanctionScreeningResultEntity(AccountOpeningRequestEntity accountOpeningRequest,
                                         String customerBVN, String sanctionScreeningScreenshot,
                                         Boolean sanctionScreeningPassed) {
        this.accountOpeningRequest = accountOpeningRequest;
        this.customerBVN = customerBVN;
        this.sanctionScreeningScreenshot = sanctionScreeningScreenshot;
        this.sanctionScreeningPassed = sanctionScreeningPassed;
    }
}
