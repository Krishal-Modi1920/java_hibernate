package org.baps.api.vtms.annotations.validator;

import org.baps.api.vtms.annotations.UniquePersonnelIdForRoleId;
import org.baps.api.vtms.models.VisitPersonnelModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniquePersonnelIdForRoleIdValidator
        implements ConstraintValidator<UniquePersonnelIdForRoleId, List<VisitPersonnelModel>> {

    @Override
    public boolean isValid(final List<VisitPersonnelModel> visitPersonnelModelList, final ConstraintValidatorContext context) {
        if (visitPersonnelModelList == null) {
            return true; // Null list is considered valid
        }

        final Map<String, Long> counts = visitPersonnelModelList.stream()
                .collect(Collectors.groupingBy(
                        model -> model.getRoleId() + "-" + model.getPersonnelId(),
                        Collectors.counting()
                ));

        return counts.values().stream().noneMatch(count -> count > 1);
    }
}

