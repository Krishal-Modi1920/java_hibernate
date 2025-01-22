package org.baps.api.vtms.controllers;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.services.VisitorService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Tag(name = "Visitor")
@RequestMapping("/visitors")
@Validated
public class VisitorController {
    
    private final VisitorService visitorService;

    @Operation(summary = "Retrieve Visitor by filters")
    @GetMapping(headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    public ResponseEntity<List<VisitorModel>> getVisitorByFilters(@RequestParam(required = false) final String search) {

        final String decodeSearch = StringUtils.isNotBlank(search) ? CommonUtils.decoderUTF(search) : null;

        return ResponseEntity.ok(visitorService.getVisitorByFilters(decodeSearch));
    }
}
