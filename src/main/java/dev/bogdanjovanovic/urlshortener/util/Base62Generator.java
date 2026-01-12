package dev.bogdanjovanovic.urlshortener.util;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;

/**
 * Base62 unique random 6-chars generator class to be used as short URL ID. Currently, there are
 * 62<sup>6</sup> possible combinations to generate Base62 character string. The possibility of
 * collision is very low.
 */
@UtilityClass
public class Base62Generator {

  private static final char[] BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
  private static final int SEQ = 6;

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static String generate() {
    final var stringBuilder = new StringBuilder();

    var timestampFactor = System.currentTimeMillis();
    for (var i = 0; i < SEQ; i++) {
      stringBuilder.append(BASE62_CHARS[(int) (timestampFactor % BASE62_CHARS.length)]);

      // increase entropy to the generated string to handle the case
      // where the request lands at the exact same millisecond
      var randomCharIndex = SECURE_RANDOM.nextInt(BASE62_CHARS.length);
      randomCharIndex += 1;
      timestampFactor /= randomCharIndex;
    }

    return stringBuilder.toString();
  }

}
