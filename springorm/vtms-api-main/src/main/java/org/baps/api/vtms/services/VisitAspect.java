package org.baps.api.vtms.services;

import org.baps.api.vtms.annotations.VisitAopAnnotation;
import org.baps.api.vtms.enumerations.PermissionEnum;
import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.responses.APIResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@Component
@Aspect
@RequiredArgsConstructor
public class VisitAspect {

    private final VisitService visitService;

    private final AuthService authService;
    
    @Pointcut("@annotation(visitAopAnnotation) && args(siteUUCode, visitId, ..)")
    public void visitMethodsPointcut(final VisitAopAnnotation visitAopAnnotation, final String siteUUCode, final String visitId) {
    }

    @Around(value = "visitMethodsPointcut(visitAopAnnotation, siteUUCode, visitId)")
    public ResponseEntity<APIResponse> aroundVisitMethod(final ProceedingJoinPoint joinPoint, final VisitAopAnnotation visitAopAnnotation, 
            final String siteUUCode, final String visitId) throws Throwable {

        // Retrieve the role tag list corresponding to the intercepted method
        final List<RoleTagEnum> roleTagList = List.of(visitAopAnnotation.roleTags());

        // Obtain updated role tags based on the intercepted method
        final Set<RoleTagEnum> existingRoleTags = visitService.findFilteredRoleTagsFromVisitByRoleTagList(visitId, siteUUCode, roleTagList);

        // Proceed with the intercepted method execution
        final ResponseEntity<?> responseEntity = (ResponseEntity<?>) joinPoint.proceed();

        // Obtain existing role tags after the method execution
        final Set<RoleTagEnum> updatedRoleTags = visitService.findFilteredRoleTagsFromVisitByRoleTagList(visitId, siteUUCode, roleTagList);

        // Perform system permission check and return the result
        return authService.checkVisitPermissionAndCompareRoleTags(visitId, siteUUCode, PermissionEnum.VIEW_VISIT_ALL_LIST, 
                responseEntity.getBody(), HttpStatus.valueOf(responseEntity.getStatusCode().value()), existingRoleTags, updatedRoleTags);
    }
}
