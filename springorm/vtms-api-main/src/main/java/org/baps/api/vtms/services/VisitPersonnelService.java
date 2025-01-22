package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.repositories.VisitPersonnelRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
public class VisitPersonnelService {

    private final RoleService roleService;

    private final VisitPersonnelRepository visitPersonnelRepository;
    
    private final Translator translator;

    /**
     * Retrieves a VisitPersonnel entity by its unique identifier and site UUID.
     *
     * @param visitPersonnelId          Unique identifier of the VisitPersonnel.
     * @param siteUUCode  UUID of the associated site.
     * @return VisitPersonnel entity if found.
     * @throws DataNotFoundException If id or siteUUCode is blank, or no entity is found.
     */
    @Transactional(readOnly = true)
    public VisitPersonnel findByIdAndSiteUUCode(final String visitPersonnelId, final String siteUUCode) {
        if (StringUtils.isBlank(visitPersonnelId) || StringUtils.isBlank(siteUUCode)) {
            // Check if the provided id is blank (null or empty). If so, throw a DataNotFoundException.
            throw new DataNotFoundException(translator.toLocal("visit_personnel.with.visitPersonnelId.not.found", visitPersonnelId));
        } else {
            // Use the visitPersonnelRepository to find a VisitPersonnel by its id.
            // If not found, throw a DataNotFoundException with an appropriate message.
            return visitPersonnelRepository.findByVisitPersonnelIdAndVisitSiteUuCode(visitPersonnelId, siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit_personnel.with.visitPersonnelId.not.found",
                        visitPersonnelId)));
        }
    }

    /**
     * Checks if personnel associated with specific roles are already associated with a visit or service
     * within a specified time frame. If any personnel are already associated during the specified time frame
     * or if personnel are associated with multiple roles that require availability checks, exceptions are thrown.
     *
     * @param visitPersonnelModelList A list of VisitPersonnelModel instances to check for association.
     * @param roleTagEnumList         A list of RoleTagEnum values to filter roles.
     * @param startDateTime           The start date and time of the time frame to check.
     * @param endDateTime             The end date and time of the time frame to check.
     * @param siteUUCode The unique code associated with the site.
     *
     * @throws DataValidationException  if personnel are associated with multiple roles that require availability checks.
     * @throws DataAlreadyExistsException if personnel are already associated during the specified time frame.
     */
    @Transactional(readOnly = true)
    public void checkVisitPersonnelAssociateInVisitOrVisitService(final List<VisitPersonnelModel> visitPersonnelModelList,
            final List<RoleTagEnum> roleTagEnumList, final LocalDateTime startDateTime, final LocalDateTime endDateTime, 
            final String siteUUCode) {

        // Extract Role IDs from the VisitPersonnelModel list
        final List<String> roleIds = visitPersonnelModelList.stream()
                .map(VisitPersonnelModel::getRoleId)
                .toList();

        // Get a map of Role IDs to existing roles with the specified Role Tags
        final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleService
                .getMapOfRoleByRoleIdsAndTag(roleIds, roleTagEnumList);

        // Extract Personnel IDs of personnel associated with roles that require availability checks
        final List<String> personnelIds = visitPersonnelModelList.stream()
                .filter(visitPersonnelModel -> mapOfRoleIdWithExistingRoleList
                        .get(visitPersonnelModel.getRoleId()).isCheckAvailability())
                .map(VisitPersonnelModel::getPersonnelId)
                .toList();

        // Find duplicate personnel IDs associated with roles that require availability checks
        final List<String> duplicatePersonnelIds = personnelIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (CollectionUtils.isNotEmpty(duplicatePersonnelIds)) {
            throw new DataValidationException(translator.toLocal("personnel_id.not.associate.with.multiple.role", duplicatePersonnelIds));
        }

        // Check if any personnel are already associated during the specified time frame
        checkPersonnelAssociateInVisitOrVisitServiceOrTourSlotExceptTourSlotId(personnelIds, startDateTime, endDateTime, null, siteUUCode);
    }


    /**
     * Checks if personnel are already associated with a visit or service within a specified time frame.
     * If any personnel are already associated during the specified time frame, a DataAlreadyExistsException is thrown.
     *
     * @param personnelIds    A list of personnel IDs to check for association.
     * @param startDateTime   The start date and time of the time frame to check.
     * @param endDateTime     The end date and time of the time frame to check.
     * @param tourSlotId      The ID of the tour slot to exclude from the check.
     * @param siteUUCode The unique code associated with the site.
     *
     * @throws DataAlreadyExistsException if personnel are already associated during the specified time frame.
     */
    @Transactional(readOnly = true)
    public void checkPersonnelAssociateInVisitOrVisitServiceOrTourSlotExceptTourSlotId(final List<String> personnelIds,
            final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String tourSlotId, final String siteUUCode) {
        
        // Find personnel already associated with a visit or service within the specified time frame
        final List<VisitPersonnel> associateVisitPersonnelList =
                visitPersonnelRepository.findAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIds(
                        personnelIds, startDateTime, endDateTime, tourSlotId, siteUUCode);

        // Extract the Personnel IDs of personnel already associated with any visit or service
        final Set<String> personnelAssociatedAnyVisitServiceOrVisit = associateVisitPersonnelList.stream()
                .map(visitPersonnel -> visitPersonnel.getPersonnel().getPersonnelId())
                .collect(Collectors.toSet());

        // If any personnel are already associated during the specified time frame, throw an exception
        if (CollectionUtils.isNotEmpty(personnelAssociatedAnyVisitServiceOrVisit)) {
            throw new DataAlreadyExistsException(translator.toLocal("personnel_id.already.associated.with.visit.or.service",
                personnelAssociatedAnyVisitServiceOrVisit, startDateTime.format(GeneralConstant.DATE_TIME_FORMATTER),
                endDateTime.format(GeneralConstant.DATE_TIME_FORMATTER)));
        }
    }


    /**
     * Finds personnel associated with specific roles within a specified time frame for visits or services,
     * excluding a specific visit service.
     *
     * @param visitPersonnelModelList A list of VisitPersonnelModel objects representing personnel to check.
     * @param roleTagEnumList         A list of Role Tags to filter personnel by.
     * @param startDateTime           The start date and time of the time frame to check availability.
     * @param endDateTime             The end date and time of the time frame to check availability.
     * @param visitServiceId          The ID of the visit service to exclude from the check.
     * @param visitId                 The ID of the visit to exclude from the check.
     * @param siteUUCode The unique code associated with the site.
     * @throws DataAlreadyExistsException If any of the specified personnel are already associated with a visit or service
     *                                    during the specified time frame, excluding the specified visit service.
     */
    @Transactional(readOnly = true)
    public void checkAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
            final List<VisitPersonnelModel> visitPersonnelModelList, final List<RoleTagEnum> roleTagEnumList,
            final LocalDateTime startDateTime, final LocalDateTime endDateTime,
            final String visitServiceId, final String visitId, final String siteUUCode) {

        // Extract the Role IDs of roles that require availability checks
        final List<String> roleIds = visitPersonnelModelList.stream().map(VisitPersonnelModel::getRoleId)
                .toList();

        final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleService
                .getMapOfRoleByRoleIdsAndTag(roleIds, roleTagEnumList);

        final List<String> personnelIds = visitPersonnelModelList.stream()
                .filter(visitPersonnelModel -> mapOfRoleIdWithExistingRoleList.get(visitPersonnelModel.getRoleId())
                        .isCheckAvailability())
                .map(VisitPersonnelModel::getPersonnelId).toList();

        final List<String> duplicatePersonnelIds = personnelIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (CollectionUtils.isNotEmpty(duplicatePersonnelIds)) {
            throw new DataValidationException(translator.toLocal("personnel_id.not.associate.with.multiple.role", duplicatePersonnelIds));
        }

        final List<VisitPersonnel> associateVisitPersonnelList =
                visitPersonnelRepository.findAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
                        personnelIds, startDateTime, endDateTime, visitServiceId, visitId, siteUUCode);

        // Extract the Personnel IDs of personnel already associated with any visit or service
        final Set<String> personnelAssociatedAnyVisitServiceOrVisit = associateVisitPersonnelList.stream()
                .map(visitPersonnel -> visitPersonnel.getPersonnel().getPersonnelId()).collect(Collectors.toSet());

        // If any personnel are already associated during the specified time frame, excluding the visit service,
        // throw an exception
        if (CollectionUtils.isNotEmpty(personnelAssociatedAnyVisitServiceOrVisit)) {
            throw new DataAlreadyExistsException(translator.toLocal("personnel_id.already.associated.with.visit.or.service",
                personnelAssociatedAnyVisitServiceOrVisit, startDateTime.format(GeneralConstant.DATE_TIME_FORMATTER),
                endDateTime.format(GeneralConstant.DATE_TIME_FORMATTER)));
        }
    }

    /**
     * Retrieves a list of VisitPersonnel entities based on the specified site unique code and personnel ID.
     *
     * @param siteUUCode   The unique code of the site for which visit personnel are being retrieved.
     * @param personnelId  The ID of the personnel for whom visit personnel are being retrieved.
     * @return List of VisitPersonnel entities that match the criteria.
     */
    @Transactional(readOnly = true)
    public List<VisitPersonnel> findVisitPersonnelListBySiteUucodeAndPersonnelId(final String siteUUCode, final String personnelId) {
        // Using the visitPersonnelRepository to find visit personnel with specified conditions
        return visitPersonnelRepository.findByVisitStatusNotAndPersonnelPersonnelIdAndPersonnelStatusNotAndVisitSiteUuCode(
                Status.DELETED, personnelId, Status.DELETED, siteUUCode);
    }
    
    /**
     * Retrieves a list of VisitPersonnel based on the provided visit ID, site unique code, and personnel ID.
     * 
     * @param visitId     The identifier of the visit.
     * @param siteUUCode  The unique code of the site.
     * @param personnelId The identifier of the personnel.
     * @return A list of VisitPersonnel matching the provided parameters.
     */
    @Transactional(readOnly = true)
    public List<VisitPersonnel> findVisitPersonnelListByVisitIdAndSiteUucodeAndPersonnelId(final String visitId, final String siteUUCode,
            final String personnelId) {
        return visitPersonnelRepository
                .findByVisitVisitIdAndVisitStatusNotAndPersonnelPersonnelIdAndPersonnelStatusNotAndVisitSiteUuCode(visitId, Status.DELETED,
                        personnelId, Status.DELETED, siteUUCode);
    }
}
