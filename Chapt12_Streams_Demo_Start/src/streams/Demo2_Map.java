package streams;

import java.util.List;

import static java.lang.System.*;
import static streams.Persons.*;

/**
 * Demo showing map, filter, flatMap.
 */
public class Demo2_Map {

  public static void main(String[] args) {

    // map, filter
    List<List<Sport>> sports1 =
            personList.stream()
                    .map(p -> p.sports())
                    .filter(sports -> sports.size() > 0)
                    .toList();
    out.println("List of Lists of Sports: %s".formatted(sports1));

    // flatMap + distinct + sorted
    List<Sport> sports2 =
            personList
                    .stream()
                    .flatMap(p -> p.sports().stream())
                    .distinct()
                    .sorted()
                    .toList();
    out.println("Flat List of Sports: %s".formatted(sports2));
  }

}
