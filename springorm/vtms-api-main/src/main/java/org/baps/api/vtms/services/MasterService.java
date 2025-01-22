package org.baps.api.vtms.services;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.LocationMapper;
import org.baps.api.vtms.models.LocationModel;
import org.baps.api.vtms.models.entities.Location;
import org.baps.api.vtms.repositories.LocationRepository;
import org.baps.api.vtms.repositories.specifications.GenericSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class MasterService {

    private final LocationMapper locationMapper;

    private final LocationRepository locationRepository;

    private final Translator translator;

    /**
     * Retrieves a list of locations based on the provided site code and optional filter.
     * If no filter is specified, all locations for the given site code are returned.
     *
     * @param siteUUCode The code identifying the site for which locations are to be retrieved.
     * @param filter   An optional filter to narrow down the location results.
     * @return A list of {@link LocationModel} objects representing the locations that match the criteria.
     */
    @Transactional(readOnly = true)
    public List<LocationModel> getLocationList(final String siteUUCode, final String filter) {

        final Specification<Location> specification = Specification.where(GenericSpecification.hasSiteCode(siteUUCode));

        final var existingLocationList = StringUtils.isBlank(filter) ? locationRepository.findAll(specification) :
            locationRepository.findAll(specification.and(toSpecification(filter, true, null, null)));

        List<LocationModel> locationModelList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existingLocationList)) {
            locationModelList = locationMapper.locationListToLocationModelList(existingLocationList);
        }

        return locationModelList;
    }

    /**
     * Retrieves a map of Location IDs with their corresponding Location entities based on a list of Location IDs
     * and a Service Template ID.
     *
     * @param locationIdList      A list of Location IDs to retrieve Location entities for.
     * @param serviceTemplateId   The ID of the Service Template associated with the Location entities.
     * @return                    A map containing Location IDs as keys and their corresponding Location entities as values.
     * @throws DataNotFoundException If no Location entities are found for the specified Location IDs and Service Template ID,
     *                                or if any of the Location IDs provided are invalid.
     */
    @Transactional(readOnly = true)
    public Map<String, Location> getMapOfLocationIdWithLocation(final List<String> locationIdList,
                                                                final String serviceTemplateId) {
        // Retrieve Location entities by Location IDs and Service Type and create a mapping
        final Map<String, Location> mapOfLocationIdWithExistingLocation = locationRepository
            .findByLocationIdInAndServiceLocationListServiceTemplateServiceTemplateId(locationIdList, serviceTemplateId)
            .stream().collect(Collectors.toMap(Location::getLocationId, l -> l));

        // Check if the mapping is not empty
        if (MapUtils.isNotEmpty(mapOfLocationIdWithExistingLocation)) {
            // Find Location IDs that were not found in the database
            final List<String> invalidLocationIds = locationIdList.stream()
                .filter(locationId -> !mapOfLocationIdWithExistingLocation.containsKey(locationId))
                .toList();

            // If invalid Location IDs were found, throw a DataNotFoundException
            if (CollectionUtils.isNotEmpty(invalidLocationIds)) {
                throw new DataNotFoundException(
                    translator.toLocal("location.with.service_template_id.not.found", invalidLocationIds, serviceTemplateId));
            }
        } else {
            // If no Location entities were found for the specified IDs, throw a DataNotFoundException
            throw new DataNotFoundException(translator.toLocal("location.with.service_template_id.not.found", locationIdList,
                    serviceTemplateId));
        }

        // Return the mapping of Location IDs to Location entities
        return mapOfLocationIdWithExistingLocation;
    }
}
