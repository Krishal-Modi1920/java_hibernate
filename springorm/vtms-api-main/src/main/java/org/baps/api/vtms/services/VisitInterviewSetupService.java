package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.enumerations.APIModuleEnum;
import org.baps.api.vtms.enumerations.LookupKeyEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.VisitInterviewSetupMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.models.VisitInterviewSetupModel;
import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitServiceBasicInfoModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.repositories.VisitLocationRepository;
import org.baps.api.vtms.repositories.VisitRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class VisitInterviewSetupService {

    private final VisitService visitService;

    private final VisitInterviewSetupMapper visitInterviewSetupMapper;

    private final VisitRepository visitRepository;

    private final PersonnelService personnelService;

    private final RoleService roleService;

    private final VisitLocationRepository visitLocationRepository;

    private final Translator translator;

    private final VisitPersonnelService visitPersonnelService;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final NotificationComposeService notificationComposeService;

    private final LookupService lookupService;

    /**
     * Retrieves the visit interview setup for a given visit ID.
     *
     * @param visitId The unique identifier of the visit.
     * @param siteUUCode The unique code associated with the site.
     * @return A VisitInterviewSetupModel representing the visit interview setup.
     */
    @Transactional(readOnly = true)
    public VisitInterviewSetupModel getAllVisitInterviewSetup(final String visitId, final String siteUUCode) {

        final Visit existingVisit = visitService.findByIdAndSiteUUCode(visitId, siteUUCode);

        final VisitInterviewSetupModel visitInterviewSetupModel = visitInterviewSetupMapper.visitToVisitInterviewSetupModel(existingVisit);

        final List<VisitServiceBasicInfoModel> visitServiceBasicInfoModels = visitInterviewSetupModel
            .getVisitServiceBasicInfoModelList()
            .stream()
            .filter(visitServiceBasicInfoModel -> !visitServiceBasicInfoModel.getVisitLocationBasicInfoModelList().isEmpty())
            .toList();

        visitInterviewSetupModel.setVisitServiceBasicInfoModelList(visitServiceBasicInfoModels);

        return visitInterviewSetupModel;
    }

    /**
     * Updates the visit interview setup for a specific visit.
     *
     * @param visitId                  The unique identifier of the visit.
     * @param visitInterviewSetupModel The model containing updated visit interview setup data.
     * @param siteUUCode The unique code associated with the site.
     * @return A VisitInterviewSetupModel representing the updated visit interview setup.
     */
    @Transactional
    public VisitInterviewSetupModel updateVisitInterviewSetup(final String visitId,
                                                              final VisitInterviewSetupModel visitInterviewSetupModel,
                                                              final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.UPDATE_VISIT_INTERVIEW_SETUP.getAllowVisitStageList(), "visit.interview_set_up.create_or_update.restrict");
        
        final List<String> interviewPackages = visitInterviewSetupModel.getVisitServiceBasicInfoModelList().stream()
                .flatMap(visitServiceBasicInfoModel -> visitServiceBasicInfoModel.getVisitLocationBasicInfoModelList().stream())
                .map(VisitLocationModel::getInterviewPackage)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        
        lookupService.validateChildLookupKeyByKey(LookupKeyEnum.VISIT_INTERVIEW_SETUP.name(), interviewPackages, "interviewPackage");

        final List<VisitPersonnel> notificationVisitPersonnelList = 
                updateVisitInterviewSetup(existingVisit, visitInterviewSetupModel, siteUUCode);

        notificationComposeService.sendVisitAssignedNotification(existingVisit, notificationVisitPersonnelList, VisitTypeEnum.VISIT.name());

        return visitInterviewSetupMapper.visitToVisitInterviewSetupModel(existingVisit);
    }

    /**
     * Updates the interview setup details for a visit, including coordinator and interviewer information.
     * 
     * @param existingVisit             The existing visit to be updated.
     * @param visitInterviewSetupModel  The model containing the updated interview setup details.
     * @param siteUUCode                The unique code for the site associated with the visit.
     * @return                          A list of VisitPersonnel objects representing personnel involved in the visit, 
     *                                  eligible for notifications.
     */
    public List<VisitPersonnel> updateVisitInterviewSetup(
        final Visit existingVisit, final VisitInterviewSetupModel visitInterviewSetupModel, final String siteUUCode) {

        final List<VisitPersonnel> notificationVisitPersonnelList = new ArrayList<>();

        // Adds or updates the interview setup coordinator and interviewer details for a visit.
        addOrUpdateInterviewSetupCoordinatorAndInterviewerDetails(existingVisit, siteUUCode, visitInterviewSetupModel);

        existingVisit.getVisitPersonnelList().stream()
        .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR))
        .findFirst()
        .ifPresent(notificationVisitPersonnelList::add);


        // Check if there are service basic info models.
        if (CollectionUtils.isNotEmpty(visitInterviewSetupModel.getVisitServiceBasicInfoModelList())) {

            // Create a map of visit location basic info models using the visit service basic info models.
            final Map<String, VisitLocationModel> visitLocationBasicInfoModelMap =
                visitInterviewSetupModel.getVisitServiceBasicInfoModelList().stream()
                    .flatMap(visitServiceBasicInfoModel -> visitServiceBasicInfoModel.getVisitLocationBasicInfoModelList().stream())
                    .collect(Collectors.toMap(VisitLocationModel::getVisitLocationId,
                        visitLocationBasicInfoModel -> visitLocationBasicInfoModel));

            // Retrieve existing visit locations by their IDs.
            final List<VisitLocation> existingVisitLocationList =
                visitLocationRepository.findAllByVisitLocationIdInAndVisitServiceVisitVisitId(visitLocationBasicInfoModelMap.keySet(),
                    existingVisit.getVisitId());

            // Create a map of existing visit locations
            final Map<String, VisitLocation> existingVisitLocationMap = existingVisitLocationList.stream()
                .collect(Collectors.toMap(VisitLocation::getVisitLocationId, visitLocation -> visitLocation));

            // Find invalid visit location IDs.
            final Set<String> invalidVisitLocationIdSet = visitLocationBasicInfoModelMap.keySet().stream()
                .filter(visitLocationId -> !existingVisitLocationMap.containsKey(visitLocationId))
                .collect(Collectors.toSet());

            // If there are invalid visit location IDs, throw a data not found exception.
            if (CollectionUtils.isNotEmpty(invalidVisitLocationIdSet)) {
                throw new DataNotFoundException(translator.toLocal("visit.location.not.found", invalidVisitLocationIdSet));
            }

            final List<String> personnelIdSet = visitLocationBasicInfoModelMap.values().stream()
                .filter(visitLocationBasicInfoModel ->
                    ObjectUtils.isNotEmpty(visitLocationBasicInfoModel.getInterviewVolunteerVisitPersonnelModel()))
                .map(visitLocationBasicInfoModel -> visitLocationBasicInfoModel.getInterviewVolunteerVisitPersonnelModel().getPersonnelId())
                .collect(Collectors.toList());

            final Map<String, Personnel> existingPersonnelIdWithPersonnelMap = 
                    personnelService.getMapOfPersonnelByPersonnelIds(personnelIdSet);

            final List<String> roleIdList = visitLocationBasicInfoModelMap.values().stream()
                .filter(visitLocationBasicInfoModel ->
                    ObjectUtils.isNotEmpty(visitLocationBasicInfoModel.getInterviewVolunteerVisitPersonnelModel()))
                .map(visitLocationBasicInfoModel -> visitLocationBasicInfoModel.getInterviewVolunteerVisitPersonnelModel().getRoleId())
                .toList();

            final Map<String, Role> existingRoleIdWithRoleMap = CollectionUtils.isNotEmpty(roleIdList)
                ? roleService.getMapOfRoleByRoleIdsAndTag(roleIdList, List.of(RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER))
                : new HashMap<>();

            // Update visit locations with interview setup and comments.
            visitLocationBasicInfoModelMap.forEach((visitLocationId, visitLocationBasicInfoModel) -> {
                final VisitLocation existingVisitLocation = existingVisitLocationMap.get(visitLocationId);
                existingVisitLocation.setInterviewPackage(visitLocationBasicInfoModel.getInterviewPackage());

                final VisitPersonnelModel interviewVolunteerVisitPersonnelModel =
                    visitLocationBasicInfoModel.getInterviewVolunteerVisitPersonnelModel();

                if (StringUtils.isBlank(visitLocationBasicInfoModel.getInterviewPackage())
                    && ObjectUtils.isNotEmpty(interviewVolunteerVisitPersonnelModel)
                    && StringUtils.isNotBlank(interviewVolunteerVisitPersonnelModel.getPersonnelId())) {
                    throw new DataValidationException(
                        translator.toLocal("interview.package.required", existingVisitLocation.getVisitLocationId()));
                }

                if (ObjectUtils.isNotEmpty(interviewVolunteerVisitPersonnelModel)
                    && ObjectUtils.isEmpty(existingVisitLocation.getInterviewVolunteerVisitPersonnel())) {

                    //add
                    final VisitPersonnel interviewCoordinatorVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                        existingPersonnelIdWithPersonnelMap.get(interviewVolunteerVisitPersonnelModel.getPersonnelId()),
                        existingRoleIdWithRoleMap.get(interviewVolunteerVisitPersonnelModel.getRoleId()),
                        RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER,
                        existingVisitLocation.getVisitService());

                    existingVisitLocation.setInterviewVolunteerVisitPersonnel(interviewCoordinatorVisitPersonnel);
                    notificationVisitPersonnelList.add(interviewCoordinatorVisitPersonnel);

                } else if (ObjectUtils.isNotEmpty(interviewVolunteerVisitPersonnelModel)
                    && ObjectUtils.isNotEmpty(existingVisitLocation.getInterviewVolunteerVisitPersonnel())) {

                    //update
                    final VisitPersonnel interviewVolunteerVisitPersonnel = existingVisitLocation.getInterviewVolunteerVisitPersonnel();

                    if (!interviewVolunteerVisitPersonnelModel.getPersonnelId()
                        .equals(interviewVolunteerVisitPersonnel.getPersonnel().getPersonnelId())) {

                        notificationVisitPersonnelList.add(interviewVolunteerVisitPersonnel);
                    }

                    if (!interviewVolunteerVisitPersonnelModel.getPersonnelId()
                        .equals(interviewVolunteerVisitPersonnel.getPersonnel().getPersonnelId())) {

                        interviewVolunteerVisitPersonnel.setStatus(Status.DELETED);

                        final VisitPersonnel interviewCoordinatorVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                                existingPersonnelIdWithPersonnelMap.get(interviewVolunteerVisitPersonnelModel.getPersonnelId()),
                                existingRoleIdWithRoleMap.get(interviewVolunteerVisitPersonnelModel.getRoleId()),
                                RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER,
                                existingVisitLocation.getVisitService());

                        existingVisitLocation.setInterviewVolunteerVisitPersonnel(interviewCoordinatorVisitPersonnel);
                    }
                } else {

                    //remove
                    if (ObjectUtils.isNotEmpty(existingVisitLocation.getInterviewVolunteerVisitPersonnel())) {
                        existingVisitLocation.removeInterviewVolunteerVisitPersonnel();
                    }
                }
            });

            // Save the updated visit locations.
            visitLocationRepository.saveAll(existingVisitLocationList);
        }
        return notificationVisitPersonnelList;
    }

    /**
     * Adds or updates interview setup coordinator and interviewer details for a visit.
     *
     * @param existingVisit            The existing visit for which the details are being updated.
     * @param visitInterviewSetupModel The model containing the interview setup details to be added or updated.
     * @param siteUUCode The unique code associated with the site.
     */
    @Transactional
    private void addOrUpdateInterviewSetupCoordinatorAndInterviewerDetails(final Visit existingVisit, final String siteUUCode,
                                                                              final VisitInterviewSetupModel visitInterviewSetupModel) {

        // Check for associated visit personnel in visit or visit service, excluding the current visit service
        visitPersonnelService.checkAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
            List.of(visitInterviewSetupModel.getInterviewCoordinatorVisitPersonnelModel()),
            List.of(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR),
            existingVisit.getStartDateTime(), existingVisit.getEndDateTime(),
            null, existingVisit.getVisitId(), siteUUCode);

        // Retrieve existing personnel and role based on the provided model
        final Personnel existingPersonnel =
            personnelService.findById(visitInterviewSetupModel.getInterviewCoordinatorVisitPersonnelModel().getPersonnelId());
        
        final Role existingRole = roleService.findById(visitInterviewSetupModel.getInterviewCoordinatorVisitPersonnelModel().getRoleId());

        // Find the existing interview coordinator, if any
        Optional<VisitPersonnel> interviewCoordinatorOptional = Optional.empty();
        if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {
            interviewCoordinatorOptional = existingVisit.getVisitPersonnelList().stream()
                .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR))
                .findFirst();
        }

        // Update existing interview coordinator or create a new one
        if (interviewCoordinatorOptional.isPresent()
                && !interviewCoordinatorOptional.get().getPersonnel().getPersonnelId().equals(existingPersonnel.getPersonnelId())) {

            interviewCoordinatorOptional.get().setStatus(Status.DELETED);
            
            final var visitPersonnel = new VisitPersonnel();
            visitPersonnel.setPersonnel(existingPersonnel);
            visitPersonnel.setRole(existingRole);
            visitPersonnel.setRoleTagEnum(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR);

            existingVisit.addVisitPersonnel(visitPersonnel);
            
        } else if (interviewCoordinatorOptional.isEmpty()) {
            final var visitPersonnel = new VisitPersonnel();
            visitPersonnel.setPersonnel(existingPersonnel);
            visitPersonnel.setRole(existingRole);
            visitPersonnel.setRoleTagEnum(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR);

            existingVisit.addVisitPersonnel(visitPersonnel);

        }

        visitRepository.save(existingVisit);
    }

    /**
     * Deletes interview setup for a visit.
     * Removes comments, interview package, and interview personnel.
     *
     * @param visitId Unique visit identifier.
     * @param siteUUCode The unique code associated with the site.
     */
    @Transactional
    public void deleteVisitInterviewSetup(final String visitId, final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.UPDATE_VISIT_INTERVIEW_SETUP.getAllowVisitStageList(), "visit.interview_set_up.delete.restrict");

        if (CollectionUtils.isNotEmpty(existingVisit.getVisitServiceList())) {
            final List<VisitLocation> existingVisitLocationList = existingVisit.getVisitServiceList().stream()
                .filter(visitService1 -> CollectionUtils.isNotEmpty(visitService1.getVisitLocationList()))
                .flatMap(visitService1 -> visitService1.getVisitLocationList().stream())
                .toList();

            existingVisitLocationList.forEach(existingVisitLocation -> {
                existingVisitLocation.setInterviewPackage(null);
                existingVisitLocation.removeInterviewVolunteerVisitPersonnel();
            });

            visitLocationRepository.saveAll(existingVisitLocationList);
        }

        if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {
            existingVisit.getVisitPersonnelList().stream()
                .filter(visitPersonnel -> visitPersonnel.getRoleTagEnum().equals(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR))
                .findFirst()
                .ifPresent(visitPersonnel -> existingVisit.getVisitPersonnelList().remove(visitPersonnel));
            visitRepository.save(existingVisit);
        }
    }
}
