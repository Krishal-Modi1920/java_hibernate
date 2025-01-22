package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitServiceModel;
import org.baps.api.vtms.models.VisitServiceWrapperModel;
import org.baps.api.vtms.services.VisitServiceService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Visit Service")
@RequestMapping("/visits/{visitId}")
@Validated
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitServiceController {

    private final VisitServiceService visitServiceService;

    @Operation(summary = "Add service to visit")
    @PostMapping(value = "/services", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).ADD_VISIT_SERVICE, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.SERVICE_COORDINATOR, RoleTagEnum.MEETING_COORDINATOR})
    public ResponseEntity<VisitServiceModel> createVisitService(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId,
        @Valid @RequestBody final VisitServiceModel visitServiceModel) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(visitServiceService.createVisitService(visitId, visitServiceModel,
                siteUUCode));
    }

    @Operation(summary = "Update visit service by visit service id ")
    @PutMapping(value = "/services/{visitServiceId}", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_VISIT_SERVICE, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.SERVICE_COORDINATOR, RoleTagEnum.MEETING_COORDINATOR})
    public ResponseEntity<VisitServiceModel> updateVisitService(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId,
        @PathVariable("visitServiceId") final String visitServiceId,
        @Valid @RequestBody final VisitServiceModel visitServiceModel) {
        
        return ResponseEntity.status(HttpStatus.OK).body(visitServiceService.updateVisitService(visitId, visitServiceId,
                visitServiceModel, siteUUCode));
    }

    @Operation(summary = "Delete service by service visit id")
    @DeleteMapping(value = "/services/{visitServiceId}", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).DELETE_VISIT_SERVICE, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.SERVICE_COORDINATOR, RoleTagEnum.MEETING_COORDINATOR})
    public ResponseEntity<Void> deleteVisitServiceById(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId,
            @PathVariable("visitServiceId") final String visitServiceId) {

        visitServiceService.deleteVisitServiceById(visitId, visitServiceId, siteUUCode);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all service by visit id")
    @GetMapping(value = "/services", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_SERVICE_LIST, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitServiceWrapperModel>> getVisitServiceList(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId, @RequestParam("serviceType") final String serviceType) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(visitServiceService.getVisitServiceListByVisitId(visitId, serviceType, siteUUCode));
    }

    @Operation(summary = "Get service by visitServiceId")
    @GetMapping(value = "/services/{visitServiceId}", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_SERVICE, "
            + "#siteUUCode)  && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitServiceModel> getVisitServiceById(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId, @PathVariable("visitServiceId") final String visitServiceId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitServiceService.getVisitServiceById(visitId,
            visitServiceId, siteUUCode));
    }
}
