package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class LookupModel {
    private String lookupId;

    private String parentLookupId;

    private String key;

    private String value;

    private JsonNode langMeta;

    private Integer sequenceNumber;

    private List<ChildLookupModel> childLookup;
}
