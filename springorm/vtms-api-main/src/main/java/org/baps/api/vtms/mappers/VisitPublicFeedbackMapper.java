package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.VisitBookingFeedbackModel;
import org.baps.api.vtms.models.entities.VisitPublicFeedback;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true),
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface VisitPublicFeedbackMapper {

    @Mapping(target = "visitPublicFeedbackId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void visitBookingFeedbackModelToVisitPublicFeedback(@MappingTarget VisitPublicFeedback visitPublicFeedback,
            VisitBookingFeedbackModel visitBookedFeedbackModel);

    VisitBookingFeedbackModel visitPublicFeedbackToVisitBookingFeedbackModel(VisitPublicFeedback existingVisitPublicFeedback);
}
