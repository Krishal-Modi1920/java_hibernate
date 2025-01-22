package org.baps.api.vtms.repositories.rsql;

import org.baps.api.vtms.constants.GeneralConstant;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@SuppressFBWarnings({"SE_BAD_FIELD"}) // 3P class ComparisonOperator is not Serializable
public class GenericRsqlSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 8324065449082591820L;

    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;

    public GenericRsqlSpecification(final String property, final ComparisonOperator operator, final List<String> arguments) {
        super();
        this.property = property;
        this.operator = operator;
        this.arguments = arguments.stream().toList();
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        final List<?> args = castArguments(root);
        final Object argument = args.get(0);
        switch (RsqlSearchOperation.getSimpleOperator(operator)) {
            case EQUAL -> {
                if (argument instanceof String) {
                    return builder.like(
                        builder.lower(root.get(property).as(String.class)),
                        argument.toString().toLowerCase().replace('*', '%')
                    );
                } else if (argument == null) {
                    return builder.isNull(root.get(property));
                } else if (argument instanceof LocalDateTime) {
                    return builder.equal(root.<LocalDateTime>get(property), argument);
                } else if (argument instanceof LocalDate) {
                    return builder.equal(root.<LocalDate>get(property), argument);
                } else if (argument instanceof LocalTime) {
                    return builder.equal(root.<LocalTime>get(property), argument);
                } else {
                    return builder.equal(root.get(property), argument);
                }
            }
            case NOT_EQUAL -> {
                if (argument instanceof String) {
                    return builder.notLike(
                        builder.lower(root.get(property).as(String.class)),
                        argument.toString().toLowerCase().replace('*', '%')
                    );
                } else if (argument == null) {
                    return builder.isNotNull(root.get(property));
                } else {
                    return builder.notEqual(root.get(property), argument);
                }
            }
            case GREATER_THAN -> {
                if (argument instanceof LocalDateTime) {
                    return builder.greaterThan(root.get(property), (LocalDateTime) argument);
                }
                return builder.greaterThan(root.get(property), argument.toString());
            }
            case GREATER_THAN_OR_EQUAL -> {
                if (argument instanceof LocalDateTime) {
                    return builder.greaterThanOrEqualTo(root.get(property), (LocalDateTime) argument);
                }
                return builder.greaterThanOrEqualTo(root.get(property), argument.toString());
            }
            case LESS_THAN -> {
                if (argument instanceof LocalDateTime) {
                    return builder.lessThan(root.get(property), (LocalDateTime) argument);
                }
                return builder.lessThan(root.get(property), argument.toString());
            }
            case LESS_THAN_OR_EQUAL -> {
                if (argument instanceof LocalDateTime) {
                    return builder.lessThanOrEqualTo(root.get(property), (LocalDateTime) argument);
                }
                return builder.lessThanOrEqualTo(root.get(property), argument.toString());
            }
            case IN -> {
                return root.get(property).in(args);
            }
            case NOT_IN -> {
                return builder.not(root.get(property).in(args));
            }
            default -> {
                return null;
            }
        }
    }

    private List<?> castArguments(final Root<T> root) {
        final Class<?> type = root.get(property).getJavaType();

        final Map<Class<?>, Function<String, Object>> castingFunctions = new HashMap<>();
        castingFunctions.put(Integer.class, Integer::parseInt);
        castingFunctions.put(Long.class, Long::parseLong);
        castingFunctions.put(LocalDateTime.class, arg -> LocalDateTime.parse(arg, GeneralConstant.DATE_TIME_FORMATTER));
        castingFunctions.put(LocalDate.class, LocalDate::parse);
        castingFunctions.put(LocalTime.class, LocalTime::parse);

        return arguments.stream()
            .map(arg -> castingFunctions.getOrDefault(type, Object::toString).apply(arg))
            .toList();
    }
}
