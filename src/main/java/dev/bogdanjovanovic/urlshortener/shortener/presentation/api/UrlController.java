package dev.bogdanjovanovic.urlshortener.shortener.presentation.api;

import dev.bogdanjovanovic.urlshortener.common.exception.BadRequestException;
import dev.bogdanjovanovic.urlshortener.shortener.application.usecase.CreateCounterBasedShortUrlUseCase;
import dev.bogdanjovanovic.urlshortener.shortener.application.usecase.CreateHashBasedShortUrlUseCase;
import dev.bogdanjovanovic.urlshortener.shortener.application.usecase.RedirectToOriginalUrlUseCase;
import dev.bogdanjovanovic.urlshortener.shortener.presentation.api.dto.request.CreateShortUrlRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {

  private final CreateHashBasedShortUrlUseCase createHashBasedShortUrlUseCase;
  private final RedirectToOriginalUrlUseCase redirectToOriginalUrlUseCase;
  private final CreateCounterBasedShortUrlUseCase createCounterBasedShortUrlUseCase;

  @PostMapping(value = "/api/v1/url", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createHashBasedShortUrl(
      @Valid @RequestBody final CreateShortUrlRequest request) {
    final var expiresAt = parseExpiresAt(request.expiresAt());
    final var uri = URI.create(request.url());
    final var location = createHashBasedShortUrlUseCase.execute(uri.hashCode(), request.alias(),
        expiresAt);
    return ResponseEntity.created(location).build();
  }

  @PostMapping(value = "/api/v2/url", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createCounterBasedShortUrl(
      @Valid @RequestBody final CreateShortUrlRequest request) {
    final var expiresAt = parseExpiresAt(request.expiresAt());
    final var uri = URI.create(request.url());
    final var location = createCounterBasedShortUrlUseCase.execute(uri.hashCode(), request.alias(),
        expiresAt);
    return ResponseEntity.created(location).build();
  }

  @GetMapping("/{code}")
  public ResponseEntity<?> redirect(@PathVariable final String code) {
    final var url = redirectToOriginalUrlUseCase.execute(code);
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.LOCATION, url);
    return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
  }

  private Instant parseExpiresAt(@Nullable final String expiresAt) {
    if (expiresAt != null) {
      try {
        final var parsed = Instant.parse(expiresAt).truncatedTo(ChronoUnit.SECONDS);
        if (parsed.isBefore(Instant.now())) {
          log.info("Expiration date cannot be in the past");
          throw new BadRequestException();
        }
        return parsed;
      } catch (DateTimeParseException ex) {
        log.info("Invalid date time format provided");
        throw new BadRequestException();
      }
    }

    return null;
  }

}
