package streams;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.System.out;
import static streams.Persons.personList;

/**
 * Demo showing creating streams.
 */
public class Demo1_Create {

  public static void main(String[] args) {

    // stream
    out.println("personList:");
    personList.stream().forEach(p -> out.println(p));

    // Arrays.stream
    String[] words = new String[] {"Java", "with", "Streams"};
    out.println("\nwords:");
    Arrays.stream(words).forEach(System.out::println);

    // Stream.of
    Stream<String> stringStream = Stream.of("Java", "with", "Streams");
    out.println("\nstringStream:");
    stringStream.forEach(System.out::println);

    // iterate
    out.println("\npowerOf2:");
    Stream<Integer> power2 = Stream.iterate(2, p2 -> p2 * 2);
    power2.takeWhile(p2 -> p2 < Integer.MAX_VALUE / 4).forEach(System.out::println);

    // generate
    Random rand = new Random();
    out.println("\noptPrime:");
    Stream<Integer> rands =
            Stream.generate(() -> rand.nextInt(100))
                    .peek(out::println);
    var prime = rands.limit(4).filter(x -> isPrime(x)).findFirst();
    out.println(prime);

  }

  private static boolean isPrime(int n) {
    for (int i = 2; i <= Math.sqrt(n); i++) {
      if (n % i == 0) return false;
    }
    return true;
  }

}
