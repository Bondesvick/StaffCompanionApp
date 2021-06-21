package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class IDValidationRequest {
    @NotBlank
    private String country;
    @NotBlank
    private String idType;
    @NotBlank
    private String idNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String middleName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String phoneNumber;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dob;

    @ApiModelProperty(hidden = true)
    private String moduleId;

    @ApiModelProperty(hidden = true)
    private String channel;

    public IDValidationRequest(String country, String idType,
                               String idNumber, String firstName,
                               String middleName, String lastName,
                               String phoneNumber, Date dob) {
        this.country = country;
        this.idType = idType;
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
    }
}
