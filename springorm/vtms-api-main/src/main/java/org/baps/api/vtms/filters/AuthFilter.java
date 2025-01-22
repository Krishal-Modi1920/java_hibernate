package org.baps.api.vtms.filters;

import org.baps.api.vtms.common.utils.CommonUtils;
import org.baps.api.vtms.common.utils.Translator;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.RedisEventEnum;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.repositories.PersonnelRepository;
import org.baps.api.vtms.services.RedisService;
import org.baps.api.vtms.services.SsoClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class AuthFilter extends OncePerRequestFilter {

    @Value("${public.auth.key}")
    private String publicAuthKey;

    private final SsoClientService ssoClientService;

    private final RedisService redisService;

    private final CommonUtils commonUtils;

    private final PersonnelRepository personnelRepository;

    private final TransactionTemplate transactionTemplate;

    private final Translator translator;

    /**
     * Handles authentication and authorization for HTTP requests by validating
     * the authorization token and setting the user's authentication context.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException if a servlet error occurs.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
        throws ServletException, IOException {

        final List<String> whiteListAPI = List.of("/swagger-ui", "/internal/api-docs", "/internal/docs.html");
        final List<String> publicAPI = List.of("/public");

        // Check if the request path is in the whitelist or if it's an OPTIONS request
        final boolean isWhiteListRequest = whiteListAPI.stream().anyMatch(path -> request.getServletPath().contains(path));
        final boolean isPublicRequest = publicAPI.stream().anyMatch(path -> request.getServletPath().contains(path));

        boolean shouldContinue = false;

        if (isWhiteListRequest) {
            shouldContinue = true;
        } else {
            if (isPublicRequest) {
                shouldContinue = validatePublicUrlToken(request, response);
            } else {
                shouldContinue = validatePrivateUrlToken(request, response);
            }
        }

        if (shouldContinue) {
            filterChain.doFilter(request, response); // Execute the filter chain only if the flag is true
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Validates the public token key present in the HTTP request headers.
     *
     * @param request  The HttpServletRequest object representing the incoming HTTP request.
     * @param response The HttpServletResponse object representing the outgoing HTTP response.
     * @return True if the public token key is valid, false otherwise.
     */
    private boolean validatePublicUrlToken(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            // Retrieve the public token key from the request headers
            final String publicTokenKey = request.getHeader(GeneralConstant.X_APP_PUBLIC_TOKEN_KEY);

            // Check if the public token key is blank or missing
            if (StringUtils.isBlank(publicTokenKey)) {
                // Send an unauthorized response with an error message
                response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("x_app_public_token_key.is_required"));
                return false;
            } else if (!publicTokenKey.equals(publicAuthKey)) {
                // Send an unauthorized response with an error message
                response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("x_app_public_token_key.invalid"));
                return false;
            }
            return true;

        } catch (NoSuchElementException | IOException e) {
            // Log any exceptions that occur during the validation process
            log.error("Error in AuthFilter: ", e);
            return false;
        }
    }

    private boolean validatePrivateUrlToken(final HttpServletRequest request, final HttpServletResponse response) {
        final var wrapperObject = new Object() {
            boolean shouldContinue = true;
        };

        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(final TransactionStatus status) {
                try {
                    final String token = request.getHeader(HttpHeaders.AUTHORIZATION);

                    if (StringUtils.isBlank(token)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("token.is_required"));
                        wrapperObject.shouldContinue = false;
                    }

                    String tokenPid = null;
                    try {
                        tokenPid = commonUtils.getClaimValue(request.getHeader(HttpHeaders.AUTHORIZATION), "pid").asString();
                    } catch (final JWTDecodeException e) {
                        log.error("Error in decode jwt: ", e);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("invalid.token"));
                        wrapperObject.shouldContinue = false;
                    }

                    if (StringUtils.isEmpty(tokenPid)) {
                        log.error("Empty tokenPid");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("pid.not.found.in.token"));
                        wrapperObject.shouldContinue = false;
                    }

                    if (StringUtils.isBlank(token) || !isAuthorizeRequest(token) || !authorizePID(tokenPid)) {
                        SecurityContextHolder.getContext().setAuthentication(null);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, translator.toLocal("invalid.token"));
                        wrapperObject.shouldContinue = false;
                    }

                } catch (NoSuchElementException | IOException e) {
                    log.error("Error in AuthFilter: ", e);
                    status.setRollbackOnly();
                    wrapperObject.shouldContinue = false;
                }
            }
        });

        return wrapperObject.shouldContinue;
    }

    /**
     * Checks whether a user is authorized based on the provided JWT token. It
     * compares the "pid" claim from the token with the stored PID in Redis.
     *
     * @param token The JWT token to validate.
     * @return true if the user is authorized, false otherwise.
     */
    public boolean isAuthorizeRequest(final String token) {

        // Extract the "pid" claim from the JWT token
        final String tokenPid = commonUtils.getClaimValue(token, "pid").asString();

        // Retrieve the stored PID from Redis based on the token
        final String existingPid = redisService.get(token, RedisEventEnum.GET_TOKEN);

        if (StringUtils.isBlank(tokenPid)) {
            log.error("Token is required.");
            return false;

        } else if (StringUtils.isNotBlank(existingPid)) {
            if (!existingPid.equals(tokenPid)) {
                log.error("Token not match from redis server");
                return false;
            } else {
                return true;
            }

        } else if (ssoClientService.isValidateToken(token)) {
            redisService.save(token, tokenPid, RedisEventEnum.SAVE_TOKEN, JWT.decode(token).getExpiresAt());
            return true;
            
        } else {
            log.error("Unauthorized token from SSO server");
            return false;
        }
    }

    /**
     * Authorizes a user with a valid PID by creating an Authentication object and
     * setting it in the SecurityContextHolder. This method is called when a user
     * is successfully authorized.
     *
     * @param pid The PID (Personnel ID) of the user to authorize.
     * @return {@code true} if the authorization is successful; {@code false} otherwise.
     */
    private boolean authorizePID(final String pid) {

        final Optional<Personnel> optionalPersonnel = personnelRepository.findByUucode(pid);
        
        if (optionalPersonnel.isEmpty()) {
            return false;
        }

        final UserDetails userDetails = new User(optionalPersonnel.get().getPersonnelId(), "", optionalPersonnel.get().getAuthorities());

        // Create an Authentication object and set the permissions.
        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Set the Authentication object in the SecurityContextHolder.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return true;
    }
}