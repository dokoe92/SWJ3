package swj3.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BSTMultiSetTest {

    @Test
    public void add_insertsElements_sizeIncreasesAccordingly() {
        SortedMultiSet<Integer> s = new BSTMultiSet<>();
        assertEquals(0, s.size());
        s.add(5);
        assertEquals(1, s.size());
        s.add(1);
        assertEquals(2, s.size());
    }

    @Test
    public void get_existingElements_returnsCorrectValues() {
        SortedMultiSet<Integer> s = new BSTMultiSet<>();
        s.add(5);
        s.add(1);
        s.add(3);
        assertEquals(1, s.get(1).intValue());
        assertEquals(3, s.get(3).intValue());
        assertEquals(5, s.get(5).intValue());
        assertNull(s.get(99));
    }

    @Test
    public void iterator_next_throwsNoSuchElementExceptionWhenEmpty() {
        SortedMultiSet<Integer> s = new BSTMultiSet<>();
        s.add(5);
        Iterator<Integer> it = s.iterator();
        it.next();
        assertThrows(NoSuchElementException.class, () -> it.next());
    }
}