package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitTourModel;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
public interface VisitTourMapper {

    @Mapping(target = "visitTourId", source = "visitService.serviceTemplate.serviceTemplateId")
    @Mapping(target = "serviceTemplateId", source = "visitService.visitServiceId")
    @Mapping(target = "serviceTemplateName", source = "visitService.serviceTemplate.name")
    @Mapping(target = "tourCoordinator", expression = "java(tourCoordinatorRoleTagEnum !=null ? "
        + "mapTourCoordinator(visitService.getVisitPersonnelList(), tourCoordinatorRoleTagEnum) : null)")
    @Mapping(target = "tourGuideList", expression = "java(tourGuideRoleTagEnum !=null ? "
        + "mapTourGuideList(visitService.getVisitPersonnelList(), tourGuideRoleTagEnum) : null)")
    @Mapping(target = "visitLocationModelList", source = "visitService.visitLocationList")
    @Mapping(target = "tourType", source = "visitService.visit.tourType")
    VisitTourModel visitServiceToVisitTourModel(VisitService visitService, RoleTagEnum tourCoordinatorRoleTagEnum,
                                                RoleTagEnum tourGuideRoleTagEnum);

    default VisitPersonnelModel mapTourCoordinator(List<VisitPersonnel> visitPersonnelList, RoleTagEnum roleTagEnum) {
        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            final List<VisitPersonnelModel> visitPersonnelModels =
                Mappers.getMapper(VisitPersonnelMapper.class).mapVisitPersonnelModelListByRoleTagList(
                    visitPersonnelList, roleTagEnum);
            if (CollectionUtils.isNotEmpty(visitPersonnelModels)) {
                return visitPersonnelModels.get(0);
            }
        }
        return null;
    }

    default List<VisitPersonnelModel> mapTourGuideList(List<VisitPersonnel> visitPersonnelList, RoleTagEnum roleTagEnum) {
        return Mappers.getMapper(VisitPersonnelMapper.class).mapVisitPersonnelModelListByRoleTagList(
            visitPersonnelList, roleTagEnum);
    }

    default List<VisitLocationModel> mapVisitLocationModelList(List<VisitLocation> visitLocationList) {
        return Mappers.getMapper(VisitLocationMapper.class).visitLocationListToVisitLocationModelList(
            visitLocationList);
    }
}
