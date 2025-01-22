package org.baps.api.vtms.annotations;

import org.baps.api.vtms.annotations.validator.UniquePersonnelIdForRoleIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniquePersonnelIdForRoleIdValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePersonnelIdForRoleId {

    String message() default "{duplicate.personnel_ids.with.role_id}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

