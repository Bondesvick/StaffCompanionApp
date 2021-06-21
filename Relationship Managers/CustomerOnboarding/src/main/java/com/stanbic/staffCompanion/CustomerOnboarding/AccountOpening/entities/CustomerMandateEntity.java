package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils.DateAudit;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.CustomerMandateDocTypeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.MandateDocumentFormatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


/** Database table to store the customer's mandate information **/
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COE_Staff_Companion_Acc_Opening_Customer_Mandate")
public class CustomerMandateEntity extends DateAudit {
    @Id
    @SequenceGenerator(
            name = "COE_Staff_Companion_Acc_Opening_Customer_Mandate_Table_Sequence",
            sequenceName = "COE_Staff_Companion_Acc_Opening_Customer_Mandate_Table_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COE_Staff_Companion_Acc_Opening_Customer_Mandate_Table_Sequence"
    )
    private Long id;

    @Column(name = "File_Name")
    private String fileName;

    @Column(name = "File_Format")
    @Enumerated(EnumType.STRING)
    private MandateDocumentFormatEnum fileFormat;

    @Column(name = "Mandate_Document_Type")
    @Enumerated(EnumType.STRING)
    private CustomerMandateDocTypeEnum documentType;

    @Column(name = "Mandate_Document_Base64_String")
    @Lob
    private String base64Data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_Opening_Request_ID", unique = true, referencedColumnName = "id")
    private AccountOpeningRequestEntity accountOpeningRequest;

    public CustomerMandateEntity(
            String fileName,
            MandateDocumentFormatEnum fileFormat,
            CustomerMandateDocTypeEnum documentType,
            String base64Data,
            AccountOpeningRequestEntity AORequestEntity) {

        this.fileName = fileName;
        this.fileFormat = fileFormat;
        this.documentType = documentType;
        this.base64Data = base64Data;
        this.accountOpeningRequest = AORequestEntity;
    }
}
