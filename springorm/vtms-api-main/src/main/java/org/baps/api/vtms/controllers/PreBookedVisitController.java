package org.baps.api.vtms.controllers;

import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.CreatePreBookedVisitModel;
import org.baps.api.vtms.models.PreBookedVisitBasicModel;
import org.baps.api.vtms.models.PreBookedVisitModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.services.PreBookedVisitService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import jakarta.validation.constraints.Min;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Pre-Booked Visit")
@RequestMapping("/pre-booked")
@Validated
public class PreBookedVisitController {

    private final PreBookedVisitService preBookedVisitService;

    @Operation(summary = "Create pre-booked visit")
    @PostMapping(value = "/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<CreatePreBookedVisitModel> createPreBookedVisit(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @Valid @RequestBody final CreatePreBookedVisitModel createPreBookedVisitModel) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(preBookedVisitService.createPreBookedVisit(createPreBookedVisitModel, siteUUCode));
    }

    @Operation(summary = "Update pre-booked visit")
    @PutMapping(value = "/{visitId}", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_PRE_BOOKED_VISIT) "
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<PreBookedVisitModel> updatePrebookedVisit(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode, @PathVariable("visitId") final String visitId,
        @Valid @RequestBody final PreBookedVisitModel preBookedVisitModel) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(preBookedVisitService.createOrUpdatePreBookedVisit(visitId, preBookedVisitModel, siteUUCode));
    }

    @Operation(summary = "Get pre-booked visit")
    @GetMapping(value = "/{visitId}", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_PRE_BOOKED_VISIT)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<PreBookedVisitModel> getPrebookedVisit(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode, @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(preBookedVisitService.getPreBookedVisit(visitId, siteUUCode));
    }

    @Operation(summary = "Get paginated prebooked visits by filters")
    @GetMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_PRE_BOOKED_VISIT_LIST) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<PaginatedResponse<List<PreBookedVisitBasicModel>>> getPaginatedPreBookedVisitsWithFilters(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @Min(1) @RequestParam(value = "pageNo") final Integer pageNo,
        @Min(1) @RequestParam(value = "pageSize") final Integer pageSize,
        @RequestParam(value = "search", required = false) final String search,
        @RequestParam(value = "tourSlotId") final String tourSlotId,
        @RequestParam(value = "visitStage", required = false) final String visitStage,
        @RequestParam(value = "selfAssignVisit", required = false) final boolean selfAssignVisit) {

        return ResponseEntity.status(HttpStatus.OK).body(preBookedVisitService.getPaginatedPreBookedVisitsWithFilters(
            pageNo, pageSize, visitStage, search, tourSlotId, siteUUCode, selfAssignVisit));
    }
}
