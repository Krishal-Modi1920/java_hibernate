package org.baps.api.vtms.controllers;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.PersonnelModel;
import org.baps.api.vtms.services.PersonnelService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    @Operation(summary = "Get personnel by token")
    @GetMapping(value = "/personnel/token", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    public ResponseEntity<PersonnelModel> getPersonnelByToken() {

        return ResponseEntity.ok(personnelService.getPersonnelModelByToken());
    }

    @Operation(summary = "Get all personnel by sorting, searching & filters")
    @GetMapping(value = "/personnel", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_PERSONNEL_LIST)")
    public ResponseEntity<List<PersonnelModel>> getAllPersonnel(
        @EnumValue(enumClass = Sort.Direction.class) @RequestParam(value =
            "sortDirection", required = false) final String sortDirection,
        @RequestParam(value = "sortProperty", required = false) final String sortProperty,
        @RequestParam(required = false) final String search,
        @RequestParam(required = false) final String roleName) {

        return ResponseEntity.ok(personnelService.getAllPersonnel(sortDirection, sortProperty, search, roleName));
    }

    @Operation(summary = "Get Available Personnel List by startDateTime and endDateTime")
    @GetMapping(value = "/personnel/available", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasPermission(T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_AVAILABLE_PERSONNEL_LIST)"
            + " && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<PersonnelModel>> getAvailablePersonnelListByFilter(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @RequestParam("startDateTime")
        @NotNull @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT) final LocalDateTime startDateTime,
        @RequestParam("endDateTime")
        @NotNull @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT) final LocalDateTime endDateTime) {

        return ResponseEntity.ok(personnelService.getAvailablePersonnelListByFilter(startDateTime, endDateTime, siteUUCode));
    }
}
