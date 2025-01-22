package org.baps.api.vtms.services;

import org.baps.api.vtms.enumerations.PermissionEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.SystemResponseStatusEnum;
import org.baps.api.vtms.models.responses.APIResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class AuthService {

    private final PersonnelService personnelService;
    
    private final VisitPersonnelService visitPersonnelService;
    
    /**
     * Checks if the currently logged-in personnel has the specified permission.
     *
     * @param validatePermissionEnum The permission to validate.
     * @return True if the personnel has the permission, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(final PermissionEnum validatePermissionEnum) {

        return personnelService.getLoginedPersonnel().getPersonnelRoleList().stream()
                .flatMap(personnelRole -> personnelRole.getRole().getRolePermissionList().stream())
                .map(rolePermission -> rolePermission.getPermission().getPermissonEnum())
                .collect(Collectors.toSet()).contains(validatePermissionEnum);
    }

    /**
     * Checks if the currently logged-in personnel has the specified permission for a specific visit.
     *
     * @param visitId                The ID of the visit.
     * @param validatePermissionEnum The permission to validate.
     * @param siteUUCode             The unique code of the site related to the visit.
     * @return True if the personnel has the permission for the visit, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean hasVisitPermission(final String visitId, final PermissionEnum validatePermissionEnum,
                                      final String siteUUCode) {

        final var personnel = personnelService.getLoginedPersonnel();

        final var visitPersonnelList = visitPersonnelService
                .findVisitPersonnelListByVisitIdAndSiteUucodeAndPersonnelId(visitId, siteUUCode, personnel.getPersonnelId());

        final Set<PermissionEnum> permissionEnumList = new HashSet<>();

        personnel.getPersonnelRoleList().forEach(personnelRole -> {
            if (personnelRole.getRole().isCheckSystemRole()) {
                permissionEnumList.addAll(personnelRole.getRole().getRolePermissionList().stream()
                        .map(rolePermission -> rolePermission.getPermission().getPermissonEnum()).collect(Collectors.toSet()));
            }
        });

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            visitPersonnelList.forEach(visitPersonnel -> {
                if (visitPersonnel.getPersonnel().equals(personnel)) {
                    permissionEnumList.addAll(visitPersonnel.getRole().getRolePermissionList().stream()
                            .map(rolePermission -> rolePermission.getPermission().getPermissonEnum()).collect(Collectors.toSet()));
                }
            });
        }

        return permissionEnumList.contains(validatePermissionEnum);
    }
    
    /**
     * Checks if the logged-in personnel has the system permission for a specific visit and service.
     *
     * @param visitId               The identifier for the visit.
     * @param siteUUCode            The site's unique universal code.
     * @param validatePermissionEnum  The permission to validate.
     * @param response              The response object to be returned.
     * @param httpStatus            The HTTP status to be returned.
     * @param beforeUpdateRoleTagEnumSet        The set of before update role tag enums to be validated.
     * @param updatedRoleTagEnumSet            The set of updated role tag enums to be validated.
     * @return                      A ResponseEntity containing the APIResponse based on permission validation.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> checkVisitPermissionAndCompareRoleTags(final String visitId, final String siteUUCode, 
            final PermissionEnum validatePermissionEnum, final Object response, final HttpStatus httpStatus,
            final Set<RoleTagEnum> beforeUpdateRoleTagEnumSet, final Set<RoleTagEnum> updatedRoleTagEnumSet) {

        // Get the logged-in personnel
        final var personnel = personnelService.getLoginedPersonnel();
        
        // Retrieve the existing VisitService based on provided identifiers.
        final var visitPersonnelList = visitPersonnelService
                .findVisitPersonnelListByVisitIdAndSiteUucodeAndPersonnelId(visitId, siteUUCode, personnel.getPersonnelId());
        
        boolean personnelExistsInVisit = false;

        // Check if the logged-in personnel exists in the visit
        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            personnelExistsInVisit = true;
        }
        
        boolean hasPermission = false;
        
        if (!personnelExistsInVisit) {
            // Check if the personnel has the permission
            hasPermission = personnel.getPersonnelRoleList().stream()
                    .filter(personnelRole -> personnelRole.getRole().isCheckSystemRole())
                    .flatMap(personnelRole -> personnelRole.getRole().getRolePermissionList().stream())
                    .map(rolePermission -> rolePermission.getPermission().getPermissonEnum())
                    .anyMatch(permissionEnum -> permissionEnum.equals(validatePermissionEnum));
        }
        
        if (personnelExistsInVisit || hasPermission) {
            
            if (beforeUpdateRoleTagEnumSet.equals(updatedRoleTagEnumSet)) {
                final APIResponse apiResponse = new APIResponse(httpStatus, httpStatus.value(), "", response);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                final APIResponse apiResponse = new APIResponse(httpStatus, 
                        SystemResponseStatusEnum.CHECK_PERMISSION.getStatus(), "", response);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            }
            
        } else {
            final APIResponse apiResponse = new APIResponse(HttpStatus.NO_CONTENT, 
                    SystemResponseStatusEnum.PERSONNEL_NOT_EXISTS_IN_VISIT.getStatus(), "", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
    }
}