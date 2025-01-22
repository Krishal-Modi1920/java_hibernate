package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryModel {

    private String countryId;

    private String countryCode;

    private String name;

    private String isdCode;

    private String divionId;
}
