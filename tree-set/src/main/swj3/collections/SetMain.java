package swj3.collections;

import java.util.Iterator;

public class SetMain {

    public static void main(String[] args) {
        // SortedMultiSet<String> stringSet = new BSTMultiSet<String>();
        SortedMultiSet<String> stringSet = new BSTMultiSet<>();

        stringSet.add("B");
        stringSet.add("A");
        stringSet.add("C");
        stringSet.add("C");

        System.out.printf("stringSet.get(\"A\") -> %s%n", stringSet.get("A"));
        System.out.printf("stringSet.get(\"X\") -> %s%n", stringSet.get("X"));
        System.out.printf("stringSet.contains(\"A\") -> %b%n", stringSet.contains("A"));
        System.out.printf("stringSet.contains(\"X\") -> %b%n", stringSet.contains("X"));

        SortedMultiSet<Integer> intSet = new BSTMultiSet<>();
        intSet.add(42); // auto boxing int --> Integer
        int value = intSet.get(42); // auto unboxing

        System.out.printf("%s%n", stringSet.toString());
        System.out.printf("%s%n", stringSet);

        System.out.printf("%s%n", intSet.toString());
        System.out.printf("%s%n", intSet);

        SortedMultiSet<String> emptySet = new BSTMultiSet<>();
        try {
          System.out.printf("stringSet.first() -> %s%n", stringSet.first());
          System.out.printf("stringSet.last() -> %s%n", stringSet.last());
          System.out.printf("emptySet.first() -> %s%n", emptySet.first());
        } catch (EmptySetException e) {
          System.out.printf("*** EmptySetException: message = %s%n", e.getMessage());
        }


        System.out.println("------ enumerate elements of stringSet ------");
        Iterator<String> stringIterator = stringSet.iterator();
        while (stringIterator.hasNext()) {
          System.out.print(stringIterator.next() + " ");
        }
        System.out.println();

        System.out.println("------ enumerate elements using iterator interface ------");
        stringIterator = stringSet.iterator();
        while (stringIterator.hasNext()) {
          System.out.print(stringIterator.next() + " ");
        }
        System.out.println();

        System.out.println("------ enumerate elements using forEach method ------");
        // Requires implementation of interface Iterable.
        // Iterator interface provides default implementation of forEach method.
        stringSet.forEach(i -> System.out.print(i + " "));
        System.out.println();

        // Requires implementation of interface Iterable.
        System.out.println("------ enumerate elements using for loop ------");
        for (String item : stringSet)
          System.out.print(item + " ");
        System.out.println();

        //System.out.println("============= SortedMultiSet<Person> (default comparator) =============");

        //SortedMultiSet<Person> personSet1 = new BSTMultiSet<>();
        //personSet1.add(new Person("Wallner", LocalDate.of(1995, 1, 1)));
        //personSet1.add(new Person("Huber", LocalDate.of(1992, 1, 1)));
        //personSet1.add(new Person("Huber", LocalDate.of(1990, 1, 1)));
        //personSet1.add(new Person("Mayr", LocalDate.of(2000, 1, 1)));
        //System.out.printf("personSet1 = %s%n", personSet1);

        //System.out.println("========= SortedMultiSet<Person> (compare by dob) =============");
        //SortedMultiSet<Person> personSet2 = new BSTMultiSet<>(
        //        (p1, p2) -> p1.getDob().compareTo(p2.getDob())
        //);
        //for(Person person : personSet1) {
        //  personSet2.add(person);
        //}
        //System.out.printf("personSet2 = %s%n", personSet2);
    }



}
