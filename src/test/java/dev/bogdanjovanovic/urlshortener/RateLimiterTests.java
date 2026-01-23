package dev.bogdanjovanovic.urlshortener;

import static org.assertj.core.api.Assertions.assertThat;

import dev.bogdanjovanovic.urlshortener.shortener.presentation.api.dto.request.CreateShortUrlRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RateLimiterTests extends BaseTest {

  @Test
  void shouldNotGetRateLimited() {
    final var body = new CreateShortUrlRequest("https://long-url.com", null, null);
    for (int i = 0; i < 5; i++) {
      client.post()
          .uri("/api/v1/url")
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .body(body)
          .exchange()
          .expectStatus().isEqualTo(201);
    }
  }

  @Test
  void shouldGetRateLimitedWhenBucketIsFilled() {
    final var body = new CreateShortUrlRequest("https://long-url.com", null, null);
    var status = -1;
    for (int i = 0; i <= 60; i++) {
      status = client.post()
          .uri("/api/v1/url")
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .body(body)
          .exchange()
          .returnResult()
          .getStatus().value();
    }
    assertThat(status).isEqualTo(429);
  }

}
