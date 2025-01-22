package org.baps.api.vtms.controllers;

import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.SiteModel;
import org.baps.api.vtms.services.SiteService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Site")
@RequestMapping("/sites")
@Validated
public class SiteController {

    private final SiteService siteService;

    @Operation(summary = "Get site by site uucode")
    @GetMapping(value = "/uucode", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<SiteModel> getSiteByUUcode(@RequestHeader(GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode) {
        return ResponseEntity.ok(siteService.findSiteByUUCode(siteUUCode));
    }
}