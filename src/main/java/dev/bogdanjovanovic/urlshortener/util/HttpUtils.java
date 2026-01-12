package dev.bogdanjovanovic.urlshortener.util;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class HttpUtils {

  public static URI locationWithPath(final String path) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .replacePath("")
        .path("/{path}")
        .buildAndExpand(path)
        .toUri();
  }

  public static URI locationFromUrl(final String url) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .replacePath("")
        .path(URI.create(url).getPath())
        .build()
        .toUri();
  }

  public static URI urlFromCurrentRequest(final String path) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .replacePath("")
        .path("/{path}")
        .buildAndExpand(path)
        .toUri();
  }

}
