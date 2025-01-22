package org.baps.api.vtms.controllers;


import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.models.StageModel;
import org.baps.api.vtms.models.VisitBasicInfoModel;
import org.baps.api.vtms.models.VisitModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.services.VisitService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Visit")
@RequestMapping("/visits")
@Validated
public class VisitController {

    private final VisitService visitService;

    @Operation(summary = "Create visit")
    @PostMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).ADD_VISIT)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitModel> createVisit(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
           @Valid @RequestBody final VisitModel visitModel) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createUpdateVisit(null, visitModel, siteUUCode, true));
    }
    
    @Operation(summary = "Create public visit")
    @PostMapping(value = "/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitModel> createPublicVisit(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
           @Valid @RequestBody final VisitModel visitModel) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createUpdateVisit(null, visitModel, siteUUCode, false));
    }

    @Operation(summary = "Update visit")
    @PutMapping(value = "/{visitId}", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_VISIT, #siteUUCode)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    @VisitAopAnnotation(roleTags = {RoleTagEnum.TEAM})
    public ResponseEntity<VisitModel> updateVisit(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId,
            @Valid @RequestBody final VisitModel visitModel) {

        return ResponseEntity.status(HttpStatus.OK).body(visitService.createUpdateVisit(visitId, visitModel, siteUUCode, null));
    }

    @Operation(summary = "Get visit by id")
    @GetMapping(value = "/{visitId}", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT, #siteUUCode)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitModel> getVisit(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitService.getById(visitId, siteUUCode, true));
    }

    @Operation(summary = "Get paginated visits by sorting & filters")
    @GetMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_LIST) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<PaginatedResponse<List<VisitBasicInfoModel>>> getPaginatedVisitsWithFilters(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @Min(1) @RequestParam(value = "pageNo") final Integer pageNo,
            @Min(1) @RequestParam(value = "pageSize") final Integer pageSize,
            @RequestParam(required = false) final String search,
            @RequestParam(value = "visitStage", required = false) final String visitStage,
            @RequestParam(value = "typeOfVisit", required = false) final String typeOfVisit,
            @RequestParam(value = "visitorType", required = false) final String visitorType,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "startDateTime", required = false) final LocalDateTime startDateTime,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "endDateTime", required = false) final LocalDateTime endDateTime,
            @EnumValue(enumClass = Sort.Direction.class)
            @RequestParam(value = "sortDirection", required = false) final String sortDirection,
            @RequestParam(value = "sortProperty", required = false) final String sortProperty,
            @RequestParam(value = "selfAssignVisit", required = false) final boolean selfAssignVisit) {

        return ResponseEntity.ok(visitService.getPaginatedVisitsWithFilters(pageNo, pageSize,
                sortDirection, sortProperty, search, visitStage, typeOfVisit, startDateTime, endDateTime, siteUUCode, selfAssignVisit,
            visitorType));
    }

    @Operation(summary = "Update visit stage")
    @PutMapping(value = "/{visitId}/stage", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_VISIT_STAGE,"
            + " #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Void> updateVisitStage(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId,
            @Valid @RequestBody final StageModel stageModel) {

        visitService.updateVisitStage(visitId, stageModel, false, siteUUCode);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all visit stage history")
    @GetMapping(value = "/{visitId}/stage", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_STAGE_LIST, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<StageModel>> getAllVisitStageHistoryByVisitId(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitService.getAllVisitStageHistoryByVisitId(visitId, siteUUCode));
    }

    @Operation(summary = "Get Visit Personnel Role and Permissions")
    @GetMapping(value = "/{visitId}/personnel", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<RoleModel>> getVisitPersonnelRolesAndPermission(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitService.getVisitPersonnelRolesAndPermission(visitId, siteUUCode));
    }

    @Operation(summary = "Get public visit by id")
    @GetMapping(value = "/{visitId}/public", headers = GeneralConstant.API_VERSION_V1)
    public ResponseEntity<VisitModel> getPublicVisit(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitService.getById(visitId, siteUUCode, false));
    }

    @Operation(summary = "Get public visit booking-details by requestNumber and filter")
    @GetMapping(value = "/booking-details/{requestNumber}/public", headers = GeneralConstant.API_VERSION_V1)
    public ResponseEntity<VisitModel> getVisitBookingDetails(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) 
        final String siteUUCode, @PathVariable("requestNumber") final String requestNumber,
        @RequestParam(value = "email", required = false) final String email,
        @RequestParam(value = "phoneNumber", required = false) final String phoneNumber,
        @RequestParam(value = "lastName", required = false) final String lastName) {

        return ResponseEntity.ok(visitService.getVisitBookingDetails(requestNumber, siteUUCode, email, phoneNumber,
                lastName));
    }
}
