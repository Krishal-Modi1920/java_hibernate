package org.baps.api.vtms.common.utils;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * The {@code Translator} class is a utility component responsible for providing internationalization (i18n)
 * support in the application. It enables the retrieval of localized messages from the configured
 * {@link MessageSource} based on the current user's locale.
 *
 * <p>The class offers two methods for translating messages:
 * - {@link #toLocal(String)}: Translates a message identified by the specified message key.
 * - {@link #toLocal(String, Object...)}: Translates a message identified by the message key with optional
 *   message parameters.
 *
 * <p>The class is designed to be used as a Spring bean and relies on the configured {@code MessageSource}
 * for message retrieval.
 *
 * @see org.springframework.context.MessageSource
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @see org.springframework.stereotype.Component
 */
@Component
@RequiredArgsConstructor
public class Translator {

    private final MessageSource messageSource;

    /**
     * Translates a message identified by the specified message key to the current user's locale.
     *
     * @param messageKey The key identifying the message to be translated
     * @return The translated message in the current user's locale
     */
    public String toLocal(final String messageKey) {
        return this.messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    /**
     * Translates a message identified by the message key with optional message parameters to the current user's locale.
     *
     * @param messageKey The key identifying the message to be translated
     * @param args       Optional message parameters
     * @return The translated message in the current user's locale with parameter substitution
     */
    public String toLocal(final String messageKey, final Object... args) {
        return this.messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}

