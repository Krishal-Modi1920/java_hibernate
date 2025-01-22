package org.baps.api.vtms.mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.baps.api.vtms.models.PersonnelBasicInfoModel;
import org.baps.api.vtms.models.PreBookedTourSlotModel;
import org.baps.api.vtms.models.TourDaySlotModel;
import org.baps.api.vtms.models.TourSlotModel;
import org.baps.api.vtms.models.TourSlotWrapperModel;
import org.baps.api.vtms.models.entities.Site;
import org.baps.api.vtms.models.entities.TourSlot;
import org.baps.api.vtms.models.entities.TourSlotPersonnel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
        )
public interface TourSlotMapper {

    @Mapping(source = "startDateTime", target = "startDateTime")
    @Mapping(source = "endDateTime", target = "endDateTime")
    @Mapping(source = "site", target = "site")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    TourSlot tourSlotWrapperModelToTourSlot(TourSlotWrapperModel tourSlotWrapperModel,
            LocalDateTime startDateTime, LocalDateTime endDateTime, Site site);

    @Mapping(target = "stage", source = "tourSlotStageEnum")
    @Mapping(target = "tourGuidePersonnelBasicInfoModel", expression =
            "java(mapPersonnelBasicInfoModelByTourSlotPersonnel(tourSlot.getTourSlotPersonnelList()))")
    TourSlotModel tourSlotToTourSlotModel(TourSlot tourSlot);

    default PersonnelBasicInfoModel mapPersonnelBasicInfoModelByTourSlotPersonnel(List<TourSlotPersonnel> tourSlotPersonnelList) {
        return Mappers.getMapper(PersonnelMapper.class).mapPersonnelBasicInfoModelByTourSlotPersonnel(tourSlotPersonnelList);
    }

    List<TourSlotModel> tourSlotListToTourSlotModelList(List<TourSlot> tourSlotList);

    @Mapping(target = "stage", source = "tourSlotStageEnum")
    PreBookedTourSlotModel tourSlotToPreBookedTourSlotModel(TourSlot tourSlot);

    List<PreBookedTourSlotModel> tourSlotListToPreBookedTourSlotModelList(List<TourSlot> tourSlotList);


    default TourSlotWrapperModel mapOfTourSlotDateWithTourSlotModelListToTourSlotWrapperModel(Map<LocalDate,
            List<TourSlotModel>> mapOfTourSlotDateWithTourSlotModelList, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        final TourSlotWrapperModel tourSlotWrapperModelResponse = new TourSlotWrapperModel();

        if (MapUtils.isNotEmpty(mapOfTourSlotDateWithTourSlotModelList)) {

            final var wrapperObject = new Object() {
                int minSlotInterval = 0;
            };

            tourSlotWrapperModelResponse.setStartDateTime(startDateTime);
            tourSlotWrapperModelResponse.setEndDateTime(endDateTime);

            tourSlotWrapperModelResponse.setTourDaySlotModelList(mapOfTourSlotDateWithTourSlotModelList.entrySet().stream()
                    .map(entry -> {
                        final TourSlotModel tourSlotModel = entry.getValue().stream().findFirst().get();

                        // Add 1 second to account for the time difference between storing and retrieving the value
                        final int tempMinSlotInterval = (int) ChronoUnit.MINUTES.between(tourSlotModel.getStartDateTime(),
                                tourSlotModel.getEndDateTime().plusSeconds(1));

                        if (tempMinSlotInterval != 0 || tempMinSlotInterval < wrapperObject.minSlotInterval) {
                            wrapperObject.minSlotInterval = tempMinSlotInterval;
                        }

                        final TourDaySlotModel tourDaySlotModel = new TourDaySlotModel();
                        tourDaySlotModel.setTourSlotDate(entry.getKey());
                        tourDaySlotModel.setTourSlotModelList(entry.getValue());
                        return tourDaySlotModel;
                    }).toList());

            tourSlotWrapperModelResponse.setSlotInterval(wrapperObject.minSlotInterval);

        }
        return tourSlotWrapperModelResponse;
    }
}
