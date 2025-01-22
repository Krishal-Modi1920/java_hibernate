package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitFeedbackModel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitFeedback;

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
public interface VisitFeedbackMapper {

    VisitFeedbackModel visitFeedbackToVisitFeedbackModel(@MappingTarget VisitFeedbackModel visitFeedbackModel, VisitFeedback visitFeedback);

    default VisitFeedbackModel visitToVisitFeedbackModel(Visit visit) {

        final var wrapperObject = new Object() {
            VisitFeedbackModel visitFeedbackModel = new VisitFeedbackModel();
        };

        wrapperObject.visitFeedbackModel.setVisitBasicInfoModel(Mappers.getMapper(VisitMapper.class).convertVisitToVisitBasicInfo(visit));

        if (CollectionUtils.isNotEmpty(visit.getVisitPersonnelList()) 
                && visit.getVisitPersonnelList().stream().anyMatch(visitPersonnel -> 
                visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.TOUR_GUIDE))) {
            wrapperObject.visitFeedbackModel.setTourGuideExists(true);
        }

        wrapperObject.visitFeedbackModel = visitFeedbackToVisitFeedbackModel(
                wrapperObject.visitFeedbackModel, visit.getVisitFeedback());

        if (CollectionUtils.isNotEmpty(visit.getVisitPersonnelList())) {
            visit.getVisitPersonnelList().stream()
                .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.TOUR_GUIDE))
                .findFirst()
                .ifPresent(visitPersonnel -> wrapperObject.visitFeedbackModel.setFeedBackRatingModelListForTourGuide(
                    visitPersonnel.getFeedBackRatingModelListForTourGuide()));
        }

        if (ObjectUtils.isNotEmpty(visit.getVisitFeedback()) 
                && CollectionUtils.isNotEmpty(visit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack())
                && !visit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack().toString().equals("null")) {
            
            wrapperObject.visitFeedbackModel.setFeedbackExists(true);

            return wrapperObject.visitFeedbackModel;
        }

        return wrapperObject.visitFeedbackModel;
    }

    @Mapping(target = "visitFeedbackId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void visitFeedbackModelToVisitFeedback(@MappingTarget VisitFeedback visitFeedback, VisitFeedbackModel visitFeedbackModel);
}
