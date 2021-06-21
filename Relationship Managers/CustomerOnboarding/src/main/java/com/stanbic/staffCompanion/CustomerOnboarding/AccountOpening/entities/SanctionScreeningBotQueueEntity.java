package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/** Database table to serve as a queue for the Sanction Screening Bot **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Bot_Queue")
public class SanctionScreeningBotQueueEntity extends DateAudit {

    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Bot_Queue_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Bot_Queue_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_Sanction_Screening_Bot_Queue_Table_Sequence"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;

    @Column(name = "Customer_First_Name")
    private String customerFirstName;

    @Column(name = "Customer_Last_Name")
    private String customerLastName;

    @Column(name = "Customer_Full_Home_Address")
    private String customerFullHomeAddress;

    @Column(name = "Customer_BVN")
    private String customerBVN;

    @Column(name = "Has_Been_Treated_By_Bot")
    private Boolean hasBeenTreatedByBot = Boolean.FALSE;

    public SanctionScreeningBotQueueEntity(AccountOpeningRequestEntity accountOpeningRequest,
                                           String customerFirstName, String customerLastName,
                                           String customerFullHomeAddress, String customerBVN) {
        this.accountOpeningRequest = accountOpeningRequest;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerFullHomeAddress = customerFullHomeAddress;
        this.customerBVN = customerBVN;
    }
}
