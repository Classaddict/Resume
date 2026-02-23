package Model;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import Model.Composite.Publishers;
import Model.Composite.Series;
import Model.Composite.Volumes;

public class CompositeTest {
    @Test
    public void oneComicTest() {
        PersonalCollection pc = new PersonalCollection();
        Comic c = new Comic("Bladee", "Icedancer Chronicles", 1, 1, LocalDate.of(3333, 3, 3));
        Publishers p = new Publishers("Bladee");
        Series s = new Series("Icedancer Chronicles");
        Volumes v = new Volumes(1);

        v.addElement(c);
        s.addElement(v);
        p.addElement(s);
        pc.addElement(p);

        assertTrue(pc.containsComic(c));
    }
}

