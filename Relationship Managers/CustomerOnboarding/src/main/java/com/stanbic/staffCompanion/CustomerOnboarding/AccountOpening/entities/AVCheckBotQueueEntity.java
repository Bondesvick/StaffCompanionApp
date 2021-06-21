package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/** Database table to serve as a queue for the Address Verification Bot **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_AV_Check_Bot_Queue")
public class AVCheckBotQueueEntity extends DateAudit {

    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_AV_Check_Bot_Queue_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_AV_Check_Bot_Queue_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_AV_Check_Bot_Queue_Table_Sequence"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;
}
