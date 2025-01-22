package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.models.VisitCountModel;
import org.baps.api.vtms.models.VisitPublicFeedbackSummaryModel;
import org.baps.api.vtms.models.VisitSummaryModel;
import org.baps.api.vtms.services.DashboardService;
import org.baps.api.vtms.services.VisitService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Dashboard")
@RequestMapping("/dashboard")
@Validated
public class DashboardController {

    private final VisitService visitService;

    private final DashboardService dashboardService;
    
    @Operation(summary = "Get Visit count")
    @GetMapping(value = "/visits/count", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_DASHBOARD_VISIT_COUNT) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitCountModel>> getVisitCount(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
        @RequestParam(value = "startDateTime") final LocalDateTime startDateTime,
        @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
        @RequestParam(value = "endDateTime") final LocalDateTime endDateTime,
        @EnumValue(enumClass = VisitTypeEnum.class)
        @RequestParam(value = "visitType") final String visitType) {

        return ResponseEntity.status(HttpStatus.OK).body(visitService.getVisitCount(siteUUCode, startDateTime, endDateTime, visitType));
    }
    
    @Operation(summary = "Get Visit Summary")
    @GetMapping(value = "/visits/summary", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_DASHBOARD_VISIT_SUMMARY) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitSummaryModel>> getVisitSummary(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "startDateTime") final LocalDateTime startDateTime,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "endDateTime") final LocalDateTime endDateTime,
            @EnumValue(enumClass = VisitTypeEnum.class)
            @RequestParam(value = "visitType") final String visitType,
            @RequestParam(required = false) final String search) {

        return ResponseEntity.status(HttpStatus.OK).body(visitService.getVisitSummary(siteUUCode, startDateTime, endDateTime, visitType,
                search));
    }
    
    @Operation(summary = "Get Prebooked Visit Summary")
    @GetMapping(value = "/pre-booked/summary", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_DASHBOARD_PREBOOKED_VISIT_SUMMARY) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitCountModel>> getPreBookedVisitSummary(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "startDateTime") final LocalDateTime startDateTime,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "endDateTime") final LocalDateTime endDateTime) {

        return ResponseEntity.status(HttpStatus.OK).body(visitService.getPreBookedVisitSummary(siteUUCode, startDateTime, endDateTime));
    }
    
    @Operation(summary = "Get Pre-booked Visit feedback Summary")
    @GetMapping(value = "/pre-booked/feedback/summary", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".VIEW_DASHBOARD_PREBOOKED_VISIT_FEEDBACK_SUMMARY) "
            + "&& @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitPublicFeedbackSummaryModel> getPreBookedVisitFeedbackSummary(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "startDateTime") final LocalDateTime startDateTime,
            @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
            @RequestParam(value = "endDateTime") final LocalDateTime endDateTime) {

        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getPreBookedVisitFeedbackSummary(siteUUCode,
                startDateTime, endDateTime));
    }

}
