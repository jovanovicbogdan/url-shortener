package dev.bogdanjovanovic.urlshortener.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record CreateShortUrlRequest(
    @URL
    @NotNull
    String url
) {

}
