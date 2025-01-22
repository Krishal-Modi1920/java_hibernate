package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 5808432219245075810L;

    @Size(min = 3, max = 36)
    @Pattern(regexp = "^[^\\s].*", message = "{document_model.title.invalid}")
    @NotBlank
    private String title;

    @NotBlank
    @Pattern(regexp = "^[^\\s].*", message = "{document_model.url.invalid}")
    private String url;

    @Size(max = 255)
    @Pattern(regexp = "(?s).*", message = "{document_model.note.invalid}")
    private String note;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt = LocalDateTime.now();
}
