package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class SiteModel {

    private String siteId;

    private String uuCode;

    private String name;

    private String shortName;

    private String description;

    private String timeZone;

    private String dateFormat;

    @DateTimeFormat(pattern = GeneralConstant.TIME_FORMAT_24)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.TIME_FORMAT_24)
    private LocalTime startTime;

    @DateTimeFormat(pattern = GeneralConstant.TIME_FORMAT_24)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.TIME_FORMAT_24)
    private LocalTime endTime;
}