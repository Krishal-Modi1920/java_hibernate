package org.baps.api.vtms.repositories.specifications;

import org.baps.api.vtms.repositories.rsql.GenericRsqlSpecBuilder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import io.github.perplexhub.rsql.RSQLJPASupport;
import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;

@Component
public class GenericCustomRsqlSpecificationBuilder {

    public <T> Specification<T> buildSpecification(final String filter) {
        // Create an initial empty specification
        Specification<T> baseSpecification = Specification.where(null);

        // Add RSQL filter if provided
        if (StringUtils.isNotBlank(filter)) {
            final Node rootNode = new RSQLParser().parse(filter);
            final GenericRsqlSpecBuilder<T> specBuilder = new GenericRsqlSpecBuilder<>();
            baseSpecification = baseSpecification.and(specBuilder.createSpecification(rootNode));
        }

        return baseSpecification;
    }
    
    public <T> Specification<T> buildSpecification(final String nestedFilter, final Map<String, JoinType> joinHints) {
        // Create an initial empty specification
        Specification<T> baseSpecification = Specification.where(null);

        // Add nested RSQL filter if provided
        if (StringUtils.isNotBlank(nestedFilter)) {
            // Create a nested RSQL specification and combine it with the base specification
            final Specification<T> nestedRsqlSpecification = RSQLJPASupport.toSpecification(nestedFilter, null, joinHints);
            baseSpecification = baseSpecification.and(nestedRsqlSpecification);
        }

        return baseSpecification;
    }
}
