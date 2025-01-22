package org.baps.api.vtms.models;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseVisitServiceModel {
    
    LocalDateTime getStartDateTime();

    LocalDateTime getEndDateTime();
    
    String getServiceType();

    List<VisitLocationModel> getVisitLocationModelList();

}
