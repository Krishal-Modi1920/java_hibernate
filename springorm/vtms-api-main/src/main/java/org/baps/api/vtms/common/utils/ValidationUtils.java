package org.baps.api.vtms.common.utils;


import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.models.VisitModel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Component
@RequiredArgsConstructor
public class ValidationUtils {
    
    private final Translator translator;
    
    /**
     * Validates and converts a comma-separated string of visit stages into a List of VisitStageEnum based on the provided VisitTypeEnum.
     *
     * @param visitStage A comma-separated string of visit stages to be validated.
     * @param visitTypeEnum The type of visit (VISIT or TOUR) to determine the valid visit stages.
     * @return A List of VisitStageEnum representing the validated visit stages.
     * @throws DataNotFoundException If an invalid visit stage is encountered.
     */
    public List<VisitStageEnum> validateVisitStageEnum(final String visitStage, final VisitTypeEnum visitTypeEnum) {
        // Set to store valid visit stages
        Set<String> validVisitStages = new HashSet<>();

        // Determine valid visit stages based on the visit type
        if (visitTypeEnum.equals(VisitTypeEnum.VISIT)) {
            validVisitStages = Arrays.stream(VisitStageEnum.values())
                .map(VisitStageEnum::name)
                .collect(Collectors.toSet());
        } else if (visitTypeEnum.equals(VisitTypeEnum.TOUR)) {
            validVisitStages = new HashSet<>();
            validVisitStages.add(VisitStageEnum.ACCEPTED.name());
            validVisitStages.add(VisitStageEnum.COMPLETED.name());
            validVisitStages.add(VisitStageEnum.CANCELLED.name());
            validVisitStages.add(VisitStageEnum.NOSHOW.name());
        }

        // List to store the converted visit stages
        final List<VisitStageEnum> visitStageEnumList = new ArrayList<>();

        // Validate and convert each visit stage
        if (StringUtils.isNotBlank(visitStage)) {
            final String[] visitStages = visitStage.split(",");
            for (String stage : visitStages) {
                if (validVisitStages.contains(stage)) {
                    visitStageEnumList.add(VisitStageEnum.valueOf(stage));
                } else {
                    // Throw exception for invalid visit stage
                    throw new DataNotFoundException(translator.toLocal("invalid.enum.value", validVisitStages));
                }
            }
        }
        return visitStageEnumList;
    }
    
    /**
     * Validates and converts a string representation of a visit type to the corresponding {@link VisitTypeEnum}.
     *
     * @param visitType The string representation of the visit type to validate.
     * @return The {@link VisitTypeEnum} value corresponding to the provided string.
     * @throws DataNotFoundException If the provided visit type is not a valid enum constant, a {@link DataNotFoundException} is thrown.
     *                              The exception message indicates that the visit type should be one of the valid enum values.
     */
    public VisitTypeEnum validateVisitType(final String visitType) {
        // If the provided visit type is valid, return the corresponding enum value
        if (EnumUtils.isValidEnum(VisitTypeEnum.class, visitType)) {
            return VisitTypeEnum.valueOf(visitType);
        } else {
            throw new DataNotFoundException(translator.toLocal("invalid.enum.value", (Object[]) VisitTypeEnum.values()));
        }
    }

    /**
     * Validates the point of contact enum value in the given VisitModel.
     * 
     * @param visitModel The VisitModel to validate.
     * @throws DataNotFoundException If the point of contact enum value is invalid.
     */
    public void validatePointOfContactEnum(final VisitModel visitModel) {
        // Set to store valid point of contact enum values
        final Set<String> validPointOfContacts = new HashSet<>();

        // Add primary point of contact enum value
        validPointOfContacts.add(VisitorContactTypeEnum.PRIMARY.name());
        
        // Add secondary point of contact enum value if secondary visitor model is not empty
        if (ObjectUtils.isNotEmpty(visitModel.getSecondaryVisitorModel())) {
            validPointOfContacts.add(VisitorContactTypeEnum.SECONDARY.name());
        }
        
        // Check if the point of contact in the visitModel is valid
        if (!validPointOfContacts.contains(visitModel.getPointOfContact())) {
            // If not valid, throw a DataNotFoundException
            throw new DataNotFoundException(translator.toLocal("invalid.enum.value", validPointOfContacts));
        }
         
    }
}