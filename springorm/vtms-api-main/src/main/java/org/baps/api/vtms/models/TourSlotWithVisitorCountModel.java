package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TourSlotWithVisitorCountModel {
    private String tourSlotId;

    private Long bookedVisitorCount;
}
