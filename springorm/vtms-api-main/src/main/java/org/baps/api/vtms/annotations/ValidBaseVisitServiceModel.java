package org.baps.api.vtms.annotations;

import org.baps.api.vtms.annotations.validator.BaseVisitServiceModelValidator;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = BaseVisitServiceModelValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBaseVisitServiceModel {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    ServiceTypeEnum serviceTag();
}
