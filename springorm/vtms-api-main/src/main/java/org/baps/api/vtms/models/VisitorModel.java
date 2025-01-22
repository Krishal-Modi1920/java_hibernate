package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitorModel {

    private String visitorId;

    @NotBlank
    @Size(max = 10)
    private String salutation;

    @NotBlank
    @Size(min = 3, max = 64)
    @Pattern(regexp = "^(?!\\s)[A-Za-z\\s]*$", message = "{visitor_model.firstName.invalid}")
    private String firstName;

    @Size(min = 3, max = 64)
    @Pattern(regexp = "^(?!\\s)[A-Za-z\\s]*$", message = "{visitor_model.middleName.invalid}")
    private String middleName;

    @NotBlank
    @Size(min = 3, max = 64)
    @Pattern(regexp = "^(?!\\s)[A-Za-z\\s]*$", message = "{visitor_model.lastName.invalid}")
    private String lastName;

    @NotBlank
    @Size(max = 6)
    private String gender;

    @Size(max = 100)
    @Pattern(regexp = "^$|^[\\d\\D][^\\s].*$", message = "{visitor_model.addressLine1.invalid}")
    private String addressLine1;

    @Size(max = 100)
    @Pattern(regexp = "^$|^[\\d\\D][^\\s].*$", message = "{visitor_model.addressLine2.invalid}")
    private String addressLine2;

    @NotBlank
    @Size(max = 36)
    private String country;

    @Size(max = 36)
    private String state;

    @NotBlank
    @Size(max = 36)
    private String city;

    @NotBlank
    @Size(min = 5, max = 8)
    private String postalCode;

    @NotBlank
    @Size(max = 255)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "{visitor_model.email.invalid}")
    private String email;

    @NotBlank
    @Size(max = 5)
    @Pattern(regexp = "^[0-9+]+$", message = "{visitor_model.phoneCountryCode.invalid}")
    private String phoneCountryCode;

    @NotBlank
    @Size(min = 8, max = 12)
    @Pattern(regexp = "^\\d+$", message = "{visitor_model.phoneNumber.invalid}")
    private String phoneNumber;

    @NotBlank
    @Size(max = 20)
    private String preferredCommMode;

    @Size(max = 56)
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.designation.invalid}")
    private String designation;

    @Size(max = 255)
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.organizationName.invalid}")
    private String organizationName;

    @Size(max = 255)
    @Pattern(regexp = "(?s).*", message = "{visitor_model.organizationAddress.invalid}")
    private String organizationAddress;

    @Size(max = 255)
    @Pattern(regexp = "^$|^[^\\s\\n].*", message = "{visitor_model.organizationWebsite.invalid}")
    private String organizationWebsite;

    @Size(max = 255)
    //@Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.telegramId.invalid}")
    private String telegramId;

    @Size(max = 255)
    //@Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.facebookId.invalid}")
    private String facebookId;

    @Size(max = 255)
    //@Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.linkedinId.invalid}")
    private String linkedinId;

    @Size(max = 255)
    //@Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.twitterId.invalid}")
    private String twitterId;

    @Size(max = 255)
    //@Pattern(regexp = "^(?!\\s*$)[A-Za-z\\s]*$|^$", message = "{visitor_model.instagramId.invalid}")
    private String instagramId;

    @Size(max = 128)
    private String visitorType;
    
    @Size(max = 512)
    @Pattern(regexp = "(?s).*", message = "{visitor_model.comments.invalid}")
    private String comments;

}
