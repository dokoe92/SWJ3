package swj3.collections;

import java.util.Objects;
import java.time.LocalDate;

public class Person implements Comparable<Person> {
    private final String name;
    private final LocalDate dob;

    public Person(String name, LocalDate dob) {
        this.name = name;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDob() {
        return dob;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, dob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dob);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Person)) return false;
        Person o = (Person) other;
        return name.equals(o.name) && dob.equals(o.dob);
    }

    @Override
    public int compareTo(Person other) {
        if (other == null) throw new NullPointerException();
        int cmp = name.compareTo(other.name);
        if (cmp != 0) return cmp;
        return dob.compareTo(other.dob);
    }
}
