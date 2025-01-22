package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.UniquePersonnelIdForRoleId;
import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.services.VisitTeamService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Visit Team")
@RequestMapping("/visits/{visitId}")
@Validated
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitTeamController {

    private final VisitTeamService visitTeamService;

    @Operation(summary = "update team to visit")
    @PutMapping(value = "/teams", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_VISIT_TEAM, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.TEAM})
    public ResponseEntity<List<VisitPersonnelModel>> updateVisitTeam(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId,
        @Valid @UniquePersonnelIdForRoleId @RequestBody final List<VisitPersonnelModel> visitPersonnelModelList) {
        
        return ResponseEntity.status(HttpStatus.OK).body(visitTeamService.createOrUpdateVisitTeam(visitId, 
                visitPersonnelModelList, siteUUCode));
    }

    @Operation(summary = "get all visit team")
    @GetMapping(value = "/teams", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_TEAM, #siteUUCode)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitPersonnelModel>> getAllVisitTeam(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitTeamService.getAllVisitTeam(visitId, siteUUCode));
    }

}