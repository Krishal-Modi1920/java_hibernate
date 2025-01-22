package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.VisitStageEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageModel implements Serializable {

    @Serial
    private static final long serialVersionUID = -8751939538487347163L;

    @EnumValue(enumClass = VisitStageEnum.class)
    private String stage;

    @Size(max = 255)
    private String reasonType;

    @Size(max = 255)
    private String reason;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StageModel(final String stage) {
        this.stage = stage;
    }
}
