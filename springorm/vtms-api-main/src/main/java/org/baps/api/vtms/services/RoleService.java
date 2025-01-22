package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.RoleMapper;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleMapper roleMapper;

    private final RoleRepository roleRepository;

    private final Translator translator;

    /**
     * Retrieves a list of RoleModel objects based on the specified RSQL filter.
     *
     * @param filter A filter expression in RSQL format (nullable).
     * @return A list of matching RoleModel objects or an empty list.
     */
    @Transactional(readOnly = true)
    public List<RoleModel> getAllRolesWithFilters(@Nullable final String filter) {

        final Specification<Role> specification = RSQLJPASupport.toSpecification(filter);

        final List<Role> roleList = roleRepository.findAll(specification);

        return roleMapper.roleListToRoleModelList(roleList);
    }

    /**
     * Retrieves a mapping of Role IDs to Role entities based on a list of Role IDs and Role Tags.
     *
     * @param roleIds         A list of Role IDs to retrieve Role entities for.
     * @param roleTagEnumList A list of Role Tags to filter Role entities by.
     * @return A mapping of Role IDs to Role entities.
     * @throws DataNotFoundException If any of the specified Role IDs are not found or do not have the specified Role Tags.
     */
    @Transactional(readOnly = true)
    public Map<String, Role> getMapOfRoleByRoleIdsAndTag(final List<String> roleIds, final List<RoleTagEnum> roleTagEnumList) {
        // Find existing Role entities based on Role IDs and ACTIVE Role Tags
        final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleRepository
            .findByRoleIdInAndRoleTagListRoleTagEnumInAndRoleTagListStatusNot(
                roleIds, roleTagEnumList, Status.DELETED)
            .stream().collect(Collectors.toMap(Role::getRoleId, r -> r));

        // Check if the mapping is not empty
        if (MapUtils.isNotEmpty(mapOfRoleIdWithExistingRoleList)) {
            // Find Role IDs that were not found in the database
            final List<String> invalidRoleIds = roleIds.stream()
                .filter(roleId -> !mapOfRoleIdWithExistingRoleList.containsKey(roleId))
                .toList();

            // If invalid Role IDs were found, throw a DataNotFoundException
            if (CollectionUtils.isNotEmpty(invalidRoleIds)) {
                throw new DataNotFoundException(translator.toLocal("role.with.role_id.and.role_tag.not.found", 
                        invalidRoleIds, roleTagEnumList));
            }
        } else {
            // If no Role entities were found for the specified IDs, throw a DataNotFoundException
            throw new DataNotFoundException(translator.toLocal("role.with.role_id.and.role_tag.not.found", roleIds, roleTagEnumList));
        }

        // Return the mapping of Role IDs to Role entities
        return mapOfRoleIdWithExistingRoleList;
    }

    /**
     * Finds a role by its corresponding RoleEnum value.
     *
     * @param roleEnum The RoleEnum value to search for.
     * @return The Role object if found, or null if not found.
     * @throws DataNotFoundException If no role with the specified RoleEnum value is found.
     */
    @Transactional(readOnly = true)
    public Role findByRoleEnum(final RoleEnum roleEnum) {

        if (ObjectUtils.isEmpty(roleEnum)) {
            throw new DataNotFoundException(translator.toLocal("role.with.uucode.not.found"));
        }

        return roleRepository.findFirstByUucode(roleEnum.name())
            .orElseThrow(() -> new DataNotFoundException(translator.toLocal("role.with.uucode.not.found", roleEnum)));
    }

    /**
     * Find a Role by its unique identifier.
     *
     * @param roleId The unique identifier of the Role to find.
     * @return The Role object if found.
     * @throws DataNotFoundException If the specified identifier is empty or if no Role is found with the given identifier.
     */
    @Transactional(readOnly = true)
    public Role findById(final String roleId) {

        if (StringUtils.isEmpty(roleId)) {
            throw new DataNotFoundException(translator.toLocal("role.with.role_id.not.found", roleId));
        }

        return roleRepository.findById(roleId)
            .orElseThrow(() -> new DataNotFoundException(translator.toLocal("role.with.role_id.not.found", roleId)));
    }
}
