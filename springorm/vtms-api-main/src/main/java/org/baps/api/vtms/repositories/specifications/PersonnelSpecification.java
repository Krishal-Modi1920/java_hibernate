package org.baps.api.vtms.repositories.specifications;


import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.SpecificationConstants;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.models.entities.Personnel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


@RequiredArgsConstructor
@Component
public class PersonnelSpecification {

    private static final Set<String> ALLOWED_PERSONNEL_SORT_PROPERTIES = Set.of(
        SpecificationConstants.FIRST_NAME,
        SpecificationConstants.LAST_NAME,
        SpecificationConstants.EMAIL,
        SpecificationConstants.PHONE_NUMBER,
        SpecificationConstants.UPDATED_AT
    );

    private final Translator translator;


    /**
     * Builds a Specification for personnel searches based on the provided criteria.
     *
     * @param search The search criteria to filter personnel records.
     * @return A Specification for personnel record searches, or null if criteria is blank.
     */
    private Specification<Personnel> buildPersonnelSearchSpecification(final String search) {
        if (StringUtils.isBlank(search)) {
            return null;
        }

        return (personnelRoot, query, criteriaBuilder) -> {
            final String[] propertiesToSearch = {SpecificationConstants.EMAIL, SpecificationConstants.PHONE_NUMBER};

            final List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(getPersonnelFullNameLikePredicate(criteriaBuilder, personnelRoot, search));

            for (String property : propertiesToSearch) {
                predicateList.add(GenericSpecification.getPropertyLikePredicate(criteriaBuilder, personnelRoot, property, search));
            }

            return criteriaBuilder.or(predicateList.toArray(Predicate[]::new));
        };
    }

    /**
     * Creates a case-insensitive partial match Predicate for filtering personnel records by full name,
     * while ignoring spaces in the search criteria.
     *
     * @param criteriaBuilder The CriteriaBuilder used for Predicate construction.
     * @param personnelRoot   The Root representing the Personnel entity in the query.
     * @param search          The search string to match against personnel full names (ignoring spaces).
     * @return A Predicate for filtering personnel records by partial full name match (ignoring spaces).
     */
    private Predicate getPersonnelFullNameLikePredicate(final CriteriaBuilder criteriaBuilder,
                                                        final Root<Personnel> personnelRoot,
                                                        final String search) {

        final String searchString = "%" + search.toLowerCase() + "%";
        return criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.concat(
                    criteriaBuilder.lower(
                        GenericSpecification.getPropertyPath(SpecificationConstants.FIRST_NAME, personnelRoot).as(String.class)),
                    " "),
                criteriaBuilder.lower(
                    GenericSpecification.getPropertyPath(SpecificationConstants.LAST_NAME, personnelRoot).as(String.class))
            ),
            searchString
        );
    }

    /**
     * Creates a Specification for filtering personnel entities by different properties.
     *
     * @param roleName The role name to filter by.
     * @return A Specification for filtering personnel by properties or null if properties are blank.
     */
    private Specification<Personnel> buildPersonnelFilterSpecification(final String roleName) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicateList = new ArrayList<>();

            if (StringUtils.isNotBlank(roleName)) {
                predicateList.add(
                    GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root,
                        SpecificationConstants.PERSONNEL_ROLE_LIST_ROLE_NAME, roleName));
            }

            return CollectionUtils.isNotEmpty(predicateList)
                ? criteriaBuilder.and(predicateList.toArray(Predicate[]::new))
                : null;
        };
    }


    /**
     * Builds a Specification for searching and filtering personnel records.
     *
     * @param sortProperty  The property by which the results should be sorted.
     * @param sortDirection The sorting direction, either "asc" (ascending) or "desc" (descending).
     * @param search        The search keyword or criteria to filter personnel records.
     * @param roleName      The name of the role to filter personnel records by.
     * @return A Specification object that can be used to filter and sort personnel records.
     */
    public Specification<Personnel> buildPersonnelSearchFilterSpecification(final String sortProperty,
                                                                            final String sortDirection,
                                                                            final String search,
                                                                            final String roleName) {

        return Specification.where(buildSortingSpecification(sortProperty, sortDirection))
            .and(buildPersonnelFilterSpecification(roleName))
            .and(buildPersonnelSearchSpecification(search));
    }


    /**
     * Builds a sorting specification for Personnel entities based on the provided sorting properties.
     *
     * @param sortProperty  The property to sort by.
     * @param sortDirection The direction of sorting (ASC or DESC).
     * @return A Specification for sorting Personnel entities.
     */
    private Specification<Personnel> buildSortingSpecification(final String sortProperty,
                                                               final String sortDirection) {

        // Check if the provided sortProperty is valid
        if (CommonUtils.isValidProperty(sortProperty, ALLOWED_PERSONNEL_SORT_PROPERTIES)) {
            throw new DataValidationException(translator.toLocal("invalid.sort.property", ALLOWED_PERSONNEL_SORT_PROPERTIES));
        }

        return (root, query, criteriaBuilder) -> {

            final Expression<?> propertyPath;

            // Determine the property path to use for sorting
            if (StringUtils.isBlank(sortProperty)) {
                propertyPath = GenericSpecification.getPropertyPath(SpecificationConstants.UPDATED_AT, root);
            } else {
                propertyPath = GenericSpecification.getPropertyPath(sortProperty, root);
            }

            final List<Order> orderList = new ArrayList<>();

            // Add the sorting order to the query
            if (CommonUtils.getSortDirection(sortDirection, translator).isAscending()) {
                orderList.add(criteriaBuilder.asc(propertyPath));
            } else if (CommonUtils.getSortDirection(sortDirection, translator).isDescending()) {
                orderList.add(criteriaBuilder.desc(propertyPath));
            }

            query.orderBy(orderList);

            return null;
        };
    }
}
