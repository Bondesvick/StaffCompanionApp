package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IDValidationResultDTO {

    private final String country;
    private final String idType;
    private final String idNumber;
    private LocalDate expiryDate;
    private final LocalDate dob;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String fullName;
    private final String title;
    private final String phoneNumber;
    private final String customerPhoto;
    private final String gender;
    private final String smileIdentityJobId;
}
