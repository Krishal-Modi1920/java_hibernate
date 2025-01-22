package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.LookupKeyEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.exceptions.AuthorizationException;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.PersonnelMapper;
import org.baps.api.vtms.mappers.VisitFeedbackMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.mappers.VisitPublicFeedbackMapper;
import org.baps.api.vtms.models.FeedBackRatingModel;
import org.baps.api.vtms.models.InternalFeedbackModel;
import org.baps.api.vtms.models.VisitBookingFeedbackModel;
import org.baps.api.vtms.models.VisitFeedbackModel;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitFeedback;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitPublicFeedback;
import org.baps.api.vtms.repositories.VisitFeedbackRepository;
import org.baps.api.vtms.repositories.VisitPersonnelRepository;
import org.baps.api.vtms.repositories.VisitPublicFeedbackRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class VisitFeedbackService {

    private static final List<VisitStageEnum> ALLOWED_VISIT_STAGE_ENUM = List.of(
        VisitStageEnum.COMPLETED,
        VisitStageEnum.CLOSED
    );

    private final VisitService visitService;

    private final RoleService roleServicee;

    private final PersonnelService personnelService;
    
    private final VisitPersonnelService visitPersonnelService;
    
    private final LookupService lookupService;

    private final Translator translator;

    private final VisitPersonnelRepository visitPersonnelRepository;

    private final VisitFeedbackRepository visitFeedbackRepository;

    private final VisitFeedbackMapper visitFeedbackMapper;

    private final PersonnelMapper personnelMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;
    
    private final VisitPublicFeedbackRepository visitPublicFeedbackRepository;

    private final VisitPublicFeedbackMapper visitPublicFeedbackMapper;

    /**
     * Finds a VisitFeedback by visitFeedbackId.
     *
     * @param id The id of the visit feedback to find.
     * @param siteUUCode The unique code associated with the site.
     * @return The VisitFeedback object with the specified visitFeedbackId.
     * @throws DataNotFoundException If the visitFeedbackId is not found.
     */
    @Transactional(readOnly = true)
    public VisitFeedback findVisitFeedbackByIdAndSiteUUcode(final String id, final String siteUUCode) throws DataNotFoundException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.feedback.not.found", id));
        } else {
            return visitFeedbackRepository.findByVisitFeedbackIdAndVisitSiteUuCode(id, siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.feedback.not.found", id)));
        }
    }

    /**
     * Finds a VisitFeedback by visitFeedbackId.
     *
     * @param id The id of the visit feedback to find.
     * @param siteUUCode The unique code associated with the site.
     * @return The VisitFeedback object with the specified visitFeedbackId.
     * @throws DataNotFoundException If the visitFeedbackId is not found.
     */
    @Transactional(readOnly = true)
    public VisitPublicFeedback findVisitPublicFeedbackByIdAndSiteUUcode(final String id, final String siteUUCode)
            throws DataNotFoundException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.feedback.not.found", id));
        } else {
            return visitPublicFeedbackRepository.findByVisitPublicFeedbackIdAndVisitSiteUuCode(id, siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.feedback.not.found", id)));
        }
    }

    /**
     * Create external visit feedback for a visit.
     *
     * @param visitId            The unique identifier of the visit.
     * @param visitFeedbackId    The unique identifier of the visit feedback.
     * @param visitFeedbackModel The visit feedback data to be created.
     * @param siteUUCode         The unique code associated with the site.
     * @return A VisitFeedbackModel containing the created feedback data.
     * @throws DataValidationException    If the provided data is not valid.
     * @throws DataAlreadyExistsException If feedback already exists for the visit.
     */
    @Transactional
    public VisitFeedbackModel createExternalVisitFeedback(final String visitId, final String visitFeedbackId,
            final VisitFeedbackModel visitFeedbackModel,  final String siteUUCode) {

        final List<String> generalFeedBackKeys = visitFeedbackModel.getFeedBackRatingModelListForGeneralFeedBack()
                .stream().map(FeedBackRatingModel::getKey).collect(Collectors.toList());

        lookupService.validateChildLookupKeyByKey(LookupKeyEnum.VISIT_GENERAL_FEEDBACK.name(), generalFeedBackKeys,
                "feedBackRatingModelListForGeneralFeedBack.key");
        
        final List<String> tourGuideFeedBackKeys = visitFeedbackModel.getFeedBackRatingModelListForTourGuide()
                .stream().map(FeedBackRatingModel::getKey).collect(Collectors.toList());
        
        lookupService.validateChildLookupKeyByKey(LookupKeyEnum.TG_RATING.name(), tourGuideFeedBackKeys,
                "feedBackRatingModelListForTourGuide.key");
        
        // Find the existing visit.
        final VisitFeedback existingVisitFeedback;

        if (StringUtils.isNotBlank(visitId)) {

            // Retrieve the existing visit based on visitId.
            final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, ALLOWED_VISIT_STAGE_ENUM,
                    "visit.external_feedback.create.restrict");

            if (ObjectUtils.isEmpty(existingVisit.getVisitFeedback())) {
                throw new DataValidationException(translator.toLocal("visit.feedback.not.found"));
            }

            existingVisitFeedback = existingVisit.getVisitFeedback();

        } else if (StringUtils.isNotBlank(visitFeedbackId)) {

            existingVisitFeedback = findVisitFeedbackByIdAndSiteUUcode(visitFeedbackId, siteUUCode);

            // Check if the visit stage is allowed for feedback.
            if (!ALLOWED_VISIT_STAGE_ENUM.contains(existingVisitFeedback.getVisit().getVisitStageEnum())) {
                throw new DataValidationException(translator.toLocal("visit.stage.should.be.for_external_feedback",
                        ALLOWED_VISIT_STAGE_ENUM));
            }

        } else {
            throw new DataNotFoundException(translator.toLocal("visit.feedback.not.found"));
        }
        
        // Check if visitor comment already exists.
        if (CollectionUtils.isNotEmpty(existingVisitFeedback.getFeedBackRatingModelListForGeneralFeedBack())
                && !existingVisitFeedback.getFeedBackRatingModelListForGeneralFeedBack().toString().equals("null")) {
            throw new DataAlreadyExistsException(translator.toLocal("feedback.already.exist"));
        }

        visitFeedbackMapper.visitFeedbackModelToVisitFeedback(existingVisitFeedback, visitFeedbackModel);

        // Update personnel ratings if applicable.
        if (CollectionUtils.isNotEmpty(existingVisitFeedback.getVisit().getVisitPersonnelList())) {
            existingVisitFeedback.getVisit().getVisitPersonnelList()
                .stream()
                .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.TOUR_GUIDE))
                .forEach(visitPersonnel -> visitPersonnel.setFeedBackRatingModelListForTourGuide(
                    visitFeedbackModel.getFeedBackRatingModelListForTourGuide()));
        }

        visitFeedbackRepository.save(existingVisitFeedback);

        return visitFeedbackMapper.visitToVisitFeedbackModel(existingVisitFeedback.getVisit());

    }

    /**
     * Get external visit feedback by visit feedback ID.
     *
     * @param visitFeedbackId The unique identifier of the visit.
     * @param siteUUCode The unique code associated with the site.
     * @return A VisitFeedbackModel containing the feedback data.
     * @throws DataNotFoundException If feedback is not found for the given id.
     */
    @Transactional(readOnly = true)
    public VisitFeedbackModel getExternalVisitFeedbackByVisitFeedbackId(final String visitFeedbackId,
                                                                        final String siteUUCode) {
       
        
        return visitFeedbackMapper.visitToVisitFeedbackModel(findVisitFeedbackByIdAndSiteUUcode(visitFeedbackId, siteUUCode).getVisit());
        
    }   

    /**
     * Get external visit feedback by visit ID.
     *
     * @param visitId    The unique identifier of the visit.
     * @param siteUUCode The unique code associated with the site.
     * @return A VisitFeedbackModel containing the feedback data.
     * @throws DataNotFoundException If feedback is not found for the given visit.
     */
    @Transactional(readOnly = true)
    public VisitFeedbackModel getExternalVisitFeedbackByVisitId(final String visitId, final String siteUUCode) {
        
        return  visitFeedbackMapper.visitToVisitFeedbackModel(visitService.findByIdAndSiteUUCode(visitId, siteUUCode));
    }
    
    /**
     * Checks if feedback exists for a specific visit.
     *
     * @param visitId    The unique identifier of the visit to check for feedback.
     * @param siteUUCode The unique code associated with the site.
     * @return True if feedback does not exist, false if feedback is present.
     */
    @Transactional(readOnly = true)
    public boolean checkExternalFeedbackExists(final String visitId, final String siteUUCode) {
        // Retrieve the existing visit using the visitService and the provided visitId
        final Visit existingVisit = visitService.findByIdAndSiteUUCode(visitId, siteUUCode);

        // Check if the existing visit's feedback or its visitor comment is empty
        if (ObjectUtils.isNotEmpty(existingVisit.getVisitFeedback()) 
                || CollectionUtils.isNotEmpty(existingVisit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack())
                || !existingVisit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack().toString().equals("null")) {
            // If either the visit's feedback or visitor comment is empty, return true
            return false;
        }
        // If both the visit's feedback and visitor comment are not empty, return false
        return true;
    }

    /**
     * Creates internal visit feedback for a specific visit and personnel.
     *
     * @param visitId               The ID of the visit for which feedback is being created.
     * @param internalFeedbackModel The internal feedback model containing personnel feedback.
     * @param siteUUCode            The unique code associated with the site.
     * @return The internal feedback model after creating feedback.
     * @throws DataValidationException    If personnel feedback is missing or null, if the visit's stage is not allowed, or if personnel
     *                                    can't give feedback.
     * @throws DataAlreadyExistsException If feedback already exists with the personnel.
     */
    @Transactional
    public InternalFeedbackModel createInternalVisitFeedback(final String visitId,
                                                             final InternalFeedbackModel internalFeedbackModel,
                                                             final String siteUUCode) {

        // Check if personnel feedback is provided and not null
        if (ObjectUtils.isEmpty(internalFeedbackModel.getPersonnelFeedback())
                || internalFeedbackModel.getPersonnelFeedback().toString().equals("null")) {
            throw new DataValidationException(translator.toLocal("personnel.feedback.not.blank"));
        }

        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                ALLOWED_VISIT_STAGE_ENUM, "visit.internal_feedback.create.restrict");

        final var logginedPersonnel = personnelService.getLoginedPersonnel();

        final var logginedPersonnelVisitPersonnelList = visitPersonnelService
                .findVisitPersonnelListByVisitIdAndSiteUucodeAndPersonnelId(visitId, siteUUCode, logginedPersonnel.getPersonnelId());

        if (CollectionUtils.isNotEmpty(logginedPersonnelVisitPersonnelList)) {

            // Check if feedback already exists with the personnel
            final var feedbackExist = logginedPersonnelVisitPersonnelList.stream()
                    .anyMatch(visitPersonnel -> ObjectUtils.isNotEmpty(visitPersonnel.getPersonnelFeedback())
                            && !visitPersonnel.getPersonnelFeedback().toString().equals("null"));
            if (feedbackExist) {
                throw new DataAlreadyExistsException(translator.toLocal("feedback.already.exist.personnel"));
            }

            logginedPersonnelVisitPersonnelList.forEach(visitPersonnel -> {
                visitPersonnel.setPersonnelFeedback(internalFeedbackModel.getPersonnelFeedback());
                internalFeedbackModel.setPersonnel(personnelMapper.personnelToPersonnelBasicInfoModel(visitPersonnel.getPersonnel()));
            });

            visitPersonnelRepository.saveAll(logginedPersonnelVisitPersonnelList);

            return internalFeedbackModel;
        } else {
            final List<String> logginedPersonnelRoles = new ArrayList<>();

            logginedPersonnelRoles.addAll(
                    logginedPersonnel.getPersonnelRoleList().stream()
                    .filter(personnelRole -> personnelRole.getRole().isCheckSystemRole())
                    .map(personnelRole -> personnelRole.getRole().getUucode())
                    .collect(Collectors.toList()));

            if (logginedPersonnelRoles.contains(RoleEnum.SUPER_ADMIN.name())) {
                final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                        logginedPersonnel, roleServicee.findByRoleEnum(RoleEnum.SUPER_ADMIN),
                        GeneralConstant.SUPER_ADMIN_TAG, null);

                visitPersonnelToAdd.setPersonnelFeedback(internalFeedbackModel.getPersonnelFeedback());

                existingVisit.addVisitPersonnel(visitPersonnelToAdd);

                visitPersonnelRepository.save(visitPersonnelToAdd);

                return internalFeedbackModel;
            } else {
                throw new DataValidationException(translator.toLocal("personnel.with.personnel_id.not.associated.visit",
                        logginedPersonnel.getPersonnelId()));
            }
        }
    }

    /**
     * Retrieves and returns internal feedback given by personnel for a specific visit.
     *
     * @param visitId    The visit's unique identifier.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of InternalFeedbackModel objects, or an empty list if no feedback is available.
     */
    @Transactional(readOnly = true)
    public List<InternalFeedbackModel> getAllInternalFeedbackByVisitId(final String visitId, final String siteUUCode) {

        final Visit existingVisit = visitService.findByIdAndSiteUUCode(visitId, siteUUCode);

        if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {
            return existingVisit.getVisitPersonnelList().stream()
                    .filter(visitPersonnel -> ObjectUtils.isNotEmpty(visitPersonnel.getPersonnelFeedback())
                            && !visitPersonnel.getPersonnelFeedback().toString().equals("null"))
                    .filter(CommonUtils.distinctByKey(visitPersonnel -> visitPersonnel.getPersonnel().getPersonnelId()))
                    .map(visitPersonnel ->
                    new InternalFeedbackModel(
                            personnelMapper.personnelToPersonnelBasicInfoModel(visitPersonnel.getPersonnel()),
                            visitPersonnel.getPersonnelFeedback())).toList();
        }

        return Collections.emptyList();
    }

    /**
     * Deletes internal feedback associated with a visit.
     * This method retrieves the existing visit based on the visit ID, validates the user's authorization,
     * and then removes the internal feedback provided by a personnel associated with the visit.
     *
     * @param visitId           The ID of the visit.
     * @param siteUUCode        The site's unique identifier code.
     * @param personnelId       The ID of the personnel.
     * @throws DataValidationException   If the personnel is not associated with the visit.
     * @throws AuthorizationException    If the personnel is not authorized to delete feedback.
     */
    @Transactional
    public void deleteInternalVisitFeedback(final String visitId, final String siteUUCode, final String personnelId) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, ALLOWED_VISIT_STAGE_ENUM,
                "visit.internal_feedback.create.restrict");

        final Personnel logginedPersonnel = personnelService.getLoginedPersonnel();

        final Personnel existingPersonnel = personnelService.findById(personnelId);
        
        final List<String> logginedPersonnelRoles = new ArrayList<>();
        
        logginedPersonnelRoles.addAll(
                logginedPersonnel.getPersonnelRoleList().stream()
                .filter(personnelRole -> personnelRole.getRole().isCheckSystemRole())
                .map(personnelRole -> personnelRole.getRole().getUucode())
                .collect(Collectors.toList()));
        
        if (!logginedPersonnelRoles.contains(RoleEnum.SUPER_ADMIN.name()) && !logginedPersonnel.equals(existingPersonnel)) {
            throw new AuthorizationException();
        }

       
        // Check if the visit has personnel associated with it
        if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {

            // Filter the visit personnel list for the current personnel
            final List<VisitPersonnel> currentPersonnelVisitPersonnelList = existingVisit.getVisitPersonnelList().stream()
                    .filter(visitPersonnel -> visitPersonnel.getPersonnel().equals(existingPersonnel) 
                            && ObjectUtils.isNotEmpty(visitPersonnel.getPersonnelFeedback())
                            && !visitPersonnel.getPersonnelFeedback().toString().equals("null")).toList();

            // If the current personnel's list is empty, they are not associated with the visit
            if (currentPersonnelVisitPersonnelList.isEmpty()) {
                throw new DataValidationException(translator.toLocal("visit.internal_feedback.with.personnel_id.not.found",
                        existingPersonnel.getPersonnelId()));
            }

            currentPersonnelVisitPersonnelList.forEach(visitPersonnel -> visitPersonnel.setPersonnelFeedback(null));

            visitPersonnelRepository.saveAll(currentPersonnelVisitPersonnelList);
        } else {
            throw new DataNotFoundException(translator.toLocal("visit.visit_personnel.is_empty"));
        }
    }

    /**
     * After visit Create external visit feedback for a visit.
     *
     * @param visitPublicFeedbackId    The unique identifier of the visit feedback.
     * @param visitPublicFeedbackModel The visit feedback data to be created.
     * @param siteUUCode         The unique code associated with the site.
     * @return A VisitFeedbackModel containing the created feedback data.
     * @throws DataValidationException    If the provided data is not valid.
     * @throws DataAlreadyExistsException If feedback already exists for the visit.
     */
    @Transactional
    public VisitBookingFeedbackModel createVisitBookingProcessFeedbackByVisitFeedbackId(final String visitPublicFeedbackId,
            final VisitBookingFeedbackModel visitPublicFeedbackModel,  final String siteUUCode) {

        final VisitPublicFeedback existingVisitPublicFeedback = findVisitPublicFeedbackByIdAndSiteUUcode(visitPublicFeedbackId, siteUUCode);

        // Check if visitor comment already exists.
        if (existingVisitPublicFeedback.isBookingFeedback()) {
            throw new DataAlreadyExistsException(translator.toLocal("feedback.already.exist"));
        }
        
        // Check if the visit stage is allowed for feedback.
        if (ALLOWED_VISIT_STAGE_ENUM.contains(existingVisitPublicFeedback.getVisit().getVisitStageEnum())) {
            throw new DataValidationException(translator.toLocal("visit.form.feedback.not_allow",
                    ALLOWED_VISIT_STAGE_ENUM));
        }

        visitPublicFeedbackMapper.visitBookingFeedbackModelToVisitPublicFeedback(existingVisitPublicFeedback, visitPublicFeedbackModel);

        existingVisitPublicFeedback.setBookingFeedback(true);
        visitPublicFeedbackRepository.save(existingVisitPublicFeedback);

        return visitPublicFeedbackMapper.visitPublicFeedbackToVisitBookingFeedbackModel(existingVisitPublicFeedback);

    }

    /**
     * Retrieves the feedback model for a visit booking process based on a given feedback ID and site code.
     *
     * @param visitPublicFeedbackId the unique identifier of the visit public feedback
     * @param siteUUCode the unique universal code for the site associated with the feedback
     * @return a {@link VisitBookingFeedbackModel} containing feedback details for the specified visit feedback
     * @throws ResourceNotFoundException if no feedback is found for the provided ID and site code
     */
    public VisitBookingFeedbackModel getVisitBookingProcessFeedbackByVisitFeedbackId(
            final String visitPublicFeedbackId, final String siteUUCode) {
        
        // Retrieve the existing feedback record based on its ID and associated site code
        final VisitPublicFeedback existingVisitPublicFeedback = 
                findVisitPublicFeedbackByIdAndSiteUUcode(visitPublicFeedbackId, siteUUCode);

        // Map the retrieved feedback entity to its corresponding feedback model and return
        return visitPublicFeedbackMapper.visitPublicFeedbackToVisitBookingFeedbackModel(existingVisitPublicFeedback);
    }

}