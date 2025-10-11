package swj3.cache.client;

import swj3.cache.*;
import swj3.cache.impl.SimpleCache;
import swj3.util.AdjustableClock;

class StateChangedLogger implements StateChangedListener {

  @Override
  public void changed(EventType type, String key, Object value) {
    System.out.printf("%s: %s -> %s%n", type, key, value);
  }
}

public class CacheClient {

  public static void main(String[] args) {

    AdjustableClock clock = new AdjustableClock();
    TransactionalCache cache = new SimpleCache(clock);

    cache.set("Linz", 200_000);
    cache.set("Wien", 200_000_000);
    cache.set("Hagenberg", 3000);
    cache.set("Linz", 220_000);

    System.out.printf("Hagenberg: %d%n", cache.get("Hagenberg"));
    clock.incrementTime(2);
    System.out.printf("Hagenberg: %d%n", cache.get("Hagenberg")); // should be evicted

    System.out.printf("Linz: %d%n", cache.get("Linz"));
    System.out.printf("Wien: %d%n", cache.get("Wien"));

    cache.del("Wien");
    System.out.printf("Wien: %d%n", cache.get("Wien"));

    System.out.printf("%n===Event Handling===%n");

    StateChangedListener listener = new StateChangedLogger();
    cache.addStateChangedListener(listener);
    cache.set("Eisenstadt", 10_000);
    cache.set("Eisenstadt", 15_000, 3);
    clock.incrementTime(3);
    cache.set("Bregenz", 30_000);
    cache.del("Bregenz");
    cache.removeStateChangedListener(listener);
    cache.set("Eisenstadt", 12_000);

    System.out.printf("%n=== Transactions ===%n");
    cache.set("Innsbruck:inhabitants", 100_000);
    System.out.printf("Innsbruck:inhabitants (before tx): %s%n", cache.get("Innsbruck:inhabitants"));


//    Transaction tx = cache.beginTransaction();
//
//    tx.set("Innsbruck:inhabitants", 130_000);
//    tx.set("Innsbruck:region", "Tirol");
//
//    System.out.printf("Innsbruck:inhabitants (in tx): %s%n", cache.get("Innsbruck:inhabitants"));
//    System.out.printf("Innsbruck:region (in tx): %s%n", cache.get("Innsbruck:region"));
//
//    tx.exec();

//    System.out.printf("Innsbruck:inhabitants (after tx): %s%n", cache.get("Innsbruck:inhabitants"));
//    System.out.printf("Innsbruck:region: (after tx): %s%n", cache.get("Innsbruck:region"));

    try (Transaction tx = cache.beginTransaction()) {
      tx.set("Innsbruck:inhabitants", 130_000);
      tx.set("Innsbruck:region", "Tirol");

      System.out.printf("Innsbruck:inhabitants (in tx): %s%n", cache.get("Innsbruck:inhabitants"));
      System.out.printf("Innsbruck:region (in tx): %s%n", cache.get("Innsbruck:region"));
    } // automatic call of close

    System.out.printf("Innsbruck:inhabitants (after tx): %s%n", cache.get("Innsbruck:inhabitants"));
    System.out.printf("Innsbruck:region: (after tx): %s%n", cache.get("Innsbruck:region"));

  }
}
