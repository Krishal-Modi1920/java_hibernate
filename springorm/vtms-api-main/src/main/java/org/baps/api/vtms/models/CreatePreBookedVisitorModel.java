package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePreBookedVisitorModel {

    private String visitorId;

    @NotBlank
    @Size(max = 32)
    private String firstName;

    @NotBlank
    @Size(max = 64)
    private String lastName;

    @NotBlank
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

    @NotBlank
    @Size(max = 12)
    private String phoneNumber;
}
