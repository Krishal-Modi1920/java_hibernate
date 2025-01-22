package org.baps.api.vtms.controllers;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.CountryModel;
import org.baps.api.vtms.models.LocationModel;
import org.baps.api.vtms.models.LookupModel;
import org.baps.api.vtms.models.StateModel;
import org.baps.api.vtms.services.CountryService;
import org.baps.api.vtms.services.LookupService;
import org.baps.api.vtms.services.MasterService;
import org.baps.api.vtms.services.StateService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Masters")
@Validated
@SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
public class MasterController {

    private final MasterService masterService;

    private final CountryService countryService;

    private final StateService stateService;

    private final LookupService lookupService;

    @Operation(summary = "Get all lookup by filter")
    @GetMapping(value = "/lookup/public", headers = GeneralConstant.API_VERSION_V1)
    public ResponseEntity<List<LookupModel>> getLookups(
            @RequestParam(value = "filter", required = false) final String filter) {

        final String decodeFilter = StringUtils.isNotBlank(filter) ? CommonUtils.decoderUTF(filter) : null;

        return ResponseEntity.ok(lookupService.getLookups(decodeFilter));
    }

    @Operation(summary = "Get countries by filter")
    @GetMapping(value = "/countries/public", headers = GeneralConstant.API_VERSION_V1)
    public ResponseEntity<List<CountryModel>> getCountryList(
            @RequestParam(value = "filter", required = false) final String filter) {

        final String decodeFilter = StringUtils.isNotBlank(filter) ? CommonUtils.decoderUTF(filter) : null;

        return ResponseEntity.ok(countryService.getCoutryList(decodeFilter));
    }

    @Operation(summary = "Get states by filter")
    @GetMapping(value = "/states/public", headers = GeneralConstant.API_VERSION_V1)
    public ResponseEntity<List<StateModel>> getStateList(
            @RequestParam(value = "filter", required = false) final String filter) {

        final String decodeFilter = StringUtils.isNotBlank(filter) ? CommonUtils.decoderUTF(filter) : null;
        
        return ResponseEntity.ok(stateService.getStateList(decodeFilter));
    }

    @Operation(summary = "Get locations by filter")
    @GetMapping(value = "/locations/public", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<LocationModel>> getLocationList(
            @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @RequestParam(value = "filter", required = false) final String filter) {

        final String decodeFilter = StringUtils.isNotBlank(filter) ? CommonUtils.decoderUTF(filter) : null;
        
        return ResponseEntity.ok(masterService.getLocationList(siteUUCode, decodeFilter));
    }
}
