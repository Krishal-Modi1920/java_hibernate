package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.common.utils.ValidationUtils;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.PermissionEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.TourSlotStageEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.TourSlotMapper;
import org.baps.api.vtms.mappers.VisitPersonnelMapper;
import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.PreBookedTourSlotModel;
import org.baps.api.vtms.models.TourSlotModel;
import org.baps.api.vtms.models.TourSlotWithVisitorCountModel;
import org.baps.api.vtms.models.TourSlotWrapperModel;
import org.baps.api.vtms.models.UpdateTourSlotStageModel;
import org.baps.api.vtms.models.base.PaginatedResponse;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Site;
import org.baps.api.vtms.models.entities.TourSlot;
import org.baps.api.vtms.models.entities.TourSlotPersonnel;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.repositories.TourSlotRepository;
import org.baps.api.vtms.repositories.VisitRepository;
import org.baps.api.vtms.repositories.specifications.TourSlotSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class TourSlotService {

    @Value("${tour_slots.interval}")
    private int tourSlotInterval;

    private final SiteService siteService;

    private final PersonnelService personnelService;

    private final VisitPersonnelService visitPersonnelService;

    private final RoleService roleService;

    private final VisitService visitService;

    private final TourSlotRepository tourSlotRepository;

    private final VisitRepository visitRepository;

    private final TourSlotMapper tourSlotMapper;

    private final VisitPersonnelMapper visitPersonnelMapper;

    private final TourSlotSpecification tourSlotSpecification;

    private final Translator translator;

    private final ValidationUtils validationUtils;

    /**
     * Finds a tour slot by its unique identifier.
     *
     * @param tourSlotId The unique identifier of the tour slot to find.
     * @param siteUUCode The unique code associated with the site.
     * @return The TourSlot object if found.
     * @throws DataNotFoundException if the tour slot with the specified ID is not found.
     */
    @Transactional(readOnly = true)
    public TourSlot findByIdAndSiteUUCode(final String tourSlotId, final String siteUUCode) throws DataNotFoundException {
        if (StringUtils.isBlank(tourSlotId)) {
            // If the tourSlotId is blank or empty, throw a DataNotFoundException.
            throw new DataAlreadyExistsException(translator.toLocal("tour_slot.with_tour_slot_id.not.found", tourSlotId));
        } else {
            // Attempt to find the tour slot by its ID in the repository, and if not found, throw a DataNotFoundException.
            return tourSlotRepository.findByTourSlotIdAndSiteUuCode(tourSlotId, siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal("tour_slot.with_tour_slot_id.not.found", tourSlotId)));
        }
    }

    /**
     * Retrieves an active TourSlot by its ID.
     *
     * @param tourSlotId The unique identifier of the TourSlot.
     * @param siteUUCode The unique code associated with the site.
     * @return The active TourSlot with the specified ID.
     * @throws DataNotFoundException If the ID is blank or if no active TourSlot is found.
     */
    @Transactional(readOnly = true)
    public TourSlot findActiveTourSlotByIdAndSiteUUCode(final String tourSlotId, final String siteUUCode) throws DataNotFoundException {
        if (StringUtils.isBlank(tourSlotId)) {
            // If the tourSlotId is blank or empty, throw a DataNotFoundException.
            throw new DataNotFoundException(translator.toLocal("tour_slot.with_tour_slot_id.not.found.or.not_active", tourSlotId));
        } else {
            // Attempt to find the tour slot by its ID in the repository, and if not found, throw a DataNotFoundException.
            final List<TourSlotStageEnum> tourSlotInactiveStageEnumList = List.of(TourSlotStageEnum.INACTIVE);

            return tourSlotRepository.findByTourSlotIdAndTourSlotStageEnumNotInAndSiteUuCode(tourSlotId, tourSlotInactiveStageEnumList,
                    siteUUCode)
                .orElseThrow(() -> new DataNotFoundException(translator.toLocal(
                    "tour_slot.with_tour_slot_id.not.found.or.not_active_or_partially", tourSlotId)));
        }
    }

    /**
     * Creates tour slots based on the provided information and adds them to the database.
     *
     * @param siteUUCode           The unique identifier of the site where tour slots will be created.
     * @param tourSlotWrapperModel The wrapper model containing tour slot information.
     * @return A wrapper model containing a list of created tour slots grouped by date.
     * @throws DataAlreadyExistsException If there are overlapping tour slots in the specified time range.
     */
    @Transactional
    public TourSlotWrapperModel createTourSlot(final String siteUUCode, final TourSlotWrapperModel tourSlotWrapperModel) {

        // Find the site based on the provided unique identifier.
        final Site existingSite = siteService.findByUUCode(siteUUCode);

        // Validate the input tour slot data.
        validateTourSlot(tourSlotWrapperModel);

        // Initialize a list to store tour slots that need to be saved to the database.
        final List<TourSlot> saveTourSlotList = new ArrayList<>();

        final LocalDateTime currentDateTime = siteService.getCurrentDateTimeFromExistingSite(existingSite);

        if (tourSlotWrapperModel.getEndDateTime().isBefore(currentDateTime)) {
            throw new DataValidationException(translator.toLocal("end_date_time.must_be_present_or_future.required"));
        }

        // Validate the input tour slot data.
        validateTourSlot(tourSlotWrapperModel);

        final LocalTime siteStartTime = existingSite.getStartTime();
        final LocalTime siteEndTime = existingSite.getEndTime();

        final LocalDateTime tempStartDateTime = tourSlotWrapperModel.getStartDateTime().withHour(siteStartTime.getHour())
            .withMinute(siteStartTime.getMinute())
            .withSecond(siteStartTime.getSecond());

        final LocalDateTime tempEndDateTime = tourSlotWrapperModel.getEndDateTime().withHour(siteEndTime.getHour())
            .withMinute(siteEndTime.getMinute())
            .withSecond(siteEndTime.getSecond());

        // Check if there are already slots in the specified time range.
        final boolean alreadySlotsExists = tourSlotRepository.existsByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
            tempStartDateTime, tempEndDateTime, siteUUCode);

        // If slots already exist, throw an exception.
        if (alreadySlotsExists) {
            throw new DataAlreadyExistsException(translator.toLocal("tour_slot.already.exists.between",
                tempStartDateTime.format(GeneralConstant.DATE_TIME_FORMATTER),
                tempEndDateTime.format(GeneralConstant.DATE_TIME_FORMATTER)));
        }

        // Initialize a time interval to create tour slots within the specified range.
        LocalDateTime updatedDateTime = tempStartDateTime;

        // Create tour slots within the specified time range.
        while (updatedDateTime.isBefore(tempEndDateTime)) {
            final LocalDateTime startDateTime = updatedDateTime;
            updatedDateTime = updatedDateTime.plusMinutes(tourSlotWrapperModel.getSlotInterval());
            LocalDateTime endDateTime = updatedDateTime;

            if (!startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate())) {
                endDateTime = endDateTime.withHour(GeneralConstant.TWENTY_THREE)
                    .withMinute(GeneralConstant.FIFTY_NINE)
                    .withSecond(GeneralConstant.FIFTY_NINE);
            }

            if ((existingSite.getStartTime().isBefore(startDateTime.toLocalTime())
                || existingSite.getStartTime().equals(startDateTime.toLocalTime()))
                && existingSite.getEndTime().isAfter(startDateTime.toLocalTime())) {

                // Create and add a tour slot to the list to be saved.
                saveTourSlotList.add(tourSlotMapper.tourSlotWrapperModelToTourSlot(tourSlotWrapperModel,
                    startDateTime, endDateTime, existingSite));
            }
        }

        // Save the created tour slots to the database.
        tourSlotRepository.saveAll(saveTourSlotList);

        return getTourSlot(tempStartDateTime, tempEndDateTime, siteUUCode);
    }

    /**
     * Validates the slot interval in the provided TourSlotWrapperModel.
     *
     * @param tourSlotWrapperModel The model containing slot interval information to be validated.
     * @throws DataValidationException If the slot interval is not divisible by tourSlotInterval minutes.
     */
    private void validateTourSlot(final TourSlotWrapperModel tourSlotWrapperModel) {
        // Get the slot interval from the provided model.
        final int slotInterval = tourSlotWrapperModel.getSlotInterval();

        if (slotInterval % tourSlotInterval != 0) {
            throw new DataValidationException(translator.toLocal("tour_slot.interval_should_be.divisible_by", tourSlotInterval));
        }
    }

    /**
     * Retrieves a TourSlotWrapperModel based on the specified criteria.
     *
     * @param startDateTime The start date and time for the search range.
     * @param endDateTime   The end date and time for the search range.
     * @param siteUUCode    The unique code associated with the site.
     * @return A TourSlotWrapperModel containing grouped tour slots within the specified range.
     */
    @Transactional(readOnly = true)
    public TourSlotWrapperModel getTourSlot(
        final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String siteUUCode) {

        // Retrieve existing tour slots within the specified time range.
        final List<TourSlot> existingTourSlotList = tourSlotRepository
            .findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
                startDateTime, endDateTime, siteUUCode);

        // Convert the saved tour slots to a list of tour slot models.
        List<TourSlotModel> tourSlotModelList = tourSlotMapper.tourSlotListToTourSlotModelList(existingTourSlotList);

        final Map<String, TourSlotModel> tourSlotModelMap = tourSlotModelList.stream()
            .collect(Collectors.toMap(TourSlotModel::getTourSlotId, tourSlotModel -> tourSlotModel));

        final List<TourSlotWithVisitorCountModel> tourSlotWithVisitorCountModelList =
            tourSlotRepository.countBookedVisitorCountByTourSlotIds(tourSlotModelMap.keySet(), siteUUCode);

        tourSlotWithVisitorCountModelList.forEach(tourSlotWithVisitorCountModel -> tourSlotModelMap
            .get(tourSlotWithVisitorCountModel.getTourSlotId()).setBookedGuestSize(
                tourSlotWithVisitorCountModel.getBookedVisitorCount()));

        // Define an object to store the start and end date times while grouping tour slots by date.
        final var wrapperObject = new Object() {
            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
        };

        if (CollectionUtils.isNotEmpty(tourSlotModelList)) {
            tourSlotModelList = tourSlotModelList.stream().sorted(Comparator.comparing(TourSlotModel::getStartDateTime)).toList();
        }

        // Group tour slots by date and update start/end date times if necessary.
        final Map<LocalDate, List<TourSlotModel>> mapOfTourSlotDateWithTourSlotModelList = tourSlotModelList.stream()
            .collect(Collectors.groupingBy(tourSlotModel -> {

                if (ObjectUtils.isEmpty(wrapperObject.startDateTime)
                    || wrapperObject.startDateTime.isAfter(tourSlotModel.getStartDateTime())) {
                    wrapperObject.startDateTime = tourSlotModel.getStartDateTime();
                }
                if (ObjectUtils.isEmpty(wrapperObject.endDateTime)
                    || wrapperObject.endDateTime.isBefore(tourSlotModel.getEndDateTime())) {
                    wrapperObject.endDateTime = tourSlotModel.getEndDateTime();
                }
                return tourSlotModel.getStartDateTime().toLocalDate();
            }));

        final Map<LocalDate, List<TourSlotModel>> sortedMapOfTourSlotDateWithTourSlotModelList =
            new TreeMap<>(mapOfTourSlotDateWithTourSlotModelList);

        // Map the grouped tour slots to a wrapper model and return it.
        return tourSlotMapper.mapOfTourSlotDateWithTourSlotModelListToTourSlotWrapperModel(sortedMapOfTourSlotDateWithTourSlotModelList,
            wrapperObject.startDateTime, wrapperObject.endDateTime);
    }

    /**
     * Retrieves a list of public tour slots for a specified tour date.
     *
     * @param tourDate   The date for which tour slots are to be retrieved.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of TourSlotModel objects representing public tour slots for the given date.
     */
    @Transactional(readOnly = true)
    public List<PreBookedTourSlotModel> getPublicTourSlot(final LocalDate tourDate, final String siteUUCode) {
        // Calculate the start and end date times for the specified tour date.
        final LocalDateTime startDateTime = tourDate.atStartOfDay();
        final LocalDateTime endDateTime = tourDate.atTime(LocalTime.MAX);

        // Retrieve existing tour slots within the specified time range that are in ACTIVE or INACTIVE stages.
        final List<TourSlot> existingTourSlotList = tourSlotRepository
            .findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
                startDateTime, endDateTime, siteUUCode);

        final Map<String, Integer> mapOfTourSlotIdWithMaxGuestCount = existingTourSlotList.stream()
            .collect(Collectors.toMap(TourSlot::getTourSlotId, TourSlot::getMaxGuestSize));

        // Convert the saved tour slots to a list of tour slot models.
        final List<PreBookedTourSlotModel> preBookedTourSlotModelList = tourSlotMapper.tourSlotListToPreBookedTourSlotModelList(
            existingTourSlotList);

        final List<TourSlotWithVisitorCountModel> tourSlotWithVisitorCountModelList =
            tourSlotRepository.countBookedVisitorCountByTourSlotIds(mapOfTourSlotIdWithMaxGuestCount.keySet(), siteUUCode);

        final Map<String, Long> mapOfTourSlotIdWithBookedVisitorCount = tourSlotWithVisitorCountModelList.stream()
            .collect(Collectors.toMap(TourSlotWithVisitorCountModel::getTourSlotId,
                TourSlotWithVisitorCountModel::getBookedVisitorCount));

        preBookedTourSlotModelList.forEach(preBookedTourSlotModel -> {
            final long maxGuestSize = MapUtils.isNotEmpty(mapOfTourSlotIdWithMaxGuestCount)
                && mapOfTourSlotIdWithMaxGuestCount.containsKey(preBookedTourSlotModel.getTourSlotId())
                ? mapOfTourSlotIdWithMaxGuestCount.get(preBookedTourSlotModel.getTourSlotId()) : 0;
            final long bookedGuestSize = MapUtils.isNotEmpty(mapOfTourSlotIdWithBookedVisitorCount)
                && mapOfTourSlotIdWithBookedVisitorCount.containsKey(preBookedTourSlotModel.getTourSlotId())
                ? mapOfTourSlotIdWithBookedVisitorCount.get(preBookedTourSlotModel.getTourSlotId()) : 0;

            preBookedTourSlotModel.setAvailableGuestSize(maxGuestSize - bookedGuestSize);
        });

        return preBookedTourSlotModelList;
    }

    /**
     * Updates a TourSlot based on the provided ID and TourSlotModel, ensuring data consistency
     * and handling changes related to tour guides and associated visits.
     *
     * @param tourSlotId    The unique identifier of the TourSlot to be updated.
     * @param tourSlotModel The model containing data to update the TourSlot.
     * @param siteUUCode    The unique code associated with the site.
     * @return A TourSlotModel representing the updated TourSlot.
     */
    @Transactional
    public TourSlotModel updateTourSlot(final String tourSlotId, final TourSlotModel tourSlotModel, final String siteUUCode) {

        final LocalDateTime currentDateTime = siteService.getCurrentDateTimeFromExistingSite(siteService.findByUUCode(siteUUCode));

        // Retrieve the existing TourSlot based on the provided ID
        final TourSlot existingTourSlot = findByIdAndSiteUUCode(tourSlotId, siteUUCode);

        if (existingTourSlot.getStartDateTime().isBefore(currentDateTime)
            || existingTourSlot.getEndDateTime().isBefore(currentDateTime)) {
            throw new DataValidationException(translator.toLocal("tour_slot.past.can_not.update"));
        }

        validateBookedSlotSizeAndUpdateTourSlotStage(tourSlotId, tourSlotModel, existingTourSlot, siteUUCode);

        final List<Visit> existingVisitList = visitRepository.findAllByTourSlotTourSlotIdAndSiteUuCode(tourSlotId, siteUUCode);

        final PersonnelBasicInfoModel tourGuidePersonnelBasicInfoModel = tourSlotModel.getTourGuidePersonnelBasicInfoModel();
        // Check if the TourSlotModel contains Personnel data
        if (ObjectUtils.isNotEmpty(tourGuidePersonnelBasicInfoModel)) {

            final Personnel existingPersonnel = personnelService.findById(tourGuidePersonnelBasicInfoModel.getPersonnelId());

            // Check if personnel are associated with visits or visit services
            visitPersonnelService.checkPersonnelAssociateInVisitOrVisitServiceOrTourSlotExceptTourSlotId(
                List.of(tourGuidePersonnelBasicInfoModel.getPersonnelId()), existingTourSlot.getStartDateTime(),
                existingTourSlot.getEndDateTime(), existingTourSlot.getTourSlotId(), siteUUCode);

            final var wrapperObject = new Object() {
                boolean tourGuideChangeInSlot = false;
                boolean tourGuideChangeInVisit = false;
            };

            if (CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {
                existingTourSlot.getTourSlotPersonnelList().forEach(tourSlotPersonnelList -> {
                    if (!tourSlotPersonnelList.getPersonnel().getPersonnelId()
                        .equals(existingPersonnel.getPersonnelId())) {
                        tourSlotPersonnelList.setStatus(Status.DELETED);
                        wrapperObject.tourGuideChangeInSlot = true;
                    }
                });
            } else {
                wrapperObject.tourGuideChangeInSlot = true;
            }

            if (wrapperObject.tourGuideChangeInSlot) {
                final TourSlotPersonnel saveTourSlotPersonnel = new TourSlotPersonnel();
                saveTourSlotPersonnel.setTourSlot(existingTourSlot);
                saveTourSlotPersonnel.setPersonnel(existingPersonnel);
                existingTourSlot.addTourSlotPersonnel(saveTourSlotPersonnel);
            }

            if (CollectionUtils.isNotEmpty(existingVisitList)) {
                existingVisitList.forEach(existingVisit -> {
                    if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {
                        existingVisit.getVisitPersonnelList().forEach(visitPersonnel -> {
                            if (!visitPersonnel.getPersonnel().getPersonnelId()
                                .equals(existingPersonnel.getPersonnelId())) {
                                visitPersonnel.setStatus(Status.DELETED);
                                wrapperObject.tourGuideChangeInVisit = true;
                            }
                        });
                    } else {
                        wrapperObject.tourGuideChangeInVisit = true;
                    }
                    if (wrapperObject.tourGuideChangeInVisit) {
                        final var tourGuideVisitPersonnel = visitPersonnelMapper.createVisitPersonnel(
                            existingVisit, existingPersonnel, roleService.findByRoleEnum(RoleEnum.TOUR_GUIDE),
                            RoleTagEnum.TOUR_GUIDE, existingVisit.getVisitServiceList().get(0));
                        existingVisit.addVisitPersonnel(tourGuideVisitPersonnel);
                    }
                });
            }
        } else {
            existingTourSlot.getTourSlotPersonnelList().forEach(personnel -> {
                personnel.setStatus(Status.DELETED);
            });

            if (CollectionUtils.isNotEmpty(existingVisitList)) {
                existingVisitList.forEach(existingVisit -> {
                    if (CollectionUtils.isNotEmpty(existingVisit.getVisitPersonnelList())) {
                        existingVisit.getVisitPersonnelList().forEach(visitPersonnel -> visitPersonnel.setStatus(Status.DELETED));
                    }
                });
            }
        }

        existingTourSlot.setMaxGuestSize(tourSlotModel.getMaxGuestSize());

        // Save the updated TourSlot
        saveTourSlotAndVisitList(existingVisitList, existingTourSlot);

        // Return a TourSlotModel representing the updated TourSlot
        return tourSlotMapper.tourSlotToTourSlotModel(existingTourSlot);
    }

    /**
     * Validates the booked slot size and updates the tour slot stage based on certain conditions.
     *
     * @param tourSlotId       The ID of the tour slot to be validated and updated.
     * @param tourSlotModel    The model containing information about the tour slot.
     * @param existingTourSlot The existing tour slot to be updated.
     * @param siteUUCode       The unique code associated with the site.
     * @throws DataValidationException if validation fails based on certain criteria.
     */
    @Transactional
    private void validateBookedSlotSizeAndUpdateTourSlotStage(final String tourSlotId, final TourSlotModel tourSlotModel, 
            final TourSlot existingTourSlot, final String siteUUCode) {

        // Calculate the total booked slot guest size
        final long bookedSlotGuestSize = tourSlotRepository.countBookedVisitorCountByTourSlotIds(Set.of(tourSlotId), siteUUCode)
            .stream().mapToLong(TourSlotWithVisitorCountModel::getBookedVisitorCount).sum();

        // Validate if booked guest size exceeds the maximum allowed size
        if (ObjectUtils.isNotEmpty(bookedSlotGuestSize) && bookedSlotGuestSize > tourSlotModel.getMaxGuestSize()) {
            throw new DataValidationException(translator.toLocal("tour_slot.max_guest_size.should_be.greater_or_equal.total_visitor",
                tourSlotModel.getMaxGuestSize(), bookedSlotGuestSize));
        }

        // Determine the stage of the tour slot
        final TourSlotStageEnum tourSlotStageEnum = TourSlotStageEnum.valueOf(tourSlotModel.getStage());

        if (!tourSlotStageEnum.isChange() && !existingTourSlot.getTourSlotStageEnum().equals(tourSlotStageEnum)) {
            throw new DataValidationException(translator.toLocal("tour_slot.stage.not_change", tourSlotStageEnum));
        }

        // Validate stage and booked guest size conditions
        if (ObjectUtils.isNotEmpty(bookedSlotGuestSize) && bookedSlotGuestSize > 0
            && tourSlotStageEnum.equals(TourSlotStageEnum.INACTIVE)) {
            throw new DataValidationException(translator.toLocal("tour_slot.active_and_visitors.present.stage.change.not.permitted"));
        } else if (tourSlotStageEnum.equals(TourSlotStageEnum.INACTIVE)
            && CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {
            existingTourSlot.getTourSlotPersonnelList().clear();
        }

        // Update tour slot stage based on booked guest size
        if (tourSlotModel.getMaxGuestSize() == bookedSlotGuestSize) {
            existingTourSlot.setTourSlotStageEnum(TourSlotStageEnum.BOOKED);
        } else if (tourSlotModel.getMaxGuestSize() / 2 <= bookedSlotGuestSize) {
            existingTourSlot.setTourSlotStageEnum(TourSlotStageEnum.PARTIALLY);
        } else if (ObjectUtils.isNotEmpty(bookedSlotGuestSize) && bookedSlotGuestSize != 0) {
            existingTourSlot.setTourSlotStageEnum(TourSlotStageEnum.ACTIVE);
        } else {
            // Update the TourSlot's stage if none of the above conditions are met
            existingTourSlot.setTourSlotStageEnum(tourSlotStageEnum);
        }
    }

    /**
     * Saves a list of Visit objects and a TourSlot object to their respective repositories.
     *
     * @param visitList The list of Visit objects to be saved.
     * @param tourSlot  The TourSlot object to be saved.
     */
    @Transactional
    public void saveTourSlotAndVisitList(final List<Visit> visitList, final TourSlot tourSlot) {
        // Save the list of Visit objects using the visitRepository's saveAll method
        visitRepository.saveAll(visitList);

        tourSlotRepository.save(tourSlot);
    }

    /**
     * Updates the stage of tour slots within a date range.
     *
     * @param updateTourSlotStageModel The model containing the update parameters.
     * @param siteUUCode               The unique code associated with the site.
     */
    @Transactional
    public void updateTourSlotStage(final UpdateTourSlotStageModel updateTourSlotStageModel, final String siteUUCode) {

        final LocalDateTime currentDateTime = siteService.getCurrentDateTimeFromExistingSite(siteService.findByUUCode(siteUUCode));

        // Create a list to store existing tour slots within the specified date range
        final List<TourSlot> existingTourSlotList = new ArrayList<>();

        // Get the start and end dates from the update model
        LocalDate startDate = updateTourSlotStageModel.getStartDate();
        final LocalDate endDate = updateTourSlotStageModel.getEndDate();

        // Iterate through each day within the date range
        while (startDate.isEqual(endDate) || startDate.isBefore(endDate)) {
            // Query and add tour slots that overlap with the specified time range on each day
            existingTourSlotList.addAll(tourSlotRepository
                .findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
                    startDate.atTime(updateTourSlotStageModel.getStartTime()),
                    startDate.atTime(updateTourSlotStageModel.getEndTime()), siteUUCode));

            // Move to the next day
            startDate = startDate.plusDays(1);
        }

        final TourSlotStageEnum tourSlotStageEnum = TourSlotStageEnum.valueOf(updateTourSlotStageModel.getStage());


        if (!tourSlotStageEnum.isChange()) {
            throw new DataValidationException(translator.toLocal("tour_slot.stage.not_change", tourSlotStageEnum));
        }

        final long bookedSlotGuestSize = tourSlotRepository.countBookedVisitorCountByTourSlotIds(
            existingTourSlotList.stream().map(TourSlot::getTourSlotId).collect(Collectors.toSet()),
            siteUUCode).stream().mapToLong(TourSlotWithVisitorCountModel::getBookedVisitorCount).sum();

        if (ObjectUtils.isNotEmpty(bookedSlotGuestSize) && bookedSlotGuestSize > 0
            && tourSlotStageEnum.equals(TourSlotStageEnum.INACTIVE)) {
            throw new DataValidationException(
                translator.toLocal("tour_slot.active_and_visitors.present.stage.change.not.permitted")
            );
        }

        // Update the stage for all existing tour slots in the list
        existingTourSlotList.forEach(existingTourSlot -> {

            if (existingTourSlot.getStartDateTime().isAfter(currentDateTime)
                && existingTourSlot.getEndDateTime().isAfter(currentDateTime)) {
                if (tourSlotStageEnum.equals(TourSlotStageEnum.INACTIVE)
                    && CollectionUtils.isNotEmpty(existingTourSlot.getTourSlotPersonnelList())) {
                    existingTourSlot.getTourSlotPersonnelList().clear();
                }
                existingTourSlot.setTourSlotStageEnum(tourSlotStageEnum);
            }

        });

        // Save the updated tour slots back to the repository
        tourSlotRepository.saveAll(existingTourSlotList);
    }

    /**
     * Checks if a tour slot is valid for booking based on the tourSlotId and the total number of visitors.
     *
     * @param tourSlotId    The ID of the tour slot to be checked.
     * @param totalVisitors The total number of visitors to be booked for the tour slot.
     * @param siteUUCode    The unique code associated with the site.
     * @return true if the tour slot is valid for booking, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean isTourSlotAvailable(final String tourSlotId, final Integer totalVisitors, final String siteUUCode) {
        return CollectionUtils.isEmpty(checkTourSlotAvailable(tourSlotId, totalVisitors, siteUUCode));
    }

    /**
     * Checks the availability of a tour slot for booking with the given number of visitors.
     * This method verifies whether a tour slot is suitable for booking based on various criteria,
     * such as its active status, available personnel, and the number of visitors already booked.
     *
     * @param tourSlotId    The unique identifier of the tour slot to be checked.
     * @param totalVisitors The total number of visitors who want to book the tour slot.
     * @param siteUUCode    The unique code associated with the site.
     * @return A set of error messages indicating any issues with the tour slot's availability.
     */
    @Transactional(readOnly = true)
    public Set<String> checkTourSlotAvailable(final String tourSlotId, final Integer totalVisitors,
                                              final String siteUUCode) {

        final Set<String> errorMessagesSet = new HashSet<>();

        // Find the existing tour slot by its ID and check if it's active.
        final var existingTourSlot = findByIdAndSiteUUCode(tourSlotId, siteUUCode);

        final List<TourSlotStageEnum> tourSlotStageEnumList = List.of(TourSlotStageEnum.ACTIVE, TourSlotStageEnum.PARTIALLY);
        // Check if the existing tour slot is not active.
        if (!tourSlotStageEnumList.contains(existingTourSlot.getTourSlotStageEnum())) {
            errorMessagesSet.add(translator.toLocal("tour_slot.stage.is", existingTourSlot.getTourSlotStageEnum()));
        }

        // Calculate the total number of visitors already booked for this tour slot.
        final var bookedSlotGuestSize = tourSlotRepository.countBookedVisitorCountByTourSlotIds(
                Set.of(existingTourSlot.getTourSlotId()), siteUUCode).stream()
            .mapToLong(TourSlotWithVisitorCountModel::getBookedVisitorCount).sum();

        // Check if there are enough available slots for the new visitors.
        if ((existingTourSlot.getMaxGuestSize() - bookedSlotGuestSize) < totalVisitors) {
            errorMessagesSet.add(translator.toLocal("tour_slot.available.guest_size.less_than_total_visitor",
                existingTourSlot.getMaxGuestSize() - bookedSlotGuestSize, totalVisitors));
        }

        return errorMessagesSet;
    }

    /**
     * Retrieves paginated tour slots associated with visits based on specified criteria.
     *
     * @param pageNo          The page number for pagination.
     * @param pageSize        The number of items per page.
     * @param sortDirection   The direction for sorting (e.g., "ASC" or "DESC").
     * @param sortProperty    The property by which to sort the results.
     * @param search          A string for searching tour slots.
     * @param startDateTime   Start date and time for filtering tour slots.
     * @param endDateTime     End date and time for filtering tour slots.
     * @param visitStage      The stage of the visit.
     * @param siteUUCode      The unique code associated with the site.
     * @param selfAssignVisit Flag indicating whether to retrieve self-assigned visits only.
     * @param hasVisit        A flag indicating whether to filter Tour Slots with associated visits.
     *                        If true, only Tour Slots with visits are included; if false, all Tour Slots are considered.
     * @return                A PaginatedResponse containing a list of TourSlotModel objects associated with the specified criteria.
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<List<TourSlotModel>> getPaginatedVisitAssociatedTourSlot(final int pageNo, final int pageSize, 
            final String sortDirection, final String sortProperty, final String search, final LocalDateTime startDateTime, 
            final LocalDateTime endDateTime,  final String visitStage, final String siteUUCode, final boolean selfAssignVisit, 
            final boolean hasVisit) {

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
        
        List<Visit> existingVisitListWithSearch = null;

        final List<VisitStageEnum> visitStageEnumList = validationUtils.validateVisitStageEnum(visitStage, VisitTypeEnum.TOUR);

        if (StringUtils.isNotBlank(search) || StringUtils.isNotBlank(visitStage) ||  ObjectUtils.isNotEmpty(startDateTime) 
                || ObjectUtils.isNotEmpty(endDateTime) || StringUtils.isNotBlank(personnelId)) {

            existingVisitListWithSearch = visitService.getTourTypeVisitListByFilter(search, 
                    visitStageEnumList, startDateTime, endDateTime, siteUUCode, personnelId);
        }
        
        final List<Visit> existingVisitListWithoutSearch = visitService.getTourTypeVisitListByFilter(
                null, visitStageEnumList, null, null, siteUUCode, personnelId);

        Set<Object> existingTourSlotIdListWithSearch = null;

        Set<Object> existingTourSlotIdListWithoutSearch = null;

        if (CollectionUtils.isNotEmpty(existingVisitListWithSearch)) {
            existingTourSlotIdListWithSearch = existingVisitListWithSearch.stream().map(
                existingVisit -> existingVisit.getTourSlot().getTourSlotId()).collect(Collectors.toSet());
        }

        if (CollectionUtils.isNotEmpty(existingVisitListWithoutSearch)) {
            existingTourSlotIdListWithoutSearch = existingVisitListWithoutSearch.stream().map(
                existingVisit -> existingVisit.getTourSlot().getTourSlotId()).collect(Collectors.toSet());
        }

        // Build a specification for filtering tour slots based on various criteria.
        final Specification<TourSlot> specification =
            tourSlotSpecification.buildTourSlotSearchFilterSpecification(sortDirection, sortProperty, search, startDateTime, endDateTime,
                existingTourSlotIdListWithSearch, existingTourSlotIdListWithoutSearch, siteUUCode, hasVisit);

        // Retrieve existing tour slots based on the specification and pagination.
        final Page<TourSlot> existingTourSlotPage = tourSlotRepository.findAll(specification, PageRequest.of(pageNo - 1, pageSize));

        // Map the retrieved tour slots to TourSlotModel.
        List<TourSlotModel> tourSlotModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingTourSlotPage.getContent())) {
            tourSlotModelList = tourSlotMapper.tourSlotListToTourSlotModelList(existingTourSlotPage.getContent());

            final Map<String, TourSlotModel> tourSlotModelMap = tourSlotModelList.stream()
                .collect(Collectors.toMap(TourSlotModel::getTourSlotId, tourSlotModel -> tourSlotModel));

            final List<TourSlotWithVisitorCountModel> tourSlotWithVisitorCountModelList =
                tourSlotRepository.countBookedVisitorCountByTourSlotIds(tourSlotModelMap.keySet(), siteUUCode);

            tourSlotWithVisitorCountModelList.forEach(tourSlotWithVisitorCountModel -> tourSlotModelMap
                .get(tourSlotWithVisitorCountModel.getTourSlotId()).setBookedGuestSize(
                    tourSlotWithVisitorCountModel.getBookedVisitorCount()));
        }

        // Calculate pagination details and prepare the response.
        return CommonUtils.calculatePaginationAndPrepareResponse(existingTourSlotPage, tourSlotModelList);
    }

}
