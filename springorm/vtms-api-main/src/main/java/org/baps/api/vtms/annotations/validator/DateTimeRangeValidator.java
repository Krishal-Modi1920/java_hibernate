package org.baps.api.vtms.annotations.validator;


import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.enumerations.LocationTagEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The {@code DateTimeRangeValidator} class is a constraint validator used in conjunction with
 * the {@link DateTimeRange} annotation to validate date-time ranges in objects. It checks that
 * the start date-time is before or equal to the end date-time based on the fields specified in
 * the {@code DateTimeRange} annotation.
 *
 * <p>The validator is applied to objects to ensure that the date-time range is valid. It is typically
 * used with JavaBeans where the start and end date-time fields are defined and annotated with the
 * {@code DateTimeRange} annotation.
 *
 * @see DateTimeRange
 * @see jakarta.validation.ConstraintValidator
 * @see jakarta.validation.ConstraintValidatorContext
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, Object> {

    private final Translator translator;

    private String startField;
    private String endField;
    private String locationTagField;

    /**
     * Initializes the validator with the configuration from the {@code DateTimeRange} annotation.
     *
     * @param constraintAnnotation The {@code DateTimeRange} annotation instance
     */
    @Override
    public void initialize(final DateTimeRange constraintAnnotation) {
        startField = constraintAnnotation.startDateTimeField();
        endField = constraintAnnotation.endDateTimeField();
        locationTagField = constraintAnnotation.locationTagEnum();
    }

    /**
     * Validates the date-time range within the given object.
     *
     * @param object   The object to be validated
     * @param context  The validation context
     * @return {@code true} if the date-time range is valid, {@code false} otherwise
     */
    @Override
    public boolean isValid(final Object object, final ConstraintValidatorContext context) {
        try {
            final Field startFieldObj = object.getClass().getDeclaredField(startField);
            final Field endFieldObj = object.getClass().getDeclaredField(endField);
            
            startFieldObj.setAccessible(true);
            endFieldObj.setAccessible(true);
            
            final LocalDateTime startDatetime = (LocalDateTime) startFieldObj.get(object);
            final LocalDateTime endDatetime = (LocalDateTime) endFieldObj.get(object);
            
            Field locationTagFieldObj = null;
            String locationTag = null;
            final List<String> locationTagEnumList = List.of(LocationTagEnum.PICKUP.name(), LocationTagEnum.DROP.name());
            
            if (StringUtils.isNotBlank(locationTagField)) {
                locationTagFieldObj = object.getClass().getDeclaredField(locationTagField);
                locationTagFieldObj.setAccessible(true);
                locationTag = (String) locationTagFieldObj.get(object);
            }
            
            if ((StringUtils.isEmpty(locationTag) || !locationTagEnumList.contains(locationTag)) 
                    && ObjectUtils.isNotEmpty(startDatetime) && ObjectUtils.isNotEmpty(endDatetime)
                    && !(startDatetime.isBefore(endDatetime))) {

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        translator.toLocal("date_time.before", startField, endField)
                        ).addConstraintViolation();

                return false;
            } else if (StringUtils.isNotEmpty(locationTag) && locationTagEnumList.contains(locationTag) 
                    && ObjectUtils.isNotEmpty(startDatetime) && ObjectUtils.isNotEmpty(endDatetime)
                    && !(startDatetime.isBefore(endDatetime) || startDatetime.isEqual(endDatetime))) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        translator.toLocal("date_time.equal_or_before", locationTag, startField, endField)
                        ).addConstraintViolation();
                return false;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getLocalizedMessage());
            throw new IllegalArgumentException(translator.toLocal("invalid.datetime.field_name"));
        }

        return true;
    }
}