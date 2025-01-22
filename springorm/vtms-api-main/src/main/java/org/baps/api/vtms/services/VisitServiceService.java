package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.APIModuleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.VisitLocationMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.mappers.VisitServiceMapper;
import org.baps.api.vtms.models.BaseVisitServiceModel;
import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitServiceModel;
import org.baps.api.vtms.models.VisitServiceWrapperModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Location;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.ServiceTemplate;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;
import org.baps.api.vtms.repositories.VisitServiceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
public class VisitServiceService {

    private final org.baps.api.vtms.services.VisitService visitService;

    private final ServiceTemplateService serviceTemplateService;

    private final RoleService roleService;

    private final PersonnelService personnelService;

    private final VisitPersonnelService visitPersonnelService;

    private final VisitServiceRepository visitServiceRepository;

    private final VisitServiceMapper visitServiceMapper;

    private final NotificationComposeService notificationComposeService;

    private final Translator translator;

    private final MasterService masterService;

    private final VisitLocationMapper visitLocationMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final VisitLocationService visitLocationService;
    
    private final CommonUtils commonUtils;

    /**
     * Retrieves a VisitService based on the given visitId and visitServiceId.
     *
     * @param visitId        The unique identifier of the visit.
     * @param visitServiceId The unique identifier of the VisitService within the visit.
     * @param siteUUCode     The unique code associated with the site.
     * @return The VisitService if found.
     * @throws DataNotFoundException If no VisitService with the specified identifiers is found.
     */
    @Transactional(readOnly = true)
    public VisitService findByVisitIdAndVisitServiceId(final String visitId, final String visitServiceId, final String siteUUCode) {
               
        // Check if the provided identifier is blank.
        if (StringUtils.isBlank(visitId) || StringUtils.isBlank(visitServiceId)) {
            // If blank, throw a DataNotFoundException with a specific message.
            throw new DataNotFoundException(translator.toLocal("visit.service.with.visit_id.not.found", visitId, visitServiceId));
        } else {
            // Otherwise, attempt to find the VisitService by its identifier.
            return visitServiceRepository.findByVisitVisitIdAndVisitStatusNotAndVisitServiceIdAndVisitSiteUuCode(visitId, Status.DELETED,
                    visitServiceId, siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.service.with.visit_id.not.found",
                    visitId, visitServiceId)));
        }
    }

    /**
     * Validates that a given coordinator is associated with the specified meeting coordinator team.
     *
     * @param existingVisitService The existing visit service containing personnel information.
     * @param coordinator          The coordinator to validate.
     * @throws DataNotFoundException If the coordinator is not found in the meeting coordinator team.
     */
    public void validateMeetingCoordinatorTeamId(final Visit existingVisitService,
                                                 final VisitPersonnelModel coordinator) {

        final List<String> teamPersonnelIds = new ArrayList<>();
        final List<String> teamRoleIds = new ArrayList<>();

        existingVisitService.getVisitPersonnelList().forEach(visitPersonnel -> {
            if (visitPersonnel.getRoleTagEnum().equals(GeneralConstant.TEAM_ROLE_TAG)) {
                teamPersonnelIds.add(visitPersonnel.getPersonnel().getPersonnelId());
                teamRoleIds.add(visitPersonnel.getRole().getRoleId());
            }
        });

        if (!teamPersonnelIds.contains(coordinator.getPersonnelId())) {
            throw new DataNotFoundException(
                translator.toLocal("personnel_id.not.found.team", coordinator.getPersonnelId(), GeneralConstant.TEAM_ROLE_TAG));
        }
    }

    /**
     * Creates a new VisitService based on the provided VisitServiceModel.
     *
     * @param visitId           The ID of the visit to which the service belongs.
     * @param visitServiceModel The VisitServiceModel containing service details.
     * @param siteUUCode        The unique code associated with the site.
     * @return The created VisitService.
     * @throws DataValidationException    If data validation fails.
     * @throws DataNotFoundException      If data is not found.
     * @throws DataAlreadyExistsException If the service already exists in the same time frame.
     */
    public VisitServiceModel createVisitService(final String visitId, final VisitServiceModel visitServiceModel,
                                                final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.CREATE_VISIT_SERVICE_OR_MEETING.getAllowVisitStageList(), "visit.service.create.restrict");

        final RoleTagEnum coordinatorRoleTagEnum = getRoleTagEnumByServiceTypeEnum(
            getServiceTypeEnumByServiceType(visitServiceModel.getServiceType()));
        Personnel meetingPersonnel = null;

        // Retrieve the existing service template based on serviceTemplateId and serviceTypeEnum.
        final var existingServiceTemplate = serviceTemplateService.findByIdAndServiceTypeEnum(visitServiceModel.getServiceTemplateId(),
            getServiceTypeEnumByServiceType(visitServiceModel.getServiceType()), siteUUCode);

        // Validate that startDateTime and endDateTime are within the visit's time frame.
        commonUtils.checkDateTimeRangeConflict(existingServiceTemplate.isCheckVisitTime(), visitServiceModel.getStartDateTime(), 
                visitServiceModel.getEndDateTime(), existingVisit.getStartDateTime(), existingVisit.getEndDateTime());

        // Check if another Visit service already exists in the same time frame.
        checkVisitServiceExists(visitId, null, existingServiceTemplate, visitServiceModel);

        // Retrieve the service coordinator information from the VisitServiceModel.
        final VisitPersonnelModel coordinator = visitServiceModel.getCoordinator();

        // Map the visitServiceModel to a VisitService entity.
        final var saveVisitService = visitServiceMapper.visitServiceModelToVisitService(visitServiceModel);
        saveVisitService.setVisit(existingVisit);
        saveVisitService.setServiceTemplate(existingServiceTemplate);

        // validate meetingPersonnel if serviceType is MEETING.
        if (getServiceTypeEnumByServiceType(visitServiceModel.getServiceType()).equals(ServiceTypeEnum.MEETING)) {
            if (ObjectUtils.isEmpty(visitServiceModel.getMeetingPersonnel())) {
                throw new DataValidationException(translator.toLocal("meeting.personnel.required"));
            }
            
            meetingPersonnel = personnelService.findById(visitServiceModel.getMeetingPersonnel().getPersonnelId());
            validateMeetingCoordinatorTeamId(existingVisit, visitServiceModel.getCoordinator());

            saveVisitService.setMeetingPersonnel(meetingPersonnel);
        }

        // Retrieve the Role and Personnel information for the coordinator.
        final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleService
            .getMapOfRoleByRoleIdsAndTag(List.of(coordinator.getRoleId()), List.of(coordinatorRoleTagEnum));

        final Map<String, Personnel> mapOfPersonnelIdWithExistingPersonnelList = personnelService
            .getMapOfPersonnelByPersonnelIds(List.of(coordinator.getPersonnelId()));

        // Check if personnel is associated with the visit or visit service within the specified time frame.
        visitPersonnelService.checkVisitPersonnelAssociateInVisitOrVisitService(
            List.of(visitServiceModel.getCoordinator()), List.of(coordinatorRoleTagEnum),
            visitServiceModel.getStartDateTime(), visitServiceModel.getEndDateTime(), siteUUCode);

        // Create a new VisitPersonnel and set its attributes.        
        final var saveVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                mapOfPersonnelIdWithExistingPersonnelList.get(coordinator.getPersonnelId()),
                mapOfRoleIdWithExistingRoleList.get(coordinator.getRoleId()), coordinatorRoleTagEnum, null);

        // Add the VisitPersonnel to the VisitService.
        saveVisitService.addVisitPersonnel(saveVisitPersonnel);
        
        addOrUpdateVisitLocationInVisitService(
            saveVisitService, visitServiceModel, getServiceTypeEnumByServiceType(visitServiceModel.getServiceType()));

        visitServiceRepository.save(saveVisitService);

        sendVisitServiceNotification(true, saveVisitPersonnel, saveVisitService, Optional.ofNullable(meetingPersonnel));

        return visitServiceMapper.visitServiceToVisitServiceModel(saveVisitService, coordinatorRoleTagEnum);
    }

    /**
     * Updates a visit service with the given visitId and visitServiceId using the provided
     * VisitServiceModel.
     *
     * @param visitId           The ID of the visit to which the service belongs.
     * @param visitServiceId    The ID of the visit service to update.
     * @param visitServiceModel The updated VisitServiceModel containing service information.
     * @param siteUUCode        The unique code associated with the site.
     * @return The updated VisitServiceModel after the update operation.
     * @throws DataValidationException if there are data validation issues (e.g., invalid date range).
     * @throws DataNotFoundException   if data related to the visit service or personnel is not found.
     */
    public VisitServiceModel updateVisitService(final String visitId, final String visitServiceId,
                                                final VisitServiceModel visitServiceModel, final String siteUUCode) {


        visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode,
                APIModuleEnum.UPDATE_VISIT_SERVICE_OR_MEETING.getAllowVisitStageList(), "visit.service.update.restrict");
        
        // Retrieve the existing visit service by visitId and visitServiceId.
        final var existingVisitService = findByVisitIdAndVisitServiceId(visitId, visitServiceId, siteUUCode);

        boolean coordinatorChanged = false;

        Optional<Personnel> changedMeetingPersonnelOptional = Optional.empty();

        // Determine the service type and coordinator role tag for the visit service.
        final ServiceTypeEnum serviceTypeEnum = getServiceTypeEnumByServiceType(visitServiceModel.getServiceType());
        final RoleTagEnum coordinatorRoleTagEnum = getRoleTagEnumByServiceTypeEnum(serviceTypeEnum);

        // Check if the updated date range is valid within the visit's date range.
        commonUtils.checkDateTimeRangeConflict(existingVisitService.getServiceTemplate().isCheckVisitTime(), 
                visitServiceModel.getStartDateTime(), visitServiceModel.getEndDateTime(), 
                existingVisitService.getVisit().getStartDateTime(), existingVisitService.getVisit().getEndDateTime());
        
        checkVisitServiceExists(visitId, visitServiceId, existingVisitService.getServiceTemplate(), visitServiceModel);

        // Extract the coordinator from the updated VisitServiceModel.
        final VisitPersonnelModel coordinator = visitServiceModel.getCoordinator();

        // validate meetingPersonnel if serviceType is MEETING.
        if (serviceTypeEnum.equals(ServiceTypeEnum.MEETING)) {
            if (ObjectUtils.isEmpty(visitServiceModel.getMeetingPersonnel())) {
                throw new DataValidationException(translator.toLocal("meeting.personnel.required"));
            }
            validateMeetingCoordinatorTeamId(existingVisitService.getVisit(), visitServiceModel.getCoordinator());

            final Personnel meetingPersonnel = personnelService.findById(visitServiceModel.getMeetingPersonnel().getPersonnelId());

            if (!existingVisitService.getMeetingPersonnel().getPersonnelId().equals(meetingPersonnel.getPersonnelId())) {

                existingVisitService.setMeetingPersonnel(meetingPersonnel);
                changedMeetingPersonnelOptional = Optional.of(meetingPersonnel);
            }
        }

        // Retrieve existing roles and personnel data.
        final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleService
            .getMapOfRoleByRoleIdsAndTag(List.of(coordinator.getRoleId()), List.of(coordinatorRoleTagEnum));

        final Map<String, Personnel> mapOfPersonnelIdWithExistingPersonnelList = personnelService
            .getMapOfPersonnelByPersonnelIds(List.of(coordinator.getPersonnelId()));

        // Check for associated visit personnel within the specified date range.
        visitPersonnelService.checkAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
            List.of(visitServiceModel.getCoordinator()), List.of(coordinatorRoleTagEnum),
            visitServiceModel.getStartDateTime(), visitServiceModel.getEndDateTime(),
            visitServiceId, null, siteUUCode);

        // Map data from the updated VisitServiceModel to the existing visit service.
        visitServiceMapper.visitServiceModelToVisitService(existingVisitService, visitServiceModel);

        // Retrieve the existing VisitPersonnel by visitPersonnelId.
        final VisitPersonnel existingVisitPersonnel = visitPersonnelService.findByIdAndSiteUUCode(visitServiceModel.getCoordinator()
            .getVisitPersonnelId(), siteUUCode);

        // Ensure that the existing personnel is associated with the correct role tag.
        if (!existingVisitPersonnel.getRoleTagEnum().equals(coordinatorRoleTagEnum)) {
            throw new DataNotFoundException(translator.toLocal("visit.personnel.not.associated.role.tag",
                visitServiceModel.getCoordinator().getVisitPersonnelId(),
                coordinatorRoleTagEnum));
        }

        // Set the updated personnel and role data for the existing VisitPersonnel.
        if (!existingVisitPersonnel.getPersonnel().getPersonnelId()
            .equals(mapOfPersonnelIdWithExistingPersonnelList
                .get(coordinator.getPersonnelId()).getPersonnelId())) {
            coordinatorChanged = true;
                        
            existingVisitPersonnel.setStatus(Status.DELETED);
            
            final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisitService.getVisit(),
                    mapOfPersonnelIdWithExistingPersonnelList.get(coordinator.getPersonnelId()),
                    mapOfRoleIdWithExistingRoleList.get(coordinator.getRoleId()), coordinatorRoleTagEnum, null);
            
            existingVisitService.addVisitPersonnel(visitPersonnelToAdd);
        }
        
        addOrUpdateVisitLocationInVisitService(existingVisitService, visitServiceModel, serviceTypeEnum);

        // Save the updated visit service to the repository.
        visitServiceRepository.save(existingVisitService);

        sendVisitServiceNotification(
            coordinatorChanged, existingVisitPersonnel, existingVisitService, changedMeetingPersonnelOptional);

        // Convert the updated visit service back to a VisitServiceModel and return it.
        return visitServiceMapper.visitServiceToVisitServiceModel(existingVisitService, coordinatorRoleTagEnum);
    }
    
    /**
     * Checks if another visit service already exists in the same time frame and throws a
     * DataAlreadyExistsException if it does.
     *
     * @param visitId               The ID of the visit to which the service belongs.
     * @param visitServiceId        The ID of the visit service being checked.
     * @param existingServiceTemplate The existing service template associated with the visit service.
     * @param visitServiceModel     The model containing information about the visit service.
     * @throws DataAlreadyExistsException if another visit service already exists in the specified time frame.
     */
    private void  checkVisitServiceExists(final String visitId, final String visitServiceId, 
            final ServiceTemplate existingServiceTemplate, final VisitServiceModel visitServiceModel) {

        String meetingPersonnelId = null;
        if (ObjectUtils.isNotEmpty(visitServiceModel.getMeetingPersonnel())) {
            meetingPersonnelId = visitServiceModel.getMeetingPersonnel().getPersonnelId();
        }
        
        // Check if another Visit service already exists in the same time frame.
        final boolean isVisitServiceExistsInSameTime = visitServiceRepository.existsByVisitServiceStartEndDateTime(
                visitId, visitServiceId, existingServiceTemplate.getServiceTemplateId(), meetingPersonnelId,
                visitServiceModel.getStartDateTime(), visitServiceModel.getEndDateTime());

        // If any other Visit service is already exists in same time.
        if (isVisitServiceExistsInSameTime) {
            throw new DataAlreadyExistsException(
                    translator.toLocal("visit.service.already.exist.datetime.range", existingServiceTemplate.getName()));
        }
    }

    /**
     * Sends notification(s) in the provided {@link VisitService}.
     *
     * @param coordinatorAddedOrChanged A boolean indicating whether the coordinator for the visit has changed.
     * @param coordinatorVisitPersonnel The {@link VisitPersonnel} representing the new coordinator.
     * @param visitService The {@link VisitService} for which the notification is being sent.
     * @param changedMeetingPersonnelOptional An optional {@link Personnel} representing personnel with
     *                                        changed meeting details, if applicable.
     */
    private void sendVisitServiceNotification(
        final boolean coordinatorAddedOrChanged, final VisitPersonnel coordinatorVisitPersonnel,
        final VisitService visitService, final Optional<Personnel> changedMeetingPersonnelOptional) {

        if (coordinatorAddedOrChanged && ObjectUtils.isNotEmpty(coordinatorVisitPersonnel)) {
            notificationComposeService.sendVisitAssignedNotification(
                visitService.getVisit(), List.of(coordinatorVisitPersonnel),
                visitService.getServiceTemplate().getServiceTypeEnum().name());
        }

        changedMeetingPersonnelOptional.ifPresent(personnel -> notificationComposeService
            .sendMeetingWithGuestNotification(visitService, personnel));
    }

    /**
     * This method adds or updates visit locations in a visit service based on the provided VisitTourModel.
     *
     * @param saveOrUpdateVisitService The visit service to update.
     * @param baseVisitServiceModel    The model containing visit location information.
     * @param serviceTypeEnum          service type to validate location
     */
    public void addOrUpdateVisitLocationInVisitService(final VisitService saveOrUpdateVisitService,
                                                       final BaseVisitServiceModel baseVisitServiceModel,
                                                       final ServiceTypeEnum serviceTypeEnum) {

        // Retrieve a map of existing locations by their IDs.
        final Map<String, Location> mapOfLocationIdWithExistingLocation;
        final Set<String> visitLocationIdToUpdate;

        if (CollectionUtils.isNotEmpty(baseVisitServiceModel.getVisitLocationModelList())) {

            mapOfLocationIdWithExistingLocation = masterService
                .getMapOfLocationIdWithLocation(baseVisitServiceModel.getVisitLocationModelList()
                    .stream().map(VisitLocationModel::getLocationId)
                    .toList(), saveOrUpdateVisitService.getServiceTemplate().getServiceTemplateId());
            
            // Extract IDs of visit locations to be updated.
            visitLocationIdToUpdate = baseVisitServiceModel.getVisitLocationModelList().stream()
                .map(VisitLocationModel::getVisitLocationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        } else {
            mapOfLocationIdWithExistingLocation = new HashMap<>();
            visitLocationIdToUpdate = new HashSet<>();
        }

        // Find existing visit locations based on their IDs.
        final List<VisitLocation> exisitingVisitLocationList =
            visitLocationService.findAllByVisitLocationIdInAndVisitServiceVisitServiceId(visitLocationIdToUpdate,
                saveOrUpdateVisitService.getVisitServiceId());

        // Create a map of existing visit locations for quick lookup.
        final Map<String, VisitLocation> existingVisitLocationMap = exisitingVisitLocationList.stream()
            .collect(Collectors.toMap(VisitLocation::getVisitLocationId, visitLocation -> visitLocation));

        // Identify invalid visit location IDs that don't exist.
        final Set<String> invalidVisitLocationIds = visitLocationIdToUpdate.stream()
            .filter(visitLocationId -> !existingVisitLocationMap.containsKey(visitLocationId))
            .collect(Collectors.toSet());

        // If invalid IDs are found, throw a DataNotFoundException.
        if (CollectionUtils.isNotEmpty(invalidVisitLocationIds)) {
            throw new DataNotFoundException(translator.toLocal("visit.location.not.found", invalidVisitLocationIds));
        }

        // Remove visit locations from the service that don't exist in the updated list.
        if (CollectionUtils.isNotEmpty(saveOrUpdateVisitService.getVisitLocationList())) {
            saveOrUpdateVisitService.getVisitLocationList().removeIf(visitLocation -> !existingVisitLocationMap
                .containsKey(visitLocation.getVisitLocationId()));
        }

        if (CollectionUtils.isNotEmpty(baseVisitServiceModel.getVisitLocationModelList())) {
            baseVisitServiceModel.getVisitLocationModelList().forEach(visitLocationModel -> {

                if (StringUtils.isNotBlank(visitLocationModel.getVisitLocationId())) {

                    // Update an existing visit location with data from the model.
                    visitLocationMapper.visitLocationModelToVisitLocation(
                        existingVisitLocationMap.get(visitLocationModel.getVisitLocationId()), visitLocationModel);

                    // Set the location of the visit location using the existing location map.
                    existingVisitLocationMap.get(visitLocationModel.getVisitLocationId()).setLocation(
                        mapOfLocationIdWithExistingLocation.get(visitLocationModel.getLocationId()));
                } else {

                    // Create a new visit location from the model and set its location.
                    final var saveVisitLocation = visitLocationMapper.visitLocationModelToVisitLocation(visitLocationModel);

                    saveVisitLocation.setLocation(mapOfLocationIdWithExistingLocation.get(visitLocationModel.getLocationId()));

                    // Add the new visit location to the visit service.
                    saveOrUpdateVisitService.addVisitLocation(saveVisitLocation);
                }
            });
        }
    }

    /**
     * Retrieves a list of VisitServiceWrapperModel objects by visitId and serviceType.
     *
     * @param visitId     The ID of the visit.
     * @param serviceType The type of service (SERVICE or MEETING).
     * @param siteUUCode  The unique code associated with the site.
     * @return A list of VisitServiceWrapperModel objects.
     * @throws DataValidationException If data validation fails.
     */
    @Transactional(readOnly = true)
    public List<VisitServiceWrapperModel> getVisitServiceListByVisitId(final String visitId, final String serviceType,
                                                                       final String siteUUCode) {

        final ServiceTypeEnum serviceTypeEnum = getServiceTypeEnumByServiceType(serviceType);

        final RoleTagEnum coordinatorRoleTagEnum = getRoleTagEnumByServiceTypeEnum(serviceTypeEnum);

        // Retrieve existing VisitService entities based on visitId, serviceType, and status filters.
        final var existingVisitService = visitServiceRepository
            .findByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnumAndServiceTemplateStatusNotAndVisitSiteUuCode(
                visitId, Status.DELETED, serviceTypeEnum, Status.DELETED, siteUUCode);

        // Map the found VisitService entities to VisitServiceModel objects filtered by the role tag.
        final List<VisitServiceModel> visitServiceModelList = visitServiceMapper.visitServiceListToVisitServiceModelListByTag(
            existingVisitService, coordinatorRoleTagEnum);

        // Group the VisitServiceModel objects by their serviceTemplateId and map them to VisitServiceWrapperModel objects.
        return visitServiceModelList.stream()
            .collect(Collectors.groupingBy(VisitServiceModel::getServiceTemplateId)).values()
            .stream().map(visitServiceMapper::visitServiceModelListToVisitServiceWrapperModel)
            .toList();
    }

    /**
     * Retrieves a VisitServiceModel by its visitId and visitServiceId.
     *
     * @param visitId        The unique identifier of the visit.
     * @param visitServiceId The unique identifier of the VisitService within the visit.
     * @param siteUUCode     The unique code associated with the site.
     * @return The VisitServiceModel representing the requested VisitService.
     */
    @Transactional(readOnly = true)
    public VisitServiceModel getVisitServiceById(final String visitId, final String visitServiceId, final String siteUUCode) {

        // Retrieve the existing VisitService using the provided identifiers.
        final var existingVisitService = findByVisitIdAndVisitServiceId(visitId, visitServiceId, siteUUCode);

        // Extract the service type from the existing VisitService.
        final ServiceTypeEnum serviceTypeEnum = existingVisitService.getServiceTemplate().getServiceTypeEnum();

        // Determine the coordinator role tag based on the service type.
        final RoleTagEnum coordinatorRoleTagEnum = getRoleTagEnumByServiceTypeEnum(serviceTypeEnum);

        // Map the existing VisitService to a VisitServiceModel, considering the coordinator role tag.
        return visitServiceMapper.visitServiceToVisitServiceModel(
            existingVisitService, coordinatorRoleTagEnum);
    }

    /**
     * Retrieves the ServiceTypeEnum corresponding to a given serviceType string.
     *
     * @param serviceType The string representation of the service type.
     * @return The ServiceTypeEnum matching the provided serviceType.
     * @throws DataValidationException If the provided serviceType is not valid.
     */
    @Transactional(readOnly = true)
    private ServiceTypeEnum getServiceTypeEnumByServiceType(final String serviceType) {
        // Check if serviceType is valid by comparing it to allowed values.
        if (!List.of(ServiceTypeEnum.SERVICE.name(), ServiceTypeEnum.MEETING.name()).contains(serviceType)) {
            // If the serviceType is not valid, throw a DataValidationException.
            throw new DataValidationException(translator.toLocal("allowed.service.type",
                ServiceTypeEnum.SERVICE.name() + ", " + ServiceTypeEnum.MEETING.name()));
        }

        // Convert the valid serviceType string to a ServiceTypeEnum.
        return ServiceTypeEnum.valueOf(serviceType);
    }

    /**
     * Retrieves the RoleTagEnum corresponding to a given ServiceTypeEnum.
     *
     * @param serviceTypeEnum The ServiceTypeEnum to determine the RoleTagEnum for.
     * @return The RoleTagEnum matching the provided ServiceTypeEnum.
     */
    @Transactional(readOnly = true)
    public RoleTagEnum getRoleTagEnumByServiceTypeEnum(final ServiceTypeEnum serviceTypeEnum) {
        RoleTagEnum coordinatorRoleTagEnum = null;

        if (serviceTypeEnum.equals(ServiceTypeEnum.SERVICE)) {
            // If the ServiceTypeEnum is SERVICE, set the coordinator role to SERVICE_COORDINATOR.
            coordinatorRoleTagEnum = RoleTagEnum.SERVICE_COORDINATOR;
        } else if (serviceTypeEnum.equals(ServiceTypeEnum.MEETING)) {
            // If the ServiceTypeEnum is MEETING, set the coordinator role to MEETING_COORDINATOR.
            coordinatorRoleTagEnum = RoleTagEnum.MEETING_COORDINATOR;
        }

        // Return the determined coordinator role tag.
        return coordinatorRoleTagEnum;
    }

    /**
     * Deletes a VisitService by its visitId and visitServiceId.
     *
     * @param visitId        The unique identifier of the visit.
     * @param visitServiceId The unique identifier of the VisitService within the visit.
     * @param siteUUCode     The unique code associated with the site.
     */
    @Transactional
    public void deleteVisitServiceById(final String visitId, final String visitServiceId, final String siteUUCode) {
        
        visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.DELETE_VISIT_SERVICE_OR_MEETING.getAllowVisitStageList(),  "visit.service.delete.restrict");
        
        // Find and retrieve the VisitService using the provided identifiers.
        final VisitService visitServiceToDelete = findByVisitIdAndVisitServiceId(visitId, visitServiceId, siteUUCode);

        // Delete the retrieved VisitService from the repository.
        visitServiceRepository.delete(visitServiceToDelete);
    }

}
