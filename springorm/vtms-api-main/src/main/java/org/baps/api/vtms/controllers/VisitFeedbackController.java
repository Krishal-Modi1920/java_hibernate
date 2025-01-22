package org.baps.api.vtms.controllers;


import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.models.InternalFeedbackModel;
import org.baps.api.vtms.models.VisitBookingFeedbackModel;
import org.baps.api.vtms.models.VisitFeedbackModel;
import org.baps.api.vtms.services.VisitFeedbackService;

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
@Tag(name = "Visit Feedback")
@RequestMapping("/visits")
@Validated
public class VisitFeedbackController {

    private final VisitFeedbackService visitFeedbackService;

    @Operation(summary = "Add external feedback to visit by visit id")
    @PostMapping(value = "/{visitId}/feedbacks/external", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".ADD_VISIT_EXTERNAL_FEEDBACK, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitFeedbackModel> createExternalFeedbackByVisitId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId,
        @Valid @RequestBody final VisitFeedbackModel visitFeedbackModel) {

        return ResponseEntity.ok(visitFeedbackService.createExternalVisitFeedback(visitId, null, visitFeedbackModel, siteUUCode));
    }

    @Operation(summary = "Get external feedback by visit id")
    @GetMapping(value = "/{visitId}/feedbacks/external", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".VIEW_VISIT_EXTERNAL_FEEDBACK, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitFeedbackModel> getExternalFeedbackByVisitId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitFeedbackService.getExternalVisitFeedbackByVisitId(visitId, siteUUCode));
    }

    @Operation(summary = "Add internal feedback to visit")
    @PostMapping(value = "/{visitId}/feedbacks/internal", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".ADD_VISIT_INTERNAL_FEEDBACK, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<InternalFeedbackModel> createInternalVisitFeedback(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId, @Valid @RequestBody final InternalFeedbackModel internalFeedbackModel) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(visitFeedbackService.createInternalVisitFeedback(visitId, internalFeedbackModel, siteUUCode));
    }

    @Operation(summary = "Get all internal feedbacks")
    @GetMapping(value = "/{visitId}/feedbacks/internal", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".VIEW_VISIT_INTERNAL_FEEDBACK, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<List<InternalFeedbackModel>> getAllInternalFeedbackByVisitId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.ok(visitFeedbackService.getAllInternalFeedbackByVisitId(visitId, siteUUCode));
    }

    @Operation(summary = "Delete internal feedbacks")
    @DeleteMapping(value = "/{visitId}/feedbacks/internal", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".DELETE_VISIT_INTERNAL_FEEDBACK, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Void> deleteInternalVisitFeedback(@
            RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
            @PathVariable("visitId") final String visitId, @RequestParam("personnelId") final String personnelId) {

        visitFeedbackService.deleteInternalVisitFeedback(visitId, siteUUCode, personnelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check visit external feedback exists")
    @GetMapping(value = "/{visitId}/feedbacks/external/exists", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    @PreAuthorize("@authService.hasVisitPermission(#visitId, T(org.baps.api.vtms.enumerations.PermissionEnum)"
            + ".CHECK_VISIT_EXTERNAL_FEEDBACK_EXISTS, #siteUUCode) && @siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<Boolean> checkExternalFeedbackExists(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitId") final String visitId) {

        return ResponseEntity.status(HttpStatus.OK).body(visitFeedbackService.checkExternalFeedbackExists(visitId, siteUUCode));
    }

    @Operation(summary = "Add external feedback by visit feedback id.")
    @PostMapping(value = "/feedbacks/{visitFeedbackId}/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitFeedbackModel> createExternalFeedbackByVisitFeedbackId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitFeedbackId") final String visitFeedbackId,
        @Valid @RequestBody final VisitFeedbackModel visitFeedbackModel) {

        return ResponseEntity.ok(visitFeedbackService.createExternalVisitFeedback(null, visitFeedbackId, visitFeedbackModel, siteUUCode));
    }

    @Operation(summary = "Get external feedback by visit feedback id")
    @GetMapping(value = "/feedbacks/{visitFeedbackId}/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitFeedbackModel> getExternalVisitFeedbackByVisitFeedbackId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitFeedbackId") final String visitFeedbackId) {

        return ResponseEntity.ok(visitFeedbackService.getExternalVisitFeedbackByVisitFeedbackId(visitFeedbackId, siteUUCode));
    }

    @Operation(summary = "Add booking-process feedback by visit feedback id.")
    @PostMapping(value = "/booking-process/feedbacks/{visitPublicFeedbackId}/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitBookingFeedbackModel> createVisitBookingProcessFeedbackByVisitFeedbackId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitPublicFeedbackId") final String visitPublicFeedbackId,
        @Valid @RequestBody final VisitBookingFeedbackModel visitPublicFeedbackModel) {

        return ResponseEntity.ok(visitFeedbackService.createVisitBookingProcessFeedbackByVisitFeedbackId(visitPublicFeedbackId,
                visitPublicFeedbackModel, siteUUCode));
    }

    @Operation(summary = "Get booking-process feedback by visit feedback id.")
    @GetMapping(value = "/booking-process/feedbacks/{visitPublicFeedbackId}/public", headers = GeneralConstant.API_VERSION_V1)
    @SecurityRequirement(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY)
    @PreAuthorize("@siteService.siteExistsByUUCode(#siteUUCode)")
    public ResponseEntity<VisitBookingFeedbackModel> getVisitBookingProcessFeedbackByVisitFeedbackId(
        @RequestHeader(name = GeneralConstant.X_APP_SITE_UUCODE) final String siteUUCode,
        @PathVariable("visitPublicFeedbackId") final String visitPublicFeedbackId) {

        return ResponseEntity.ok(visitFeedbackService.getVisitBookingProcessFeedbackByVisitFeedbackId(visitPublicFeedbackId, siteUUCode));
    }

}
