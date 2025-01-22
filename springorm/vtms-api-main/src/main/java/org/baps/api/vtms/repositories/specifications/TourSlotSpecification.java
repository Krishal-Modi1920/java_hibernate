package org.baps.api.vtms.repositories.specifications;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.SpecificationConstants;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.TourSlot;
import org.baps.api.vtms.models.entities.TourSlotPersonnel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


@RequiredArgsConstructor
@Component
public class TourSlotSpecification {

    private Set<String> sortingProperties = Set.of(
        SpecificationConstants.START_DATE_TIME,
        SpecificationConstants.UPDATED_AT
    );

    private final Translator translator;

    /**
     * Builds a Specification for filtering TourSlot entities based on search criteria and visit IDs.
     *
     * @param sortDirection               The direction for sorting (e.g., "ASC" or "DESC").
     * @param sortProperty                The property by which to sort the results.
     * @param search                      Search criteria for filtering tour slots.
     * @param startDateTime               Start date and time filter for tour slots.
     * @param endDateTime                 End date and time filter for tour slots.
     * @param tourSlotIdListWithSearch    Set of tourSlot IDs for additional filtering.
     * @param tourSlotIdListWithoutSearch Set of tourSlot IDs for additional filtering.
     * @param siteUUCode                  The unique code associated with the site filter for tour slots.
     * @param hasVisit                    A flag indicating whether to filter Tour Slots with associated visits.
     *                                    If true, only Tour Slots with visits are included; if false, all Tour Slots are considered.
     * @return Specification for filtering TourSlot entities.
     */
    public Specification<TourSlot> buildTourSlotSearchFilterSpecification(final String sortDirection, final String sortProperty,
                                                                          final String search, final LocalDateTime startDateTime,
                                                                          final LocalDateTime endDateTime,
                                                                          final Set<Object> tourSlotIdListWithSearch,
                                                                          final Set<Object> tourSlotIdListWithoutSearch,
                                                                          final String siteUUCode, 
                                                                          final boolean hasVisit) {

        return Specification.where(buildSortingSpecification(sortDirection, sortProperty))
            .and(buildTourSlotFilterSpecification(startDateTime, endDateTime, tourSlotIdListWithoutSearch, hasVisit))
            .and(GenericSpecification.hasSiteCode(siteUUCode))
            .and(buildTourSlotSearchSpecification(search))
            .or(buildTourSlotFilterSpecificationForAssociatedVisit(tourSlotIdListWithSearch));
    }

    /**
     * Builds a Specification for sorting TourSlot entities based on the provided sort direction and property.
     *
     * @param sortDirection The sorting direction ("ASC" for ascending, "DESC" for descending).
     * @param sortProperty  The property by which to sort the entities.
     * @return A Specification for sorting TourSlot entities.
     */
    private Specification<TourSlot> buildSortingSpecification(final String sortDirection, final String sortProperty) {
        return (root, query, criteriaBuilder) -> {
            // Default sorting direction is ascending if not specified
            Sort.Direction direction = Sort.Direction.ASC;

            if (StringUtils.isNotBlank(sortDirection)) {
                direction = Sort.Direction.valueOf(sortDirection);
            }

            Expression<?> propertyPath = null;

            // Determine the property path to use for sorting
            if (StringUtils.isBlank(sortProperty)) {
                propertyPath = GenericSpecification.getPropertyPath(SpecificationConstants.START_DATE_TIME, root);
            } else if (CommonUtils.isValidProperty(sortProperty, sortingProperties)) {
                throw new DataValidationException(translator.toLocal("invalid.sort.property", sortingProperties));
            } else {
                propertyPath = GenericSpecification.getPropertyPath(sortProperty, root);
            }

            // Apply sorting to the query
            query.orderBy(direction == Sort.Direction.ASC ? criteriaBuilder.asc(propertyPath) : criteriaBuilder.desc(propertyPath));

            return null;
        };
    }


    /**
     * This method builds a Specification for filtering TourSlots based on associated Visit IDs.
     * It uses a set of TourSlot IDs to create a Specification that filters TourSlots having IDs in the provided set.
     *
     * @param tourSlotIdListWithSearch A set of TourSlot IDs to filter the associated TourSlots.
     * @return The Specification for filtering TourSlots based on the provided set of TourSlot IDs.
     */
    private Specification<TourSlot> buildTourSlotFilterSpecificationForAssociatedVisit(final Set<Object> tourSlotIdListWithSearch) {

        // Build the Specification using a lambda expression.
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(tourSlotIdListWithSearch)) {
                return GenericSpecification.getPropertyInPredicate(root,
                    SpecificationConstants.TOUR_SLOT_ID, tourSlotIdListWithSearch);
            } else {
                return null;
            }
        };
    }

    /**
     * Builds a Specification for searching TourSlot entities based on a search string and visit IDs.
     * If the search string is blank, no specific filtering is applied.
     *
     * @param search Search criteria for filtering tour slots.
     * @return Specification for searching TourSlot entities.
     */
    private Specification<TourSlot> buildTourSlotSearchSpecification(final String search) {
        if (StringUtils.isBlank(search)) {
            return null;  // If the search string is blank, return null to indicate no specific filtering is needed.
        }

        // Build the Specification using a lambda expression.
        return (root, query, criteriaBuilder) -> {

            final List<Predicate> orPredicateList = new ArrayList<>();

            // Add predicates for searching by tour guide personnel full name and phone number.
            orPredicateList.add(hasTourGuidePersonnelFullNameLike(criteriaBuilder, root, search));

            orPredicateList.add(hasTourGuidePersonnelPhoneNumberLike(criteriaBuilder, root, search));

            return criteriaBuilder.or(orPredicateList.toArray(Predicate[]::new));
        };
    }

    /**
     * Creates a Predicate for filtering TourSlot entities based on a search string matching the full name
     * of associated tour guide personnel.
     *
     * @param criteriaBuilder CriteriaBuilder for building predicates.
     * @param root            Root entity (TourSlot) for the query.
     * @param search          Search criteria for filtering by full name.
     * @return Predicate for filtering TourSlot entities based on full name.
     */
    private Predicate hasTourGuidePersonnelFullNameLike(final CriteriaBuilder criteriaBuilder,
                                                        final Root<TourSlot> root, final String search) {

        // Prepare the search string for case-insensitive matching.
        final String searchString = "%" + search.toLowerCase() + "%";

        // Create joins to navigate to the relevant entities.
        final Join<TourSlot, TourSlotPersonnel> tourSlotLeftJoinTourSlotPersonnel =
            root.join(SpecificationConstants.TOUR_SLOT_PERSONNEL_LIST,
                JoinType.LEFT);

        final Join<TourSlotPersonnel, Personnel> tourSlotPersonnelLeftJoinPersonnel =
            tourSlotLeftJoinTourSlotPersonnel.join(SpecificationConstants.PERSONNEL, JoinType.LEFT);

        // Build the Predicate using CriteriaBuilder operations.
        return criteriaBuilder.and(
            criteriaBuilder.notEqual(tourSlotLeftJoinTourSlotPersonnel.get(SpecificationConstants.STATUS),
                Status.DELETED),
            criteriaBuilder.notEqual(tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.STATUS),
                Status.DELETED),
            criteriaBuilder.like(
                criteriaBuilder.concat(
                    criteriaBuilder.concat(
                        criteriaBuilder.lower(
                            tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.FIRST_NAME)
                                .as(String.class)), " "),
                    criteriaBuilder.lower(tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.LAST_NAME)
                        .as(String.class))
                ),
                searchString
            )
        );
    }

    /**
     * Creates a Predicate for filtering TourSlot entities based on a search string matching the phone number
     * of associated tour guide personnel.
     *
     * @param criteriaBuilder CriteriaBuilder for building predicates.
     * @param root            Root entity (TourSlot) for the query.
     * @param search          Search criteria for filtering by phone number.
     * @return Predicate for filtering TourSlot entities based on phone number.
     */
    private Predicate hasTourGuidePersonnelPhoneNumberLike(final CriteriaBuilder criteriaBuilder,
                                                           final Root<TourSlot> root, final String search) {

        // Prepare the search string for case-insensitive matching.
        final String searchString = "%" + search.toLowerCase() + "%";

        // Create joins to navigate to the relevant entities.
        final Join<TourSlot, TourSlotPersonnel> tourSlotLeftJoinTourSlotPersonnel =
            root.join(SpecificationConstants.TOUR_SLOT_PERSONNEL_LIST,
                JoinType.LEFT);

        final Join<TourSlotPersonnel, Personnel> tourSlotPersonnelLeftJoinPersonnel =
            tourSlotLeftJoinTourSlotPersonnel.join(SpecificationConstants.PERSONNEL, JoinType.LEFT);

        // Build the Predicate using CriteriaBuilder operations.
        return criteriaBuilder.and(
            criteriaBuilder.notEqual(tourSlotLeftJoinTourSlotPersonnel.get(SpecificationConstants.STATUS),
                Status.DELETED),
            criteriaBuilder.notEqual(tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.STATUS),
                Status.DELETED),
            criteriaBuilder.like(
                criteriaBuilder.concat(
                    criteriaBuilder.concat(
                        criteriaBuilder.lower(
                            tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.PHONE_COUNTRY_CODE)
                                .as(String.class)), " "),
                    criteriaBuilder.lower(tourSlotPersonnelLeftJoinPersonnel.get(SpecificationConstants.PHONE_NUMBER)
                        .as(String.class))
                ),
                searchString
            )
        );
    }

    /**
     * Builds a Specification for filtering TourSlot entities based on date-time ranges.
     *
     * @param startDateTime               Start date and time filter for tour slots.
     * @param endDateTime                 End date and time filter for tour slots.
     * @param tourSlotIdListWithoutSearch tourSlotIdListWithoutSearch filter for tour slots.
     * @param hasVisit                    A flag indicating whether to filter Tour Slots with associated visits.
     *                                    If true, only Tour Slots with visits are included; if false, all Tour Slots are considered.
     * @return Specification for filtering TourSlot entities based on date-time ranges.
     */
    private Specification<TourSlot> buildTourSlotFilterSpecification(final LocalDateTime startDateTime,
                                                                     final LocalDateTime endDateTime,
                                                                     final Set<Object> tourSlotIdListWithoutSearch, 
                                                                     final boolean hasVisit) {

        // Build the Specification using a lambda expression.
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> andPredicateList = new ArrayList<>();

            // Check if both startDateTime and endDateTime are provided.
            if (ObjectUtils.isNotEmpty(startDateTime) && ObjectUtils.isNotEmpty(endDateTime)) {
                // Create a predicate for overlapping date-time range.
                andPredicateList.add(
                    criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.START_DATE_TIME, root)
                                .as(LocalDateTime.class), endDateTime),
                        criteriaBuilder.greaterThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.END_DATE_TIME, root)
                                .as(LocalDateTime.class), startDateTime)
                    )
                );
            } else if (ObjectUtils.isNotEmpty(startDateTime)) {
                // Create a predicate for startDateTime being greater than or equal to either start or end date-time.
                andPredicateList.add(
                    criteriaBuilder.or(
                        criteriaBuilder.greaterThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.START_DATE_TIME, root)
                                .as(LocalDateTime.class), startDateTime),
                        criteriaBuilder.greaterThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.END_DATE_TIME, root)
                                .as(LocalDateTime.class), startDateTime)
                    )
                );
            } else if (ObjectUtils.isNotEmpty(endDateTime)) {
                // Create a predicate for endDateTime being less than or equal to either start or end date-time.
                andPredicateList.add(
                    criteriaBuilder.or(
                        criteriaBuilder.lessThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.START_DATE_TIME, root)
                                .as(LocalDateTime.class), endDateTime),
                        criteriaBuilder.lessThanOrEqualTo(
                            GenericSpecification.getPropertyPath(SpecificationConstants.END_DATE_TIME, root)
                                .as(LocalDateTime.class), endDateTime)
                    )
                );
            }

            if (hasVisit) {
                andPredicateList.add(GenericSpecification.getPropertyInPredicate(root,
                    SpecificationConstants.TOUR_SLOT_ID, tourSlotIdListWithoutSearch));
            }

            return criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new));
        };
    }

}
