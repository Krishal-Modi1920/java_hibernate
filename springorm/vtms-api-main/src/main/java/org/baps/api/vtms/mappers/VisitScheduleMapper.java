package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitScheduleModel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
         nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
        )
public interface VisitScheduleMapper {

    @Mapping(target = "serviceName", source = "serviceTemplate.name")
    @Mapping(source = "visitPersonnelList", target = "visitPersonnelModelList", qualifiedByName = "mapVisitPersonnelModelList")
    @Mapping(target = "visitLocationModelList", source = "visitService.visitLocationList")
    VisitScheduleModel visitServiceToVisitScheduleModel(VisitService visitService);
    
    default List<VisitScheduleModel> visitServiceListToVisitScheduleModelList(List<VisitService> visitServiceList) {
        if (CollectionUtils.isEmpty(visitServiceList)) {
            return Collections.emptyList();
        }

        return visitServiceList.stream()
                .map(this::visitServiceToVisitScheduleModel)
                .sorted(Comparator.comparing(VisitScheduleModel::getStartDateTime))
                .collect(Collectors.toList());
    }

    @Mapping(source = "visitPersonnelList", target = "visitPersonnelModelList", qualifiedByName = "mapVisitPersonnelModelListForVisit")
    VisitScheduleModel visitToVisitScheduleModel(Visit visit);

    @Named("mapVisitPersonnelModelList")
    default List<VisitPersonnelModel> mapVisitPersonnelModelList(List<VisitPersonnel> visitPersonnelList) {
        return Mappers.getMapper(VisitPersonnelMapper.class).visitPersonnelListToVisitPersonnelModelList(visitPersonnelList);
    }

    @Named("mapVisitPersonnelModelListForVisit")
    default List<VisitPersonnelModel> mapVisitPersonnelModelListForVisit(List<VisitPersonnel> visitPersonnelList) {
        return Mappers.getMapper(VisitPersonnelMapper.class).mapVisitPersonnelModelListForVisit(visitPersonnelList);
    }

    default List<VisitLocationModel> mapVisitLocationModelList(List<VisitLocation> visitLocationList) {
        final List<VisitLocationModel> visitLocationModelList =
            Mappers.getMapper(VisitLocationMapper.class).visitLocationListToVisitLocationModelList(visitLocationList);
        if (CollectionUtils.isNotEmpty(visitLocationList)) {
            return visitLocationModelList.stream()
                .sorted(Comparator.comparing(VisitLocationModel::getStartDateTime))
                .toList();
        }
        return Collections.emptyList();
    }
}
