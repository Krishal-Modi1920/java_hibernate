package org.baps.api.vtms.annotations.validator;

import org.baps.api.vtms.annotations.ValidVisitTourModel;
import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.enumerations.LocationTagEnum;
import org.baps.api.vtms.models.BaseVisitServiceModel;
import org.baps.api.vtms.models.VisitTourModel;

import lombok.RequiredArgsConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP"})
public class VisitTourModelValidator implements ConstraintValidator<ValidVisitTourModel, VisitTourModel> {

    private static final String VISIT_LOCATION_MODEL_LIST = "visitLocationModelList";

    private final CommonUtils commonUtils;

    @Override
    public boolean isValid(final VisitTourModel value, final ConstraintValidatorContext context) {
        boolean isValid = true;

        if (CollectionUtils.isNotEmpty(value.getVisitLocationModelList())) {
            isValid = isValidVisitTourModel(context, value);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
        }

        return isValid;
    }

    /**
     * Validates the tour model to ensure that it contains exactly one pickup location and exactly one drop location.
     *
     * @param context          The constraint validator context.
     * @param visitServiceModel The BaseVisitServiceModel to validate.
     * @return {@code true} if the tour model is valid, {@code false} otherwise.
     */
    private boolean isValidVisitTourModel(final ConstraintValidatorContext context, final BaseVisitServiceModel visitServiceModel) {

        boolean isValid = true;

        // Count the number of pickup locations in the visit location list.
        final long pickupLocationCount = visitServiceModel.getVisitLocationModelList().stream()
            .filter(visitLocationModel -> StringUtils.isNotBlank(visitLocationModel.getLocationTagEnum())
                && visitLocationModel.getLocationTagEnum().equals(LocationTagEnum.PICKUP.name())).count();

        if (pickupLocationCount != 1) {
            // If the number of pickup locations is not exactly one, mark the model as invalid.
            isValid = false;
            commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST, "exactly_one.location.required",
                    LocationTagEnum.PICKUP.name());
        }

        // Count the number of drop locations in the visit location list.
        final long dropLocationCount = visitServiceModel.getVisitLocationModelList().stream()
            .filter(visitLocationModel -> StringUtils.isNotBlank(visitLocationModel.getLocationTagEnum())
                && visitLocationModel.getLocationTagEnum().equals(LocationTagEnum.DROP.name())).count();

        if (dropLocationCount != 1) {
            // If the number of drop locations is not exactly one, mark the model as invalid.
            isValid = false;
            commonUtils.addErrorToContext(context, VISIT_LOCATION_MODEL_LIST, "exactly_one.location.required", LocationTagEnum.DROP.name());
        }

        return isValid;
    }

}
