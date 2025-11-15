package swj3.pc.common;

import java.math.BigDecimal;
import java.math.MathContext;

// Approximate pi with this formula:
//   pi = 3 + 4 * Σ (-1)^(k+1) / [(2k) * (2k + 1) * (2k + 2)], for k = 1..inf
public record PiApproximationTask(int terms, int precision) implements Task {

    @Override
    public void execute() {
        try {
            MathContext mc = new MathContext(precision);
            BigDecimal sum = BigDecimal.valueOf(0);

            for (int k = 1; k <= terms; k++) {
                BigDecimal denom = BigDecimal.valueOf(2 * k)
                        .multiply(BigDecimal.valueOf(2 * k + 1))
                        .multiply(BigDecimal.valueOf(2 * k + 2));
                BigDecimal fraction = BigDecimal.valueOf(1).divide(denom, mc);
                sum = (k % 2 == 0) ? sum.subtract(fraction, mc) : sum.add(fraction, mc);

                Thread.sleep(1); // simulate time-consuming work
            }

            BigDecimal pi = BigDecimal.valueOf(4).multiply(sum, mc).add(BigDecimal.valueOf(3), mc);

            System.out.println("%s: pi ≈ %s".formatted(this, pi));
        } catch (InterruptedException e) {
            System.out.println("%s: interrupted".formatted(this));
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        return "PiApproximationTask(terms=%d, precision=%d)".formatted(terms, precision);
    }
}
