package dev.bogdanjovanovic.urlshortener.common.util;

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

  public static String generate() {
    final var stringBuilder = new StringBuilder();

    var timestampFactor = System.currentTimeMillis();
    for (var i = 0; i < SEQ; i++) {
      stringBuilder.append(BASE62_CHARS[(int) (timestampFactor % BASE62_CHARS.length)]);
      timestampFactor /= 62;
    }

    return stringBuilder.toString();
  }

  public static String generate(int input) {
    if (input == 0) {
      return "0";
    }

    if (input < 0) {
      input = Math.abs(input);
    }

    final var stringBuilder = new StringBuilder();

    while (input > 0) {
      stringBuilder.append(BASE62_CHARS[input % BASE62_CHARS.length]);
      input /= 62;
    }

    return stringBuilder.toString();
  }

}
