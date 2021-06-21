package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.CustomerMandateDocTypeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.MandateDocumentFormatEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMandateInfo {
    private String fileName;
    private MandateDocumentFormatEnum fileFormat;
    @ApiModelProperty(hidden = true)
    private CustomerMandateDocTypeEnum documentType;
    private String base64Data;
}
