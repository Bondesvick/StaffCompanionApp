package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.accountGeneration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountGenRequest {
    private String customerCIF;
}
