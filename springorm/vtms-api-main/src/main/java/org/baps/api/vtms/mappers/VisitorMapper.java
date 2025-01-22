package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.models.CreatePreBookedVisitorModel;
import org.baps.api.vtms.models.PreBookedVisitorModel;
import org.baps.api.vtms.models.VisitorBasicInfoModel;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.models.entities.VisitVisitor;
import org.baps.api.vtms.models.entities.Visitor;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
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
public interface VisitorMapper {

    @Mapping(target = "visitorId", ignore = true)
    Visitor visitorModelToVisitor(@MappingTarget Visitor visitor, VisitorModel visitorModel);

    @Mapping(target = "visitorId", ignore = true)
    @Mapping(target = "gender", expression = "java(org.apache.commons.lang3.StringUtils.isBlank(preBookedVisitorModel.getGender()) "
            + "? \"\" : preBookedVisitorModel.getGender())")
    @Mapping(target = "phoneNumber", expression = "java(org.apache.commons.lang3.StringUtils.isBlank("
            + "preBookedVisitorModel.getPhoneNumber())? \"\" : preBookedVisitorModel.getPhoneNumber())")
    @Mapping(target = "phoneCountryCode", constant = "")
    Visitor visitorModelToPreBookedVisitorModel(@MappingTarget Visitor visitor, PreBookedVisitorModel preBookedVisitorModel);

    @Mapping(target = "visitorId", ignore = true)
    @Mapping(target = "salutation", constant = "")
    @Mapping(target = "gender", constant = "")
    @Mapping(target = "phoneCountryCode", constant = "")
    Visitor createPreBookedVisitorModelToVisitor(CreatePreBookedVisitorModel createPreBookedVisitorModel);

    VisitorModel visitorToVisitorModel(Visitor visitor);

    List<VisitorModel> visitorListToVisitorModelList(List<Visitor> visitorList);

    CreatePreBookedVisitorModel visitorToCreatePreBookedVisitorModel(Visitor visitor);

    PreBookedVisitorModel visitorToPreBookedVisitorModel(Visitor visitor);

    VisitorBasicInfoModel visitorToVisitorBasicInfoModel(Visitor visitor);

    default VisitorModel mapVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                           List<VisitVisitor> visitVisitors) {
        if (CollectionUtils.isNotEmpty(visitVisitors)) {
            final Optional<VisitVisitor> firstVisitVisitor = visitVisitors.stream()
                .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
                .findFirst();
            if (firstVisitVisitor.isPresent()) {
                return visitorToVisitorModel(firstVisitVisitor.get().getVisitor());
            }
        }
        return null;
    }

    default PreBookedVisitorModel mapPreBookedVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                           List<VisitVisitor> visitVisitors) {
        if (CollectionUtils.isNotEmpty(visitVisitors)) {
            final Optional<VisitVisitor> firstVisitVisitor = visitVisitors.stream()
                .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
                .findFirst();
            if (firstVisitVisitor.isPresent()) {
                return visitorToPreBookedVisitorModel(firstVisitVisitor.get().getVisitor());
            }
        }
        return null;
    }

    default CreatePreBookedVisitorModel mapCreatePreBookedVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                           List<VisitVisitor> visitVisitors) {
        if (CollectionUtils.isNotEmpty(visitVisitors)) {
            final Optional<VisitVisitor> firstVisitVisitor = visitVisitors.stream()
                .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
                .findFirst();
            if (firstVisitVisitor.isPresent()) {
                return visitorToCreatePreBookedVisitorModel(firstVisitVisitor.get().getVisitor());
            }
        }
        return null;
    }

    default VisitorBasicInfoModel mapVisitorBasicInfoModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                                        List<VisitVisitor> visitVisitors) {
        if (CollectionUtils.isNotEmpty(visitVisitors)) {
            return visitVisitors.stream()
                .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
                .findFirst().map(visitVisitor -> visitorToVisitorBasicInfoModel(visitVisitor.getVisitor()))
                .orElse(null);
        }
        return null;
    }
    


    default VisitorBasicInfoModel mapVisitorBasicInfoModelByVisitContactTypeForVisitFeedback(VisitorContactTypeEnum visitorContactTypeEnum,
            List<VisitVisitor> visitVisitors) {
        
        if (CollectionUtils.isNotEmpty(visitVisitors)) {
            final var existingPrimaryVisitor = visitVisitors.stream()
                    .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
                    .findFirst();

            if (existingPrimaryVisitor.isPresent()) {
                final VisitorBasicInfoModel visitorBasicInfoModel = new VisitorBasicInfoModel();
                
                final Visitor visitor = existingPrimaryVisitor.get().getVisitor();
                visitorBasicInfoModel.setFirstName(visitor.getFirstName());
                visitorBasicInfoModel.setLastName(visitor.getLastName());

                return visitorBasicInfoModel;
            }
        }
        return null;
    }
}
