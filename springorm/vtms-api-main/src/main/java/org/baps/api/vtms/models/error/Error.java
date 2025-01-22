package org.baps.api.vtms.models.error;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@Builder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Error {
    private List<ErrorDetail> details;

    @Data
    @Builder
    @JsonInclude(NON_EMPTY)
    public static class ErrorDetail {
        private ErrorType type;
        private String field;
        private String message;
        private String debug;
        private String action;
        private String link;
    }
}
