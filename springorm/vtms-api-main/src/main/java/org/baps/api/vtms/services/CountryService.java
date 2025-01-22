package org.baps.api.vtms.services;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.CountryMapper;
import org.baps.api.vtms.models.CountryModel;
import org.baps.api.vtms.models.entities.Country;
import org.baps.api.vtms.repositories.CountryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class CountryService {

    private final CountryMapper countryMapper;

    private final CountryRepository countryRepository;

    private final Translator translator;

    /**
     * Finds a country by its ID.
     *
     * @param countryId The ID of the country to find.
     * @return The {@link Country} object if found.
     * @throws DataNotFoundException If the country with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public Country findCountryById(final String countryId) {
        
        return countryRepository.findById(countryId).orElseThrow(() -> 
            new DataNotFoundException(translator.toLocal("country.with.country_id.not_found", countryId)));
    }    
    
    /**
     * Finds countries based on their International Dialing Code (ISD code).
     *
     * @param isdCode The International Dialing Code (ISD code) of the countries to find.
     * @return A {@link List} of {@link Country} objects representing the countries with the specified ISD code.
     *         If no countries are found, an empty list is returned.
     */
    @Transactional(readOnly = true)
    public List<Country> findCountriesByIsdCode(final String isdCode) {
        
        return countryRepository.findByIsdCode(isdCode);
    }    
    
    /**
     * Retrieves a list of CountryModel objects representing countries or regions.
     * This method queries the database for Country entities and converts them into
     * corresponding CountryModel objects for use in the application.
     *
     * @param filter A filter string for narrowing down the results (can be null or empty).
     * @return A list of CountryModel objects, or an empty list if no records match the filter.
     */
    @Transactional(readOnly = true)
    public List<CountryModel> getCoutryList(final String filter) {
        
        final var existingCountries = StringUtils.isBlank(filter) ? countryRepository.findAll() :
            countryRepository.findAll(toSpecification(filter, true, null, null));

        List<CountryModel> countryModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingCountries)) {
            countryModelList = countryMapper.countryListToCountryModelList(existingCountries);
        }

        return countryModelList;
    }                                             
}
