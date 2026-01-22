package dev.bogdanjovanovic.urlshortener.shortener.application;

import java.util.List;
import java.util.function.Consumer;
import redis.clients.jedis.AbstractTransaction;
import redis.clients.jedis.params.SetParams;

public interface CacheService {

  String set(String key, String value);

  String set(String key, String value, SetParams params);

  String get(String key);

  Boolean exists(String key);

  Long incrAndGet(String key);

  List<Object> multi(final Consumer<AbstractTransaction> transaction);

}
