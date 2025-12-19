package streams;

import java.util.List;
import java.util.Map;

import static java.lang.System.out;
import static java.util.stream.Collectors.*;
import static streams.Persons.personList;

/**
 * Demo showing groupingBy and partitioningBy.
 */
public class Demo5_Grouping {

  public static void main(String[] args) {

    // groupingBy
    Map<Integer, List<Person>> groupedByAge =
            personList.stream()
                    .collect(groupingBy(Person::age));

    out.println("Persons grouped by age: %s".formatted(groupedByAge));

    // partitioningBy
    Map<Boolean, List<Person>> soccerOrNotSoccer =
            personList.stream()
                    .collect(partitioningBy(p -> p.sports().contains(Sport.SOCCER)));

    out.println("Persons partitioned by playing Soccer: %s".formatted(soccerOrNotSoccer));


  }

}
