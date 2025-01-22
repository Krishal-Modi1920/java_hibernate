package org.baps.api.vtms.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.baps.api.vtms.annotations.validator.DateTimeRangeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The {@code DateTimeRange} annotation is used to specify that the annotated class or annotation
 * type represents a range of date and time. It can be applied to classes or annotation types and
 * defines two fields for specifying the start and end date-time values of a range.
 *
 * <p>The {@code DateTimeRange} annotation is used in conjunction with the {@code DateTimeRangeValidator}
 * to validate that the start date-time is before or equal to the end date-time. The validation is performed
 * based on the fields specified in the annotation, which are "startDateTimeField" and "endDateTimeField".
 *
 * <p>For example, you can annotate a class with {@code DateTimeRange} like this:
 * <pre>
 * {@code @DateTimeRange(startDateTimeField = "startDate", endDateTimeField = "endDate")}
 * public class Event {
 *     private LocalDateTime startDate;
 *     private LocalDateTime endDate;
 * }
 * </pre>
 *
 * <p>In this example, the {@code DateTimeRangeValidator} will ensure that the "startDate" is before or equal
 * to the "endDate".
 *
 * <p>The annotation supports message customization through the "message" attribute. It also allows you
 * to specify validation groups and payloads using the "groups" and "payload" attributes, respectively.
 *
 * @see DateTimeRangeValidator
 * @see jakarta.validation.Constraint
 * @see jakarta.validation.Payload
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateTimeRangeValidator.class)
@Documented
public @interface DateTimeRange {

    /**
     * Returns the error message template.
     *
     * @return the error message template
     */
    String message() default "{something_went_wrong}";


    /**
     * Returns the validation groups this constraint belongs to.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns the payload associated with the constraint.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Returns the name of the field that represents the start date-time.
     *
     * @return the name of the start date-time field
     */
    String startDateTimeField() default "startDateTime";

    /**
     * Returns the name of the field that represents the end date-time.
     *
     * @return the name of the end date-time field
     */
    String endDateTimeField() default "endDateTime";

    /**
     * Defines a container annotation type for multiple {@code DateTimeRange} annotations.
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * Returns an array of {@code DateTimeRange} annotations.
         *
         * @return an array of DateTimeRange annotations
         */
        DateTimeRange[] value();
    }
    
    /**
     * Returns the name of the field that represents the location Tag.
     *
     * @return the name of the location Tag field
     */
    String locationTagEnum() default "";
}