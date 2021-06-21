package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BVNInformationDTO {
    private final String BVN;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final Boolean watchListed;
    private final String email;
    private final String gender;
    private final String phoneNumber;
    private final String NIN;
    private final String nameOnCard;
    private final String nationality;
    private final String title;
    private final String customerPhotoBase64;
}
