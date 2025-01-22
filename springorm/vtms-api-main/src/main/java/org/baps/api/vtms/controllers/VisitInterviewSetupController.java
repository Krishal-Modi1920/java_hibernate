package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitInterviewSetupModel;
import org.baps.api.vtms.services.VisitInterviewSetupService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/visits/{visitId}")
@Tag(name = "Visit Interview Setup")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitInterviewSetupController {

    private final VisitInterviewSetupService visitInterviewSetupService;

    @Operation(summary = "Get all interview setup by visit id")
    @GetMapping(value = "/interview-setup", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_INTERVIEW_SETUP,"
            + " #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitInterviewSetupModel> getAllVisitInterviewSetup(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitInterviewSetupService.getAllVisitInterviewSetup(visitId, siteUUCode));
    }

    @Operation(summary = "Update interview setup by visit id")
    @PutMapping(value = "/interview-setup", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".UPDATE_VISIT_INTERVIEW_SETUP, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER, RoleTagEnum.INTERVIEW_SETUP_COORDINATOR})
    public ResponseEntity<VisitInterviewSetupModel> updateVisitInterviewSetup(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId,
            @Valid @RequestBody final VisitInterviewSetupModel visitInterviewSetupModel) {
        
        return ResponseEntity.status(HttpStatus.OK).body(visitInterviewSetupService.updateVisitInterviewSetup(visitId,
                visitInterviewSetupModel, siteUUCode));
    }

    @Operation(summary = "Delete interview setup by visit id")
    @DeleteMapping(value = "/interview-setup", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".DELETE_VISIT_INTERVIEW_SETUP, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER, RoleTagEnum.INTERVIEW_SETUP_COORDINATOR})
    public ResponseEntity<Void> deleteVisitInterviewSetup(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        visitInterviewSetupService.deleteVisitInterviewSetup(visitId, siteUUCode);

        return ResponseEntity.noContent().build();
    }
}
