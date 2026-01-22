package dev.bogdanjovanovic.urlshortener.common.util;

import java.net.URI;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@UtilityClass
public class HttpResponseUtils {

  /**
   * Builds URI from current request by appending path to the end to be used in Location response
   * header.
   *
   * @param path value to append
   *
   * @return new URI with appended path
   */
  public static URI buildLocationWithPath(final String path) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .replacePath("")
        .path("/{path}")
        .buildAndExpand(path)
        .toUri();
  }

  public static URI buildLocationFromUrlPath(final String url) {
    final var path = URI.create(url).getPath();
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .replacePath("")
        .path("{path}")
        .buildAndExpand(path)
        .toUri();
  }

}
