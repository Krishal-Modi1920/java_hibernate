package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true),
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface VisitLocationMapper {

    @Mapping(target = "visitLocationId", ignore = true)
    VisitLocation visitLocationModelToVisitLocation(VisitLocationModel visitLocationModel);

    @Mapping(target = "visitLocationId", ignore = true)
    void visitLocationModelToVisitLocation(@MappingTarget VisitLocation visitLocation, VisitLocationModel visitLocationModel);

    @Mapping(target = "locationId", source = "location.locationId")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "interviewVolunteerVisitPersonnelModel", source = "interviewVolunteerVisitPersonnel")
    VisitLocationModel visitLocationToVisitLocationModel(VisitLocation visitLocation);

    default List<VisitLocationModel> visitLocationListToVisitLocationModelList(List<VisitLocation> visitLocationList) {

        if (CollectionUtils.isNotEmpty(visitLocationList)) {
            final List<VisitLocationModel> visitLocationBasicInfoModelList =  visitLocationList.stream()
                    .map(this::visitLocationToVisitLocationModel)
                    .toList();
            return sortVisitLocations(visitLocationBasicInfoModelList);
        }

        return Collections.emptyList();
    }

    default VisitPersonnelModel mapInterviewVolunteerVisitPersonnelModel(VisitPersonnel visitPersonnel) {
        if (ObjectUtils.isNotEmpty(visitPersonnel)) {
            return Mappers.getMapper(VisitPersonnelMapper.class).visitPersonnelToVisitPersonnelModel(visitPersonnel);
        }
        return null;
    }

    default List<VisitLocationModel> sortVisitLocations(List<VisitLocationModel> visitLocations) {
        return visitLocations.stream()
                .sorted(Comparator
                        .comparing(VisitLocationModel::getStartDateTime)
                        .thenComparing(VisitLocationModel::getLocationTagEnum, Comparator.comparingInt(this::mapLocationTagEnum)))
                .collect(Collectors.toList());
    }

    default int mapLocationTagEnum(String locationTagEnum) {
        switch (locationTagEnum) {
            case "PICKUP":
                return 0;
            case "DROP":
                return 2;
            default:
                return 1;
        }
    }
}
