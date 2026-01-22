package dev.bogdanjovanovic.urlshortener.common.filter;

import dev.bogdanjovanovic.urlshortener.common.service.BucketTokenRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

  private final BucketTokenRateLimiter bucketTokenRateLimiter;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain
  ) throws ServletException, IOException {
    final String ip = request.getRemoteAddr();

    final var isAllowed = bucketTokenRateLimiter.isAllowed(ip);

    if (isAllowed) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(429);
//    response.setHeader("X-Rate-Limit-Remaining", "1");
//    response.setHeader("X-Rate-Limit-Retry-After-Seconds", "60");
      response.setContentType(MediaType.TEXT_PLAIN_VALUE);
      response.getWriter().append("Too many requests");
    }

  }
}
