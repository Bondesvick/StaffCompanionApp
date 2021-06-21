package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.bvnValidationService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedboxBVNValidationResponse {
    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseMessage")
    private String responseMessage;

    @JsonProperty("BVN")
    private String BVN;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("MiddleName")
    private String middleName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("DateOfBirth")
    private String dateOfBirth;

    @JsonProperty("WatchListed")
    private String watchListed;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("PhoneNumber2")
    private String phoneNumber2;

    @JsonProperty("NIN")
    private String NIN;

    @JsonProperty("NameOnCard")
    private String nameOnCard;

    @JsonProperty("Nationality")
    private String nationality;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Base64Image")
    private String customerPhotoBase64;
}
