package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.APIModuleEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.repositories.VisitPersonnelRepository;
import org.baps.api.vtms.repositories.VisitRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class VisitTeamService {

    private final VisitService visitService;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final RoleService roleService;

    private final PersonnelService personnelService;

    private final VisitPersonnelService visitPersonnelService;

    private final NotificationComposeService notificationComposeService;

    private final VisitPersonnelRepository visitPersonnelRepository;

    private final VisitRepository visitRepository;

    private final Translator translator;

    /**
     * Create or Updates the visit team personnel for a specific visit based on the provided visitId and a list
     * of VisitPersonnelModel objects.
     *
     * @param visitId                 The unique identifier of the visit to be updated.
     * @param visitPersonnelModelList A list of VisitPersonnelModel objects representing the updated
     *                                visit personnel.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of VisitPersonnelModel objects representing the updated visit team personnel.
     * @throws DataNotFoundException If any of the specified Visit Personnel IDs are not found.
     */
    public List<VisitPersonnelModel> createOrUpdateVisitTeam(final String visitId,
            final List<VisitPersonnelModel> visitPersonnelModelList, final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.CREATE_OR_UPDATE_VISIT_TEAM.getAllowVisitStageList(), "visit.service.create_or_update.restrict");

        final boolean visitAccepted;

        final var visitPersonnelIdsSet = visitPersonnelModelList.stream()
                .map(VisitPersonnelModel::getVisitPersonnelId).collect(Collectors.toSet());

        // Remove visit personnel not present in the list
        existingVisit.getVisitPersonnelList().removeIf(visitPersonnel -> visitPersonnel.getRoleTagEnum()
                .equals(GeneralConstant.TEAM_ROLE_TAG) && !visitPersonnelIdsSet.contains(visitPersonnel.getVisitPersonnelId()));

        // Collect role and personnel IDs
        final var roleIds = visitPersonnelModelList.stream().map(VisitPersonnelModel::getRoleId)
                .toList();

        // Retrieve existing roles and personnel based on their IDs
        final var mapOfRoleIdWithExistingRoleList = roleService
                .getMapOfRoleByRoleIdsAndTag(roleIds, List.of(GeneralConstant.TEAM_ROLE_TAG));

        visitAccepted = checkVisitAccepted(mapOfRoleIdWithExistingRoleList, existingVisit);

        final var personnelIds = visitPersonnelModelList.stream()
                .map(VisitPersonnelModel::getPersonnelId).toList();

        final var mapOfPersonnelIdWithExistingPersonnelList = personnelService
                .getMapOfPersonnelByPersonnelIds(personnelIds);

        // Check for associated visit personnel
        visitPersonnelService.checkAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
                visitPersonnelModelList, List.of(GeneralConstant.TEAM_ROLE_TAG),
                existingVisit.getStartDateTime(), existingVisit.getEndDateTime(),
                null, existingVisit.getVisitId(), siteUUCode);

        // Filter visit personnel IDs to update
        final var visitPersonnelIdSetToUpdate = visitPersonnelModelList.stream()
                .map(VisitPersonnelModel::getVisitPersonnelId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        // Create a map of existing visit personnel by their IDs
        final var existingVisitPersonnel =
                visitPersonnelRepository.findAllByVisitPersonnelIdInAndVisitVisitIdAndRoleTagEnumAndVisitStatusNotAndVisitSiteUuCode(
                        visitPersonnelIdSetToUpdate, existingVisit.getVisitId(), GeneralConstant.TEAM_ROLE_TAG, Status.DELETED, siteUUCode);

        // Create a map of existing visit personnel by their IDs
        final var existingVisitPersonnelMap = existingVisitPersonnel.stream()
                .collect(Collectors.toMap(VisitPersonnel::getVisitPersonnelId, visitPersonnel -> visitPersonnel));

        // Find invalid personnel IDs (not in the existing visit personnel)
        final Set<String> invalidPersonnelIds = visitPersonnelIdSetToUpdate.stream()
                .filter(s -> !existingVisitPersonnelMap.containsKey(s))
                .collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(invalidPersonnelIds)) {
            throw new DataNotFoundException(translator.toLocal("visit_personnel.with.visitPersonnelId.not.found", invalidPersonnelIds));
        }

        final List<VisitPersonnel> addOrUpdatedVisitPersonnel = new ArrayList<>();

        visitPersonnelModelList.forEach(visitPersonnelModel -> {

            if (StringUtils.isNotBlank(visitPersonnelModel.getVisitPersonnelId())) {

                final var visitPersonnelToUpdate = existingVisitPersonnelMap.get(visitPersonnelModel.getVisitPersonnelId());

                if (visitPersonnelToUpdate.getRole().getUucode().equals(RoleEnum.VISIT_ADMIN.name())
                        && !visitPersonnelToUpdate.getPersonnel().getPersonnelId().equals(visitPersonnelModel.getPersonnelId())) {
                    throw new DataValidationException(translator.toLocal("personnel.role.change.restrict", RoleEnum.VISIT_ADMIN.name()));
                }

                final var personnelChanged =
                        !existingVisitPersonnelMap.get(visitPersonnelModel.getVisitPersonnelId()).getPersonnel().getPersonnelId()
                        .equals(visitPersonnelModel.getPersonnelId());

                if (personnelChanged) {
                    
                    visitPersonnelToUpdate.setStatus(Status.DELETED);
                    
                    final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                            mapOfPersonnelIdWithExistingPersonnelList.get(visitPersonnelModel.getPersonnelId()),
                            mapOfRoleIdWithExistingRoleList.get(visitPersonnelModel.getRoleId()), GeneralConstant.TEAM_ROLE_TAG, null);
                    
                    existingVisit.addVisitPersonnel(visitPersonnelToAdd);
                    addOrUpdatedVisitPersonnel.add(visitPersonnelToAdd);
                }

            } else {
                if (mapOfRoleIdWithExistingRoleList.get(visitPersonnelModel.getRoleId()).getUucode()
                        .equals(RoleEnum.VISIT_ADMIN.name())) {
                    throw new DataValidationException(translator.toLocal("personnel.role.add.restrict", RoleEnum.VISIT_ADMIN.name()));
                }

                final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                        mapOfPersonnelIdWithExistingPersonnelList.get(visitPersonnelModel.getPersonnelId()),
                        mapOfRoleIdWithExistingRoleList.get(visitPersonnelModel.getRoleId()), GeneralConstant.TEAM_ROLE_TAG, null);
                
                existingVisit.addVisitPersonnel(visitPersonnelToAdd);
                addOrUpdatedVisitPersonnel.add(visitPersonnelToAdd);
            }
        });

        if (visitAccepted) {

            final var personnel = personnelService.getLoginedPersonnel();

            final var visitPersonnel = new VisitPersonnel();
            visitPersonnel.setPersonnel(personnel);
            visitPersonnel.setRole(roleService.findByRoleEnum(RoleEnum.VISIT_ADMIN));
            visitPersonnel.setRoleTagEnum(RoleTagEnum.TEAM);

            existingVisit.addVisitPersonnel(visitPersonnel);
        }

        visitRepository.save(existingVisit);

        sendVisitTeamNotification(existingVisit, addOrUpdatedVisitPersonnel, visitAccepted);

        return visitPersonnelMapper.mapVisitPersonnelModelListByRoleTagList(
                existingVisit.getVisitPersonnelList(), GeneralConstant.TEAM_ROLE_TAG);
    }

    /**
     * Checks if the visit is accepted based on the presence of specific roles and the current visit stage.
     *
     * @param mapOfRoleIdWithExistingRoleList A map containing role IDs mapped to existing roles for the visit.
     * @param existingVisit The existing visit entity being checked.
     * @return true if the visit is accepted, false otherwise.
     * @throws DataValidationException if required roles are not present or if the visit stage is invalid.
     */
    @Transactional(readOnly = true)
    private boolean checkVisitAccepted(final  Map<String, Role> mapOfRoleIdWithExistingRoleList, final Visit existingVisit) {

        // Check if Relationship Manager role is present
        final boolean relationShipManagerPresent = mapOfRoleIdWithExistingRoleList.values().stream()
                .anyMatch(existingRole -> existingRole.getUucode().equals(RoleEnum.RELATIONSHIP_MANAGER.name()));

        boolean visitAccepted = false;

        if (!relationShipManagerPresent) {
            throw new DataValidationException(translator.toLocal("value.required", 
                    roleService.findByRoleEnum(RoleEnum.RELATIONSHIP_MANAGER)));
        }

        // Check if Guest Visit Coordinator role is present
        final boolean guestVisitCoordinatorPresent = mapOfRoleIdWithExistingRoleList.values().stream()
                .anyMatch(existingRole -> existingRole.getUucode().equals(RoleEnum.GUEST_VISIT_COORDINATOR.name()));


        // Check if the visit is in the PENDING stage
        if (!guestVisitCoordinatorPresent) {
            throw new DataValidationException(translator.toLocal("value.required",
                    roleService.findByRoleEnum(RoleEnum.GUEST_VISIT_COORDINATOR)));
        }

        if (existingVisit.getVisitStageEnum().equals(VisitStageEnum.PENDING)) {

            visitService.validateVisitStage(existingVisit, VisitStageEnum.ACCEPTED);
            existingVisit.setVisitStageEnum(VisitStageEnum.ACCEPTED);

            // Set visitAccepted to true
            visitAccepted =  true;
        }

        if (!visitAccepted) {

            // If visit is not accepted, check for the presence of Visit Admin role
            final boolean visitAdminPresent = mapOfRoleIdWithExistingRoleList.values().stream()
                    .anyMatch(existingRole -> existingRole.getUucode().equals(RoleEnum.VISIT_ADMIN.name()));

            if (!visitAdminPresent) {
                throw new DataValidationException(translator.toLocal("value.required", RoleEnum.VISIT_ADMIN));
            }
        }
        // Return false as the visit is not accepted
        return visitAccepted;
    }

    private void sendVisitTeamNotification(final Visit visit, final List<VisitPersonnel> visitPersonnelList, final boolean visitAccepted) {

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            notificationComposeService.sendNewVisitAssigned(visitPersonnelList);
        }

        if (visitAccepted) {
            notificationComposeService.sendVisitConfirmationNotification(visit);
            notificationComposeService.sendVisitAcceptedSuccessfully(visit);
        }
    }

    /**
     * Retrieves a list of VisitPersonnelModel objects representing the entire visit team for a given visit.
     *
     * @param visitId The unique identifier of the visit for which the visit team is to be retrieved.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of VisitPersonnelModel objects representing the visit team associated with the specified visit.
     */
    @Transactional(readOnly = true)
    public List<VisitPersonnelModel> getAllVisitTeam(final String visitId, final String siteUUCode) {
        return visitPersonnelMapper.visitPersonnelListToVisitPersonnelModelList(
                visitPersonnelRepository.findAllByVisitVisitIdAndRoleTagEnumAndVisitSiteUuCode(visitId,
                        GeneralConstant.TEAM_ROLE_TAG, siteUUCode));
    }
}