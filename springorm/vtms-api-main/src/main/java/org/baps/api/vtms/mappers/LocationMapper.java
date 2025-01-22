package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.LocationModel;
import org.baps.api.vtms.models.entities.Location;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
        )
public interface LocationMapper {

    @Mapping(source = "parentLocation.locationId", target = "parentLocationId")
    LocationModel locationToLocationModel(Location location);
    
    List<LocationModel> locationListToLocationModelList(List<Location> locationList);
}
