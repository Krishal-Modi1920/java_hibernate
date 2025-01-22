package org.baps.api.vtms.models;

import lombok.Data;

@Data
public class LocationModel {

    private String locationId;
    
    private String parentLocationId;
    
    private String name;
    
    private int duration;
    
    private int sequence;

}
