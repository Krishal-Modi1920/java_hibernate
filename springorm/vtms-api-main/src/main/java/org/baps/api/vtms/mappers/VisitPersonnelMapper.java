package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
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
public interface VisitPersonnelMapper {

    @Mapping(source = "personnel.personnelId", target = "personnelId")
    @Mapping(source = "personnel", target = "personnelName", qualifiedByName = "mapPersonnelName")
    @Mapping(source = "personnel", target = "phoneNumber", qualifiedByName = "mapPhoneNumber")
    @Mapping(target = "email", expression =
            "java(mapEmail(visitPersonnel.getPersonnel()))")
    @Mapping(source = "role.roleId", target = "roleId")
    @Mapping(source = "role.uucode", target = "roleUucode")
    @Mapping(source = "role.name", target = "roleName")
    VisitPersonnelModel visitPersonnelToVisitPersonnelModel(VisitPersonnel visitPersonnel);

    default List<VisitPersonnelModel> visitPersonnelListToVisitPersonnelModelList(List<VisitPersonnel> visitPersonnelList) {
        
        return Optional.ofNullable(visitPersonnelList)
                .map(list -> list.stream()
                        .sorted(Comparator.comparing(VisitPersonnel::getCreatedAt))
                        .map(this::visitPersonnelToVisitPersonnelModel)
                        .toList())
                .orElse(Collections.emptyList());
    }
    
    @Named("mapPersonnelName")
    default String mapPersonnelName(Personnel personnel) {
        return personnel.getFirstName() + " " + personnel.getLastName();
    }

    @Named("mapPhoneNumber")
    default String mapPhoneNumber(Personnel personnel) {
        return personnel.getPhoneCountryCode() + " " + personnel.getPhoneNumber();
    }

    default List<VisitPersonnelModel> mapVisitPersonnelModelListForVisit(List<VisitPersonnel> visitPersonnelList) {

        return visitPersonnelList.stream()
            .filter(existingVisitPersonnel -> ObjectUtils.isEmpty(existingVisitPersonnel.getVisitService()))
            .map(this::visitPersonnelToVisitPersonnelModel)
            .toList();
    }

    default List<VisitPersonnelModel> mapVisitPersonnelModelListByRoleTagList(List<VisitPersonnel> visitPersonnelList,
            RoleTagEnum roleTagEnum) {
        return visitPersonnelList.stream()
                .filter(existingVisitPersonnel -> existingVisitPersonnel.getRoleTagEnum().equals(roleTagEnum) 
                        && existingVisitPersonnel.getStatus().equals(Status.ACTIVE))
                .map(this::visitPersonnelToVisitPersonnelModel)
                .toList();
    }


    default VisitPersonnel createVisitPersonnel(Visit visit, Personnel personnel, Role role, RoleTagEnum roleTagEnum,
            VisitService visitService) {
        final var visitPersonnel = new VisitPersonnel();
        visitPersonnel.setVisit(visit);
        visitPersonnel.setPersonnel(personnel);
        visitPersonnel.setRole(role);
        visitPersonnel.setRoleTagEnum(roleTagEnum);
        visitPersonnel.setVisitService(visitService);
        return visitPersonnel;
    }

    default void updateVisitPersonnel(VisitPersonnel visitPersonnel, Personnel personnel, Role role, RoleTagEnum roleTagEnum) {
        visitPersonnel.setPersonnel(personnel);
        visitPersonnel.setRole(role);
        visitPersonnel.setRoleTagEnum(roleTagEnum);
    }
    
    default String mapEmail(Personnel personnel) {
        return Mappers.getMapper(PersonnelMapper.class).mapEmailByPersonnel(personnel);
    }
}
