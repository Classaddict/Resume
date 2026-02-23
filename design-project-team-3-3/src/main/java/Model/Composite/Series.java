package Model.Composite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Comic;
import Model.ComicElement;

public class Series implements ComicElement {
    private final ArrayList<ComicElement> elements;
    private final String series;

    public Series(String series) {
        this.elements = new ArrayList<>();
        this.series = series;
    }

    public void addElement(ComicElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add a null Series.");
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
        for (ComicElement s : elements) {
            int sCount = s.getIssueCount();
            total += sCount;
        }
        return total;
    }

    @Override
    public double getValue() {
        double total = 0;
        for (ComicElement s : elements) {
            double sValue = s.getValue();
            total += sValue;
        }
        return total;
    }
    
    public ArrayList<ComicElement> getElements() {
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
        return String.format("%s  (%d issues | $%.2f)", series, getIssueCount(), getValue());
    }

    public String getSeries() {
        return series;
    }
}
