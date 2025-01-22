package org.baps.api.vtms.controllers;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.DocumentModel;
import org.baps.api.vtms.services.VisitService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@RestController
@Tag(name = "Visit Document")
@RequestMapping("/visits")
@Validated
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class VisitDocumentController {

    private final VisitService visitService;

    @Operation(summary = "Add document to visit")
    @PostMapping(value = "/{visitId}/documents", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).ADD_VISIT_DOCUMENT, "
            + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<DocumentModel> addVisitDocument(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
                                                          @PathVariable("visitId") final String visitId,
                                                          @Valid @RequestBody final DocumentModel documentModel) {

        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.addVisitDocument(visitId, documentModel, siteUUCode));
    }

    @Operation(summary = "Delete document from visit")
    @DeleteMapping(value = "/{visitId}/documents", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).DELETE_VISIT_DOCUMENT, "
        + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Void> deleteVisitDocument(@RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
                                                    @PathVariable("visitId") final String visitId,
                                                    @RequestParam("title") final String title) {

        visitService.deleteVisitDocument(visitId, CommonUtils.decoderUTF(title), siteUUCode);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all visit document")
    @GetMapping(value = "/{visitId}/documents", headers = GeneralConstant.API_VERSION_V1)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum).VIEW_VISIT_DOCUMENT_LIST, "
        + "#siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<DocumentModel>> getAllVisitDocumentsByVisitId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitService.getAllVisitDocumentsByVisitId(visitId, siteUUCode));
    }
}
