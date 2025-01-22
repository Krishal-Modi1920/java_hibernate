package org.baps.api.vtms.services;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.LookupMapper;
import org.baps.api.vtms.models.ChildLookupModel;
import org.baps.api.vtms.models.LookupModel;
import org.baps.api.vtms.models.entities.Lookup;
import org.baps.api.vtms.repositories.LookupRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class LookupService {

    private final LookupMapper lookupMapper;

    private final LookupRepository lookupRepository;

    private final Translator translator;

    /**
     * Retrieves a list of LookupModel objects representing various lookup data.
     * This method queries the database for Lookup entities and converts them into
     * corresponding LookupModel objects for use in the application.
     *
     * @param filter A filter string for narrowing down the results (can be null or empty).
     * @return A list of LookupModel objects, or an empty list if no records match the filter.
     */
    @Transactional(readOnly = true)
    public List<LookupModel> getLookups(final String filter) {
        
        final var existingLookups = StringUtils.isBlank(filter) ? lookupRepository.findAll() :
            lookupRepository.findAll(toSpecification(filter, true, null, null));

        List<LookupModel> lookupModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingLookups)) {
            lookupModelList = lookupMapper.lookupListToLookupModelList(existingLookups);
        }

        return lookupModelList;
    }
    
    /**
     * Validates child lookup values against an existing lookup by key.
     *
     * @param lookupKey         The key of the lookup to validate against.
     * @param childLookupValues The list of child lookup values to validate.
     * @param field             The name of the field being validated.
     * @throws DataNotFoundException     If the lookup with the provided key does not exist.
     * @throws DataValidationException    If any of the child lookup values are invalid.
     */
    @Transactional(readOnly = true)
    public void validateChildLookupValueByKey(final String lookupKey, final List<String> childLookupValues, final String field) {

        final var existingLookup = findLookupByKey(lookupKey);

        final Set<String> existingChildLookupValues = existingLookup.getChildLookupModelList().stream()
                .map(ChildLookupModel::getValue)
                .collect(Collectors.toSet());
        
        if (CollectionUtils.isNotEmpty(childLookupValues)) { 
            childLookupValues.stream().forEach(childLookupValue -> {
                if (StringUtils.isBlank(childLookupValue) || !existingChildLookupValues.contains(childLookupValue)) { 
                    throw new DataValidationException(translator.toLocal("_is.invalid", field));
                }
            });
        }
    }
    
    /**
     * Validates child lookup values against an existing lookup by key.
     *
     * @param lookupKey         The key of the lookup to validate against.
     * @param childLookupKeys   The list of child lookup keys to validate.
     * @param field             The name of the field being validated.
     * @throws DataNotFoundException     If the lookup with the provided key does not exist.
     * @throws DataValidationException    If any of the child lookup values are invalid.
     */
    @Transactional(readOnly = true)
    public void validateChildLookupKeyByKey(final String lookupKey, final List<String> childLookupKeys, final String field) {

        final var existingLookup = findLookupByKey(lookupKey);
        
        final Set<String> existingChildLookupKeys = existingLookup.getChildLookupModelList().stream()
                .map(ChildLookupModel::getKey)
                .collect(Collectors.toSet());
        
        if (CollectionUtils.isNotEmpty(childLookupKeys)) { 
            childLookupKeys.stream().forEach(childLookupKey -> {
                if (StringUtils.isBlank(childLookupKey) || !existingChildLookupKeys.contains(childLookupKey)) { 
                    throw new DataValidationException(translator.toLocal("_is.invalid", field));
                }
            });
        }
    }
    
    /**
     * Retrieves a lookup entry from the database based on the provided lookup key.
     * This method operates in a read-only mode, ensuring that no changes are made to the database.
     *
     * @param lookupKey The unique key identifying the lookup entry to be retrieved.
     * @return A Lookup object corresponding to the provided key, or null if no entry is found.
     */
    @Transactional(readOnly = true)
    public Lookup findLookupByKey(final String lookupKey) {
        return lookupRepository.findByKey(lookupKey).orElseThrow(() -> 
            new DataNotFoundException(translator.toLocal("lookup.with.lookup_key.not.found", lookupKey)));
    }
    
}
