package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.models.CreatePreBookedVisitModel;
import org.baps.api.vtms.models.CreatePreBookedVisitorModel;
import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.PreBookedVisitBasicModel;
import org.baps.api.vtms.models.PreBookedVisitModel;
import org.baps.api.vtms.models.PreBookedVisitorModel;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitVisitor;

import java.util.List;

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
public interface PreBookedVisitMapper {
    
    @Mapping(target = "visitId", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "visitTypeEnum", constant = "TOUR")
    @Mapping(target = "startDateTime", ignore = true)
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(target = "totalVisitors", ignore = true)
    Visit preBookedVisitModellToVisit(@MappingTarget Visit visit, PreBookedVisitModel preBookedVisitModel);

    @Mapping(target = "visitId", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "startDateTime", ignore = true)
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(target = "visitTypeEnum", constant = "TOUR")
    Visit createPreBookedVisitModellToVisit(CreatePreBookedVisitModel createPreBookedVisitModel);
    
    @Mapping(source = "visit.visitStageEnum", target = "stage")
    @Mapping(target = "primaryVisitorModel", expression =
        "java(mapPreBookedVisitorModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.PRIMARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "tourSlotId", source = "tourSlot.tourSlotId")
    PreBookedVisitModel visitToPreBookedVisitModel(Visit visit);

    @Mapping(target = "primaryVisitorModel", expression =
        "java(mapCreatePreBookedVisitorModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.PRIMARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "tourSlotId", source = "tourSlot.tourSlotId")
    @Mapping(target = "publicFeedbackId", source = "visitPublicFeedback.visitPublicFeedbackId")
    CreatePreBookedVisitModel visitToCreatePreBookedVisitModel(Visit visit);
    
    @Mapping(source = "visit.visitStageEnum", target = "stage")
    @Mapping(target = "primaryVisitorModel", expression =
        "java(mapVisitorModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.PRIMARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "tourGuidePersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.TOUR_GUIDE,"
        + " visit.getVisitPersonnelList()))")
    @Mapping(target = "tourSlotId", source = "tourSlot.tourSlotId")
    PreBookedVisitBasicModel visitToPreBookedVisitBasicModel(Visit visit);

    List<PreBookedVisitBasicModel> visitListToPreBookedVisitBasicModelList(List<Visit> visitList);

    default VisitorModel mapVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                           List<VisitVisitor> visitVisitors) {
        return Mappers.getMapper(VisitorMapper.class).mapVisitorModelByVisitContactType(visitorContactTypeEnum, visitVisitors);
    }

    default PreBookedVisitorModel mapPreBookedVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                                             List<VisitVisitor> visitVisitors) {
        return Mappers.getMapper(VisitorMapper.class).mapPreBookedVisitorModelByVisitContactType(visitorContactTypeEnum, visitVisitors);
    }

    default PersonnelBasicInfoModel mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(RoleEnum roleEnum,
                                                                                       List<VisitPersonnel> visitPersonnelList) {
        return Mappers.getMapper(PersonnelMapper.class).mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(roleEnum, visitPersonnelList);
    }

    default CreatePreBookedVisitorModel mapCreatePreBookedVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
            List<VisitVisitor> visitVisitors) {
        return Mappers.getMapper(VisitorMapper.class).mapCreatePreBookedVisitorModelByVisitContactType(
                visitorContactTypeEnum, visitVisitors);
    }
}
