package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTemplateModel {

    private String serviceTemplateId;

    private String name;
    
    private String description;

    private String serviceTypeEnum;

    private String subType;

    private JsonNode langMeta;

    private JsonNode fields;

    private String siteId;
    
    private boolean checkVisitTime;
}
