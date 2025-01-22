package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitInterviewSetupModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitServiceBasicInfoModel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;

import java.util.Collections;
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
public interface VisitInterviewSetupMapper {

    @Mapping(source = "visitPersonnelList", target = "interviewCoordinatorVisitPersonnelModel")
    @Mapping(source = "visitServiceList", target = "visitServiceBasicInfoModelList")
    VisitInterviewSetupModel visitToVisitInterviewSetupModel(Visit visit);

    default VisitPersonnelModel mapInterviewCoordinatorVisitPersonnelModel(List<VisitPersonnel> visitPersonnelList) {

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            return visitPersonnelList.stream()
                .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR))
                .findFirst()
                .map(visitPersonnel ->
                    Mappers.getMapper(VisitPersonnelMapper.class)
                        .visitPersonnelToVisitPersonnelModel(visitPersonnel)
                ).orElse(null);
        }
        return null;
    }

    default List<VisitServiceBasicInfoModel> mapVisitServiceBasicInfoModelList(List<VisitService> visitServiceList) {

        if (CollectionUtils.isNotEmpty(visitServiceList)) {
            return Mappers.getMapper(VisitServiceMapper.class).visitServiceListToVisitServiceBasicInfoModelList(visitServiceList);
        }

        return Collections.emptyList();
    }
}
