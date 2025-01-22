package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.enumerations.EmailSourceEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.PersonnelModel;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.PersonnelRole;
import org.baps.api.vtms.models.entities.TourSlotPersonnel;
import org.baps.api.vtms.models.entities.VisitPersonnel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
public interface PersonnelMapper {

    @Mapping(source = "personnelRoleList", target = "roleModelList")
    PersonnelModel personnelToPersonnelModel(Personnel personnel);

    PersonnelBasicInfoModel personnelToPersonnelBasicInfoModel(Personnel personnel);

    List<PersonnelModel> personnelListToPersonnelModelList(List<Personnel> personnelList);

    default List<RoleModel> mapRoleModelList(List<PersonnelRole> personnelRoleList) {

        List<RoleModel> roleModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(personnelRoleList)) {
            roleModelList = Mappers.getMapper(RoleMapper.class).roleListToRoleModelList(personnelRoleList.stream()
                .map(PersonnelRole::getRole).toList());
        }
        return roleModelList;
    }

    default PersonnelBasicInfoModel mapPersonnelBasicInfoModelByVisitPersonnelRoleEnum(RoleEnum roleEnum,
                                                                                       List<VisitPersonnel> visitPersonnelList) {
        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            return visitPersonnelList.stream()
                .filter(visitPersonnel -> visitPersonnel.getRole().getUucode().equals(roleEnum.name()))
                .findFirst().map(visitPersonnel -> this.personnelToPersonnelBasicInfoModel(visitPersonnel.getPersonnel()))
                .orElse(null);
        }
        return null;
    }

    default PersonnelBasicInfoModel mapPersonnelBasicInfoModelByTourSlotPersonnel(List<TourSlotPersonnel> tourSlotPersonnelList) {
        if (CollectionUtils.isNotEmpty(tourSlotPersonnelList)) {
            return tourSlotPersonnelList.stream()
                .findFirst().map(tourSlotPersonnel -> this.personnelToPersonnelBasicInfoModel(tourSlotPersonnel.getPersonnel()))
                .orElse(null);
        }
        return null;
    }

    default String mapEmailByPersonnel(Personnel personnel) {
        String email = "";
        if (ObjectUtils.isNotEmpty(personnel)) {
            if (personnel.getEmailSourceEnum().equals(EmailSourceEnum.BAPS)) {
                email = personnel.getEmail();
            } else if (personnel.getEmailSourceEnum().equals(EmailSourceEnum.PERSONAL)) {
                email = personnel.getPersonalEmail();
            }
        }
        return email;
    }
}
