package swj3.pc;

import java.math.BigInteger;

public record FactorialTask(int number) implements Task {

  @Override
  public void execute() {
    String threadName = Thread.currentThread().getName();
    try {
      BigInteger result = BigInteger.ONE;
      for (int i = 2; i <= number; i++) {
        result = result.multiply(BigInteger.valueOf(i));
        Thread.sleep(20); // Simulate time-consuming computation
      }
      System.out.printf("%s (%s)%n  %d! = %s%n", this, threadName, number, result);
    }
    catch (InterruptedException e) {
      System.out.printf("%s (%s)%n  interrupted%n", this, threadName);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public String toString() {
    return "FactorialTask(%d)".formatted(number);
  }
}