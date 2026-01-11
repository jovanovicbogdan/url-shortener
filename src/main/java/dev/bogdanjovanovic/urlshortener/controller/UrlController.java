package dev.bogdanjovanovic.urlshortener.controller;

import dev.bogdanjovanovic.urlshortener.controller.dto.request.CreateShortUrlRequest;
import dev.bogdanjovanovic.urlshortener.usecase.CreateShortUrlUseCase;
import dev.bogdanjovanovic.urlshortener.usecase.RetrieveUrlUseCase;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UrlController {

  private final CreateShortUrlUseCase createShortUrlUseCase;
  private final RetrieveUrlUseCase retrieveUrlUseCase;

  @PostMapping(value = "/api/v1/url", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> shortUrl(@Valid @RequestBody final CreateShortUrlRequest request) {
    final var code = createShortUrlUseCase.execute(request.url());
    final var location = ServletUriComponentsBuilder.fromCurrentRequest()
            .replacePath("")
            .path("/{code}")
            .buildAndExpand(code)
            .toUri();
    return ResponseEntity.created(location).build();
  }

  @GetMapping("/{code}")
  public ResponseEntity<?> redirect(@PathVariable final String code) {
    final var url = retrieveUrlUseCase.execute(code);
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.LOCATION, url);
    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }

}
