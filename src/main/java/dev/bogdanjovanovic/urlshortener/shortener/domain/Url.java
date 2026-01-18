package dev.bogdanjovanovic.urlshortener.shortener.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table
public class Url {

  @Id
  private Long urlId;
  private String originalUrl;
  private String shortUrl;
  @Nullable
  private String alias;
  @Nullable
  private Instant expiresAt;

  public boolean hasExpired() {
    if (expiresAt == null) {
      return false;
    }
    return expiresAt.isBefore(Instant.now());
  }

}
