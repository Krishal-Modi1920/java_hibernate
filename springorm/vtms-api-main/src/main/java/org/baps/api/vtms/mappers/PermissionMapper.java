package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.PermissionModel;
import org.baps.api.vtms.models.entities.Permission;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
    )
public interface PermissionMapper {


    @Mapping(source = "permissonEnum", target = "name")
    PermissionModel permissionToPermissionModel(Permission permission);
    
    List<PermissionModel> permissionListToPermissionModelList(List<Permission> permissionList);

}
