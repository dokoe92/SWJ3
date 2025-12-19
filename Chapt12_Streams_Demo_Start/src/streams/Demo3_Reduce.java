package streams;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static java.lang.System.out;
import static streams.Persons.personList;

/**
 * Demo showing reduce, min, max.
 */
public class Demo3_Reduce {

  public static void main(String[] args) {

    // reduce(z, acc, comb)
    int sum1 =
            personList.stream()
                    .reduce(0,
                            (s, p) -> s + p.age(),
                            (s1, s2) -> s1 + s2);
    out.println("Sum of ages 1: %d".formatted(sum1));

    // reduce(z, acc) mit map
    int sum2 =
            personList.stream()
                    .mapToInt(p -> p.age())
//                    .reduce(0,
//                            (s, a) -> s + a);
                    .sum();
    out.println("Sum of ages 2: %d".formatted(sum2));

    // average
    OptionalDouble average =
            personList.stream()
                    .mapToInt(p -> p.age())
                    .average();

    // max mit reduce
    Optional<Person> oldestOptional =
            personList.stream()
                    .reduce((o, p) -> p.age() > o.age() ? p : o);

    Optional<Person> oldestWithMax =
            personList.stream()
                    .max(Comparator.comparing(p -> p.age()));
    out.println("Oldest: %s".formatted(oldestOptional.orElseGet(() -> new Person("NN", 0, List.of()))));

    // min
    Optional<Integer> minAge =
            null;  // TODO
    out.println("Youngest: %s".formatted(minAge));

    // max mit sports().size()
    Optional<Person> fittest =
            personList.stream()
                    .max(Comparator.comparingInt(p -> p.sports().size()));
    out.println("Fittest: %s".formatted(fittest));
  }

}
