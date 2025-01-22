package org.baps.api.vtms.models;

import lombok.Data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotBlank;


@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PersonnelBasicInfoModel {

    @NotBlank
    private String personnelId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String phoneCountryCode;

    private String phoneNumber;

    private String email;
}