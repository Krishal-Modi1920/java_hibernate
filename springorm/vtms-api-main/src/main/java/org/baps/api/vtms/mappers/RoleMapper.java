package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.PermissionModel;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.models.RoleTagModel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.RolePermission;
import org.baps.api.vtms.models.entities.RoleTag;

import java.util.ArrayList;
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
public interface RoleMapper {

    @Mapping(source = "rolePermissionList", target = "permissionModelList")
    @Mapping(source = "roleTagList", target = "roleTagModelList")
    RoleModel roleToRoleModel(Role role);

    List<RoleModel> roleListToRoleModelList(List<Role> roleList);

    default List<PermissionModel> mapPermissionModelList(List<RolePermission> rolePermissionList) {
        List<PermissionModel> permissionModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(rolePermissionList)) {
            permissionModelList = Mappers.getMapper(PermissionMapper.class).permissionListToPermissionModelList(rolePermissionList.stream()
                .map(RolePermission::getPermission).toList());
        }

        return permissionModelList;
    }

    default List<RoleTagModel> mapRoleTagModelList(List<RoleTag> roleTagList) {
        return Mappers.getMapper(RoleTagMapper.class).roleTagListToRoleTagModelList(roleTagList);
    }
}
