package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.ServiceTemplateBasicInfoModel;
import org.baps.api.vtms.models.ServiceTemplateModel;
import org.baps.api.vtms.models.entities.ServiceTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public interface ServiceTemplateMapper {
    
    ServiceTemplateModel serviceTemplateToServiceTemplateModel(ServiceTemplate serviceTemplate);

    ServiceTemplateBasicInfoModel serviceTemplateToServiceTemplateBasicInfoModel(ServiceTemplate serviceTemplate);

    @Mapping(target = "serviceTemplateId", source = "serviceTemplateId") // Specify the mapping explicitly
    List<ServiceTemplateModel> serviceTemplateListToServiceTemplateModelList(List<ServiceTemplate> serviceTemplateList);
    
    default List<ServiceTemplateBasicInfoModel> serviceTemplateListToServiceTemplateBasicInfoModelList(
            List<ServiceTemplate> serviceTemplateBasicInfoModelList) {
        return serviceTemplateBasicInfoModelList.stream()
                .map(this::serviceTemplateToServiceTemplateBasicInfoModel)
                .collect(Collectors.toList());
    }
    
    default Set<String> serviceTemplateModelListToServiceTemplateIdSet(List<ServiceTemplateBasicInfoModel> serviceTemplateModelList) {
        return serviceTemplateModelList.stream().map(ServiceTemplateBasicInfoModel::getServiceTemplateId).collect(Collectors.toSet());
    }
}
