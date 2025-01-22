package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.VisitTourModel;
import org.baps.api.vtms.services.VisitTourService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@Tag(name = "Visit Tour")
@RequestMapping("/visits/{visitId}")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitTourController {

    private final VisitTourService visitTourService;
    
    @Operation(summary = "Create visit tour")
    @PostMapping(value = "/tours", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).ADD_VISIT_TOUR, #siteUUCode)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.TOUR_GUIDE, RoleTagEnum.TOUR_COORDINATOR})
    public ResponseEntity<VisitTourModel> createVisitTour(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId, @Valid @RequestBody final VisitTourModel visitTourModel) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(visitTourService.createVisitTour(visitId, visitTourModel, siteUUCode));
    }

    @Operation(summary = "update visit tour")
    @PutMapping(value = "/tours", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_VISIT_TOUR, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.TOUR_GUIDE, RoleTagEnum.TOUR_COORDINATOR})
    public ResponseEntity<VisitTourModel> updateVisitTour(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId,  @Valid @RequestBody final VisitTourModel visitTourModel) {

        return ResponseEntity.status(HttpStatus.OK).body(visitTourService.updateVisitTour(visitId, visitTourModel, siteUUCode));

    }

    @Operation(summary = "Get visit tour")
    @GetMapping(value = "/tours", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_TOUR, #siteUUCode)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitTourModel> getVisitTourByVisitId(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitTourService.getVisitTourByVisitId(visitId, siteUUCode));
    }

}
