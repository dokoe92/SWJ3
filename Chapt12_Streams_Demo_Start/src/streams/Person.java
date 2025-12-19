package streams;

import java.util.List;

/**
 * Persons with name, age and list of sports.
 * @param name the name of the person
 * @param age the age of the person
 * @param sports the list of sports of this person
 */
public record Person(String name, int age, List<Sport> sports) { }

