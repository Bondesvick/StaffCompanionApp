package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.BankAccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewBankAccountRequest {
    @NotBlank
    private String relationshipManagerSAPId;
    @NotBlank
    @Email
    private String customerEmail;
    @NotBlank
    private String customerPhoneNumber;
    @NotBlank
    private String customerBVN;
    private BankAccountType accountType;
    @NotBlank
    private String accountSchemeCode;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date customerDOB;
}
