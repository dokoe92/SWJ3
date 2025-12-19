package streams;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static streams.Persons.personList;
import static java.util.stream.Collectors.*;

/**
 * Demo showing collect.
 */
public class Demo4_Collect {

  public static void main(String[] args) {

    // collect(s, a, c)
    List<String> listOfNames =
            personList.stream()
                    .map(p -> p.name())
//                    .collect(toList());
                    .toList();

    out.println("List of names: %s".formatted(listOfNames));

    // Collectors.toList, toSet, toMap, toCollection
    Set<Person> setOfNames =
            personList.stream()
                    .collect(toSet());

    out.println("Set of names: %s".formatted(setOfNames));

    SortedSet<String> sortedNames =
            personList.stream()
                    .map(Person::name)
                    .collect(Collectors.toCollection(() -> new TreeSet<>()));

    out.println("Sorted set of names: %s".formatted(sortedNames));

    Map<String, Integer> name2Age =
            personList.stream()
                    .collect(toMap(p -> p.name(), p -> p.age()));

    out.println("Map of names to age: %s".formatted(name2Age));

  }

}
