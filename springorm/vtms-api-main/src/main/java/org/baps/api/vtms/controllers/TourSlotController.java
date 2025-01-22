package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.PreBookedTourSlotModel;
import org.baps.api.vtms.models.TourSlotModel;
import org.baps.api.vtms.models.TourSlotWrapperModel;
import org.baps.api.vtms.models.UpdateTourSlotStageModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.services.TourSlotService;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Tour Slots")
@RequestMapping("/tour-slots")
@Validated
public class TourSlotController {

    private final TourSlotService tourSlotService;

    @Operation(summary = "Create tour slots")
    @PostMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).CREATE_TOUR_SLOT)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<TourSlotWrapperModel> createTourSlot(@RequestHeader(GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
                                                               @Valid @RequestBody final TourSlotWrapperModel tourDaySlotWrapperModel) {

        return ResponseEntity.status(HttpStatus.CREATED).body(tourSlotService.createTourSlot(siteUUCode, tourDaySlotWrapperModel));
    }

    @Operation(summary = "Update tour slots stage")
    @PutMapping(value = "/{tourSlotId}", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_TOUR_SLOT)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<TourSlotModel> updateTourSlot(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
                                                        @PathVariable("tourSlotId") final String tourSlotId,
                                                        @Valid @RequestBody final TourSlotModel tourSlotModel) {

        return ResponseEntity.status(HttpStatus.OK).body(tourSlotService.updateTourSlot(tourSlotId, tourSlotModel, siteUUCode));
    }

    @Operation(summary = "Get tour slots")
    @GetMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_TOUR_SLOT_LIST)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<TourSlotWrapperModel> getTourSlot(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @RequestParam(value = "startDateTime")
        @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT) final LocalDateTime startDateTime,
        @RequestParam(value = "endDateTime")
        @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT) final LocalDateTime endDateTime) {

        return ResponseEntity.status(HttpStatus.OK).body(tourSlotService.getTourSlot(startDateTime, endDateTime, siteUUCode));
    }

    @Operation(summary = "Get public tour slots")
    @GetMapping(value = "/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<PreBookedTourSlotModel>> getPublicTourSlot(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @RequestParam(value = "tourDate")
        @DateTimeFormat(pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD) final LocalDate tourDate) {

        return ResponseEntity.status(HttpStatus.OK).body(tourSlotService.getPublicTourSlot(tourDate, siteUUCode));
    }

    @Operation(summary = "Update tour slots stage")
    @PutMapping(value = "/stages", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).UPDATE_TOUR_SLOT_STAGE)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Void> updateTourSlotStage(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
                                                    @Valid @RequestBody final UpdateTourSlotStageModel updateTourSlotStageModel) {

        tourSlotService.updateTourSlotStage(updateTourSlotStageModel, siteUUCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Check tour slot available for visit booking")
    @GetMapping(value = "/{tourSlotId}/check/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Map<String, Boolean>> checkTourSlot(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("tourSlotId") final String tourSlotId,
        @RequestParam("totalVisitors") @Min(1) @Max(10000) final Integer totalVisitors) {

        final Map<String, Boolean> mapOfCheckTourSlot = new HashMap<>();
        mapOfCheckTourSlot.put("isValid", tourSlotService.isTourSlotAvailable(tourSlotId, totalVisitors, siteUUCode));
        return ResponseEntity.status(HttpStatus.OK).body(mapOfCheckTourSlot);
    }

    @Operation(summary = "Get visit associated tour slots")
    @GetMapping(value = "/visits-associated", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION) 
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_TOUR_LIST_VISIT_ASSOCIATED) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<PaginatedResponse<List<TourSlotModel>>> getPaginatedVisitAssociatedTourSlot(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @Min(1) @RequestParam(value = "pageNo") final int pageNo,
            @Min(1) @RequestParam(value = "pageSize") final int pageSize,
            @RequestParam(required = false) final String search,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "startDateTime", required = false) final LocalDateTime startDateTime,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "endDateTime", required = false) final LocalDateTime endDateTime,
            @RequestParam(value = "visitStage", required = false) final String visitStage,
            @EnumValue(enumClass = Sort.Direction.class)
            @RequestParam(value = "sortDirection", required = false) final String sortDirection,
            @RequestParam(value = "sortProperty", required = false) final String sortProperty,
            @RequestParam(value = "selfAssignVisit") final boolean selfAssignVisit,
            @RequestParam(value = "hasVisit", required = false) final boolean hasVisit) {
        
        final String searchFilter = StringUtils.isNotBlank(search) ? CommonUtils.decoderUTF(search) : null;

        return ResponseEntity.status(HttpStatus.OK).body(tourSlotService.getPaginatedVisitAssociatedTourSlot(pageNo, pageSize, 
                sortDirection, sortProperty, searchFilter, startDateTime, endDateTime, visitStage, siteUUCode, selfAssignVisit, 
                hasVisit));
    }
}
