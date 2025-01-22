package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateModel {

    private String stateId;

    private String abbrevation;

    private String name;

    private String countryId;
}
