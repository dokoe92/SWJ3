package streams;

import java.util.List;

import static streams.Sport.*;

/**
 * Class with statics for persons.
 */
public class Persons {
  private Persons() {}

  public static List<Person> personList =
      List.of(
          new Person("Hans", 25, List.of(TENNIS, SOCCER)),
          new Person("Fritz", 23, List.of(SKIING, SOCCER)),
          new Person("Lois", 23, List.of(GOLF)),
          new Person("Peter", 24, List.of()),
          new Person("Paul", 24, List.of(SWIMMING, SKIING, GOLF))
      );


}