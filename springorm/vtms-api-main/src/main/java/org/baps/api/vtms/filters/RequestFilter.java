package org.baps.api.vtms.filters;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestFilter implements Filter {

    private static final String REDACTED = "<Redacted>";
    private static final String KEY_REQUEST_ID = "requestId";
    private static final String RESPONSE_HEADER_REQUEST_ID = "x-app-request-id";

    private static final String DOC_URL = "/docs/index.html";
    private static final String PROMETHEUS_URL = "/internal/prometheus";
    private static final String HEALTH_CHECK_URL = "/internal/health";
    private static final String REFRESH_TOKEN_URL = "/token/refresh";

    private final String[] redactRequestBodyURLs = {REFRESH_TOKEN_URL};
    private final String[] redactResponseBodyURLs = {DOC_URL, PROMETHEUS_URL, REFRESH_TOKEN_URL};
    // Disable logging of health check API calls as they are creating a lot of noise
    private final String[] disableRequestLoggingURLs = {HEALTH_CHECK_URL, PROMETHEUS_URL};

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        final CachedBodyRequestWrapper
            requestWrapper = new CachedBodyRequestWrapper(servletRequest);
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(servletResponse);

        final Instant startTime = Instant.now();

        try {
            MDC.put(KEY_REQUEST_ID, UUID.randomUUID().toString());

            if (Arrays.stream(disableRequestLoggingURLs).noneMatch(requestWrapper.getRequestURI()::contains)) {
                if (log.isInfoEnabled()) {
                    log.info("START request: Method={} API={} RequestBody={}", servletRequest.getMethod(), getRequestURI(servletRequest),
                        getRequestBody(requestWrapper));
                }

                servletResponse.addHeader(RESPONSE_HEADER_REQUEST_ID, MDC.get(KEY_REQUEST_ID));
            }
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (Arrays.stream(disableRequestLoggingURLs).noneMatch(requestWrapper.getRequestURI()::contains)
                || responseWrapper.getStatus() != HttpServletResponse.SC_OK) {
                final String responseBody = getResponseBody(requestWrapper, responseWrapper);

                responseWrapper.copyBodyToResponse();
                log.trace("ResponseBody={}", responseBody);

                if (log.isInfoEnabled()) {
                    final long totalTime = Duration.between(startTime, Instant.now()).toMillis();
                    log.info("END request: ResponseStatus={} Duration={}ms", responseWrapper.getStatus(), totalTime);
                }
            }
            // clean up MDC
            MDC.remove(KEY_REQUEST_ID);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private String getRequestURI(final HttpServletRequest servletRequest) {
        if (ObjectUtils.isEmpty(servletRequest.getQueryString())) {
            return servletRequest.getRequestURI();
        }
        return servletRequest.getRequestURI() + "?" + servletRequest.getQueryString();
    }

    private String getRequestBody(final CachedBodyRequestWrapper requestWrapper) throws IOException {
        if (Arrays.stream(redactRequestBodyURLs).anyMatch(requestWrapper.getRequestURI()::contains)) {
            return REDACTED;
        }
        return requestWrapper.getReader().lines().map(String::trim).collect(Collectors.joining());
    }

    private String getResponseBody(final CachedBodyRequestWrapper requestWrapper, final ContentCachingResponseWrapper responseWrapper) {
        if (Arrays.stream(redactResponseBodyURLs).anyMatch(requestWrapper.getRequestURI()::contains)) {
            return REDACTED;
        }
        final String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        return ObjectUtils.isEmpty(responseBody) ? null : responseBody;
    }
}
