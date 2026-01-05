package dev.bogdanjovanovic.urlshortener.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("url")
public class UrlEntity {

  @Id
  private Long urlId;
  private String url;
  private String shortUrl;

}
