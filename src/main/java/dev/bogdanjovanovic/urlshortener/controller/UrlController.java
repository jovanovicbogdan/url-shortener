package dev.bogdanjovanovic.urlshortener.controller;

import dev.bogdanjovanovic.urlshortener.controller.dto.request.CreateShortUrlRequest;
import dev.bogdanjovanovic.urlshortener.usecase.CreateShortUrlUseCase;
import dev.bogdanjovanovic.urlshortener.usecase.RedirectToOriginalUrlUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {

  private final CreateShortUrlUseCase createShortUrlUseCase;
  private final RedirectToOriginalUrlUseCase redirectToOriginalUrlUseCase;

  @PostMapping(value = "/api/v1/url", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> shortUrl(@Valid @RequestBody final CreateShortUrlRequest request) {
    final var location = createShortUrlUseCase.execute(request.url());
    return ResponseEntity.created(location).build();
  }

  @GetMapping("/{code}")
  public ResponseEntity<?> redirect(@PathVariable final String code) {
    final var url = redirectToOriginalUrlUseCase.execute(code);
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.LOCATION, url);
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

}
