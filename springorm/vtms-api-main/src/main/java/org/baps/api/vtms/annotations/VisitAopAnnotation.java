package org.baps.api.vtms.annotations;

import org.baps.api.vtms.enumerations.RoleTagEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitAopAnnotation {

    RoleTagEnum[] roleTags() default {};
}
