package org.baps.api.vtms.common.utils;


import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.models.base.PaginatedResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;


@Component
@RequiredArgsConstructor
public class CommonUtils {
    
    private final Translator translator;
    
    /**
     * Calculates pagination information based on a Page object and prepares a paginated response.
     *
     * @param page     The Page object containing data to be paginated.
     * @param response The response data to be included in the paginated response.
     * @param <T>      The type of the response data.
     * @return A PaginatedResponse containing pagination information and the response data.
     */
    public static <T> PaginatedResponse<T> calculatePaginationAndPrepareResponse(final Page<?> page, final T response) {
        final PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setTotalCount(page.getTotalElements());
        paginatedResponse.setTotalPages(page.getTotalPages());
        paginatedResponse.setPageNo(page.getNumber() + 1);
        paginatedResponse.setPageSize(page.getContent().size());
        paginatedResponse.setResponse(response);
        return paginatedResponse;
    }

    /**
     * Creates an empty {@link PaginatedResponse} object with the specified page number and page size.
     *
     * @param <T>       The type of data to be paginated
     * @param pageNo     The page number
     * @return An empty {@link PaginatedResponse} with the total count set to 0, total pages set to 0,
     *         and the specified page number and page size.
     */
    public static <T> PaginatedResponse<T> createEmptyPaginationAndResponse(final int pageNo) {
        final PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setTotalCount(0L);
        paginatedResponse.setTotalPages(0);
        paginatedResponse.setPageNo(pageNo);
        paginatedResponse.setPageSize(0);
        return paginatedResponse;
    }

    /**
     * Extracts a specific claim value from a JSON Web Token (JWT) using the provided key.
     *
     * @param token The JWT from which to extract the claim.
     * @param key   The key identifying the claim to extract.
     * @return A Claim object representing the extracted claim value, or null if the claim is not present.
     */
    public Claim getClaimValue(final String token, final String key) {
        // Decode the JWT
        final DecodedJWT decodedJWT = JWT.decode(token);

        // Get and return the claim using the provided key
        return decodedJWT.getClaim(key);
    }

    /**
     * Converts a string representation of sort direction to a {@link org.springframework.data.domain.Sort.Direction} value.
     *
     * @param sortDirection The string representation of sort direction ("asc" for ascending, "desc" for descending).
     * @param translator Translator is translator the message.
     * @return The corresponding {@link org.springframework.data.domain.Sort.Direction} value.
     * @throws DataValidationException If the provided sort direction is invalid.
     */
    public static Sort.Direction getSortDirection(final String sortDirection, final Translator translator) throws DataValidationException {

        final Sort.Direction direction;

        // Check if the provided sortDirection is valid
        if (StringUtils.isNotBlank(sortDirection) && !EnumUtils.isValidEnum(Sort.Direction.class, sortDirection)) {
            throw new DataValidationException(
                    translator.toLocal("invalid.sort.direction", Arrays.toString(Sort.Direction.values())));
        } else if (StringUtils.isNotBlank(sortDirection)) {
            direction = Sort.Direction.valueOf(sortDirection.toUpperCase());
        } else {
            direction = Sort.Direction.DESC;
        }
        return direction;
    }

    /**
     * Checks if a given property is valid by verifying its presence in a set of valid properties.
     *
     * @param property    The property to be validated.
     * @param propertySet The set of valid properties to compare against.
     * @return {@code true} if the property is valid; otherwise, {@code false}.
     */
    public static boolean isValidProperty(final String property, final Set<String> propertySet) {
        return StringUtils.isNotBlank(property) && !propertySet.contains(property);
    }

    /**
     * Decodes a URL-encoded string using the UTF-8 character encoding.
     *
     * @param encoded The URL-encoded string to decode.
     * @return The decoded string in UTF-8 encoding.
     */
    public static String decoderUTF(final String encoded) {
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    /**
     * Adds a custom error message to the validation context.
     *
     * @param context      The validation context.
     * @param messageKey   The error message to add.
     * @param propertyName The property name associated with the error.
     * @param args         Extra arguments.
     */
    public void addErrorToContext(final ConstraintValidatorContext context, final String propertyName, final String messageKey,
                                   final Object... args) {

        context.buildConstraintViolationWithTemplate(translator.toLocal(messageKey, args))
            .addPropertyNode(propertyName).addConstraintViolation();
    }
    
    /**
     * Checks for date and time conflicts between two date-time ranges.
     *
     * @param isCheckTime        Flag indicating whether to perform time-based checks.
     * @param firstStartDateTime Start date and time of the first range.
     * @param firstEndDateTime   End date and time of the first range.
     * @param secondStartDateTime Start date and time of the second range.
     * @param secondEndDateTime   End date and time of the second range.
     * @throws DataValidationException if a conflict is detected, with a message indicating the conflicting date-time range.
     */
    public void checkDateTimeRangeConflict(final boolean isCheckTime, 
                                           final LocalDateTime firstStartDateTime,
                                           final LocalDateTime firstEndDateTime,
                                           final LocalDateTime secondStartDateTime,
                                           final LocalDateTime secondEndDateTime) {
        if (isCheckTime && (firstStartDateTime.isBefore(secondStartDateTime) || firstEndDateTime.isAfter(secondEndDateTime))) {
            throw new DataValidationException(translator.toLocal("datetime.range.should_be.between.datetime.range",
                    secondStartDateTime.format(GeneralConstant.DATE_TIME_FORMATTER), 
                    secondEndDateTime.format(GeneralConstant.DATE_TIME_FORMATTER)));
        }
    }
    
    /**
     * Creates a predicate for filtering elements based on distinct values of a specified key.
     *
     * @param <T>           the type of elements to be filtered
     * @param keyExtractor function to extract the key for distinct filtering
     * @return a predicate for distinct filtering based on the specified key
     */
    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        // ConcurrentHashMap efficiently tracks seen keys for filtering
        final Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
        // Predicate checks and updates seen keys
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
    }
    
    /**
     * Retrieves a list of active visit stages based on the specified visit type.
     *
     * @param visitTypeEnum The type of visit for which to retrieve active stages.
     * @return A List of VisitStageEnum representing the active stages for the given visit type.
     */
    public static List<VisitStageEnum> getActiveVisitStageEnumListByVisitTypeEnum(final VisitTypeEnum visitTypeEnum) {
        if (VisitTypeEnum.VISIT.equals(visitTypeEnum))  { 
            return List.of(VisitStageEnum.PENDING, VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, VisitStageEnum.COMPLETED);
        } else if (VisitTypeEnum.TOUR.equals(visitTypeEnum)) { 
            return List.of(VisitStageEnum.ACCEPTED);
        }
        return Collections.emptyList();
    }
}
