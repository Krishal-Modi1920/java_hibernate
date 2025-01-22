package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.common.utils.ValidationUtils;
import org.baps.api.vtms.enumerations.PermissionEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.enumerations.TourSlotStageEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.PreBookedVisitMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.mappers.VisitorMapper;
import org.baps.api.vtms.models.CreatePreBookedVisitModel;
import org.baps.api.vtms.models.PreBookedVisitBasicModel;
import org.baps.api.vtms.models.PreBookedVisitModel;
import org.baps.api.vtms.models.PreBookedVisitorModel;
import org.baps.api.vtms.models.TourSlotWithVisitorCountModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.TourSlot;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPublicFeedback;
import org.baps.api.vtms.models.entities.VisitService;
import org.baps.api.vtms.models.entities.VisitVisitor;
import org.baps.api.vtms.models.entities.Visitor;
import org.baps.api.vtms.repositories.TourSlotRepository;
import org.baps.api.vtms.repositories.VisitRepository;
import org.baps.api.vtms.repositories.specifications.VisitSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class PreBookedVisitService {

    private final PersonnelService personnelService;

    private final org.baps.api.vtms.services.VisitService visitService;

    private final RoleService roleService;

    private final TourSlotService tourSlotService;

    private final ServiceTemplateService serviceTemplateService;

    private final PreBookedVisitMapper preBookedVisitMapper;

    private final VisitSpecification visitSpecification;

    private final VisitRepository visitRepository;

    private final TourSlotRepository tourSlotRepository;

    private final VisitorMapper visitorMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final Translator translator;

    private final SiteService siteService;

    private final ValidationUtils validationUtils;
    
    private final CountryService countryService;
    
    private final StateService stateService;

    private final NotificationComposeService notificationComposeService;

    /**
     * Creates a pre-booked visit and returns its model representation.
     *
     * @param createPreBookedVisitModel The model containing information for creating the pre-booked visit.
     * @param siteUUCode                The unique code associated with the site.
     * @return A CreatePreBookedVisitModel representing the newly created pre-booked visit.
     * @throws DataValidationException If the specified tour slot is not available for the requested number of visitors.
     */
    public CreatePreBookedVisitModel createPreBookedVisit(final CreatePreBookedVisitModel createPreBookedVisitModel,
                                                          final String siteUUCode) {

        final var savedVisit = savePreBookedVisit(createPreBookedVisitModel, siteUUCode);
        notificationComposeService.sendVisitRequestReceived(savedVisit);
        
        savedVisit.setVisitBookedFeedback(new VisitPublicFeedback());
        visitRepository.save(savedVisit);

        return preBookedVisitMapper.visitToCreatePreBookedVisitModel(savedVisit);
    }

    @Transactional
    private Visit savePreBookedVisit(final CreatePreBookedVisitModel createPreBookedVisitModel,
            final String siteUUCode) {
        // Check tour slot availability
        final var tourSlotAvailableErrorSet = tourSlotService.checkTourSlotAvailable(
                createPreBookedVisitModel.getTourSlotId(), createPreBookedVisitModel.getTotalVisitors(), siteUUCode);

        // If slot is not available, throw a DataValidationException
        if (CollectionUtils.isNotEmpty(tourSlotAvailableErrorSet)) {
            throw new DataValidationException(String.join(", ", tourSlotAvailableErrorSet));
        }

        // Create a new Visit and set its properties
        final var saveVisit = preBookedVisitMapper.createPreBookedVisitModellToVisit(createPreBookedVisitModel);
        
        countryService.findCountryById(createPreBookedVisitModel.getPrimaryVisitorModel().getCountry());
        
        if (StringUtils.isNoneBlank(createPreBookedVisitModel.getPrimaryVisitorModel().getState())) {
            stateService.findStateById(createPreBookedVisitModel.getPrimaryVisitorModel().getState());
        }
        
        saveVisit.setSite(siteService.findByUUCode(siteUUCode));

        // Retrieve and set Tour Slot details
        final var existingTourSlot = tourSlotService.findActiveTourSlotByIdAndSiteUUCode(
                createPreBookedVisitModel.getTourSlotId(), siteUUCode);
        saveVisit.setStartDateTime(existingTourSlot.getStartDateTime());
        saveVisit.setEndDateTime(existingTourSlot.getEndDateTime());
        saveVisit.setTourSlot(existingTourSlot);
        saveVisit.setVisitStageEnum(VisitStageEnum.ACCEPTED);
        saveVisit.setRequestNumber(visitService.getNextVisitNumber(saveVisit.getVisitTypeEnum(), siteUUCode));

        // Map primary visitor and add to the Visit
        final var visitor = visitorMapper.createPreBookedVisitorModelToVisitor(createPreBookedVisitModel.getPrimaryVisitorModel());
        final var visitVisitor = new VisitVisitor();
        visitVisitor.setVisitorContactTypeEnum(VisitorContactTypeEnum.PRIMARY);
        visitVisitor.setVisitor(visitor);
        saveVisit.addVisitVisitor(visitVisitor);

        // Create a VisitService for the Visit
        final var saveVisitService = new VisitService();
        saveVisitService.setStartDateTime(existingTourSlot.getStartDateTime());
        saveVisitService.setEndDateTime(existingTourSlot.getEndDateTime());
        saveVisitService.setServiceTemplate(serviceTemplateService.findFirstByServiceTypeEnum(ServiceTypeEnum.TOUR, siteUUCode));
        saveVisit.addVisitService(saveVisitService);

        // Add Tour Guides as Visit Personnel
        if (CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {

            existingTourSlot.getTourSlotPersonnelList().forEach(tourSlotPersonnel -> {
                final var tourGuideVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(saveVisit, tourSlotPersonnel.getPersonnel(),
                    roleService.findByRoleEnum(RoleEnum.TOUR_GUIDE), RoleTagEnum.TOUR_GUIDE, null);
                saveVisitService.addVisitPersonnel(tourGuideVisitPersonnel);
                saveVisit.addVisitPersonnel(tourGuideVisitPersonnel);
            });
        }

        calculateGuestSizeAndUpdateTourSlotStatus(existingTourSlot, createPreBookedVisitModel.getTotalVisitors(), siteUUCode);

        visitRepository.save(saveVisit);
        
        return saveVisit;
    }
    
    /**
     * Calculates the total number of guests for a given tour slot, updates the booking status of the tour slot,
     * and sets the appropriate tour slot stage based on the number of booked guests.
     *
     * @param existingTourSlot The existing TourSlot entity for which the guest size and status will be updated.
     * @param totalVisitor The total number of visitors to be added or considered in the calculation.
     * @param siteUUCode The unique identifier of the site associated with the tour slot.
     */
    @Transactional(readOnly = true)
    private void calculateGuestSizeAndUpdateTourSlotStatus(final TourSlot existingTourSlot, final int totalVisitor,
            final String siteUUCode) {

        // Calculate the total number of visitors already booked for this tour slot.
        var bookedSlotGuestSize = tourSlotRepository.countBookedVisitorCountByTourSlotIds(
                Set.of(existingTourSlot.getTourSlotId()), siteUUCode).stream()
            .mapToLong(TourSlotWithVisitorCountModel::getBookedVisitorCount).sum();

        bookedSlotGuestSize = bookedSlotGuestSize + totalVisitor;
        
        if (existingTourSlot.getMaxGuestSize() == bookedSlotGuestSize) {
            existingTourSlot.setTourSlotStageEnum(TourSlotStageEnum.BOOKED);
        } else if (existingTourSlot.getMaxGuestSize() / 2 <= bookedSlotGuestSize) {
            existingTourSlot.setTourSlotStageEnum(TourSlotStageEnum.PARTIALLY);
        }
    }

    /**
     * Creates or updates a pre-booked visit based on the provided parameters.
     * If a valid visitId is provided, it updates an existing visit. Otherwise, it creates a new visit.
     *
     * @param visitId             The unique identifier of the visit to update, or null for a new visit.
     * @param preBookedVisitModel The PreBookedVisitModel containing visit details to be created or updated.
     * @param siteUUCode          The unique code associated with the site.
     * @return A PreBookedVisitModel representing the created or updated visit.
     */
    @Transactional
    public PreBookedVisitModel createOrUpdatePreBookedVisit(@Nullable final String visitId, final PreBookedVisitModel preBookedVisitModel,
            final String siteUUCode) {

        // Create a wrapper object to hold the Visit
        final var wrapperObject = new Object() {
            Visit saveOrUpdateVisit = new Visit();
        };

        // If visitId is not blank, find the existing visit by ID
        if (StringUtils.isNotBlank(visitId)) {
            final List<VisitStageEnum> allowedVisitStageEnum =  List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.COMPLETED);
            
            // Retrieve the existing visit based on visitId.
            wrapperObject.saveOrUpdateVisit = visitService.findByIdAndSiteUUCodeAndAllowedStage(visitId, siteUUCode, allowedVisitStageEnum,
                    "pre_booked_visit.details.update.restrict");
        }

        // Map the properties from the PreBookedVisitModel to the Visit
        wrapperObject.saveOrUpdateVisit = preBookedVisitMapper.preBookedVisitModellToVisit(wrapperObject.saveOrUpdateVisit,
            preBookedVisitModel);

        // Create or update primary visitors within the visit
        createOrUpdatePreBookedVisitor(wrapperObject.saveOrUpdateVisit, preBookedVisitModel.getPrimaryVisitorModel(),
            VisitorContactTypeEnum.PRIMARY);

        final var existingTourSlot = tourSlotService.findActiveTourSlotByIdAndSiteUUCode(preBookedVisitModel.getTourSlotId(), siteUUCode);

        if (StringUtils.isBlank(wrapperObject.saveOrUpdateVisit.getVisitId())) {

            wrapperObject.saveOrUpdateVisit.setSite(siteService.findByUUCode(siteUUCode));

            final var tourSlotAvailableErrorSet = tourSlotService.checkTourSlotAvailable(
                    preBookedVisitModel.getTourSlotId(), preBookedVisitModel.getTotalVisitors(), siteUUCode);

            if (CollectionUtils.isNotEmpty(tourSlotAvailableErrorSet)) {
                throw new DataValidationException(String.join(", ", tourSlotAvailableErrorSet));
            }

            // Set the start and end date times, and the tour slot for the visit
            wrapperObject.saveOrUpdateVisit.setStartDateTime(existingTourSlot.getStartDateTime());
            wrapperObject.saveOrUpdateVisit.setEndDateTime(existingTourSlot.getEndDateTime());
            wrapperObject.saveOrUpdateVisit.setTourSlot(existingTourSlot);
            wrapperObject.saveOrUpdateVisit.setTotalVisitors(preBookedVisitModel.getTotalVisitors());
            wrapperObject.saveOrUpdateVisit.setVisitStageEnum(VisitStageEnum.ACCEPTED);
            wrapperObject.saveOrUpdateVisit.setRequestNumber(visitService.getNextVisitNumber(
                wrapperObject.saveOrUpdateVisit.getVisitTypeEnum(), siteUUCode));

            if (CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {

                // Create VisitService and add tour guides as personnel
                final var saveVisitService = new VisitService();
                saveVisitService.setStartDateTime(existingTourSlot.getStartDateTime());
                saveVisitService.setEndDateTime(existingTourSlot.getEndDateTime());
                saveVisitService.setServiceTemplate(serviceTemplateService.findFirstByServiceTypeEnum(ServiceTypeEnum.TOUR, siteUUCode));
                wrapperObject.saveOrUpdateVisit.addVisitService(saveVisitService);

                if (CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {
                    existingTourSlot.getTourSlotPersonnelList().forEach(tourSlotPersonnel -> {
                        final var tourGuideVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(null,
                            tourSlotPersonnel.getPersonnel(), roleService.findByRoleEnum(RoleEnum.TOUR_GUIDE),
                            RoleTagEnum.TOUR_GUIDE, null);
                        saveVisitService.addVisitPersonnel(tourGuideVisitPersonnel);
                        wrapperObject.saveOrUpdateVisit.addVisitPersonnel(tourGuideVisitPersonnel);
                    });
                }
            }

            calculateGuestSizeAndUpdateTourSlotStatus(existingTourSlot, preBookedVisitModel.getTotalVisitors(), siteUUCode);
        }
        tourSlotService.saveTourSlotAndVisitList(List.of(wrapperObject.saveOrUpdateVisit), existingTourSlot);
        // Map the saved Visit instance back to a VisitModel and return it
        return preBookedVisitMapper.visitToPreBookedVisitModel(wrapperObject.saveOrUpdateVisit);
    }

    /**
     * Creates or updates a pre-booked visitor associated with a visit based on the provided parameters.
     *
     * @param visit                 The visit to which the visitor is associated.
     * @param preBookedVisitorModel The pre-booked visitor model with information to be created or updated.
     * @param contactType           The type of contact for the visitor (e.g., phone, email).
     * @throws DataNotFoundException If the visitor is not found or there's an issue during the data operation.
     */
    @Transactional
    public void createOrUpdatePreBookedVisitor(final Visit visit, final PreBookedVisitorModel preBookedVisitorModel,
                                               final VisitorContactTypeEnum contactType) throws DataNotFoundException {

        // Check if the preBookedVisitorModel is not empty
        if (ObjectUtils.isNotEmpty(preBookedVisitorModel)) {

            // Try to find a visitor of the specified contact type in the visit's list of visitors
            final var optionalVisitVisitor = visit.getVisitVisitorList().stream()
                .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(contactType))
                .findFirst();

            // If no visitor of the given contact type exists and the model's visitorId is blank
            if (optionalVisitVisitor.isEmpty() && StringUtils.isBlank(preBookedVisitorModel.getVisitorId())) {

                // Create a new visitor and visit visitor
                final var visitor = visitorMapper.visitorModelToPreBookedVisitorModel(new Visitor(), preBookedVisitorModel);
                final var visitVisitor = new VisitVisitor();
                visitVisitor.setVisitorContactTypeEnum(contactType);
                visitVisitor.setVisitor(visitor);
                visit.addVisitVisitor(visitVisitor);

            } else if (optionalVisitVisitor.isPresent()
                && optionalVisitVisitor.get().getVisitor().getVisitorId().equals(preBookedVisitorModel.getVisitorId())) {

                // Update existing visitor based on the provided details
                visitorMapper.visitorModelToPreBookedVisitorModel(optionalVisitVisitor.get().getVisitor(), preBookedVisitorModel);
                if (StringUtils.isBlank(optionalVisitVisitor.get().getVisitor().getSalutation())) {
                    optionalVisitVisitor.get().getVisitor().setSalutation("");
                }
            } else {
                // Throw an exception if visitor data is conflicting or not found
                throw new DataNotFoundException(
                    translator.toLocal("visitor.with.visitor_id.not.found", contactType.name(), preBookedVisitorModel.getVisitorId()));
            }

        } else {
            // Remove visitor if not present in the model
            visit.getVisitVisitorList().removeIf(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(contactType));
        }
    }

    /**
     * Retrieves a pre-booked visit by its unique identifier (visitId) and maps it to a PreBookedVisitModel.
     *
     * @param visitId The unique identifier of the pre-booked visit to retrieve.
     * @param siteUUCode The unique code associated with the site.
     * @return A PreBookedVisitModel representing the retrieved pre-booked visit, or null if not found.
     */
    @Transactional(readOnly = true)
    public PreBookedVisitModel getPreBookedVisit(final String visitId, final String siteUUCode) {

        // Use the visitService to find the visit by its ID and map it to a PreBookedVisitModel
        return preBookedVisitMapper.visitToPreBookedVisitModel(visitService.findByIdAndSiteUUCode(visitId, siteUUCode));
    }

    /**
     * Retrieves a paginated list of pre-booked visits with optional filtering based on visit stage, search criteria, and tour slot ID.
     *
     * @param pageNo                The page number of the paginated result.
     * @param pageSize              The number of items to be included in each page of the paginated result.
     * @param visitStage            The stage of the visit to filter by.
     * @param search                Optional search criteria to filter visits.
     * @param tourSlotId            The ID of the tour slot to filter visits by.
     * @param siteUUCode            The unique code associated with the site for filtering visits.
     * @param selfAssignVisit       Flag indicating whether to retrieve self-assigned visits only.
     * @return A paginated response containing a list of pre-booked visits based on the provided filters.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<List<PreBookedVisitBasicModel>> getPaginatedPreBookedVisitsWithFilters(
        final Integer pageNo, final Integer pageSize, final String visitStage, @Nullable final String search, final String tourSlotId, 
        final String siteUUCode, final boolean selfAssignVisit) {

        final String personnelId;

        final Personnel personnel = personnelService.getLoginedPersonnel();

        // Initialize lists to store permission enums and role names.
        final List<PermissionEnum> permissionEnumList = new ArrayList<>();

        personnel.getPersonnelRoleList().forEach(personnelRole -> permissionEnumList.addAll(
            personnelRole.getRole().getRolePermissionList().stream()
                .map(rolePermission -> rolePermission.getPermission().getPermissonEnum())
                .collect(Collectors.toSet())
        ));

        // Determine personnel ID based on permissions
        if (!selfAssignVisit && permissionEnumList.contains(PermissionEnum.VIEW_PRE_BOOKED_VISIT_ALL_LIST)) {
            personnelId = null;
        } else if (selfAssignVisit && permissionEnumList.contains(PermissionEnum.VIEW_PRE_BOOKED_VISIT_SELF_ASSIGN_LIST)) {
            personnelId = personnel.getPersonnelId();
        } else {
            // Return an empty pagination and response if no permission is granted
            return CommonUtils.createEmptyPaginationAndResponse(pageNo);
        }
        
        final List<VisitStageEnum> visitStageEnumList = validationUtils.validateVisitStageEnum(visitStage, VisitTypeEnum.TOUR);
        
        // Build a specification for visit filtering
        final Specification<Visit> specification =
            visitSpecification.buildVisitSpecification(null, null, search, visitStageEnumList, null, null, null, personnelId,
                VisitTypeEnum.TOUR, tourSlotId, siteUUCode, null);

        // Retrieve a paginated list of existing visits
        final Page<Visit> existingVisitPage = visitRepository.findAll(specification, PageRequest.of(pageNo - 1, pageSize));

        List<PreBookedVisitBasicModel> preBookedVisitBasicModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingVisitPage.getContent())) {
            preBookedVisitBasicModelList = preBookedVisitMapper.visitListToPreBookedVisitBasicModelList(existingVisitPage.getContent());
        }
        // Calculate pagination and prepare the response
        return CommonUtils.calculatePaginationAndPrepareResponse(existingVisitPage, preBookedVisitBasicModelList);
    }
}
