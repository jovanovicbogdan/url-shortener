package dev.bogdanjovanovic.urlshortener.shortener.presentation.api.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record CreateShortUrlRequest(

    @URL
    @NotNull
    String url,

    @Nullable
    String alias,

    /**
     * Date time in a format of 'yyyy-MM-ddTHH:mm:ssZ', e.g. '2026-01-01T13:00:00Z' UTC
     */
    @Nullable
    String expiresAt

) {

}
