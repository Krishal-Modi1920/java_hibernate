package org.baps.api.vtms.annotations;

import org.baps.api.vtms.annotations.validator.EnumValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The {@code EnumValue} annotation is used to specify that the annotated field or parameter should
 * have a value that is part of the specified enumeration class. It is typically used to enforce that
 * only valid enumeration values are allowed for a particular field or parameter.
 *
 * <p>The annotation is applied to fields and parameters, and it requires the specification of the
 * enumeration class using the "enumClass" attribute. The validator, {@link EnumValueValidator}, checks
 * if the value of the annotated element is a valid enumeration value.
 *
 * <p>For example, you can annotate a field like this:
 * <pre>
 * {@code @EnumValue(enumClass = MyEnum.class)}
 * private String status;
 * </pre>
 * This ensures that the "status" field can only have values that belong to the "MyEnum" enumeration.
 *
 * @see EnumValueValidator
 * @see jakarta.validation.Constraint
 * @see jakarta.validation.Payload
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {

    /**
     * Returns the error message template for invalid enum values.
     *
     * @return the error message template
     */
    String message() default "{invalid.enum.value}";

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
     * Returns the class of the enumeration to be used for validation.
     *
     * @return the enumeration class
     */
    Class<? extends Enum<?>> enumClass();
}
