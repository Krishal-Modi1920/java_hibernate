package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.LookupModel;
import org.baps.api.vtms.models.entities.Lookup;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true),
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface LookupMapper {

    @Mapping(source = "parentLookup.lookupId", target = "parentLookupId")
    @Mapping(source = "childLookupModelList", target = "childLookup")
    LookupModel lookupToLookupModel(Lookup lookup);

    List<LookupModel> lookupListToLookupModelList(List<Lookup> lookupList);

}
