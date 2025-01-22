package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.VisitSummaryServiceModel;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitService;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true),
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface VisitSummaryMapper {
    
    @Mapping(target = "locationName", source = "location.name")
    VisitSummaryServiceModel visitLocationToVisitSummaryServiceModel(VisitLocation visitLocation);

    default VisitSummaryServiceModel mapVisitSummaryServiceModel(VisitService visitService, VisitLocation visitLocation) {
        final VisitSummaryServiceModel visitSummaryServiceModelForService = new VisitSummaryServiceModel();
        visitSummaryServiceModelForService.setServiceTemplateName(visitService.getServiceTemplate().getName());
        visitSummaryServiceModelForService.setStartDateTime(visitService.getStartDateTime());
        visitSummaryServiceModelForService.setEndDateTime(visitService.getEndDateTime());
        visitSummaryServiceModelForService.setMeetingPersonnel(Mappers.getMapper(VisitServiceMapper.class).mapMeetingPersonnel(
                visitService.getMeetingPersonnel()));
        if (ObjectUtils.isNotEmpty(visitLocation)) {
            visitSummaryServiceModelForService.setLocationName(visitLocation.getLocation().getName());
        }
        return visitSummaryServiceModelForService;
    }
    
}
