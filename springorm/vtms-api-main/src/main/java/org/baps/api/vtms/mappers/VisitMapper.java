package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.ServiceTemplateBasicInfoModel;
import org.baps.api.vtms.models.VisitBasicInfoModel;
import org.baps.api.vtms.models.VisitModel;
import org.baps.api.vtms.models.VisitorBasicInfoModel;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;
import org.baps.api.vtms.models.entities.VisitVisitor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
public interface VisitMapper {

    @Mapping(target = "visitId", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "visitTypeEnum", constant = "VISIT")
    @Mapping(target = "requestedServiceIds", expression = "java(mapRequestedServiceIds(visitModel.getRequestedServices()))")
    @Mapping(target = "totalVisitors", expression = "java(mapTotalVisitors(visitModel))")
    Visit visitModelToVisit(@MappingTarget Visit visit, VisitModel visitModel);

    @Mapping(source = "visit.visitTypeEnum", target = "type")
    @Mapping(source = "visit.visitStageEnum", target = "stage")
    @Mapping(target = "primaryVisitorModel", expression =
        "java(mapVisitorModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.PRIMARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "secondaryVisitorModel", expression =
        "java(mapVisitorModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.SECONDARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "relationshipManagerPersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.RELATIONSHIP_MANAGER,"
            + " visit.getVisitPersonnelList()))")
    @Mapping(target = "guestVisitCoordinatorPersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.GUEST_VISIT_COORDINATOR,"
            + " visit.getVisitPersonnelList()))")
    @Mapping(target = "tourSlotId", source = "tourSlot.tourSlotId")
    @Mapping(target = "publicFeedbackId", source = "visitPublicFeedback.visitPublicFeedbackId")
    VisitModel visitToVisitModel(Visit visit);

    @Mapping(source = "visit.visitTypeEnum", target = "type")
    @Mapping(source = "visit.visitStageEnum", target = "stage")
    @Mapping(target = "primaryVisitorModel", expression =
        "java(mapVisitorBasicInfoModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.PRIMARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "secondaryVisitorModel", expression =
        "java(mapVisitorBasicInfoModelByVisitContactType(org.baps.api.vtms.enumerations.VisitorContactTypeEnum.SECONDARY,"
            + " visit.getVisitVisitorList()))")
    @Mapping(target = "relationshipManagerPersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.RELATIONSHIP_MANAGER,"
            + " visit.getVisitPersonnelList()))")
    @Mapping(target = "guestVisitCoordinatorPersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.GUEST_VISIT_COORDINATOR,"
        + " visit.getVisitPersonnelList()))")
    @Mapping(target = "guestUsherPersonnelBasicInfoModel", expression =
        "java(mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(org.baps.api.vtms.enumerations.RoleEnum.GUEST_USHER,"
            + " visit.getVisitPersonnelList()))")
    @Mapping(target = "serviceNameList", expression = "java(mapServiceNameList(visit.getVisitServiceList()))")
    @Mapping(target = "tourType", source = "visit.tourType")
    @Mapping(target = "meetingPersonnelList", expression = "java(mapMeetingPersonnelList(visit.getVisitServiceList()))")
    VisitBasicInfoModel visitToVisitBasicInfo(Visit visit);

    default List<String> mapMeetingPersonnelList(List<VisitService> visitServiceList) {
        return visitServiceList.stream().filter(Objects::nonNull)
            .filter(visitService -> visitService.getServiceTemplate().getServiceTypeEnum().equals(ServiceTypeEnum.MEETING))
            .map(visitService -> visitService.getMeetingPersonnel().getFirstName() + " " + visitService.getMeetingPersonnel().getLastName())
            .toList();
    }

    default List<VisitModel> visitListToVisitModelList(List<Visit> visitList) {
        return visitList.stream().map(this::visitToVisitModel).toList();
    }

    default List<VisitBasicInfoModel> visitListToVisitBasicInfoList(List<Visit> visitList) {
        return visitList.stream().map(this::visitToVisitBasicInfo).toList();
    }

    default VisitorModel mapVisitorModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                           List<VisitVisitor> visitVisitors) {
        return Mappers.getMapper(VisitorMapper.class).mapVisitorModelByVisitContactType(visitorContactTypeEnum, visitVisitors);
    }

    default VisitorBasicInfoModel mapVisitorBasicInfoModelByVisitContactType(VisitorContactTypeEnum visitorContactTypeEnum,
                                                                             List<VisitVisitor> visitVisitors) {
        return Mappers.getMapper(VisitorMapper.class).mapVisitorBasicInfoModelByVisitContactType(visitorContactTypeEnum, visitVisitors);
    }

    default PersonnelBasicInfoModel mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(RoleEnum roleEnum,
                                                                                       List<VisitPersonnel> visitPersonnelList) {
        return Mappers.getMapper(PersonnelMapper.class).mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(roleEnum, visitPersonnelList);
    }
    
    default VisitBasicInfoModel convertVisitToVisitBasicInfo(Visit visit) {
        
        final var visitBasicInfoModel = new VisitBasicInfoModel();
        visitBasicInfoModel.setVisitId(visit.getVisitId());
        visitBasicInfoModel.setRequestNumber(visit.getRequestNumber());
        visitBasicInfoModel.setTotalVisitors(visit.getTotalVisitors());
        visitBasicInfoModel.setStartDateTime(visit.getStartDateTime());
        visitBasicInfoModel.setEndDateTime(visit.getEndDateTime());
        visitBasicInfoModel.setPrimaryVisitorModel(Mappers.getMapper(VisitorMapper.class)
                .mapVisitorBasicInfoModelByVisitContactTypeForVisitFeedback(VisitorContactTypeEnum.PRIMARY, visit.getVisitVisitorList()));
        
        return visitBasicInfoModel;
    }

    default Set<String> mapRequestedServiceIds(List<ServiceTemplateBasicInfoModel> serviceTemplateBasicInfoModelList) {
        return Mappers.getMapper(ServiceTemplateMapper.class)
                .serviceTemplateModelListToServiceTemplateIdSet(serviceTemplateBasicInfoModelList);
    }
    
    default Integer mapTotalVisitors(VisitModel visitModel) {
        Integer totalVisitor = 0;
        if (ObjectUtils.isNotEmpty(visitModel.getChildFemaleCount())) {
            totalVisitor = totalVisitor + visitModel.getChildFemaleCount();
        }
        if (ObjectUtils.isNotEmpty(visitModel.getChildMaleCount())) {
            totalVisitor = totalVisitor + visitModel.getChildMaleCount();
        }
        if (ObjectUtils.isNotEmpty(visitModel.getSeniorFemaleCount())) {
            totalVisitor = totalVisitor + visitModel.getSeniorFemaleCount();
        }
        if (ObjectUtils.isNotEmpty(visitModel.getSeniorMaleCount())) {
            totalVisitor = totalVisitor + visitModel.getSeniorMaleCount();
        }
        if (ObjectUtils.isNotEmpty(visitModel.getAdultFemaleCount())) {
            totalVisitor = totalVisitor + visitModel.getAdultFemaleCount();
        }
        if (ObjectUtils.isNotEmpty(visitModel.getAdultMaleCount())) {
            totalVisitor = totalVisitor + visitModel.getAdultMaleCount();
        }
        return totalVisitor;
    }

    default List<String> mapServiceNameList(List<VisitService> visitServiceList) {
        return Mappers.getMapper(VisitServiceMapper.class).mapServiceNameList(visitServiceList);
    }
}
