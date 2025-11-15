package swj3.pc.common;

import java.math.BigInteger;

public record FactorialTask(int number) implements Task {

    @Override
    public void execute() {
        try {
            BigInteger result = BigInteger.ONE;
            for (int i = 2; i <= number; i++) {
                result = result.multiply(BigInteger.valueOf(i));
                Thread.sleep(20); // Simulate time-consuming computation
            }
            System.out.println("%s: %d! = %s".formatted(this, number, result));
        } catch (InterruptedException e) {
            System.out.println("%s: interrupted".formatted(this));
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        return "FactorialTask(%d)".formatted(number);
    }
}