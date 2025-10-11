package swj3.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import swj3.cache.EventType;
import swj3.cache.StateChangedListener;
import swj3.cache.Transaction;
import swj3.cache.TransactionalCache;
import swj3.util.AdjustableClock;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleCacheTest {
  private TransactionalCache cache;
  private AdjustableClock clock;

  @BeforeEach
  void setUp() {
    clock = new AdjustableClock();
    cache = new SimpleCache(clock);
  }

  @Test
  void setAndGet_shouldReturnStoredValue() {
    cache.set("foo", "bar");
    assertEquals("bar", cache.get("foo"));
  }

  @Test
  void setWithTtl_shouldEvictAfterExpiration() {
    cache.set("key", "value", 2);
    assertEquals("value", cache.get("key"));
    clock.incrementTime(3);
    assertNull(cache.get("key"));
  }

  @Test
  void setWithTtl_shouldReturnValueBeforeExpiration() {
    cache.set("key", "value", 2);
    assertEquals("value", cache.get("key"));
    clock.incrementTime(1);
    assertEquals("value", cache.get("key"));
  }

  @Test
  void remove_shouldDeleteKey() {
    cache.set("foo", "bar");
    cache.del("foo");
    assertNull(cache.get("foo"));
  }

  @Test
  void addStateChangedListener_shouldTriggerEvent() {
    AtomicReference<EventType> eventType = new AtomicReference<>(); // wrapper type needed so that value can be changed
    // in lambda expression
    StateChangedListener listener = (type, key, value) -> eventType.set(type);

    cache.addStateChangedListener(listener);
    cache.set("foo", "bar");
    assertEquals(EventType.KEY_ADDED, eventType.get());
  }

  @Test
  void removeStateChangedListener_shouldStopTriggeringEvent() {
    AtomicReference<EventType> eventType = new AtomicReference<>(); // wrapper type needed so that value can be changed
    StateChangedListener listener = (type, key, value) -> eventType.set(type);

    cache.addStateChangedListener(listener);
    cache.removeStateChangedListener(listener);
    cache.set("bar", "baz");
    assertNull(eventType.get());
  }

  @Test
  void setExistingKey_shouldTriggerValueChangedEvent() {
    AtomicReference<EventType> eventType = new AtomicReference<>();
    cache.addStateChangedListener((type, key, value) -> eventType.set(type));
    cache.set("key", "value1");
    cache.set("key", "value2");
    assertEquals(EventType.VALUE_CHANGED, eventType.get());
  }

  @Test
  void expiredKey_shouldTriggerKeyEvictedEvent() {
    AtomicReference<EventType> eventType = new AtomicReference<>();
    cache.addStateChangedListener((type, key, value) -> eventType.set(type));
    cache.set("key", "value", 1);
    clock.incrementTime(2);
    cache.get("key");
    assertEquals(EventType.KEY_EVICTED, eventType.get());
  }

  @Test
  void transaction_exec_shouldPersistChanges() {
    Transaction tx = cache.beginTransaction();
    tx.set("key1", "value1");
    tx.set("key2", 123);
    tx.del("key3");

    // Before exec, changes are not visible
    assertNull(cache.get("key1"));
    assertNull(cache.get("key2"));

    // After exec, key/value pairs should be inserted
    tx.exec();
    assertEquals("value1", cache.get("key1"));
    assertEquals(123, cache.get("key2"));
  }

  @Test
  void transaction_exec_shouldRemoveKey() {
    cache.set("key", "value");
    Transaction tx = cache.beginTransaction();
    tx.del("key");

    // Before exec, key still exists
    assertEquals("value", cache.get("key"));

    // After exec, key/value pair should be deleted
    tx.exec();
    assertNull(cache.get("key"));
  }

  @Test
  void transaction_exec_shouldThrowIfExecutedTwice() {
    Transaction tx = cache.beginTransaction();
    tx.set("key", "value");
    tx.exec();
    assertThrows(IllegalStateException.class, tx::exec);
  }
}
