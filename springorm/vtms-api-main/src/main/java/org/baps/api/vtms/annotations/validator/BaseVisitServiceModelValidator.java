package org.baps.api.vtms.annotations.validator;

import org.baps.api.vtms.annotations.ValidBaseVisitServiceModel;
import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.enumerations.LocationTagEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.models.BaseVisitServiceModel;
import org.baps.api.vtms.models.VisitLocationModel;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;

@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP"})
public class BaseVisitServiceModelValidator implements ConstraintValidator<ValidBaseVisitServiceModel, BaseVisitServiceModel> {

    private static final String VISIT_LOCATION_MODEL_LIST = "visitLocationModelList";

    private final CommonUtils commonUtils;

    @Override
    public boolean isValid(final BaseVisitServiceModel baseVisitServiceModel, final ConstraintValidatorContext context) {
        boolean isValid = true;

        if (CollectionUtils.isNotEmpty(baseVisitServiceModel.getVisitLocationModelList())) {

            if (checkVisitLocationTimingConflict(baseVisitServiceModel.getVisitLocationModelList())) {
                // Check if there are timing conflicts between visit locations.
                isValid = false;
                commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST, "location.time.conflict");
            }

            if (checkVisiPickupLocationTimeGreaterThanDropLocation(baseVisitServiceModel.getVisitLocationModelList())) {
                // Check if there are timing conflicts between visit locations.
                isValid = false;
                commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST, "drop_location.before.pickup_location");
            }

            if (isInvalidValidVisitLocationsTimeRange(baseVisitServiceModel.getStartDateTime(),
                baseVisitServiceModel.getEndDateTime(), baseVisitServiceModel.getVisitLocationModelList())) {
                // Check if visit location timings are within the visit tour's timings.
                isValid = false;
                
                if (baseVisitServiceModel.getServiceType().equals(ServiceTypeEnum.TOUR.name())) { 
                    commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST,
                            "visitLocationModelList.date_time.shoud_be.with_in.tour");
                } else if (baseVisitServiceModel.getServiceType().equals(ServiceTypeEnum.SERVICE.name())) { 
                    commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST,
                            "visitLocationModelList.date_time.shoud_be.with_in.service");
                } else if (baseVisitServiceModel.getServiceType().equals(ServiceTypeEnum.MEETING.name())) { 
                    commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST,
                            "visitLocationModelList.date_time.shoud_be.with_in.meeting");
                } else {
                    commonUtils.addErrorToContext(context, "", "invalid.service_type");
                }
            }

        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
        }

        return isValid;
    }


    /**
     * Checks if visit location timings are within a specified time range.
     *
     * @param visitLocationModelList The list of VisitLocationModel objects to check.
     * @param startDateTime          The startDateTime of the visit tour.
     * @param endDateTime            The endDateTime of the visit tour.
     * @return true if visit location timings are valid; false otherwise.
     */
    private boolean isInvalidValidVisitLocationsTimeRange(final LocalDateTime startDateTime,
                                                          final LocalDateTime endDateTime,
                                                          final List<VisitLocationModel> visitLocationModelList) {

        return visitLocationModelList.stream().anyMatch(
            visitLocationModel -> visitLocationModel.getStartDateTime().isBefore(startDateTime)
                || visitLocationModel.getEndDateTime().isAfter(endDateTime));
    }

    /**
     * Checks for timing conflicts between visit locations.
     *
     * @param visitLocationModelList The list of VisitLocationModel objects to check for conflicts.
     * @return true if there are no timing conflicts; false otherwise.
     */
    private boolean checkVisitLocationTimingConflict(final List<VisitLocationModel> visitLocationModelList) {

        return visitLocationModelList.stream()
            .anyMatch(location1 ->
                visitLocationModelList.stream()
                    .anyMatch(location2 ->
                        !location1.equals(location2) && isConflict(location1, location2)));
    }

    /**
     * Checks if any pickup location's time is greater than any drop location's time.
     *
     * @param visitLocationModelList A list of visit location models to be checked
     * @return {@code true} if any pickup location's time is greater than any drop location's time, {@code false} otherwise
     */
    private boolean checkVisiPickupLocationTimeGreaterThanDropLocation(final List<VisitLocationModel> visitLocationModelList) {
        
        return visitLocationModelList.stream()
                .filter(visitLocationModel ->
                        visitLocationModel.getLocationTagEnum().equals(LocationTagEnum.PICKUP.name()))
                .anyMatch(pickUpLocation ->
                        visitLocationModelList.stream()
                                .filter(visitLocationModel ->
                                        visitLocationModel.getLocationTagEnum().equals(LocationTagEnum.DROP.name()))
                                .anyMatch(dropLocation ->
                                isVisiPickupLocationTimeGreaterThanDropLocation(pickUpLocation, dropLocation))
                );
    }
    
    /**
     * Checks if the start time of the pickup location is after the start time of the drop location or if the end time of the
     * pickup location is after the start time of the drop location.
     *
     * @param pickUpLocation The pickup location to compare.
     * @param dropLocation   The drop location to compare.
     * @return {@code true} if the pickup location's start time is after the drop location's start time or if the pickup
     *         location's end time is after the drop location's start time, {@code false} otherwise.
     */
    private boolean isVisiPickupLocationTimeGreaterThanDropLocation(final VisitLocationModel pickUpLocation, 
            final VisitLocationModel dropLocation) {

        return pickUpLocation.getStartDateTime().isAfter(dropLocation.getStartDateTime()) 
                || pickUpLocation.getEndDateTime().isAfter(dropLocation.getStartDateTime());
    }

    /**
     * Checks if there is a timing conflict between two VisitLocationModel objects.
     *
     * @param location1 The first VisitLocationModel.
     * @param location2 The second VisitLocationModel.
     * @return true if there is a timing conflict; false otherwise.
     */
    private boolean isConflict(final VisitLocationModel location1, final VisitLocationModel location2) {

        return location1.getStartDateTime().isBefore(location2.getEndDateTime())
            && location1.getEndDateTime().isAfter(location2.getStartDateTime());
    }
}
