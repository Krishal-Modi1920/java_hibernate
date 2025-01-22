package org.baps.api.vtms.annotations.validator;


import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.common.utils.Translator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The {@code EnumValueValidator} class is a constraint validator used with the {@link EnumValue} annotation
 * to validate whether a field or parameter has a value that belongs to a specified enumeration class.
 *
 * <p>The validator checks if the annotated element's value is a valid enumeration value within the given
 * enumeration class, and it provides support for both single values and lists of values.
 *
 * @see EnumValue
 * @see jakarta.validation.ConstraintValidator
 * @see jakarta.validation.ConstraintValidatorContext
 */
@Component
@RequiredArgsConstructor
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private final Translator translator;
    
    private Enum<?>[] enumValues;

    /**
     * Initializes the validator with the enumeration class provided in the {@code EnumValue} annotation.
     *
     * @param enumValue The {@code EnumValue} annotation instance
     */
    @Override
    public void initialize(final EnumValue enumValue) {
        enumValues = enumValue.enumClass().getEnumConstants();
    }

    /**
     * Validates whether the value of the annotated element is a valid enumeration value.
     *
     * <p>The validation supports different data types, including single values and lists of values.
     *
     * @param value   The value to be validated
     * @param context The validation context
     * @return {@code true} if the value is valid, {@code false} if it is not
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true; // Null values are considered valid
        }

        if (value instanceof CharSequence charSequence) {
            return isValidSingleValue(charSequence, context);

        } else if (value instanceof List<?> valueList) {
            return isValidList(valueList, context);
        }

        return false; // Invalid type
    }

    /**
     * Validates a single value for being a valid enumeration value.
     *
     * @param value   The single value to be validated
     * @param context The validation context
     * @return {@code true} if the value is valid, {@code false} if it is not
     */
    private boolean isValidSingleValue(final CharSequence value, final ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    translator.toLocal("invalid.enum.value", Arrays.toString(enumValues))
            ).addConstraintViolation();

            for (Enum<?> enumValue : enumValues) {
                if (enumValue.name().equals(value.toString())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Validates a list of values for being valid enumeration values.
     *
     * @param valueList The list of values to be validated
     * @param context   The validation context
     * @return {@code true} if all values in the list are valid, {@code false} if at least one is not
     */
    private boolean isValidList(final List<?> valueList, final ConstraintValidatorContext context) {
        for (Object listItem : valueList) {
            if (!(listItem instanceof CharSequence)) {
                return false; // Invalid list element type
            }

            if (!isValidSingleValue((CharSequence) listItem, context)) {
                return false; // At least one element is invalid
            }
        }
        return true;
    }
}

