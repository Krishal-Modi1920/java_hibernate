package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.common.utils.ValidationUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.APIModuleEnum;
import org.baps.api.vtms.enumerations.LocationTagEnum;
import org.baps.api.vtms.enumerations.LookupKeyEnum;
import org.baps.api.vtms.enumerations.PermissionEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.RoleMapper;
import org.baps.api.vtms.mappers.ServiceTemplateMapper;
import org.baps.api.vtms.mappers.VisitMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.mappers.VisitSummaryMapper;
import org.baps.api.vtms.mappers.VisitorMapper;
import org.baps.api.vtms.models.DocumentModel;
import org.baps.api.vtms.models.RoleModel;
import org.baps.api.vtms.models.ServiceTemplateBasicInfoModel;
import org.baps.api.vtms.models.StageModel;
import org.baps.api.vtms.models.VisitBasicInfoModel;
import org.baps.api.vtms.models.VisitCountModel;
import org.baps.api.vtms.models.VisitModel;
import org.baps.api.vtms.models.VisitSummaryModel;
import org.baps.api.vtms.models.VisitSummaryServiceModel;
import org.baps.api.vtms.models.VisitTabModel;
import org.baps.api.vtms.models.VisitorModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.ServiceTemplate;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitFeedback;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitPublicFeedback;
import org.baps.api.vtms.models.entities.VisitVisitor;
import org.baps.api.vtms.models.entities.Visitor;
import org.baps.api.vtms.repositories.VisitPersonnelRepository;
import org.baps.api.vtms.repositories.VisitRepository;
import org.baps.api.vtms.repositories.VisitServiceRepository;
import org.baps.api.vtms.repositories.specifications.VisitSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class VisitService {

    private static final int MAX_VISIT_NUMBER = 9999;

    private final RoleService roleService;

    private final NotificationComposeService notificationComposeService;

    private final SiteService siteService;

    private final PersonnelService personnelService;
    
    private final VisitPersonnelService visitPersonnelService;
    
    private final CountryService countryService;
    
    private final StateService stateService;
    
    private final LookupService lookupService;
    
    private final ServiceTemplateService serviceTemplateService;

    private final VisitMapper visitMapper;

    private final RoleMapper roleMapper;

    private final VisitorMapper visitorMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final VisitSummaryMapper visitSummaryMapper;
    
    private final ServiceTemplateMapper serviceTemplateMapper;

    private final VisitSpecification visitSpecification;

    private final VisitRepository visitRepository;

    private final VisitServiceRepository visitServiceRepository;

    private final VisitPersonnelRepository visitPersonnelRepository;

    private final Translator translator;

    private final ValidationUtils validationUtils;

    /**
     * Finds the last created visit of a specific type for a given site.
     * 
     * @param siteUUCode The unique identifier of the site.
     * @param visitTypeEnum The type of visit to search for.
     * @return The last created visit of the specified type for the given site, or null if not found.
     */
    @Transactional(readOnly = true)
    public Visit findLastCreatedVisitBySiteIdAndVisitType(final String siteUUCode, final VisitTypeEnum visitTypeEnum) {
        return visitRepository.findTopBySiteUuCodeAndVisitTypeEnumOrderByCreatedAtDesc(siteUUCode, visitTypeEnum);
    }

    /**
     * Generates a unique visit number based on the visit type, site UUID, and existing visit data.
     *
     * @param visitTypeEnum The type of visit (VISIT or TOUR).
     * @param siteUUCode    The UUID code of the site.
     * @return A unique visit number for the given visit type and site.
     * @throws DataValidationException If the visit type is invalid.
     */
    @Transactional(readOnly = true)
    public String getNextVisitNumber(final VisitTypeEnum visitTypeEnum, final String siteUUCode) {

        // Initialize a StringBuilder to build the visit number
        final StringBuilder visitNumber = new StringBuilder();

        // Switch based on the visit type
        switch (visitTypeEnum) {
            case VISIT -> visitNumber.append(VisitTypeEnum.VISIT.getShortName());
            case TOUR -> visitNumber.append(VisitTypeEnum.TOUR.getShortName());
            default -> throw new DataValidationException(translator.toLocal("invalid.enum.value", visitTypeEnum));
        }

        // Append another separator
        visitNumber.append("-");
        visitNumber.append(siteService.getCurrentDateTimeFromExistingSite(
                siteService.findByUUCode(siteUUCode)).format(DateTimeFormatter.ofPattern("ddMMyy")));

        // Append a separator
        visitNumber.append("-");

        // Retrieve the last created visit for the given site and visit type
        final Visit lastVisit = findLastCreatedVisitBySiteIdAndVisitType(siteUUCode, visitTypeEnum);

        int visitNumberCount = 1;

        // Check if the last visit's request number contains the generated visit number
        if (ObjectUtils.isNotEmpty(lastVisit) && lastVisit.getRequestNumber().contains(visitNumber)) {
            // Extract the visit number count from the last visit's request number
            visitNumberCount = Integer.parseInt(lastVisit.getRequestNumber().replace(visitNumber, "")) + 1;
        }

        // Check if the visit number count is within the allowable range
        if (visitNumberCount <= MAX_VISIT_NUMBER) {
            // Append the formatted visit number count with leading zeros
            visitNumber.append(String.format("%04d", visitNumberCount));
        } else {
            // Append the visit number count without formatting
            visitNumber.append(visitNumberCount);
        }

        // Return the generated visit number as a string
        return visitNumber.toString();
    }

    /**
     * Retrieves a VisitModel by visitId.
     *
     * @param visitId  The visitId of the visit to retrieve.
     * @param siteUUCode The unique code associated with the site.
     * @param isPrivate to define visit is public or private.
     * @return The VisitModel object representing the visit with the specified visitId.
     */
    @Transactional(readOnly = true)
    public VisitModel getById(final String visitId, final String siteUUCode, final boolean isPrivate) throws DataNotFoundException {
        final Visit existingVisit = findByIdAndSiteUUCodeAndIsPrivate(visitId, siteUUCode, isPrivate);
        final VisitModel visitModel = visitMapper.visitToVisitModel(existingVisit);

        if (isPrivate) {
            final VisitTabModel visitTabModel = new VisitTabModel();

            visitTabModel.setSecondaryVisitorAvailable(ObjectUtils.isNotEmpty(visitModel.getSecondaryVisitorModel()));
            visitTabModel.setTourAvailable(visitServiceRepository.existsByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnum(
                    visitId, Status.DELETED, ServiceTypeEnum.TOUR));
            visitTabModel.setServicesAvailable(visitServiceRepository
                    .existsByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnum(
                    visitId, Status.DELETED, ServiceTypeEnum.SERVICE));
            visitTabModel.setMeetingsAvailable(visitServiceRepository
                    .existsByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnum(
                    visitId, Status.DELETED, ServiceTypeEnum.MEETING));
            visitTabModel.setDocumentsAvailable(CollectionUtils.isNotEmpty(existingVisit.getDocumentModelList()));
            visitTabModel.setInterviewCoordinatorAvailable(
                    visitPersonnelRepository.existsVisitPersonnelByVisitVisitIdAndRoleTagEnumAndStatusNotAndVisitSiteUuCode(visitId,
                            RoleTagEnum.INTERVIEW_SETUP_COORDINATOR, Status.DELETED, siteUUCode));

            if (ObjectUtils.isNotEmpty(existingVisit.getVisitFeedback())
                    && CollectionUtils.isNotEmpty(existingVisit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack())
                    && !existingVisit.getVisitFeedback().getFeedBackRatingModelListForGeneralFeedBack().toString().equals("null")) {
                visitTabModel.setExternalFeedback(true);
            }
            visitModel.setVisitTabModel(visitTabModel);
        }
        
        if (CollectionUtils.isNotEmpty(existingVisit.getRequestedServiceIds())) {

            final List<ServiceTemplate> existingServiceTemplates = serviceTemplateService
                    .findByServiceTemplateIdsAndSiteUUCode(existingVisit.getRequestedServiceIds(), existingVisit.getSite().getUuCode());  
            final List<ServiceTemplateBasicInfoModel> serviceTemplateModelList = serviceTemplateMapper
                    .serviceTemplateListToServiceTemplateBasicInfoModelList(existingServiceTemplates);
            visitModel.setRequestedServices(serviceTemplateModelList);       
        }
        return visitModel;
    }

    /**
     * Finds a Visit by visitId.
     *
     * @param id The id of the visit to find.
     * @param siteUUCode The unique code associated with the site.
     * @return The Visit object with the specified visitId.
     * @throws DataNotFoundException If the visitId is not found.
     */
    @Transactional(readOnly = true)
    public Visit findByIdAndSiteUUCode(final String id, final String siteUUCode) throws DataNotFoundException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id));
        } else {
            return visitRepository.findByVisitIdAndSiteUuCode(id, siteUUCode)
                    .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id)));
        }
    }
    


    /**
     * Finds a Visit by visitId.
     *
     * @param id The id of the visit to find.
     * @param siteUUCode The unique code associated with the site.
     * @param isPrivate to define visit is public or private.
     * @return The Visit object with the specified visitId.
     * @throws DataNotFoundException If the visitId is not found.
     */
    @Transactional(readOnly = true)
    public Visit findByIdAndSiteUUCodeAndIsPrivate(final String id, final String siteUUCode, final boolean isPrivate) 
            throws DataNotFoundException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id));
        } else {
            return visitRepository.findByVisitIdAndSiteUuCode(id, siteUUCode)
                    .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id)));
        }
    }
    
    /**
     * Retrieves a visit by its unique identifier, site code, and checks if the visit stage is allowed.
     *
     * @param id                    The unique identifier of the visit.
     * @param siteUUCode            The site code associated with the visit.
     * @param allowedVisitStageEnum A list of allowed visit stages for validation.
     * @param errorMsg              The error message to be used in case of data not found.
     * @return                      The found Visit instance if it exists and meets the criteria.
     * @throws DataNotFoundException    If the visit with the specified id and site code is not found.
     * @throws DataValidationException  If the visit stage is not among the allowed stages.
     */
    @Transactional(readOnly = true)
    public Visit findByIdAndSiteUUCodeAndAllowedStage(final String id, final String siteUUCode, 
            final List<VisitStageEnum> allowedVisitStageEnum, final String errorMsg) throws DataNotFoundException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id));
        } else {
            final Visit existingVisit = visitRepository.findByVisitIdAndSiteUuCode(id, siteUUCode)
                    .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.with.visit_id.not.found", id)));
            
            if (!allowedVisitStageEnum.contains(existingVisit.getVisitStageEnum())) {
                throw new DataValidationException(translator.toLocal(errorMsg, allowedVisitStageEnum));
            }
            
            return existingVisit;
        }
    }

    /**
     * Retrieves a paginated list of visits with optional filters and sorting.
     *
     * @param pageNo        The page number to retrieve.
     * @param pageSize      The number of items per page.
     * @param sortDirection The sorting direction, can be "asc" (ascending) or "desc" (descending). (Optional)
     * @param sortProperty  The property to sort the results by. (Optional)
     * @param search        A search query to filter visits. (Optional)
     * @param visitStage    A filter for visit stage enumeration. (Optional)
     * @param typeOfVisit   A filter for the type of visit. (Optional)
     * @param startDateTime The start date and time for filtering visits. (Optional)
     * @param endDateTime   The end date and time for filtering visits. (Optional)
     * @param siteUUCode    The unique code associated with the site for filtering visits.
     * @param selfAssignVisit     Flag indicating whether to retrieve self-assigned visits only.
     * @param visitorType   A filter for the visitor type. (Optional)
     * @return A paginated response containing a list of VisitBasicInfo objects.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<List<VisitBasicInfoModel>> getPaginatedVisitsWithFilters(final Integer pageNo, final Integer pageSize,
            @Nullable final String sortDirection, @Nullable final String sortProperty, @Nullable final String search, 
            @Nullable final String visitStage, @Nullable final String typeOfVisit,
            @Nullable final LocalDateTime startDateTime, @Nullable final LocalDateTime endDateTime,
            final String siteUUCode, final boolean selfAssignVisit, @Nullable final String visitorType) {

        // Retrieve the personnel information based on the PID.
        final Personnel personnel = personnelService.getLoginedPersonnel();

        // Initialize lists to store permission enums and role names.
        final List<PermissionEnum> permissionEnumList = new ArrayList<>();

        personnel.getPersonnelRoleList().forEach(personnelRole -> permissionEnumList.addAll(personnelRole.getRole().getRolePermissionList()
                .stream().map(rolePermission -> rolePermission.getPermission().getPermissonEnum())
                .collect(Collectors.toSet())));

        final String personnelId;

        if (!selfAssignVisit && permissionEnumList.contains(PermissionEnum.VIEW_VISIT_ALL_LIST)) {
            personnelId = null;
        } else if (selfAssignVisit && permissionEnumList.contains(PermissionEnum.VIEW_VISIT_SELF_ASSIGN_LIST)) {
            personnelId = personnel.getPersonnelId();
        } else {
            return CommonUtils.createEmptyPaginationAndResponse(pageNo);
        }

        final List<VisitStageEnum> visitStageEnumList = validationUtils.validateVisitStageEnum(visitStage, VisitTypeEnum.VISIT);

        final Specification<Visit> specification =
                visitSpecification.buildVisitSpecification(sortProperty, sortDirection, search,
                        visitStageEnumList, typeOfVisit, startDateTime, endDateTime, personnelId, VisitTypeEnum.VISIT, null,
                    siteUUCode, visitorType);

        final Page<Visit> existingVisitPage = visitRepository.findAll(specification, PageRequest.of(pageNo - 1, pageSize));

        List<VisitBasicInfoModel> visitBasicInfoModels = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingVisitPage.getContent())) {
            visitBasicInfoModels = visitMapper.visitListToVisitBasicInfoList(existingVisitPage.getContent());
        }
        return CommonUtils.calculatePaginationAndPrepareResponse(existingVisitPage, visitBasicInfoModels);
    }

    /**
     * Validates a proposed stage change for a Visit.
     *
     * @param existingVisit  The Visit whose stage is being changed.
     * @param visitStageEnum The new stage to be assigned to the Visit.
     * @throws DataAlreadyExistsException If the new stage matches the existing stage, and the existing stage is not 'PENDING'.
     * @throws DataValidationException    If the stage change is not allowed based on various criteria.
     */
    @Transactional(readOnly = true)
    public void validateVisitStage(final Visit existingVisit, final VisitStageEnum visitStageEnum) {

        // Get the existing visit's stage
        final VisitStageEnum existingStage = existingVisit.getVisitStageEnum();

        // Check if the existing stage is not 'PENDING' and is the same as the provided visitStageEnum
        if (!existingStage.equals(VisitStageEnum.PENDING) && existingStage.equals(visitStageEnum)) {
            // Throw an exception indicating that the data already exists
            throw new DataAlreadyExistsException(translator.toLocal("visit.stage.already.exist", visitStageEnum.name()));
        }

        // Check if the existing stage cannot be changed (if it's not marked as 'changeable')
        if (!existingStage.isChange()) {
            throw new DataValidationException(translator.toLocal("visit.stage.not_update", existingStage));
        }

        if (visitStageEnum.getOrder() < existingVisit.getVisitStageEnum().getOrder()) {
            throw new DataValidationException(translator.toLocal("visit.stage.not_update.to", visitStageEnum));
        }
            
        // Check if the provided visitStageEnum has a previous stage defined and it's not the same as the existing stage
        if (existingVisit.getVisitTypeEnum().equals(VisitTypeEnum.VISIT) 
                && CollectionUtils.isNotEmpty(visitStageEnum.getVisitPreviousStageList())
              && !visitStageEnum.getVisitPreviousStageList().contains(existingStage)) {
            throw new DataValidationException(translator.toLocal("visit.stage.should.be", visitStageEnum.getVisitPreviousStageList()));
        } else if (existingVisit.getVisitTypeEnum().equals(VisitTypeEnum.TOUR) 
                && CollectionUtils.isNotEmpty(visitStageEnum.getTourPreviousStageList())
              && !visitStageEnum.getTourPreviousStageList().contains(existingStage)) {
            throw new DataValidationException(translator.toLocal("visit.stage.should.be", visitStageEnum.getTourPreviousStageList()));
        } 

        if (visitStageEnum.equals(VisitStageEnum.COMPLETED) && existingVisit.getEndDateTime().toLocalDate()
                .isAfter(siteService.getCurrentDateTimeFromExistingSite(existingVisit.getSite()).toLocalDate())) {
            throw new DataValidationException(translator.toLocal("visit.future_date.not.completed"));
        } else if (visitStageEnum.equals(VisitStageEnum.CHECK_IN) && !existingVisit.getEndDateTime().toLocalDate()
                .isEqual(siteService.getCurrentDateTimeFromExistingSite(existingVisit.getSite()).toLocalDate())) {
            throw new DataValidationException(translator.toLocal("visit.date.should_be.same_date.for_checkin"));
        }
    }
    
    /**
     * Validates the fields in the given VisitModel.
     * 
     * @param visitModel The VisitModel to validate.
     */
    private void validateVisitModel(final VisitModel visitModel) {
        // Validate the type of visit field
        lookupService.validateChildLookupValueByKey(LookupKeyEnum.TYPE_OF_VISIT.name(), List.of(visitModel.getTypeOfVisit()),
                "typeOfVisit");
        
        // Validate the point of contact enum value
        validationUtils.validatePointOfContactEnum(visitModel);
    }

    /**
     * Creates or updates a visit based on the provided VisitModel data.
     *
     * @param visitId    The ID of the existing visit to be updated (nullable if creating a new visit).
     * @param visitModel The VisitModel object containing the data for the visit.
     * @param siteUUCode The unique code associated with the site.
     * @param isPrivate to define visit is public or private.
     * @return The created or updated VisitModel object representing the visit.
     */
    public VisitModel createUpdateVisit(@Nullable final String visitId, final VisitModel visitModel, final String siteUUCode, 
            final Boolean isPrivate) {
        
        validateVisitModel(visitModel);
        
        // Create a new Visit instance
        var saveOrUpdateVisit = new Visit();

        // If visitId is not blank, find the existing visit by ID
        if (StringUtils.isNotBlank(visitId)) {
            
            saveOrUpdateVisit = findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode,
                    APIModuleEnum.UPDATE_VISIT.getAllowVisitStageList(), "visit.details.update.restrict");

            if (!saveOrUpdateVisit.getStartDateTime().equals(visitModel.getStartDateTime())
                            || !saveOrUpdateVisit.getEndDateTime().equals(visitModel.getEndDateTime())) {
                throw new DataValidationException(translator.toLocal("visit.date_time.update.restrict"));
            }

        }

        final Set<String> serviceTemplateIds = visitModel.getRequestedServices().stream()
                .map(ServiceTemplateBasicInfoModel::getServiceTemplateId).collect(Collectors.toSet());                

        serviceTemplateService.findByServiceTemplateIdsAndSiteUUCode(serviceTemplateIds, siteUUCode);
        
        // Map data from the VisitModel to the Visit entity
        saveOrUpdateVisit = visitMapper.visitModelToVisit(saveOrUpdateVisit, visitModel);

        // Create or update primary and secondary visitors within the visit
        createUpdateVisitor(saveOrUpdateVisit, visitModel.getPrimaryVisitorModel(), VisitorContactTypeEnum.PRIMARY);
        createUpdateVisitor(saveOrUpdateVisit, visitModel.getSecondaryVisitorModel(), VisitorContactTypeEnum.SECONDARY);

        saveOrUpdateVisitAndSendEmail(visitModel, siteUUCode, isPrivate, saveOrUpdateVisit);

        // Map the saved Visit instance back to a VisitModel and return it
        return getById(saveOrUpdateVisit.getVisitId(), siteUUCode, saveOrUpdateVisit.isPrivate());
    }

    private void saveOrUpdateVisitAndSendEmail(final VisitModel visitModel, final String siteUUCode,
            final Boolean isPrivate, final Visit saveOrUpdateVisit) {

        boolean newVisit = true;
        boolean relationShipManagerAddedOrChanged = false;
        
        if (StringUtils.isBlank(saveOrUpdateVisit.getVisitId())) {

            saveOrUpdateVisit.setPrivate(isPrivate);
            createStageHistory(saveOrUpdateVisit, siteUUCode);

            // Create and set request number for new visit
            saveOrUpdateVisit.setRequestNumber(getNextVisitNumber(saveOrUpdateVisit.getVisitTypeEnum(), siteUUCode));

            final var visitPersonnel = visitPersonnelMapper.createVisitPersonnel(saveOrUpdateVisit,
                    personnelService.findById(visitModel.getRelationshipManagerPersonnelBasicInfoModel().getPersonnelId()),
                    roleService.findByRoleEnum(RoleEnum.RELATIONSHIP_MANAGER), RoleTagEnum.TEAM, null);

            relationShipManagerAddedOrChanged = true;

            saveOrUpdateVisit.addVisitPersonnel(visitPersonnel);
        } else {
            newVisit = false;
            final var visitPersonnelRMOptional = saveOrUpdateVisit.getVisitPersonnelList().stream()
                    .filter(visitPersonnel -> visitPersonnel.getRole().getUucode().equals(RoleEnum.RELATIONSHIP_MANAGER.name()))
                    .findFirst();

            if (visitPersonnelRMOptional.isPresent()) {
                if (!visitPersonnelRMOptional.get().getPersonnel().getPersonnelId()
                        .equals(visitModel.getRelationshipManagerPersonnelBasicInfoModel().getPersonnelId())) {
                    
                    visitPersonnelRMOptional.get().setStatus(Status.DELETED);
                    final var visitPersonnel = visitPersonnelMapper.createVisitPersonnel(saveOrUpdateVisit,
                            personnelService.findById(visitModel.getRelationshipManagerPersonnelBasicInfoModel().getPersonnelId()),
                            roleService.findByRoleEnum(RoleEnum.RELATIONSHIP_MANAGER), RoleTagEnum.TEAM, null);
                    
                    saveOrUpdateVisit.addVisitPersonnel(visitPersonnel);
                    relationShipManagerAddedOrChanged = true;
                }
            } else {
                throw new DataNotFoundException(translator.toLocal("visit.personnel.with.role.not.found",
                        RoleEnum.RELATIONSHIP_MANAGER));
            }
        }
        if (ObjectUtils.isNotEmpty(isPrivate) && !isPrivate) {
            saveOrUpdateVisit.setVisitBookedFeedback(new VisitPublicFeedback());
        }
        visitRepository.save(saveOrUpdateVisit);

        sendVisitServiceNotifications(saveOrUpdateVisit, newVisit, relationShipManagerAddedOrChanged, siteUUCode);
    }

    private void sendVisitServiceNotifications(final Visit visit, final boolean newVisit,
            final boolean relationShipManagerAddedOrChanged, final String siteUUCode) {

        if (newVisit) {
            notificationComposeService.sendVisitApprovalPending(visit, siteUUCode);
            notificationComposeService.sendVisitRequestReceived(visit);
            notificationComposeService.sendVisitCreatedSuccessfully(visit);
        }

        if (relationShipManagerAddedOrChanged) {
            visit.getVisitPersonnelList().stream()
                .filter(visitPersonnel -> visitPersonnel.getRole().getUucode().equals(RoleEnum.RELATIONSHIP_MANAGER.name()))
                .findFirst().ifPresent(visitPersonnel -> notificationComposeService
                    .sendVisitAssignedNotification(visit, List.of(visitPersonnel), "VISIT"));
        }
    }
    
    /**
     * Creates and adds a stage history for a given Visit, updating its current stage and associated site.
     *
     * @param visit      The Visit for which the stage history is to be created and added.
     * @param siteUUCode The site code associated with the Visit.
     */
    private void createStageHistory(final Visit visit, final String siteUUCode) {
        final var stageModel = new StageModel();
        stageModel.setCreatedAt(siteService.getCurrentDateTimeFromExistingSite(siteService.findByUUCode(siteUUCode)));
        stageModel.setStage(VisitStageEnum.PENDING.name());
        visit.addStageHistory(stageModel);
        visit.setVisitStageEnum(VisitStageEnum.PENDING);
        visit.setSite(siteService.findByUUCode(siteUUCode));
    }

    /**
     * Creates or updates a visitor within a Visit based on the provided VisitorModel and contact type.
     *
     * @param visit        The Visit instance to which the visitor should be added or updated.
     * @param visitorModel The VisitorModel containing information about the visitor to be created or updated.
     * @param contactType  The type of contact associated with the visitor (PRIMARY or SECONDARY).
     * @throws DataNotFoundException If the visitor with the provided visitorId is not found when attempting to update.
     */
    public void createUpdateVisitor(final Visit visit, final VisitorModel visitorModel, final VisitorContactTypeEnum contactType)
            throws DataNotFoundException {
        if (ObjectUtils.isNotEmpty(visitorModel)) {
            
            lookupService.validateChildLookupValueByKey(LookupKeyEnum.GENDER.name(), List.of(visitorModel.getGender()), "gender");
            lookupService.validateChildLookupValueByKey(LookupKeyEnum.PREFERRED_COMM_MODE.name(), 
                    List.of(visitorModel.getPreferredCommMode()), "preferredCommMode");
            
            if (StringUtils.isNotBlank(visitorModel.getVisitorType())) {
                lookupService.validateChildLookupValueByKey(LookupKeyEnum.VISITOR_TYPE.name(), List.of(visitorModel.getVisitorType()), 
                        "visitorType");
            }
            
            final Optional<VisitVisitor> optionalVisitVisitor = visit.getVisitVisitorList().stream()
                    .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(contactType))
                    .findFirst();

            countryService.findCountryById(visitorModel.getCountry());
            
            if (CollectionUtils.isEmpty(countryService.findCountriesByIsdCode(visitorModel.getPhoneCountryCode()))) {
                throw new DataNotFoundException(translator
                        .toLocal("country.with.phone_country_code.not_found", visitorModel.getPhoneCountryCode()));
            }
            
            if (StringUtils.isNoneBlank(visitorModel.getState())) {
                stateService.findStateById(visitorModel.getState());
            }
            
            if (optionalVisitVisitor.isEmpty() && StringUtils.isBlank(visitorModel.getVisitorId())) {

                // Create a new visitor and visit visitor
                final Visitor visitor = visitorMapper.visitorModelToVisitor(new Visitor(), visitorModel);
                final VisitVisitor visitVisitor = new VisitVisitor();
                visitVisitor.setVisitorContactTypeEnum(contactType);
                visitVisitor.setVisitor(visitor);
                visit.addVisitVisitor(visitVisitor);

            } else if (optionalVisitVisitor.isPresent()
                    && optionalVisitVisitor.get().getVisitor().getVisitorId().equals(visitorModel.getVisitorId())) {

                // Update existing visitor
                visitorMapper.visitorModelToVisitor(optionalVisitVisitor.get().getVisitor(), visitorModel);
            } else {
                throw new DataNotFoundException(
                        translator.toLocal("visitor.with.visitor_id.not.found", contactType.name(), visitorModel.getVisitorId()));
            }
        } else {
            // Remove visitor if not present in the model
            visit.getVisitVisitorList().removeIf(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(contactType));
        }
    }

    /**
     * Updates the stage of a visit, allowing for changes based on specified conditions.
     *
     * @param visitId        The unique identifier of the visit to be updated.
     * @param stageModel     The new stage model to be applied to the visit.
     * @param allowAnyStatus A boolean flag to allow updates to any visit stage, regardless of the current stage.
     * @param siteUUCode The unique code associated with the site.
     * @throws DataValidationException    If the new stage is not allowed or doesn't match the previous stage (if applicable).
     * @throws DataAlreadyExistsException If the new stage is the same as the existing stage.
     */
    //     This method performs the following tasks:
    //     1. Defines a set of allowed visit stages to change, such as CANCELLED, DECLINED, COMPLETED, and CLOSED.
    //     2. Retrieves the existing visit by its unique identifier.
    //     3. Verifies that the new stage requested is allowed and that it is not the same as the current stage.
    //     4. Ensures that the existing stage can be changed (not a non-changeable stage).
    //     5. Validates that the new stage's previous stage matches the current stage (if there is a previous stage).
    //     6. Updates the visit's stage with the new stage, and records the stage history.
    //     7. If the new stage is COMPLETED, creates a VisitFeedback instance and associates it with the visit.
    //     8. Saves the updated visit in the repository.
    public void updateVisitStage(final String visitId, final StageModel stageModel, final boolean allowAnyStatus, final String siteUUCode) {

        final Visit existingVisit = findByIdAndSiteUUCode(visitId, siteUUCode);

        final VisitStageEnum visitStageEnum = VisitStageEnum.valueOf(stageModel.getStage());

        validateVisitStage(existingVisit, visitStageEnum);

        existingVisit.setVisitStageEnum(visitStageEnum);
        existingVisit.addStageHistory(stageModel);

        if (visitStageEnum.equals(VisitStageEnum.COMPLETED)) {
            existingVisit.setVisitFeedback(new VisitFeedback());
        }

        visitRepository.save(existingVisit);

        if (visitStageEnum == VisitStageEnum.CANCELLED) {
            notificationComposeService.sendVisitCancelledNotification(existingVisit);
        } else if (visitStageEnum == VisitStageEnum.DECLINED) {
            notificationComposeService.sendVisitDeclinedNotification(existingVisit);
        } else if (visitStageEnum == VisitStageEnum.COMPLETED) {
            notificationComposeService.sendVisitFeedbackNotification(List.of(existingVisit));
        }

    }

    /**
     * Retrieves a visit's stage history by visit ID.
     *
     * @param visitId The visit's unique identifier.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of stages representing the visit's stage history.
     */
    @Transactional(readOnly = true)
    public List<StageModel> getAllVisitStageHistoryByVisitId(final String visitId, final String siteUUCode) {

        return findByIdAndSiteUUCode(visitId, siteUUCode).getStageModelList();
    }

    /**
     * Add a new document and associates it with an existing visit identified by the given visitId.
     *
     * @param visitId       The unique identifier of the visit to which the document will be associated.
     * @param documentModel The document model representing the document to be created and associated.
     * @param siteUUCode The unique code associated with the site.
     * @return The added document model.
     */
    @Transactional
    public DocumentModel addVisitDocument(final String visitId, final DocumentModel documentModel, final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.CREATE_VISIT_DOCUMENT.getAllowVisitStageList(), "visit.document.create.restrict");

        if (CollectionUtils.isNotEmpty(existingVisit.getDocumentModelList())
                && existingVisit.getDocumentModelList().stream().anyMatch(existingDoc ->
                existingDoc.getTitle().equals(documentModel.getTitle()))) {
            throw new DataAlreadyExistsException(translator.toLocal("document.already.exist.with.title", documentModel.getTitle()));
        }
        existingVisit.addDocument(documentModel);

        visitRepository.save(existingVisit);

        return documentModel;
    }

    /**
     * Retrieves a list of visit documents by visit ID.
     *
     * @param visitId The visit's unique identifier.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of documents associated with the visit.
     * @throws IllegalArgumentException If visitId is null or empty.
     */
    @Transactional(readOnly = true)
    public List<DocumentModel> getAllVisitDocumentsByVisitId(final String visitId, final String siteUUCode) {

        return findByIdAndSiteUUCode(visitId, siteUUCode).getDocumentModelList();
    }

    /**
     * Delete a visit's document by visit ID and document title.
     *
     * @param visitId The visit's unique identifier.
     * @param title   The document's unique identifier.
     * @param siteUUCode The unique code associated with the site.
     */
    @Transactional
    public void deleteVisitDocument(final String visitId, final String title, final String siteUUCode) {
        
        // Retrieve the existing visit based on visitId.
        final var existingVisit = findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, 
                APIModuleEnum.DELETE_VISIT_DOCUMENT.getAllowVisitStageList(), "visit.document.delete.restrict");

        if (CollectionUtils.isEmpty(existingVisit.getDocumentModelList())
                || existingVisit.getDocumentModelList().stream().noneMatch(existingDoc -> existingDoc.getTitle().equals(title))) {
            throw new DataNotFoundException(translator.toLocal("document.with_title.not.found", title));
        }

        existingVisit.getDocumentModelList().removeIf(existingDoc -> existingDoc.getTitle().equals(title));

        visitRepository.save(existingVisit);
    }

    /**
     * Updates the visit stages based on the specified site date and time.
     * This method retrieves a list of existing visits with pending or accepted stages
     * and end date-time less than the provided site date-time. It then updates the
     * visit stages based on certain conditions, such as marking a visit as expired
     * or completed. Finally, it saves the updated visits and sends notification for
     * completed visits with feedback.
     *
     * @param siteDateTime The date and time of the site for which visit stages should be updated.
     */
    public void updateVisitStages(final LocalDateTime siteDateTime) {

        final List<Visit> existingVisitList = visitRepository.findAllByVisitStageEnumInAndEndDateTimeLessThan(
                Set.of(VisitStageEnum.PENDING, VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN), siteDateTime);

        final List<Visit> completedVisitList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingVisitList)) {
            existingVisitList.forEach(existingVisit -> {

                if (existingVisit.getVisitTypeEnum().equals(VisitTypeEnum.VISIT)) {
                    if (existingVisit.getVisitStageEnum().equals(VisitStageEnum.PENDING)) {
                        existingVisit.setVisitStageEnum(VisitStageEnum.EXPIRED);
                        existingVisit.addStageHistory(new StageModel(VisitStageEnum.EXPIRED.name()));
                    } else if (existingVisit.getVisitStageEnum().equals(VisitStageEnum.ACCEPTED)) {
                        existingVisit.setVisitStageEnum(VisitStageEnum.NOSHOW);
                        final StageModel stageModel = new StageModel();
                        stageModel.setStage(VisitStageEnum.NOSHOW.name());
                        stageModel.setReason("NOSHOW automatically");
                        existingVisit.addStageHistory(stageModel);
                    } else if (existingVisit.getVisitStageEnum().equals(VisitStageEnum.CHECK_IN)) {
                        existingVisit.setVisitStageEnum(VisitStageEnum.COMPLETED);
                        existingVisit.setVisitFeedback(new VisitFeedback());
                        final StageModel stageModel = new StageModel();
                        stageModel.setStage(VisitStageEnum.COMPLETED.name());
                        stageModel.setReason("COMPLETED automatically");
                        existingVisit.addStageHistory(stageModel);
                    }
                } else if (existingVisit.getVisitTypeEnum().equals(VisitTypeEnum.TOUR)) {
                    if (existingVisit.getVisitStageEnum().equals(VisitStageEnum.ACCEPTED)) {
                        existingVisit.setVisitStageEnum(VisitStageEnum.NOSHOW);
                        existingVisit.addStageHistory(new StageModel(VisitStageEnum.NOSHOW.name()));
                    }
                }
            });

            visitRepository.saveAll(existingVisitList);

            notificationComposeService.sendVisitFeedbackNotification(completedVisitList);
        }
    }

    /**
     * Retrieves the roles and permissions associated with a visit for a specific user based on their authentication token.
     *
     * @param visitId The ID of the visit for which roles and permissions are being retrieved.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of `RoleModel` objects representing the roles and permissions for the user in the specified visit.
     */
    @Transactional(readOnly = true)
    public List<RoleModel> getVisitPersonnelRolesAndPermission(final String visitId, final String siteUUCode) {

        // Retrieve the personnel information based on the PID.
        final var personnel = personnelService.getLoginedPersonnel();

        final var visitPersonnelList = visitPersonnelRepository
                .findByVisitVisitIdAndVisitStatusNotAndPersonnelPersonnelIdAndPersonnelStatusNotAndVisitSiteUuCode(visitId, Status.DELETED,
                        personnel.getPersonnelId(), Status.DELETED, siteUUCode);

        // Initialize a list to store the existing roles associated with the visit personnel.
        final List<Role> existingRoles = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            // Extract the roles from the visit personnel and add them to the existingRoles list.
            existingRoles.addAll(visitPersonnelList.stream().map(VisitPersonnel::getRole).toList());
        }

        // Convert the list of Role objects to a list of RoleModel objects using the roleMapper.
        return roleMapper.roleListToRoleModelList(existingRoles);
    }

    /**
     * Retrieves a list of pre-booked visits of the specified tour type based on the provided filter criteria.
     *
     * @param search             A search string to filter visits by relevant information.
     * @param visitStageEnumList A list of visit stage enums to filter visits by specific stages.
     * @param startDateTime      The start date and time to filter visits that occurred after this date and time.
     * @param endDateTime        The end date and time to filter visits that occurred before this date and time.
     * @param siteUUCode         The unique identifier for the site to filter visits by a specific location.
     * @param personnelId         The personnel ID for filtering visits.
     * @return A list of {@link Visit} objects representing pre-booked visits of the specified tour type that match the criteria.
     */
    @Transactional(readOnly = true)
    public List<Visit> getTourTypeVisitListByFilter(final String search, final List<VisitStageEnum> visitStageEnumList,
            final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String siteUUCode, final String personnelId) {

        // Build a Specification for filtering visits based on the search string and visit type.
        final Specification<Visit> specification =
                visitSpecification.buildVisitSpecification(null, null, search,
                        visitStageEnumList, null, startDateTime, endDateTime, personnelId, VisitTypeEnum.TOUR, null,
                    siteUUCode, null);

        // Retrieve visits based on the built specification.
        return visitRepository.findAll(specification);

    }

    /**
     * Retrieves a list of {@link VisitCountModel} objects based on specified criteria.
     *
     * @param siteUUCode        The site code for which visit counts are to be retrieved.
     * @param startDateTime     The start date and time for the visit count calculation.
     * @param endDateTime       The end date and time for the visit count calculation.
     * @param visitType         The string representation of the visit type to filter the results.
     * @return A list of {@link VisitCountModel} objects representing the visit counts.
     * @throws IllegalArgumentException If the provided visit type is not a valid enum constant, 
     *                      an {@link IllegalArgumentException} is thrown.
     *                      The exception message indicates that the visit type should be one of the valid enum values.
     */
    @Transactional(readOnly = true)
    public List<VisitCountModel> getVisitCount(final String siteUUCode, final LocalDateTime startDateTime, final LocalDateTime endDateTime,
            final String visitType) {
        final VisitTypeEnum visitTypeEnum = validationUtils.validateVisitType(visitType);
        
        final List<VisitStageEnum> activeVisitStageEnumList = CommonUtils.getActiveVisitStageEnumListByVisitTypeEnum(visitTypeEnum);

        return visitRepository.findVisitCountGroupByStartDateAndTime(siteUUCode, startDateTime, endDateTime, visitTypeEnum,
                activeVisitStageEnumList);
    }

    /**
     * Retrieves and summarizes visit-related information based on specified parameters.
     * This method fetches details such as basic visit information, personnel involved, and associated services.
     * 
     * @param siteUUCode       Unique identifier for the site.
     * @param startDateTime    Start date and time for the visit range.
     * @param endDateTime      End date and time for the visit range.
     * @param visitType        Type of visit.
     * @param search            A search query to filter visits. (Optional)
     * @return                 List of VisitSummaryModel containing summarized visit details.
     */
    @Transactional(readOnly = true)
    public List<VisitSummaryModel> getVisitSummary(final String siteUUCode, final LocalDateTime startDateTime, 
            final LocalDateTime endDateTime, final String visitType, final  String search) {

        final VisitTypeEnum visitTypeEnum = validationUtils.validateVisitType(visitType);

        final List<VisitStageEnum> activeVisitStageEnumList = CommonUtils.getActiveVisitStageEnumListByVisitTypeEnum(visitTypeEnum);

        // Get the logged-in personnel
        final var personnel = personnelService.getLoginedPersonnel();

        final Specification<Visit> specification = visitSpecification.buildVisitSpecificationForVisitSummary(search,
                startDateTime, endDateTime, personnel.getPersonnelId(), activeVisitStageEnumList, visitTypeEnum, siteUUCode);

        final var existingVisitList = visitRepository.findAll(specification);

        return existingVisitList.stream()
                .map(existingVisit -> {

                    // Initialize a VisitSummaryModel for the current visit
                    final VisitSummaryModel visitSummaryModel = new VisitSummaryModel();

                    // Set basic visit information in the VisitSummaryModel
                    final List<VisitSummaryServiceModel> visitSummaryServiceModelList = new ArrayList<>();
                    visitSummaryModel.setVisitBasicInfoModel(visitMapper.convertVisitToVisitBasicInfo(existingVisit));

                    // Filter existing visit personnel for the logged-in personnel, considering distinct roles
                    List<VisitPersonnel> existingVisitPersonnelInVisit = existingVisit.getVisitPersonnelList().stream()
                            .filter(existinVisitPersonnel -> existinVisitPersonnel.getPersonnel().equals(personnel))
                            .toList();

                    // Remove duplicate roles for the logged-in personnel within the same visit
                    existingVisitPersonnelInVisit = existingVisitPersonnelInVisit.stream()
                            .filter(CommonUtils.distinctByKey(p -> p.getRole().getRoleId()))
                            .toList();

                    // Remove duplicate roles for the logged-in personnel within the same visit
                    if (CollectionUtils.isNotEmpty(existingVisitPersonnelInVisit)) {
                        visitSummaryModel.setVisitPersonnelModelList(visitPersonnelMapper
                                .visitPersonnelListToVisitPersonnelModelList(existingVisitPersonnelInVisit));
                    }

                    // Process each service in the existing visit
                    existingVisit.getVisitServiceList().stream().forEach(existingVisitService -> {

                        // Filter existing visit personnel for the logged-in personnel within the current service
                        final List<VisitPersonnel> existingVisitPersonnelInService = existingVisitService.getVisitPersonnelList().stream()
                                .filter(existinVisitPersonnel -> existinVisitPersonnel.getPersonnel().equals(personnel)).toList();

                        // Check if personnel participated in the service
                        if (CollectionUtils.isNotEmpty(existingVisitPersonnelInService)) {

                            // Process service information and add it to the VisitSummaryServiceModelList
                            if (CollectionUtils.isNotEmpty(existingVisitService.getVisitLocationList())) {

                                // Process each location in the service
                                existingVisitService.getVisitLocationList().stream().forEach(existingVisitLocation -> {

                                    if (!existingVisitService.getServiceTemplate().getServiceTypeEnum().equals(ServiceTypeEnum.TOUR)
                                            || existingVisitService.getServiceTemplate().getServiceTypeEnum().equals(ServiceTypeEnum.TOUR)
                                            && existingVisitLocation.getLocationTagEnum().equals(LocationTagEnum.PICKUP)) {
                                        final var visitSummaryServiceModelForService = 
                                                visitSummaryMapper.mapVisitSummaryServiceModel(existingVisitService, existingVisitLocation);
                                        visitSummaryServiceModelList.add(visitSummaryServiceModelForService);
                                    }

                                    if (!ObjectUtils.isEmpty(existingVisitLocation.getInterviewVolunteerVisitPersonnel()) 
                                            && existingVisitPersonnelInService.contains(existingVisitLocation
                                                    .getInterviewVolunteerVisitPersonnel())) {
                                        final var visitSummaryServiceModel = visitSummaryMapper
                                                .visitLocationToVisitSummaryServiceModel(existingVisitLocation);  
                                        visitSummaryServiceModel.setServiceTemplateName(GeneralConstant.AUDIO_VIDEO);
                                        visitSummaryServiceModelList.add(visitSummaryServiceModel);
                                    }  
                                });
                            } else {
                                final var visitSummaryServiceModelForService = 
                                        visitSummaryMapper.mapVisitSummaryServiceModel(existingVisitService, null);
                                visitSummaryServiceModelList.add(visitSummaryServiceModelForService);
                            }
                        }
                    });
                    // Set the VisitSummaryServiceModelList in the VisitSummaryModel
                    visitSummaryModel.setVisitSummaryServiceModelList(visitSummaryServiceModelList);

                    return visitSummaryModel;
                }).collect(Collectors.toList());
    }

    /**
     * Retrieves and summarizes visit-related information based on specified parameters.
     * This method fetches details such as basic visit information.
     * 
     * @param siteUUCode       Unique identifier for the site.
     * @param startDateTime    Start date and time for the visit range.
     * @param endDateTime      End date and time for the visit range.
     * @return                 List of VisitSummaryModel containing summarized visit details.
     */
    @Transactional(readOnly = true)
    public List<VisitCountModel> getPreBookedVisitSummary(final String siteUUCode, final LocalDateTime startDateTime, 
            final LocalDateTime endDateTime) {

        // Get the logged-in personnel
        final var personnel = personnelService.getLoginedPersonnel();
        
        final List<VisitStageEnum> activeVisitStageEnumList = CommonUtils.getActiveVisitStageEnumListByVisitTypeEnum(VisitTypeEnum.TOUR);

        return visitRepository.findVisitCountByPersonnelIdAndFilter(siteUUCode, startDateTime, endDateTime, VisitTypeEnum.TOUR,
                personnel.getPersonnelId(), activeVisitStageEnumList, Status.DELETED);
    }
    
    /**
     * Finds the RoleTagEnum associated with a specific VisitPersonnel and VisitService, based on provided criteria.
     * 
     * @param visitId           The identifier for the visit.
     * @param siteUUCode        The site's unique universal code.
     * @param roleTagEnumList  The list of RoleTagEnums to filter against.
     * @return                  A set of RoleTagEnum values matching the provided criteria.
     */
    @Transactional(readOnly = true)
    public Set<RoleTagEnum> findFilteredRoleTagsFromVisitByRoleTagList(final String visitId,
            final String siteUUCode, final List<RoleTagEnum> roleTagEnumList) {
        
        final var personnel = personnelService.getLoginedPersonnel();
        
        // Retrieve the existing VisitService based on provided identifiers.
        final var visitPersonnelList = visitPersonnelService
                .findVisitPersonnelListByVisitIdAndSiteUucodeAndPersonnelId(visitId, siteUUCode, personnel.getPersonnelId());
     
        // Initialize a set to hold the existing RoleTagEnums.
        Set<RoleTagEnum> existingRoleTags = new HashSet<>();
        
        // Check if the existing VisitService has associated VisitPersonnel.
        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {
            // Filter VisitPersonnel based on RoleTagEnum and personnel, then map them to RoleTagEnum values.
            existingRoleTags = visitPersonnelList.stream()
                    .filter(visitPersonnel -> roleTagEnumList.contains(visitPersonnel.getRoleTagEnum()))
                    .map(VisitPersonnel::getRoleTagEnum).collect(Collectors.toSet());
        }

        // Return the set of existing RoleTagEnums.
        return existingRoleTags;
    }

    public Visit saveVisit(final Visit visit) {
        return visitRepository.save(visit);
    }

    public List<Visit> getNextDayVisitList() {
        // Get start and end of the next day
        final LocalDate tomorrow = LocalDate.now().plusDays(1);
        final LocalDateTime startOfNextDay = LocalDateTime.of(tomorrow, LocalTime.MIN);
        final LocalDateTime endOfNextDay = LocalDateTime.of(tomorrow, LocalTime.MAX);

        return visitRepository.findVisitsForNextDay(startOfNextDay, endOfNextDay);
    }

    public VisitModel getVisitBookingDetails(final String requestNumber, final String siteUUCode, final String email,
            final String phoneNumber, final String lastName) {
        
        final Visit existingVisit;
        if (StringUtils.isBlank(requestNumber) || StringUtils.isBlank(siteUUCode)) {
            throw new DataNotFoundException(translator.toLocal("visit.not.found"));
        } else {
            existingVisit = visitRepository.findVisitByRequestNumberAndFilter(siteUUCode, requestNumber, email, phoneNumber, lastName,
                    Status.ACTIVE, Status.ACTIVE, Status.ACTIVE)
                    .orElseThrow(() -> new DataNotFoundException(translator.toLocal("visit.not.found")));
        }
        
        final VisitModel visitModel = visitMapper.visitToVisitModel(existingVisit);

        if (CollectionUtils.isNotEmpty(existingVisit.getRequestedServiceIds())) {

            final List<ServiceTemplate> existingServiceTemplates = serviceTemplateService
                    .findByServiceTemplateIdsAndSiteUUCode(existingVisit.getRequestedServiceIds(), existingVisit.getSite().getUuCode());  
            final List<ServiceTemplateBasicInfoModel> serviceTemplateModelList = serviceTemplateMapper
                    .serviceTemplateListToServiceTemplateBasicInfoModelList(existingServiceTemplates);
            visitModel.setRequestedServices(serviceTemplateModelList);       
        }
        return visitModel;
    }
}