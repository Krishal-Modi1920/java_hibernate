package org.baps.api.vtms.services;

import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.SpecificationConstants;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.mappers.ServiceTemplateMapper;
import org.baps.api.vtms.models.ServiceTemplateModel;
import org.baps.api.vtms.models.entities.ServiceTemplate;
import org.baps.api.vtms.repositories.ServiceTemplateRepository;
import org.baps.api.vtms.repositories.specifications.GenericSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.apache.commons.collections.CollectionUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class ServiceTemplateService {

    private final Translator translator;

    private final ServiceTemplateMapper serviceTemplateMapper;

    private final ServiceTemplateRepository serviceTemplateRepository;

    /**
     * Retrieves a list of ServiceTemplateModel objects based on the specified RSQL filter.
     *
     * @param siteUUCode The unique code associated with the site.
     * @param filter     A filter expression in RSQL format (nullable).
     * @return A list of matching ServiceTemplateModel objects or an empty list.
     */
    @Transactional(readOnly = true)
    public List<ServiceTemplateModel> getAllServiceTemplatesWithFilters(final String siteUUCode,
                                                                        @Nullable final String filter) {

        final Specification<ServiceTemplate> specification = Specification.where(GenericSpecification.hasSiteCode(siteUUCode));

        final List<ServiceTemplate> serviceTemplateList =
            serviceTemplateRepository.findAll(specification.and(RSQLJPASupport.toSpecification(filter)),
                Sort.by(Sort.Direction.ASC, SpecificationConstants.NAME));

        return serviceTemplateMapper.serviceTemplateListToServiceTemplateModelList(serviceTemplateList);
    }

    /**
     * Retrieves a Service Template by its ID and Service Type.
     *
     * @param serviceTemplateId              The ID of the Service Template to retrieve.
     * @param serviceTypeEnum The Service Type of the Service Template.
     * @param siteUUCode      The unique code associated with the site.
     * @return The Service Template with the specified ID and Service Type.
     * @throws DataNotFoundException If no Service Template with the given ID and Service Type is found.
     */
    @Transactional(readOnly = true)
    public ServiceTemplate findByIdAndServiceTypeEnum(final String serviceTemplateId, final ServiceTypeEnum serviceTypeEnum,
                                                      final String siteUUCode) {

        // Attempt to find a Service Template by its ID and Service Type, or throw an exception if not found
        return serviceTemplateRepository.findByServiceTemplateIdAndServiceTypeEnumAndSiteUuCode(
                serviceTemplateId, serviceTypeEnum, siteUUCode).orElseThrow(() -> new DataNotFoundException(translator.toLocal(
                "service.template.with.service_template_id.and.service.type.not.found", serviceTemplateId, serviceTypeEnum)));
    }

    /**
     * Retrieves the first ServiceTemplate of the specified Service Type associated with the given site unique code.
     *
     * @param serviceTypeEnum The type of service to filter by.
     * @param siteUUCode      The unique code associated with the site.
     * @return The first ServiceTemplate matching the provided Service Type and site unique code.
     * @throws DataNotFoundException if no ServiceTemplate is found with the specified Service Type.
     */
    @Transactional(readOnly = true)
    public ServiceTemplate findFirstByServiceTypeEnum(final ServiceTypeEnum serviceTypeEnum, final String siteUUCode) {

        // Attempt to find a Service Template by its ID and Service Type, or throw an exception if not found
        return serviceTemplateRepository.findFirstByServiceTypeEnumAndSiteUuCode(serviceTypeEnum, siteUUCode)
            .orElseThrow(() -> new DataNotFoundException(translator.toLocal("service.template.with.service.type.not.found",
                    serviceTypeEnum)));
    }
    

    @Transactional(readOnly = true)
    public List<ServiceTemplate> findByServiceTemplateIdsAndSiteUUCode(final Set<String> serviceTemplateIds, final String siteUUCode) {
        
        final List<ServiceTemplate> existingServiceTemplates = 
                serviceTemplateRepository.findByServiceTemplateIdInAndSiteUuCode(serviceTemplateIds, siteUUCode);
        
        final Set<String> existingServiceTemplateIds = existingServiceTemplates.stream()
                .map(ServiceTemplate::getServiceTemplateId)
                .collect(Collectors.toSet());
        
        final Set<String> invalidServiceTemplateIds = serviceTemplateIds.stream()
                .filter(serviceTemplateId -> !existingServiceTemplateIds.contains(serviceTemplateId))
                .collect(Collectors.toSet());
        
        if (CollectionUtils.isNotEmpty(invalidServiceTemplateIds)) {
            throw new DataValidationException(translator.toLocal("service_template.invalid_service_template_ids",
                    invalidServiceTemplateIds));
        }
        // Attempt to find a Service Template by its ID and Service Type, or throw an exception if not found
        return existingServiceTemplates;
    }


}
