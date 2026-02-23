package Model.Composite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Comic;
import Model.ComicElement;

public class Volumes implements ComicElement {
    private final ArrayList<ComicElement> elements;
    private final int volume;

    public Volumes(int volume) {
        this.elements = new ArrayList<>();
        this.volume = volume;
    }

    public void addElement(ComicElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add a null Publisher.");
        }
        elements.add(element);
    }

    public boolean removeElement(ComicElement element) {
        return elements.remove(element);
    }

    @Override
    public ComicElement[] search(String query) {
        List<ComicElement> results = new ArrayList<>();
        for (ComicElement element : elements) {
            ComicElement[] childResults = element.search(query);
            Collections.addAll(results, childResults);
        }
        return results.toArray(ComicElement[]::new);
    }

    @Override
    public int getIssueCount() {
        int total = 0;
        for (ComicElement series : elements) {
            int sCount = series.getIssueCount();
            total += sCount;
        }
        return total;
    }

    @Override
    public double getValue() {
        double total = 0;
        for (ComicElement series : elements) {
            double sValue = series.getValue();
            total += sValue;
        }
        return total;
    }
    
    public ArrayList<ComicElement> getElements() {
        // ArrayList<ComicElement> e = new ArrayList<>();
        // Collections.copy(e, this.elements);
        return new ArrayList<>(this.elements);
    }

    @Override
    public boolean containsComic(Comic comic) {
        for (ComicElement ce : elements) {
            if (ce.containsComic(comic)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Vol.%d  (%d issues | $%.2f)", volume, getIssueCount(), getValue());
    }

    public int getVolume() {
        return volume;
    }
}
