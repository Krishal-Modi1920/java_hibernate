package org.baps.api.vtms.controllers;


import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.services.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Role")
@RequestMapping("/roles")
@Validated
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Get all roles filters")
    @GetMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_ROLE_LIST)")
    public ResponseEntity<List<RoleModel>> getAllRolesWithFilters(
        @RequestParam(value = "filter", required = false) final String filter) {
        
        System.err.println(roleService.findByRoleEnum(RoleEnum.SUPER_ADMIN).getName());

        final String decodeFilter = StringUtils.isNotBlank(filter) ? CommonUtils.decoderUTF(filter) : null;

        return ResponseEntity.ok(roleService.getAllRolesWithFilters(decodeFilter));
    }
}
