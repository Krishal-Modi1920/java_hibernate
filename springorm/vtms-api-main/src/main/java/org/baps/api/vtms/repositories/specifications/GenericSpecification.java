package org.baps.api.vtms.repositories.specifications;

import org.baps.api.vtms.constants.SpecificationConstants;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.collections.CollectionUtils;

@UtilityClass
public class GenericSpecification {


    /**
     * Creates a Predicate for performing a "LIKE" operation on a property in a JPA query.
     *
     * @param criteriaBuilder The CriteriaBuilder instance to build the query predicates.
     * @param root            The root entity in the query.
     * @param property        The name of the property to filter.
     * @param value           The value to search for within the property (case-insensitive).
     * @return A Predicate representing the "LIKE" operation.
     */
    public static Predicate getPropertyLikePredicate(final CriteriaBuilder criteriaBuilder,
                                                     final Root<?> root,
                                                     final String property,
                                                     final String value) {
        return criteriaBuilder.like(
            criteriaBuilder.lower(getPropertyPath(property, root).as(String.class)),
            "%" + value.toLowerCase() + "%"
        );
    }

    /**
     * Creates a Predicate for checking if a property in the entity is equal to a given value.
     *
     * @param <T>             The type of the entity being queried.
     * @param criteriaBuilder The CriteriaBuilder used to build the Predicate.
     * @param root            The root entity in the JPA query.
     * @param property        The name of the property to compare.
     * @param value           The value to compare the property against.
     * @return A Predicate that checks if the property is equal to the given value.
     */
    public static <T> Predicate getPropertyEqualPredicate(final CriteriaBuilder criteriaBuilder,
                                                          final Root<T> root,
                                                          final String property,
                                                          final Object value) {
        return criteriaBuilder.equal(getPropertyPath(property, root), value);
    }

    /**
     * Create a predicate for an IN query for a specific property.
     *
     * @param root     The root entity from which to access the property.
     * @param property The name of the property for which to create the IN predicate.
     * @param values   The list of values to check for in the property.
     * @param <T>      The type of the entity.
     * @return A predicate for an IN query.
     */
    public static <T> Predicate getPropertyInPredicate(final Root<T> root,
                                                       final String property,
                                                       final Set<Object> values) {

        // Get the path to the specified property within the entity.
        final Path<?> propertyPath = getPropertyPath(property, root);

        if (CollectionUtils.isEmpty(values)) {
            return propertyPath.in(new HashSet<>());
        }

        // Create an IN predicate to check if the property matches any of the values.
        return propertyPath.in(values);
    }

    /**
     * Creates a Predicate for checking if a property in the entity is not equal to a given value.
     *
     * @param <T>             The type of the entity being queried.
     * @param criteriaBuilder The CriteriaBuilder used to build the Predicate.
     * @param root            The root entity in the JPA query.
     * @param property        The name of the property to compare.
     * @param value           The value to compare the property against.
     * @return A Predicate that checks if the property is not equal to the given value.
     */
    public static <T> Predicate getPropertyNotEqualPredicate(final CriteriaBuilder criteriaBuilder,
                                                             final Root<T> root,
                                                             final String property,
                                                             final Object value) {
        return criteriaBuilder.notEqual(getPropertyPath(property, root), value);
    }

    /**
     * Retrieves the Path representing a nested property of an entity.
     *
     * @param key  The dot-separated path to the desired property.
     * @param root The root entity in the query.
     * @param <T>  The type of the root entity.
     * @return A Path representing the specified nested property.
     */
    public static <T> Path<T> getPropertyPath(final String key, final Root<T> root) {
        final String[] fields = key.split("\\.");
        Path<T> propertyFullPath = root;

        for (String field : fields) {
            propertyFullPath = propertyFullPath.get(field);
        }

        return propertyFullPath;
    }

    public static <T> Specification<T> hasSiteCode(final String siteUUCode) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                getPropertyEqualPredicate(criteriaBuilder, root, SpecificationConstants.SITE_UUCODE, siteUUCode)
            );
    }
}
