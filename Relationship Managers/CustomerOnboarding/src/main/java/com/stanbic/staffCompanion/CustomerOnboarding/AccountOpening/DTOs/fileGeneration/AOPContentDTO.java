package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.fileGeneration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AOPContentDTO {
    private String customerFirstName;
    private String customerMiddleName;
    private String customerLastName;
}
