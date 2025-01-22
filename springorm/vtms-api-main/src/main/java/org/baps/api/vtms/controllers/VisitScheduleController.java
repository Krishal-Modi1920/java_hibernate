package org.baps.api.vtms.controllers;

import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.VisitScheduleModel;
import org.baps.api.vtms.services.VisitScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Visit Schedule")
@RequestMapping("/visits/{visitId}")
@Validated
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitScheduleController {

    private final VisitScheduleService visitScheduleService;

    @Operation(summary = "Get all service by visit id")
    @GetMapping(value = "/schedules", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_SCHEDULE, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<VisitScheduleModel>> getVisitScheduleList(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitScheduleService.getVisitScheduleList(visitId, siteUUCode));
    }
}
