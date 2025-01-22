package org.baps.api.vtms.models;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class FeedBackRatingModel implements Serializable {
    
    @Serial
    private static final long serialVersionUID = -6586185573255318497L;
    
    @NotBlank
    private String key;

    @NotNull
    @Min(value = 0)
    @Max(value = 5)
    private int rating;

}
