package org.baps.api.vtms.repositories.specifications;

import org.baps.api.vtms.constants.SpecificationConstants;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Visitor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


@Component
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class VisitorSpecification {

    private final EntityManager entityManager;

    /**
     * Executes a search and grouping operation on {@code Visitor} entities based on specified criteria.
     *
     * @param search The search string to filter visitors by. Can be null or empty to skip search filtering.
     * @param visitTypeEnum The visit type to filter by. Can be null to skip filtering by visit type.
     * @return A list of {@code String} objects representing the minimum visitor IDs for each grouped set of visitors.
     */
    public List<String> groupBydVisitorIdSearchExecuteSpecification(final String search, final VisitTypeEnum visitTypeEnum) {

        // Combine search and visit type specifications using AND operator
        final Specification<Visitor> visitSpe =  Specification.where(buildVisitorSearchFilter(search, visitTypeEnum))
                .and(buildFilterSpecification(visitTypeEnum));

        // Initialize CriteriaBuilder, CriteriaQuery, and root entity
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);
        final Root<Visitor> root = query.from(Visitor.class);

        // Build predicate for the combined specifications
        final Predicate predicate = visitSpe.toPredicate(root, query, criteriaBuilder);
        if (predicate != null) {
            query.where(predicate);
        }
        
        // Build a grouped query to fetch minimum visitor IDs for each group
        query.multiselect(criteriaBuilder.min(root.get(SpecificationConstants.VISITOR_ID)));
        query.groupBy(root.get(SpecificationConstants.FIRST_NAME), root.get(SpecificationConstants.LAST_NAME),
                root.get(SpecificationConstants.GENDER), root.get(SpecificationConstants.EMAIL),
                root.get(SpecificationConstants.PHONE_COUNTRY_CODE), root.get(SpecificationConstants.PHONE_NUMBER));
        final List<Object[]> objectList =  entityManager.createQuery(query).getResultList();

        return objectList.stream().map(objArray -> objArray[0].toString()).toList();
    }

    /**
     * Builds a JPA Specification for filtering {@code Visitor} entities based on the specified visit type.
     *
     * @param visitTypeEnum The visit type to filter by. Can be null to skip filtering by visit type.
     * @return A Specification object for filtering {@code Visitor} entities.
     */
    private Specification<Visitor> buildFilterSpecification(final VisitTypeEnum visitTypeEnum) {
        
        // Create a list to hold the individual predicates for the filter conditions
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> andPredicateList = new ArrayList<>();

            // Add a predicate for filtering by visit type if the provided visitTypeEnum is not null
            if (ObjectUtils.isNotEmpty(visitTypeEnum)) {
                andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root, 
                        SpecificationConstants.VISIT_VISITOR_VISIT_VISIT_TYPE_ENUM, visitTypeEnum));
            }
            
            // Combine multiple predicates using AND operator
            return CollectionUtils.isNotEmpty(andPredicateList)
                    ? criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new))
                            : null;
        };
    }

    /**
     * Builds a Specification for filtering Visitor entities based on a search string and visit type.
     *
     * @param search The search string to be used for filtering visitor information.
     * @param visitTypeEnum The type of visit to be used for filtering.
     * @return A Specification representing the criteria for filtering visitors.
     */
    private Specification<Visitor> buildVisitorSearchFilter(final String search, final VisitTypeEnum visitTypeEnum) {

        // If the search string is blank, return null (no filtering)
        if (StringUtils.isBlank(search)) {
            return null;
        }

        // Build the Specification using a lambda expression
        return (root, query, criteriaBuilder) -> {

            // Create a list to store AND predicates
            final List<Predicate> andPredicateList =  new ArrayList<>();

            // Add AND predicate for filtering based on visit type
            if (ObjectUtils.isNotEmpty(visitTypeEnum)) {
                andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root, 
                        SpecificationConstants.VISIT_VISITOR_VISIT_VISIT_TYPE_ENUM, visitTypeEnum));
            }

            // Add AND predicate for filtering out deleted visits
            andPredicateList.add(GenericSpecification.getPropertyNotEqualPredicate(criteriaBuilder, root, 
                    SpecificationConstants.VISIT_VISITOR_VISIT_VISIT_STATUS, Status.DELETED));

            // Add AND predicate for filtering out deleted visitors
            andPredicateList.add(GenericSpecification.getPropertyNotEqualPredicate(criteriaBuilder, root, 
                    SpecificationConstants.VISIT_VISITOR_STATUS, Status.DELETED));

            // Combine AND predicates with an AND operator
            final Predicate andPredicate = criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new));

            // Create a list to store OR predicates
            final List<Predicate> orPredicateList =  new ArrayList<>();

            // Add OR predicate for filtering based on visitor full name
            orPredicateList.add(hasVisitorFullNameLike(criteriaBuilder, root, search));

            // Add OR predicate for filtering based on visitor email
            orPredicateList.add(hasVisitorEmailLike(criteriaBuilder, root, search));

            // Add OR predicate for filtering based on visitor phone
            orPredicateList.add(hasVisitorPhoneLike(criteriaBuilder, root, search));

            // Combine OR predicates with an OR operator
            final Predicate orPredicate = criteriaBuilder.or(orPredicateList.toArray(Predicate[]::new));

            // Combine AND and OR predicates with an AND operator
            return criteriaBuilder.and(andPredicate, orPredicate);
        };
    }

    /**
     * Creates a Predicate for filtering entities based on a search string matching the concatenation
     * of visitor's first name and last name.
     *
     * @param criteriaBuilder The CriteriaBuilder used to construct the criteria queries.
     * @param root The root entity in the from clause.
     * @param search The search string to be used for filtering.
     * @return A Predicate representing the criteria for filtering based on the full name.
     */
    private Predicate hasVisitorFullNameLike(final CriteriaBuilder criteriaBuilder, final Root<Visitor> root, final String search) {

        // Prepare the search string with wildcard and case-insensitive matching
        final String searchString = "%" + search.toLowerCase() + "%";

        // Build the Predicate for concatenation of visitor's first name and last name
        return criteriaBuilder.like(
                criteriaBuilder.concat(
                        criteriaBuilder.concat(
                                criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                                        SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_FIRST_NAME, root).as(String.class)), " "),
                        criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                                SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_LAST_NAME, root).as(String.class))
                        ), searchString
                );
    }

    /**
     * Creates a Predicate for filtering entities based on a search string matching the email property.
     *
     * @param criteriaBuilder The CriteriaBuilder used to construct the criteria queries.
     * @param root The root entity in the from clause.
     * @param search The search string to be used for filtering.
     * @return A Predicate representing the criteria for filtering based on email.
     */
    private Predicate hasVisitorEmailLike(final CriteriaBuilder criteriaBuilder, final Root<Visitor> root, final String search) {

        // Prepare the search string with wildcard and case-insensitive matching
        final String searchString = "%" + search.toLowerCase() + "%";

        // Build the Predicate for matching the email property (converted to lower case)
        return criteriaBuilder.like(
                criteriaBuilder.lower(
                        GenericSpecification.getPropertyPath(SpecificationConstants.EMAIL, root).as(String.class)), searchString);
    }

    /**
     * Creates a Predicate for filtering entities based on a search string matching the concatenation
     * of phone country code and phone number.
     *
     * @param criteriaBuilder The CriteriaBuilder used to construct the criteria queries.
     * @param root The root entity in the from clause.
     * @param search The search string to be used for filtering.
     * @return A Predicate representing the criteria for filtering.
     */
    private Predicate hasVisitorPhoneLike(final CriteriaBuilder criteriaBuilder, final Root<Visitor> root, final String search) {

        // Prepare the search string with wildcard and case-insensitive matching
        final String searchString = "%" + search.toLowerCase() + "%";

        // Build the Predicate for concatenation of phone country code and phone number
        return criteriaBuilder.like(
                criteriaBuilder.concat(
                        criteriaBuilder.concat(
                                criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                                        SpecificationConstants.PHONE_COUNTRY_CODE, root).as(String.class)), " "),
                        criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                                SpecificationConstants.PHONE_NUMBER, root).as(String.class))
                        ), searchString);
    }
}
