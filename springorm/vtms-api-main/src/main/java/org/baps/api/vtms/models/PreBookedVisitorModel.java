package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreBookedVisitorModel {

    private String visitorId;

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

    @Size(max = 6)
    private String gender;

    @Size(max = 100)
    private String addressLine1;

    @Size(max = 100)
    private String addressLine2;

    @Size(max = 36)
    private String country;

    @Size(max = 36)
    private String state;

    @Size(max = 36)
    private String city;

    @Size(min = 5, max = 8)
    private String postalCode;

    @NotBlank
    @Size(max = 255)
    private String email;

    @Size(max = 12)
    private String phoneNumber;

}
