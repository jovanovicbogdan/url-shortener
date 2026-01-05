package dev.bogdanjovanovic.urlshortener.controller;

import dev.bogdanjovanovic.urlshortener.controller.dto.request.CreateShortUrlRequest;
import dev.bogdanjovanovic.urlshortener.controller.dto.response.ShortUrlResponse;
import dev.bogdanjovanovic.urlshortener.usecase.CreateShortUrlUseCase;
import dev.bogdanjovanovic.urlshortener.usecase.RetrieveUrlUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/url", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UrlController {

  private final CreateShortUrlUseCase createShortUrlUseCase;
  private final RetrieveUrlUseCase retrieveUrlUseCase;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ShortUrlResponse shortUrl(@RequestBody final CreateShortUrlRequest request) {
    final var shortUrl = createShortUrlUseCase.execute(request.url());
    return new ShortUrlResponse(shortUrl);
  }

  @GetMapping
  public ResponseEntity<?> retrieveUrl(@RequestParam("url") final String shortUrl) {
    final var url = retrieveUrlUseCase.execute(shortUrl);
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.LOCATION, url);
    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }

}
