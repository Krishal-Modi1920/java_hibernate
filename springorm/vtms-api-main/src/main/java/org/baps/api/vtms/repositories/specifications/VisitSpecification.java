package org.baps.api.vtms.repositories.specifications;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.SpecificationConstants;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.Role;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitVisitor;
import org.baps.api.vtms.models.entities.Visitor;
import org.baps.api.vtms.repositories.VisitRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
public class VisitSpecification {

    private static final Map<String, String> MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME = new HashMap<>();
   
    private static final EnumMap<VisitTypeEnum, Set<String>> MAP_OF_VISIT_TYPE_ENUM_WITH_SET_OF_ALLOWED_SORTING_PROPERTIES 
        = new EnumMap<>(VisitTypeEnum.class); 


    static {
        MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.put(SpecificationConstants.RELATIONSHIP_MANAGER_NAME,
            RoleEnum.RELATIONSHIP_MANAGER.name());
        MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.put(SpecificationConstants.GUEST_VISIT_COORDINATOR_NAME,
            RoleEnum.GUEST_VISIT_COORDINATOR.name());
        MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.put(SpecificationConstants.TOUR_GUIDE_NAME,
            RoleEnum.TOUR_GUIDE.name());

        MAP_OF_VISIT_TYPE_ENUM_WITH_SET_OF_ALLOWED_SORTING_PROPERTIES.put(VisitTypeEnum.VISIT, Set.of(
            SpecificationConstants.REQUEST_NUMBER,
            SpecificationConstants.PRIMARY_VISITOR_NAME,
            SpecificationConstants.TYPE_OF_VISIT,
            SpecificationConstants.RELATIONSHIP_MANAGER_NAME,
            SpecificationConstants.GUEST_VISIT_COORDINATOR_NAME,
            SpecificationConstants.TOTAL_VISITORS,
            SpecificationConstants.START_DATE_TIME,
            SpecificationConstants.VISIT_STAGE_ENUM,
            SpecificationConstants.UPDATED_AT
        ));
    }

    private final VisitRepository visitRepository;

    private final Translator translator;

    /**
     * Builds a Specification for filtering and searching for Visit entities based on various criteria.
     *
     * @param sortProperty       The property by which to sort the results.
     * @param sortDirection      The direction of sorting, either "asc" (ascending) or "desc" (descending).
     * @param search             A string to search for within Visit entities.
     * @param visitStageEnumList The list of visit stage as an enum value.
     * @param typeOfVisit        The type of visit.
     * @param startDateTime      The start date and time for filtering visits.
     * @param endDateTime        The end date and time for filtering visits.
     * @param personnelId        The personnel ID for filtering by personnel.
     * @param visitTypeEnum      The visit type for filtering visits.
     * @param tourSlotId         The tour slot id for filtering visits.
     * @param siteUUCode         The unique code associated with the site for filtering visits.
     * @param visitorType        The visitor type.
     * @return A Specification Visit object representing the combined filtering criteria.
     */
    public Specification<Visit> buildVisitSpecification(final String sortProperty, final String sortDirection, 
            final String search, final List<VisitStageEnum> visitStageEnumList, final String typeOfVisit, 
            final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String personnelId,
            final VisitTypeEnum visitTypeEnum, final String tourSlotId, final String siteUUCode, final String visitorType) {

        Set<Object> excludeVisitIds = null;

        if (StringUtils.isNotBlank(sortProperty) && MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.containsKey(sortProperty)) {
            excludeVisitIds = visitRepository.findAllByVisitPersonnelListRoleUucodeAndSiteUuCode(
                    MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.get(sortProperty), siteUUCode).stream()
                    .map(Visit::getVisitId).collect(Collectors.toSet());
        }

        return Specification.where(buildSortingSpecification(sortProperty, sortDirection, excludeVisitIds, visitTypeEnum))
            .and(buildFilterSpecification(visitStageEnumList, typeOfVisit, startDateTime, endDateTime, personnelId, visitTypeEnum,
                tourSlotId, siteUUCode, visitorType))
            .and(buildVisitSearchSpecification(search, visitTypeEnum, siteUUCode));
    }
    
    /**
     * Builds a Specification for filtering and searching for Visit entities based on various criteria.
     *
     * @param search             A string to search for within Visit entities.
     * @param startDateTime      The start date and time for filtering visits.
     * @param endDateTime        The end date and time for filtering visits.
     * @param personnelId        The personnel ID for filtering by personnel.
     * @param visitTypeEnum      The visit type for filtering visits.
     * @param visitStageEnumList The list of visit stage as an enum value.
     * @param siteUUCode         The unique code associated with the site for filtering visits.
     * @return A Specification Visit object representing the combined filtering criteria.
     */
    public Specification<Visit> buildVisitSpecificationForVisitSummary(final String search, 
            final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String personnelId,
            final List<VisitStageEnum> visitStageEnumList, final VisitTypeEnum visitTypeEnum,  final String siteUUCode) {

        return Specification.where(buildSortingSpecification(null, null, null, visitTypeEnum))
            .and(buildFilterSpecification(visitStageEnumList, null, startDateTime, endDateTime, personnelId, visitTypeEnum,
                    null, siteUUCode, null))
            .and(buildVisitSearchSpecificationForVisitSummary(search, visitTypeEnum, siteUUCode));
    }
    
    private Specification<Visit> buildSortingSpecification(final String sortProperty, final String sortDirection,
                                                           final Set<Object> excludeVisitIds, final VisitTypeEnum visitTypeEnum) {

        // Check if the provided sortProperty is valid
        if (CommonUtils.isValidProperty(sortProperty, MAP_OF_VISIT_TYPE_ENUM_WITH_SET_OF_ALLOWED_SORTING_PROPERTIES.get(visitTypeEnum))) {
            throw new DataValidationException(translator.toLocal("invalid.sort.property",
                MAP_OF_VISIT_TYPE_ENUM_WITH_SET_OF_ALLOWED_SORTING_PROPERTIES.get(visitTypeEnum)));
        }

        return (root, query, criteriaBuilder) -> {

            final List<Predicate> andPredicateList = new ArrayList<>();
            final Expression<?> propertyPath;

            // Determine the property path to use for sorting
            if (StringUtils.isBlank(sortProperty)) {
                propertyPath = GenericSpecification.getPropertyPath(SpecificationConstants.UPDATED_AT, root);

            } else if (MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.containsKey(sortProperty)) {
                final Join<Visit, VisitPersonnel> visitPersonnelJoin = root.join(SpecificationConstants.VISIT_PERSONNEL_LIST,
                    JoinType.LEFT);
                final Join<VisitPersonnel, Personnel> personnelJoin = visitPersonnelJoin.join(SpecificationConstants.PERSONNEL,
                    JoinType.LEFT);
                final Join<VisitPersonnel, Role> rolesJoin = visitPersonnelJoin.join(SpecificationConstants.ROLE, JoinType.LEFT);

                if (CollectionUtils.isNotEmpty(excludeVisitIds)) {
                    andPredicateList.add(criteriaBuilder.or(
                        criteriaBuilder.not(GenericSpecification.getPropertyInPredicate(root,
                            SpecificationConstants.VISIT_ID, excludeVisitIds)),
                        criteriaBuilder.equal(rolesJoin.get(SpecificationConstants.UUCODE),
                            MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.get(sortProperty))));
                }

                propertyPath = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.equal(rolesJoin.get(SpecificationConstants.UUCODE),
                            MAP_OF_ROLE_PROPERTY_NAME_WITH_ROLE_NAME.get(sortProperty)),
                        criteriaBuilder.concat(
                            criteriaBuilder.concat(
                                criteriaBuilder.lower(personnelJoin.get(SpecificationConstants.FIRST_NAME)),
                                " "
                            ),
                            criteriaBuilder.lower(personnelJoin.get(SpecificationConstants.LAST_NAME))));

            } else if (SpecificationConstants.PRIMARY_VISITOR_NAME.equals(sortProperty)) {
                final Join<Visit, VisitVisitor> visitVisitorJoin = root.join(SpecificationConstants.VISIT_VISITOR_LIST,
                    JoinType.LEFT);
                final Join<VisitVisitor, Visitor> visitorVisitorJoin = visitVisitorJoin.join(SpecificationConstants.VISITOR,
                    JoinType.LEFT);

                andPredicateList.add(
                    criteriaBuilder.equal(visitVisitorJoin.get(SpecificationConstants.CONTACT_TYPE_ENUM),
                        VisitorContactTypeEnum.PRIMARY));
                propertyPath = criteriaBuilder.concat(criteriaBuilder.concat(visitorVisitorJoin
                    .get(SpecificationConstants.FIRST_NAME), " "), visitorVisitorJoin.get(SpecificationConstants.LAST_NAME));

            } else {
                propertyPath = GenericSpecification.getPropertyPath(sortProperty, root);
            }


            final Expression<Boolean> isNullExpression = criteriaBuilder.isNull(propertyPath);
            // Add the sorting order to the query
            if (CommonUtils.getSortDirection(sortDirection, translator).isAscending()) {
                query.orderBy(criteriaBuilder.asc(isNullExpression), criteriaBuilder.asc(propertyPath));
            } else if (CommonUtils.getSortDirection(sortDirection, translator).isDescending()) {
                query.orderBy(criteriaBuilder.asc(isNullExpression), criteriaBuilder.desc(propertyPath));
            }

            return CollectionUtils.isNotEmpty(andPredicateList)
                ? criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new))
                : null;
        };
    }

    /**
     * Builds a specification for filtering Visit entities based on visit stage, type of visit, and date range.
     *
     * @param visitStageEnumList The list of visit stage as an enum value.
     * @param typeOfVisit        The type of visit to filter by.
     * @param startDateTime      The start date and time to filter visits by.
     * @param endDateTime        The end date and time to filter visits by.
     * @param personnelId        The personnel ID for filtering by personnel.
     * @param visitTypeEnum      The visit type for filtering visits.
     * @param tourSlotId         The tour slot id for filtering visits.
     * @param siteUUCode         The unique code associated with the site for filtering visits.
     * @param visitorType        The visitor type to filter by.
     * @return A Specification Visit that can be used in JPA queries.
     */
    private Specification<Visit> buildFilterSpecification(final List<VisitStageEnum> visitStageEnumList, final String typeOfVisit,
                                                          final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                                          final String personnelId, final VisitTypeEnum visitTypeEnum,
                                                          final String tourSlotId, final String siteUUCode, final String visitorType) {

        return (root, query, criteriaBuilder) -> {

            final List<Predicate> andPredicateList = new ArrayList<>();
            
            andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root,
                    SpecificationConstants.SITE_UUCODE, siteUUCode));

            if (CollectionUtils.isNotEmpty(visitStageEnumList)) {
                andPredicateList.add(GenericSpecification.getPropertyPath(SpecificationConstants.VISIT_STAGE_ENUM, root)
                    .in(visitStageEnumList));
            }

            if (StringUtils.isNotBlank(typeOfVisit)) {
                andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root,
                    SpecificationConstants.TYPE_OF_VISIT, typeOfVisit));
            }

            if (StringUtils.isNotBlank(visitorType)) {
                final Join<Visit, VisitVisitor> visitVisitorListJoin = root.join("visitVisitorList");
                final Join<VisitVisitor, Visitor> visitorJoin = visitVisitorListJoin.join("visitor");
                andPredicateList.add(criteriaBuilder.equal(visitorJoin.get("visitorType"), visitorType));
            }

            if (ObjectUtils.isNotEmpty(startDateTime) && ObjectUtils.isNotEmpty(endDateTime)) {
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

            if (StringUtils.isNotBlank(personnelId)) {

                final Set<String> visitIdSet = visitRepository.findAllByVisitPersonnelIdAndSiteUuCode(personnelId, siteUUCode)
                    .stream().map(Visit::getVisitId).collect(Collectors.toSet());

                andPredicateList.add(root.get(SpecificationConstants.VISIT_ID).in(visitIdSet));
            }

            if (ObjectUtils.isNotEmpty(visitTypeEnum)) {
                andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root,
                    SpecificationConstants.VISIT_TYPE_ENUM, visitTypeEnum));
            }

            if (StringUtils.isNotBlank(tourSlotId)) {
                andPredicateList.add(
                    GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root, SpecificationConstants.VISIT_TOUR_SLOT_ID,
                        tourSlotId)
                );
            }

            return CollectionUtils.isNotEmpty(andPredicateList)
                ? criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new))
                : null;
        };
    }

    /**
     * Builds a search specification for filtering Visit entities based on search criteria and visit type.
     *
     * @param search        The search string to filter by.
     * @param visitTypeEnum The type of visit (e.g., VisitTypeEnum.VISIT or VisitTypeEnum.TOUR) to consider.
     * @param siteUUCode    The unique code associated with the site for filtering visits.
     * @return A Specification that can be used in a query to filter Visit entities.
     */
    public Specification<Visit> buildVisitSearchSpecification(final String search, final VisitTypeEnum visitTypeEnum,
                                                              final String siteUUCode) {
        if (StringUtils.isBlank(search)) {
            return null;  // If the search string is blank, return null to indicate no specific filtering is needed.
        }

        return (root, query, criteriaBuilder) -> {

            final List<Predicate> orPredicateList = new ArrayList<>();

            // Determine the role names to consider based on the visit type.
            if (visitTypeEnum.equals(VisitTypeEnum.VISIT)) {

                // Define an array of properties to search for in the Visit entity.
                final String[] propertiesToSearch = {SpecificationConstants.REQUEST_NUMBER};

                // Create a set to store role names based on the visit type.
                final Set<String> roleUucodeSet = new HashSet<>();

                roleUucodeSet.add(RoleEnum.RELATIONSHIP_MANAGER.name());
                roleUucodeSet.add(RoleEnum.GUEST_VISIT_COORDINATOR.name());

                // Query the visitRepository to find visit IDs associated with the specified role names and search string.
                final Set<String> visitIdSet = visitRepository.findAllByVisitPersonnelLikePersonnelNameAndRoleUucodeSet(
                        roleUucodeSet, "%" + search.toLowerCase() + "%", siteUUCode)
                    .stream().map(Visit::getVisitId).collect(Collectors.toSet());

                if (CollectionUtils.isNotEmpty(visitIdSet)) {
                    // Add a condition to filter by visit IDs.
                    orPredicateList.add(root.get(SpecificationConstants.VISIT_ID).in(visitIdSet));
                }

                // Add conditions to search for the specified properties in the Visit entity.
                for (String property : propertiesToSearch) {
                    orPredicateList.add(GenericSpecification.getPropertyLikePredicate(criteriaBuilder, root, property, search));
                }

                // Add a custom condition to search for visitor full names.
                orPredicateList.add(hasVisitorFullNameLike(criteriaBuilder, root, search));

                final Predicate orPredicate = criteriaBuilder.or(orPredicateList.toArray(Predicate[]::new));

                final Predicate andPredicate = hasVisitorStatus(criteriaBuilder, root);

                return criteriaBuilder.and(andPredicate, orPredicate);

            } else if (visitTypeEnum.equals(VisitTypeEnum.TOUR)) {
                final List<Predicate> andPredicateList = new ArrayList<>();

                andPredicateList.add(criteriaBuilder.isNotNull(root.get(SpecificationConstants.TOUR_SLOT)));

                andPredicateList.add(hasVisitorLike(criteriaBuilder, root, search));

                return criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new));
            }
            return null;
        };
    }
    
    /**
     * Builds a search specification for filtering Visit entities based on search criteria and visit type.
     *
     * @param search        The search string to filter by.
     * @param visitTypeEnum The type of visit (e.g., VisitTypeEnum.VISIT or VisitTypeEnum.TOUR) to consider.
     * @param siteUUCode    The unique code associated with the site for filtering visits.
     * @return A Specification that can be used in a query to filter Visit entities.
     */
    public Specification<Visit> buildVisitSearchSpecificationForVisitSummary(final String search, final VisitTypeEnum visitTypeEnum,
            final String siteUUCode) {
        if (StringUtils.isBlank(search)) {
            return null;  // If the search string is blank, return null to indicate no specific filtering is needed.
        }

        return (root, query, criteriaBuilder) -> {

            final List<Predicate> orPredicateList = new ArrayList<>();

            // Define an array of properties to search for in the Visit entity.
            final String[] propertiesToSearch = {SpecificationConstants.REQUEST_NUMBER};

            // Add conditions to search for the specified properties in the Visit entity.
            for (String property : propertiesToSearch) {
                orPredicateList.add(GenericSpecification.getPropertyLikePredicate(criteriaBuilder, root, property, search));
            }

            // Add a custom condition to search for visitor full names.
            orPredicateList.add(hasVisitorFullNameLike(criteriaBuilder, root, search));

            final Predicate orPredicate = criteriaBuilder.or(orPredicateList.toArray(Predicate[]::new));

            final Predicate andPredicate = hasVisitorStatus(criteriaBuilder, root);

            return criteriaBuilder.and(andPredicate, orPredicate);
        };
    }

    /**
     * This method generates a Predicate to filter Visit entities based on visitor-related criteria.
     *
     * @param criteriaBuilder The CriteriaBuilder used to construct predicates.
     * @param root            The root entity (Visit) from which attributes are accessed.
     * @param search          The search string for filtering visitors.
     * @return A Predicate representing the combined conditions for filtering visits based on visitor criteria.
     */
    private Predicate hasVisitorLike(final CriteriaBuilder criteriaBuilder,
                                     final Root<Visit> root,
                                     final String search) {

        if (StringUtils.isBlank(search)) {
            return null;  // If the search string is blank, return null to indicate no specific filtering is needed.
        }

        // Combine AND predicates with an AND operator
        final Predicate andPredicate = hasVisitorStatus(criteriaBuilder, root);

        final List<Predicate> orPredicateList = new ArrayList<>();

        // Add a custom condition to search for visitor full names.
        orPredicateList.add(hasVisitorFullNameLike(criteriaBuilder, root, search));

        // Add a custom condition to search for visitor phone.
        orPredicateList.add(hasVisitorPhoneLike(criteriaBuilder, root, search));

        // Add a custom condition to search for visitor email.
        orPredicateList.add(hasVisitorEmailLike(criteriaBuilder, root, search));

        final Predicate orPredicate = criteriaBuilder.or(orPredicateList.toArray(Predicate[]::new));

        return criteriaBuilder.and(andPredicate, orPredicate);
    }

    /**
     * This method generates a Predicate to filter Visit entities based on visitor-related criteria.
     *
     * @param criteriaBuilder The CriteriaBuilder used to construct predicates.
     * @param root            The root entity (Visit) from which attributes are accessed.
     * @return A Predicate representing the combined conditions for filtering visits based on visitor criteria.
     */
    private Predicate hasVisitorStatus(final CriteriaBuilder criteriaBuilder,
                                     final Root<Visit> root) {

        // List to store AND predicates for status filtering.
        final List<Predicate> andPredicateList = new ArrayList<>();
        
        // Exclude visits with visitor status set to DELETED.
        andPredicateList.add(GenericSpecification.getPropertyNotEqualPredicate(criteriaBuilder, root,
            SpecificationConstants.VISIT_VISITOR_STATUS, Status.DELETED));

        // Exclude visits with any visitor in the visitor list having status set to DELETED.
        andPredicateList.add(GenericSpecification.getPropertyNotEqualPredicate(criteriaBuilder, root,
            SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_STATUS, Status.DELETED));

        andPredicateList.add(GenericSpecification.getPropertyEqualPredicate(criteriaBuilder, root,
            SpecificationConstants.VISIT_VISITOR_CONTACT_TYPE_ENUM, VisitorContactTypeEnum.PRIMARY));

        return criteriaBuilder.and(andPredicateList.toArray(Predicate[]::new));
    }
    
    /**
     * Builds a predicate for searching Visit entities based on visitor names.
     *
     * @param criteriaBuilder The CriteriaBuilder for building predicates.
     * @param root            The root entity in the JPA query.
     * @param search          The search keyword for filtering by visitor names.
     * @return A Predicate for searching Visit entities by visitor names.
     */
    private Predicate hasVisitorFullNameLike(final CriteriaBuilder criteriaBuilder,
                                             final Root<Visit> root, final String search) {

        final String searchString = "%" + search.toLowerCase() + "%";

        return criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.concat(
                    criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                        SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_FIRST_NAME, root).as(String.class)), " "),
                criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                        SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_LAST_NAME, root)
                    .as(String.class))
            ),
            searchString
        );
    }

    /**
     * Creates a Predicate for filtering Visit entities based on a search string matching the concatenated
     * phone number (country code and phone number) of associated visitors.
     *
     * @param criteriaBuilder CriteriaBuilder for building predicates.
     * @param root            Root entity (Visit) for the query.
     * @param search          Search criteria for filtering by visitor phone number.
     * @return Predicate for filtering Visit entities based on visitor phone number.
     */
    private Predicate hasVisitorPhoneLike(final CriteriaBuilder criteriaBuilder,
                                          final Root<Visit> root,
                                          final String search) {

        // Prepare the search string for case-insensitive matching.
        final String searchString = "%" + search.toLowerCase() + "%";

        // Build the Predicate using CriteriaBuilder operations.
        return criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.concat(
                    criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                            SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_PHONE_COUNTRY_CODE, root)
                        .as(String.class)), " "),
                criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                        SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_PHONE_NUMBER, root)
                    .as(String.class))
            ),
            searchString
        );
    }

    /**
     * Creates a Predicate for filtering Visit entities based on a search string matching the email
     * of associated visitors.
     *
     * @param criteriaBuilder CriteriaBuilder for building predicates.
     * @param root            Root entity (Visit) for the query.
     * @param search          Search criteria for filtering by visitor email.
     * @return Predicate for filtering Visit entities based on visitor email.
     */
    private Predicate hasVisitorEmailLike(final CriteriaBuilder criteriaBuilder,
                                          final Root<Visit> root, final String search) {

        // Prepare the search string for case-insensitive matching.
        final String searchString = "%" + search.toLowerCase() + "%";

        // Build the Predicate using CriteriaBuilder operations.
        return criteriaBuilder.like(
            criteriaBuilder.lower(GenericSpecification.getPropertyPath(
                SpecificationConstants.VISIT_VISITOR_LIST_VISITOR_EMAIL, root).as(String.class)),
            searchString
        );
    }
}
