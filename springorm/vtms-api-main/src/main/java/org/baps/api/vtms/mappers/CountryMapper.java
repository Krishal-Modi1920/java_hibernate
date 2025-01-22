package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.CountryModel;
import org.baps.api.vtms.models.entities.Country;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
        )
public interface CountryMapper {

    CountryModel countryToCountryModel(Country country);

    List<CountryModel> countryListToCountryModelList(List<Country> countryList);

}
