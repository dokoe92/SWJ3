package swj3.cache.impl;

import swj3.cache.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

public class SimpleCache implements TransactionalCache {
  private final Map<String, Entry> dataStore = new HashMap<>();
  private final Clock clock;
  //  private final SortedMap<LocalDateTime, Set<String>> expiryTimes = new TreeMap<>();
  private final NavigableMap<LocalDateTime, Set<String>> expiryTimes = new TreeMap<>();
  private final List<StateChangedListener> listeners = new ArrayList<>();

  public SimpleCache() {
    this(Clock.systemDefaultZone());
  }

  public SimpleCache(Clock clock) {
    this.clock = clock;
  }

  @Override
  public Object get(String key) {
    evictExpiredEntries();
//    var entry = dataStore.get(key);
    Entry entry = dataStore.get(key);
    return entry == null ? null : entry.value;
  }

  @Override
  public void set(String key, Object value) {
    set(key, value, Long.MAX_VALUE);
  }

  @Override
  public void set(String key, Object value, long ttl) {
    evictExpiredEntries();

    // Remove previous expiration if exists
    Entry existingEntry = dataStore.get(key); // O(1)
    if (existingEntry != null) {
      Set<String> oldSet = expiryTimes.get(existingEntry.expireTime());
      if (oldSet != null) {
        oldSet.remove(key);
      }
    }
    // Add tge key to the expiryTime (O(log(t))
    LocalDateTime expireTime = ttl == Long.MAX_VALUE
            ? LocalDateTime.MAX : LocalDateTime.now(clock).plusSeconds(ttl);

    Set<String> keySet = expiryTimes.get(expireTime);
    if (keySet == null) {
      keySet = new HashSet<>();
      expiryTimes.put(expireTime, keySet); // O(log(t))
    }
    keySet.add(key);

    boolean isNew = dataStore.put(key, new Entry(value, expireTime)) == null; // O(1)
    fireStateChanged(isNew ? EventType.KEY_ADDED : EventType.VALUE_CHANGED, key, value);

  }

  // Complexity: O(log t)
  @Override
  public void del(String key) {
    evictExpiredEntries();

    Entry v = dataStore.remove(key); // O(1)
    if (v == null) return;
    Set<String> keySet = expiryTimes.get(v.expireTime()); // O(log t)
    if (keySet != null) keySet.remove(key);
    fireStateChanged(EventType.KEY_REMOVED, key, v.value);

  }

  // Version 1: Uses the Map interface to iterate over all expiration timestamps and
  // associated keys. Removes expired keys from both dataStore and expiryTimes efficiently.
  //
  // Complexity: O(t + e + e * log(t)),
  //   where t = number timestamps, n = number of entries, e = number of expired entries
//  private void evictExpiredEntries_v1() {
//    LocalDateTime now = LocalDateTime.now(clock);
//    List<LocalDateTime> expired = new ArrayList<>();
//    List<String> toEvict = new ArrayList<>();
//
//    // Determine expired entries and their associated timestamps
//    for (Map.Entry<LocalDateTime, Set<String>> entry : expiryTimes.entrySet()) { // O(t)
//      if (entry.getKey().isBefore(now) || entry.getKey().isEqual(now)) {
//        expired.add(entry.getKey());
//        toEvict.addAll(entry.getValue());
//      }
//    }
//
//    // Remove expired keys from dataStore and fire events
//    for (String key : toEvict) {              // O(e)
//      Object value = dataStore.remove(key); // O(1)
//      fireStateChanged(EventType.KEY_EVICTED, key, value);
//    }
//
//    // Remove expired entries from expiryTimes
//    for (LocalDateTime ts : expired) { // O(e * log(t))
//      expiryTimes.remove(ts);
//    }
//  }

  // Version 2: Uses NavigableMap to efficiently obtain all expired timestamps and
  // associated keys. Removes expired keys from both dataStore and expiryTimes in a single pass.
  //
  // Complexity: O(log(t) + e * log(t)),
  //   where t = number timestamps, e = number of expired entries
  private void evictExpiredEntries_v2() {
    LocalDateTime now = LocalDateTime.now(clock);
    // Get a view of all expiration times that are in the past or equal to now.
    NavigableMap<LocalDateTime, Set<String>> expired = expiryTimes.headMap(now, true); // O(log t)
    List<String> toEvict = new ArrayList<>();

    for (Set<String> keys : expired.values()) { // O(e)
      toEvict.addAll(keys);
    }

    // Remove expired keys from dataStore and fire events
    for (String key : toEvict) {       // 0(e)
      Entry v = dataStore.remove(key); // O(1)
      if (v != null) fireStateChanged(EventType.KEY_EVICTED, key, v.value);
    }

    // Remove expired entries from expiryTimes
    expired.clear(); // O(e * log(t))
  }

  private void evictExpiredEntries() {
    evictExpiredEntries_v2();
  }

  @Override
  public void addStateChangedListener(StateChangedListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeStateChangedListener(StateChangedListener listener) {
    listeners.remove(listener);
  }

  private void fireStateChanged(EventType type, String key, Object value) {
    for (StateChangedListener listener : listeners) {
      listener.changed(type, key, value);
    }
  }

  @Override
  public Transaction beginTransaction() {
    return new SimpleCacheTransaction();
  }

  private record Entry(Object value, LocalDateTime expireTime) {

  }

  private class SimpleCacheTransaction implements Transaction {
    private final SimpleCache cache = SimpleCache.this;
    private final List<Runnable> actions = new ArrayList<>();
    private boolean executed = false;


    @Override
    public Transaction set(String key, Object value) {
      return set(key, value, Long.MAX_VALUE);
    }

    @Override
    public Transaction set(String key, Object value, long ttl) {
      actions.add(() -> cache.set(key, value, ttl));
      return this;
    }

    @Override
    public Transaction del(String key) {
      actions.add(() -> cache.del(key));
      return this;
    }

    @Override
    public void exec() {
      if (this.executed) throw new IllegalStateException("Transaction is already executed");
      for (Runnable action : actions) {
        action.run();
      }
      this.executed = true;
    }
  }
}

