package swj3.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class AdjustableClock extends Clock {

  private Instant currentTime;

  public AdjustableClock() {
    this.currentTime = Instant.now();
  }

  @Override
  public ZoneId getZone() {
    return ZoneId.systemDefault();
  }

  @Override
  public Clock withZone(ZoneId zone) {
    if (zone.equals(getZone())) {
      return this;
    }
    return Clock.system(zone);
  }

  @Override
  public Instant instant() {
    return currentTime;
  }

  public void incrementTime(long seconds) {
    currentTime = currentTime.plusSeconds(seconds);
  }
}
