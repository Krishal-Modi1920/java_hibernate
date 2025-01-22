package org.baps.api.vtms.models;

import lombok.Data;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class MasterModel {

    private List<LookupModel> lookupList;

    private List<CountryModel> countryList;

    private List<StateModel> stateModelList;
}
