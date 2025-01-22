package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitorBasicInfoModel {

    private String visitorId;

    @NotBlank
    @Size(max = 10)
    private String salutation;

    @NotBlank
    @Size(max = 32)
    private String firstName;

    @Size(max = 32)
    private String middleName;

    @NotBlank
    @Size(max = 64)
    private String lastName;

    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 5)
    private String phoneCountryCode;

    @NotBlank
    @Size(max = 12)
    private String phoneNumber;

    private String organizationName;

}
