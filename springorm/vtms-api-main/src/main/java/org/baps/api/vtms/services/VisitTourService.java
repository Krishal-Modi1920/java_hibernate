package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.APIModuleEnum;
import org.baps.api.vtms.enumerations.LookupKeyEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.VisitLocationMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.mappers.VisitServiceMapper;
import org.baps.api.vtms.mappers.VisitTourMapper;
import org.baps.api.vtms.models.VisitLocationModel;
import org.baps.api.vtms.models.VisitPersonnelModel;
import org.baps.api.vtms.models.VisitTourModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Location;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;
import org.baps.api.vtms.repositories.VisitPersonnelRepository;
import org.baps.api.vtms.repositories.VisitServiceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
public class VisitTourService {
    
    private final org.baps.api.vtms.services.VisitService visitService;

    private final RoleService roleService;

    private final PersonnelService personnelService;

    private final ServiceTemplateService serviceTemplateService;

    private final VisitPersonnelService visitPersonnelService;

    private final MasterService masterService;

    private final VisitServiceMapper visitServiceMapper;

    private final VisitTourMapper visitTourMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final VisitLocationMapper visitLocationMapper;

    private final VisitServiceRepository visitServiceRepository;

    private final VisitPersonnelRepository visitPersonnelRepository;

    private final NotificationComposeService notificationComposeService;

    private final Translator translator;

    private final VisitServiceService visitServiceService;
    
    private final CommonUtils commonUtils;
    private final LookupService lookupService;

    /**
     * Creates a new VisitTourModel associated with a visit.
     *
     * @param visitId        The unique identifier of the visit to which the tour will be associated.
     * @param visitTourModel The VisitTourModel representing the tour to be created.
     * @param siteUUCode The unique code associated with the site.
     * @return The created VisitTourModel.
     * @throws DataNotFoundException      If the specified service template or visit is not found.
     * @throws DataAlreadyExistsException If a tour already exists for the visit.
     * @throws DataValidationException    If startDateTime or endDateTime of the tour is outside the visit's timeframe.
     */
    public VisitTourModel createVisitTour(final String visitId, final VisitTourModel visitTourModel,
                                          final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.CREATE_VISIT_TOUR.getAllowVisitStageList(), "visit.tour.create_or_update.restrict");
        
        // Retrieve the existing service template by serviceTemplateId and service type
        final var existingServiceTemplate = serviceTemplateService.findByIdAndServiceTypeEnum(
            visitTourModel.getServiceTemplateId(), GeneralConstant.TOUR_SERVICE_TYPE_ENUM, siteUUCode);

        // Check if a tour already exists for the visit; if yes, throw an exception
        final var existingVisitService = visitServiceRepository
            .findByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnumAndServiceTemplateStatusNotAndVisitSiteUuCode(visitId,
                Status.DELETED, GeneralConstant.TOUR_SERVICE_TYPE_ENUM, Status.DELETED, siteUUCode);

        if (CollectionUtils.isNotEmpty(existingVisitService)) {
            throw new DataAlreadyExistsException(translator.toLocal("tour.already.exist"));
        }

        if (visitTourModel.getStartDateTime() != null && visitTourModel.getEndDateTime() != null) {
            commonUtils.checkDateTimeRangeConflict(true, visitTourModel.getStartDateTime(), visitTourModel.getEndDateTime(),
                existingVisit.getStartDateTime(), existingVisit.getEndDateTime());
        }

        // Create a list to store personnel associated with the tour
        final List<VisitPersonnelModel> visitPersonnelModelList = new ArrayList<>();

        if (visitTourModel.getTourCoordinator() != null) {
            visitPersonnelModelList.add(visitTourModel.getTourCoordinator());
        }

        if (CollectionUtils.isNotEmpty(visitTourModel.getTourGuideList())) {
            visitPersonnelModelList.addAll(visitTourModel.getTourGuideList());
        }

        // Map the visitServiceModel to a VisitService entity.
        final var saveVisitService = visitServiceMapper.visitTourModelToVisitService(visitTourModel);
        saveVisitService.setVisit(existingVisit);
        saveVisitService.setServiceTemplate(existingServiceTemplate);

        if (CollectionUtils.isNotEmpty(visitPersonnelModelList)) {
            visitPersonnelService.checkVisitPersonnelAssociateInVisitOrVisitService(visitPersonnelModelList,
                GeneralConstant.TOUR_ROLE_TAG_LIST, existingVisit.getStartDateTime(),
                existingVisit.getEndDateTime(), siteUUCode);

            final List<String> roleIds = visitPersonnelModelList.stream().map(VisitPersonnelModel::getRoleId)
                .toList();

            final Map<String, Role> mapOfRoleIdWithExistingRoleList = roleService
                .getMapOfRoleByRoleIdsAndTag(roleIds, GeneralConstant.TOUR_ROLE_TAG_LIST);

            final List<String> personnelIds = visitPersonnelModelList.stream()
                .map(VisitPersonnelModel::getPersonnelId).toList();

            final Map<String, Personnel> mapOfPersonnelIdWithExistingPersonnelList = personnelService
                .getMapOfPersonnelByPersonnelIds(personnelIds);

            if (CollectionUtils.isNotEmpty(visitTourModel.getTourGuideList())) {
                visitTourModel.getTourGuideList().forEach(visitPersonnel -> {

                    // Create a new VisitPersonnel and set its attributes.
                    final var saveVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                        mapOfPersonnelIdWithExistingPersonnelList
                            .get(visitPersonnel.getPersonnelId()),
                        mapOfRoleIdWithExistingRoleList
                            .get(visitPersonnel.getRoleId()),
                        GeneralConstant.TOUR_GUIDE_ROLE_TAG, null);

                    // Add the VisitPersonnel to the VisitService.
                    saveVisitService.addVisitPersonnel(saveVisitPersonnel);
                });
            }

            // Create a new VisitPersonnel and set its attributes.
            final var saveVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(existingVisit,
                mapOfPersonnelIdWithExistingPersonnelList
                    .get(visitTourModel.getTourCoordinator().getPersonnelId()),
                mapOfRoleIdWithExistingRoleList
                    .get(visitTourModel.getTourCoordinator().getRoleId()),
                GeneralConstant.TOUR_COORDINATOR_ROLE_TAG, null);

            // Add the VisitPersonnel to the VisitService.
            saveVisitService.addVisitPersonnel(saveVisitPersonnel);

        }

        if (CollectionUtils.isNotEmpty(visitTourModel.getVisitLocationModelList())) {
            final Map<String, Location> mapOfLocationIdWithExistingLocation = masterService
                .getMapOfLocationIdWithLocation(visitTourModel.getVisitLocationModelList()
                    .stream().map(VisitLocationModel::getLocationId)
                    .toList(), existingServiceTemplate.getServiceTemplateId());

            visitTourModel.getVisitLocationModelList().forEach(visitLocation -> {
                final var saveVisitLocation = visitLocationMapper.visitLocationModelToVisitLocation(visitLocation);
                saveVisitLocation.setLocation(mapOfLocationIdWithExistingLocation.get(visitLocation.getLocationId()));

                saveVisitService.addVisitLocation(saveVisitLocation);
            });
        }

        addOrUpdateTourType(visitTourModel.getTourType(), existingVisit);

        visitServiceRepository.save(saveVisitService);

        if (CollectionUtils.isNotEmpty(saveVisitService.getVisitPersonnelList())) {
            notificationComposeService.sendVisitAssignedNotification(
                existingVisit, saveVisitService.getVisitPersonnelList(), ServiceTypeEnum.TOUR.name());
        }
        return visitTourMapper.visitServiceToVisitTourModel(saveVisitService, GeneralConstant.TOUR_COORDINATOR_ROLE_TAG,
            GeneralConstant.TOUR_GUIDE_ROLE_TAG);
    }

    public void addOrUpdateTourType(final String tourType, final Visit visit) {
        if (StringUtils.isNotBlank(tourType)) {
            lookupService.validateChildLookupValueByKey(LookupKeyEnum.TOUR_TYPE.name(), List.of(tourType),
                "tourType");
            visit.setTourType(tourType);
            visitService.saveVisit(visit);
        }
    }

    /**
     * Updates a VisitTour based on the provided visitId and VisitTourModel.
     *
     * @param visitId        The unique identifier of the visit associated with the tour.
     * @param visitTourModel The VisitTourModel containing the updated tour information.
     * @param siteUUCode The unique code associated with the site.
     * @return The updated VisitTourModel.
     */
    public VisitTourModel updateVisitTour(final String visitId, final VisitTourModel visitTourModel, final String siteUUCode) {

        // Retrieve the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.UPDATE_VISIT_TOUR.getAllowVisitStageList(), "visit.tour.create_or_update.restrict");

        final var existingVisitServiceList = visitServiceRepository
                .findByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnumAndServiceTemplateStatusNotAndVisitSiteUuCode(visitId,
                        Status.DELETED, GeneralConstant.TOUR_SERVICE_TYPE_ENUM, Status.DELETED, siteUUCode);

        if (CollectionUtils.isEmpty(existingVisitServiceList)) {
            throw new DataNotFoundException(translator.toLocal("tour_template.not.found"));
        }

        final var existingVisitService = existingVisitServiceList.get(0);

        // Check if startDateTime or endDateTime of the tour is outside the visit's timeframe
        if (visitTourModel.getStartDateTime() != null && visitTourModel.getEndDateTime() != null) {
            commonUtils.checkDateTimeRangeConflict(true, visitTourModel.getStartDateTime(), visitTourModel.getEndDateTime(),
                existingVisit.getStartDateTime(), existingVisit.getEndDateTime());
        }

        // Map the visitServiceModel to a VisitService entity.
        final var updateVisitService = visitServiceMapper.visitTourModelToVisitService(existingVisitService, visitTourModel);

        final List<VisitPersonnel> addOrUpdateVisitPersonnelInVisitService =
                addOrUpdateVisitPersonnelInVisitService(updateVisitService, visitTourModel, siteUUCode);

        visitServiceService.addOrUpdateVisitLocationInVisitService(updateVisitService, visitTourModel, ServiceTypeEnum.TOUR);

        addOrUpdateTourType(visitTourModel.getTourType(), existingVisit);

        visitServiceRepository.save(updateVisitService);

        if (CollectionUtils.isNotEmpty(addOrUpdateVisitPersonnelInVisitService)) {
            notificationComposeService.sendVisitAssignedNotification(
                existingVisit, addOrUpdateVisitPersonnelInVisitService, ServiceTypeEnum.TOUR.name());
        }

        return visitTourMapper.visitServiceToVisitTourModel(updateVisitService, GeneralConstant.TOUR_COORDINATOR_ROLE_TAG,
                GeneralConstant.TOUR_GUIDE_ROLE_TAG);
    }

    /**
     * Adds or updates visit personnel in the provided {@link VisitService} based on the information
     * from the given {@link VisitTourModel}.
     * @param existingVisitService The existing {@link VisitService} to update.
     * @param visitTourModel The {@link VisitTourModel} containing visit personnel information.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of {@link VisitPersonnel} objects added or updated in the visit service.
     */
    private List<VisitPersonnel> addOrUpdateVisitPersonnelInVisitService(
        final VisitService existingVisitService, final VisitTourModel visitTourModel, final String siteUUCode) {

        final List<VisitPersonnel> addOrUpdatedVisitPersonnelList = new ArrayList<>();

        final List<VisitPersonnelModel> visitPersonnelModelList = getVisitPersonnelModelListByTourModel(visitTourModel);

        // Extract unique visit personnel IDs from the models.
        final Set<String> visitPersonnelIdsSet = visitPersonnelModelList.stream()
            .map(VisitPersonnelModel::getVisitPersonnelId).collect(Collectors.toSet());

        // Remove visit personnel from the existing VisitService that are not in the updated list.
        existingVisitService.getVisitPersonnelList().removeIf(visitPersonnel -> (GeneralConstant.TOUR_ROLE_TAG_LIST.contains(
            visitPersonnel.getRoleTagEnum())) && !visitPersonnelIdsSet.contains(visitPersonnel.getVisitPersonnelId()));

        // Extract role IDs from the visit personnel models.
        final List<String> roleIds = visitPersonnelModelList.stream()
            .map(VisitPersonnelModel::getRoleId)
            .toList();

        // Retrieve a map of existing roles based on role IDs and role tags.
        final Map<String, Role> mapOfRoleIdWithExistingRoleList = CollectionUtils.isNotEmpty(roleIds) ? roleService
            .getMapOfRoleByRoleIdsAndTag(roleIds, GeneralConstant.TOUR_ROLE_TAG_LIST) : new HashMap<>();

        // Extract personnel IDs from the visit personnel models.
        final List<String> personnelIds = visitPersonnelModelList.stream()
            .map(VisitPersonnelModel::getPersonnelId)
            .toList();

        // Retrieve a map of existing personnel based on personnel IDs.
        final Map<String, Personnel> mapOfPersonnelIdWithExistingPersonnelList = CollectionUtils.isNotEmpty(personnelIds) ? personnelService
            .getMapOfPersonnelByPersonnelIds(personnelIds) : new HashMap<>();

        // Check for associated visit personnel in other visits or visit services.
        if (CollectionUtils.isNotEmpty(visitPersonnelModelList)) {
            visitPersonnelService.checkAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
                visitPersonnelModelList, GeneralConstant.TOUR_ROLE_TAG_LIST,
                existingVisitService.getStartDateTime(), existingVisitService.getEndDateTime(),
                existingVisitService.getVisitServiceId(), null, siteUUCode);
        }

        // If there are tour guides in the model, process them separately.
        if (CollectionUtils.isNotEmpty(visitTourModel.getTourGuideList())) {

            // Extract visit personnel IDs to update.
            final Set<String> visitPersonnelIdSetToUpdate = visitTourModel.getTourGuideList().stream()
                .map(VisitPersonnelModel::getVisitPersonnelId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

            // Retrieve existing tour guide visit personnel based on their IDs and role tag.
            final List<VisitPersonnel> exisitingTGVisitPersonnelList = visitPersonnelRepository
                .findAllByVisitPersonnelIdInAndRoleTagEnumAndVisitServiceVisitServiceIdAndVisitServiceStatusNotAndVisitSiteUuCode(
                    visitPersonnelIdSetToUpdate, GeneralConstant.TOUR_GUIDE_ROLE_TAG, existingVisitService.getVisitServiceId(),
                    Status.DELETED, siteUUCode);

            // Create a map of existing tour guide visit personnel for quick lookup.
            final Map<String, VisitPersonnel> exisityingTGVisitPersonnelMap = exisitingTGVisitPersonnelList.stream()
                .collect(Collectors.toMap(VisitPersonnel::getVisitPersonnelId, visitPersonnel -> visitPersonnel));

            // Identify invalid tour guide visit personnel IDs.
            final Set<String> invalidTGVisitPersonnelIds = visitPersonnelIdSetToUpdate
                .stream().filter(s -> !exisityingTGVisitPersonnelMap.containsKey(s))
                .collect(Collectors.toSet());

            // If invalid IDs are found, throw a DataNotFoundException.
            if (CollectionUtils.isNotEmpty(invalidTGVisitPersonnelIds)) {
                throw new DataNotFoundException(translator.toLocal("visit_personnel.with.visitPersonnelId.not.found",
                        invalidTGVisitPersonnelIds));
            }

            // Process each tour guide visit personnel model in the provided list.
            visitTourModel.getTourGuideList().forEach(visitPersonnelModel -> {

                if (StringUtils.isNotBlank(visitPersonnelModel.getVisitPersonnelId())) {

                    final VisitPersonnel existingVisitPersonnel =
                        exisityingTGVisitPersonnelMap.get(visitPersonnelModel.getVisitPersonnelId());

                    if (!visitPersonnelModel.getPersonnelId().equals(existingVisitPersonnel.getPersonnel().getPersonnelId())) {

                        existingVisitPersonnel.setStatus(Status.DELETED);

                        final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisitService.getVisit(),
                                mapOfPersonnelIdWithExistingPersonnelList.get(visitPersonnelModel.getPersonnelId()),
                                mapOfRoleIdWithExistingRoleList.get(visitPersonnelModel.getRoleId()), 
                                GeneralConstant.TOUR_GUIDE_ROLE_TAG, null);

                        existingVisitService.addVisitPersonnel(visitPersonnelToAdd);
                        
                        addOrUpdatedVisitPersonnelList.add(existingVisitPersonnel);
                    }

                } else {
                    // Create a new tour guide visit personnel from the model and add it to the existing VisitService.

                    final VisitPersonnel addVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(
                        existingVisitService.getVisit(),
                        mapOfPersonnelIdWithExistingPersonnelList
                            .get(visitPersonnelModel.getPersonnelId()),
                        mapOfRoleIdWithExistingRoleList
                            .get(visitPersonnelModel.getRoleId()),
                        GeneralConstant.TOUR_GUIDE_ROLE_TAG, null);

                    existingVisitService.addVisitPersonnel(addVisitPersonnel);

                    addOrUpdatedVisitPersonnelList.add(addVisitPersonnel);
                }
            });
        }

        if (visitTourModel.getTourCoordinator() != null) {
            if (visitTourModel.getTourCoordinator().getVisitPersonnelId() != null) {
                final VisitPersonnel existingVisitPersonnelTourCoordinator = visitPersonnelService.findByIdAndSiteUUCode(
                    visitTourModel.getTourCoordinator().getVisitPersonnelId(), siteUUCode);

                if (!existingVisitPersonnelTourCoordinator.getRoleTagEnum().equals(GeneralConstant.TOUR_COORDINATOR_ROLE_TAG)) {
                    throw new DataNotFoundException(translator.toLocal("visit.personnel.not.associated.role.tag",
                        visitTourModel.getTourCoordinator().getVisitPersonnelId(),
                        GeneralConstant.TOUR_COORDINATOR_ROLE_TAG));
                }

                if (!existingVisitPersonnelTourCoordinator.getPersonnel().getPersonnelId()
                    .equals(visitTourModel.getTourCoordinator().getPersonnelId())) {

                    existingVisitPersonnelTourCoordinator.setStatus(Status.DELETED);

                    final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisitService.getVisit(),
                        mapOfPersonnelIdWithExistingPersonnelList.get(visitTourModel.getTourCoordinator().getPersonnelId()),
                        mapOfRoleIdWithExistingRoleList.get(visitTourModel.getTourCoordinator().getRoleId()),
                        GeneralConstant.TOUR_COORDINATOR_ROLE_TAG, null);

                    existingVisitService.addVisitPersonnel(visitPersonnelToAdd);

                    addOrUpdatedVisitPersonnelList.add(existingVisitPersonnelTourCoordinator);
                }

                existingVisitPersonnelTourCoordinator.setRole(mapOfRoleIdWithExistingRoleList
                    .get(visitTourModel.getTourCoordinator().getRoleId()));
            } else {
                final var visitPersonnelToAdd = visitPersonnelMapper.createVisitPersonnel(existingVisitService.getVisit(),
                    mapOfPersonnelIdWithExistingPersonnelList.get(visitTourModel.getTourCoordinator().getPersonnelId()),
                    mapOfRoleIdWithExistingRoleList.get(visitTourModel.getTourCoordinator().getRoleId()),
                    GeneralConstant.TOUR_COORDINATOR_ROLE_TAG, null);

                existingVisitService.addVisitPersonnel(visitPersonnelToAdd);

                addOrUpdatedVisitPersonnelList.add(visitPersonnelToAdd);
            }
        }
        return addOrUpdatedVisitPersonnelList;
    }

    /**
     * Retrieves a list of {@link VisitPersonnelModel} objects based on the information provided in the
     * given {@link VisitTourModel}. The list includes the tour coordinator and, if present, any tour guides.
     *
     * @param visitTourModel The {@link VisitTourModel} containing information about visit personnel.
     * @return A list of {@link VisitPersonnelModel} objects, starting with the tour coordinator,
     *         followed by any tour guides if available.
     */
    @Transactional(readOnly = true)
    private List<VisitPersonnelModel> getVisitPersonnelModelListByTourModel(final VisitTourModel visitTourModel) {

        // Create a list to hold visit personnel models, starting with the tour coordinator.
        final List<VisitPersonnelModel> visitPersonnelModelList = new ArrayList<>();
        if (visitTourModel.getTourCoordinator() != null) {
            visitPersonnelModelList.add(visitTourModel.getTourCoordinator());
        }

        // If there are tour guides, add them to the list as well.
        if (CollectionUtils.isNotEmpty(visitTourModel.getTourGuideList())) {
            visitPersonnelModelList.addAll(visitTourModel.getTourGuideList());
        }

        return visitPersonnelModelList;
    }

    /**
     * Retrieves a VisitTourModel by its visit ID.
     *
     * @param visitId The unique identifier of the visit.
     * @param siteUUCode The unique code associated with the site.
     * @return A VisitTourModel representing the visit tour.
     * @throws DataNotFoundException If the tour with the specified visit ID is not found.
     */
    @Transactional(readOnly = true)
    public VisitTourModel getVisitTourByVisitId(final String visitId, final String siteUUCode) {
        // Attempt to find an existing visit service by visit ID and various criteria
        final var existingVisitService = visitServiceRepository
            .findByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnumAndServiceTemplateStatusNotAndVisitSiteUuCode(visitId,
                Status.DELETED, GeneralConstant.TOUR_SERVICE_TYPE_ENUM, Status.DELETED, siteUUCode);

        // Check if no existing visit service is found, and throw an exception if none exists
        if (CollectionUtils.isEmpty(existingVisitService)) {
            throw new DataNotFoundException(translator.toLocal("visit.tour.not.found", visitId));
        }

        return visitTourMapper.visitServiceToVisitTourModel(existingVisitService.get(0),
                GeneralConstant.TOUR_COORDINATOR_ROLE_TAG, GeneralConstant.TOUR_GUIDE_ROLE_TAG);
    }

}
