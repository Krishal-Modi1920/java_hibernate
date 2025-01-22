package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitServiceBasicInfoModel;
import org.baps.api.vtms.models.VisitServiceModel;
import org.baps.api.vtms.models.VisitServiceWrapperModel;
import org.baps.api.vtms.models.VisitTourModel;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
public interface VisitServiceMapper {

    @Mapping(target = "visitServiceId", ignore = true)
    @Mapping(target = "meetingPersonnel", ignore = true)
    VisitService visitServiceModelToVisitService(VisitServiceModel visitServiceModel);

    @Mapping(target = "visitServiceId", ignore = true)
    @Mapping(target = "meetingPersonnel", ignore = true)
    void visitServiceModelToVisitService(@MappingTarget VisitService visitService, VisitServiceModel visitServiceModel);

    @Mapping(target = "visitServiceId", ignore = true)
    VisitService visitTourModelToVisitService(VisitTourModel visitTourModel);

    @Mapping(target = "visitServiceId", ignore = true)
    VisitService visitTourModelToVisitService(@MappingTarget VisitService existingVisitService, VisitTourModel visitTourModel);

    @Mapping(target = "serviceTemplateId", source = "visitService.serviceTemplate.serviceTemplateId")
    @Mapping(target = "serviceTemplateName", source = "visitService.serviceTemplate.name")
    @Mapping(target = "coordinator", expression = "java(coordinatorRoleTagEnum != null ? mapServiceCoordinator("
        + "visitService.getVisitPersonnelList(), coordinatorRoleTagEnum) : null)")
    @Mapping(target = "serviceType", source = "visitService.serviceTemplate.serviceTypeEnum")
    @Mapping(target = "visitLocationModelList", source = "visitService.visitLocationList")
    VisitServiceModel visitServiceToVisitServiceModel(VisitService visitService, RoleTagEnum coordinatorRoleTagEnum);

    @Mapping(target = "serviceTemplateName", source = "visitService.serviceTemplate.name")
    @Mapping(target = "visitLocationBasicInfoModelList", source = "visitService.visitLocationList")
    @Mapping(source = "visitPersonnelList", target = "visitPersonnelModelList", qualifiedByName = "mapVisitPersonnelModelList")
    VisitServiceBasicInfoModel visitServiceToVisitServiceBasicInfoModel(VisitService visitService);

    List<VisitServiceBasicInfoModel> visitServiceListToVisitServiceBasicInfoModelList(List<VisitService> visitServiceList);

    default VisitPersonnelModel mapServiceCoordinator(List<VisitPersonnel> visitPersonnelList, RoleTagEnum roleTagEnum) {
        return Mappers.getMapper(VisitPersonnelMapper.class)
            .mapVisitPersonnelModelListByRoleTagList(visitPersonnelList, roleTagEnum).get(0);
    }

    @Named("mapVisitPersonnelModelList")
    default List<VisitPersonnelModel> mapVisitPersonnelModelList(List<VisitPersonnel> visitPersonnelList) {
        return Mappers.getMapper(VisitPersonnelMapper.class).visitPersonnelListToVisitPersonnelModelList(visitPersonnelList);
    }

    default PersonnelBasicInfoModel mapMeetingPersonnel(Personnel personnel) {
        return Mappers.getMapper(PersonnelMapper.class).personnelToPersonnelBasicInfoModel(personnel);
    }

    VisitServiceWrapperModel visitServiceModelToVisitServiceWrapperModel(VisitServiceModel visitServiceModel);

    default VisitServiceWrapperModel visitServiceModelListToVisitServiceWrapperModel(List<VisitServiceModel> visitServiceModelList) {
        if (CollectionUtils.isNotEmpty(visitServiceModelList)) {
            final VisitServiceWrapperModel visitServiceWrapperModel = visitServiceModelToVisitServiceWrapperModel(
                visitServiceModelList.get(0));
            visitServiceWrapperModel.setVisitServiceModelList(visitServiceModelList);
            return visitServiceWrapperModel;
        }
        return null;
    }

    default List<VisitServiceModel> visitServiceListToVisitServiceModelListByTag(List<VisitService> visitServiceList,
                                                                                 RoleTagEnum serviceCoordinatorRoleTagEnum) {
        return visitServiceList.stream().map(existingVisitService ->
                visitServiceToVisitServiceModel(existingVisitService, serviceCoordinatorRoleTagEnum))
            .sorted(Comparator.comparing(VisitServiceModel::getStartDateTime)).toList();
    }

    default List<VisitLocationModel> mapVisitLocationModelList(List<VisitLocation> visitLocationList) {
        if (CollectionUtils.isNotEmpty(visitLocationList)) {
            return Mappers.getMapper(VisitLocationMapper.class)
                    .visitLocationListToVisitLocationModelList(visitLocationList);
        }
        return Collections.emptyList();
    }
    
    default List<String> mapServiceNameList(List<VisitService> visitServiceList) {
        if (CollectionUtils.isNotEmpty(visitServiceList)) {
            return visitServiceList.stream().map(visitService -> 
                visitService.getServiceTemplate().getName()
            ).toList();
        }
        return Collections.emptyList();
    }

}
