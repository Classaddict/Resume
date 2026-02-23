package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Composite.Publishers;
import Model.Decorator.ValueDecorator;
import Model.Strategy.ComicSorter;
import Model.Strategy.SortByName;
/**
 * Represents a user's personal comic book collection.
 *
 * Role in the Composite pattern: <b>Composite</b> (root node of the hierarchy).
 *
 * The collection organises comics into the following hierarchy:
 *   PersonalCollection → Publishers → Series → Volumes → Comic (Leaf)
 *
 * Operations on the PersonalCollection cascade recursively through
 * the child {@link ComicElement}s, allowing statistics (issue count,
 * aggregate value) to be gathered at every level.
 *
 * The class also acts as the <b>Receiver</b> in the Command pattern –
 * concrete commands such as {@code AddComicCommand}, {@code RemoveComicCommand},
 * and {@code EditComicCommand} delegate to the methods defined here.
 */
public class PersonalCollection implements ComicElement {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Flat list of all {@link ComicElement}s directly owned by this collection.
     * In the full system these would be {@link Publishers} composite nodes;
     * for simplicity the collection also accepts raw {@link Comic} leaves so
     * that manually-entered comics can be added without a publisher hierarchy.
     */
    private final List<ComicElement> elements;
    private ComicSorter sorter;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Creates an empty PersonalCollection. */
    public PersonalCollection() {
        this.elements = new ArrayList<>();
        this.sorter = new SortByName(); // Calvin added this to make it a part of a collection's state
    }

    /** Sets the strategy for the collection - Calvin's addition*/
    public void setStrategy(ComicSorter sorter) {
        this.sorter = sorter;
    }


    // -------------------------------------------------------------------------
    // Composite management (add / remove / get)
    // -------------------------------------------------------------------------

    /**
     * Adds a {@link ComicElement} to this collection.
     * In the Composite pattern this allows both leaf Comics and composite
     * nodes (Publishers, Series, Volumes) to be inserted at the top level.
     *
     * @param element the element to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code element} is {@code null}
     */
    public void addElement(ComicElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add a null ComicElement.");
        }
        elements.add(element);
    }

    /**
     * Removes a {@link ComicElement} from this collection.
     *
     * @param element the element to remove
     * @return {@code true} if the element was present and removed
     */
    public boolean removeElement(ComicElement element) {
        return elements.remove(element);
    }

    /**
     * Returns an unmodifiable view of the direct children of this collection.
     *
     * @return list of child {@link ComicElement}s
     */
    public List<ComicElement> getElements() {
        return List.copyOf(elements);
    }

    // -------------------------------------------------------------------------
    // Comic-level convenience methods (used by Command subsystem)
    // -------------------------------------------------------------------------

    /**
     * Adds a {@link Comic} leaf directly to the collection.
     * This is a convenience wrapper over {@link #addElement(ComicElement)}
     * that enforces the type and checks for duplicates.
     *
     * @param comic the comic to add
     * @throws IllegalArgumentException if the comic is {@code null} or already present
     */
    public void addComic(Comic comic) {
        if (comic == null) {
            throw new IllegalArgumentException("Cannot add a null Comic.");
        }
        if (containsComic(comic)) {
            throw new IllegalArgumentException(
                    "Comic is already in the collection: " + comic);
        }
        elements.add(comic);
    }

    /**
     * Removes a {@link Comic} leaf from the collection.
     *
     * @param comic the comic to remove
     * @return {@code true} if the comic was found and removed
     */
    public boolean removeComic(Comic comic) {
        return elements.remove(comic);
    }

    /**
     * Replaces an existing comic with an edited version.
     * The old comic is removed and the new comic is inserted at the same index.
     * If the old comic is not found, the new comic is appended at the end.
     *
     * @param oldComic the comic to replace
     * @param newComic the replacement comic with updated attributes
     * @throws IllegalArgumentException if either argument is {@code null}
     */
    public void editComic(Comic oldComic, Comic newComic) {
        if (oldComic == null || newComic == null) {
            throw new IllegalArgumentException("Comics must not be null.");
        }
        int index = elements.indexOf(oldComic);
        if (index >= 0) {
            elements.set(index, newComic);
        } else {
            // Comic not found at top level – still add the new version
            elements.add(newComic);
        }
    }

    /**
     * Returns {@code true} if the given comic (by equality) exists anywhere
     * in the flat list of direct children.
     *
     * @param comic the comic to check
     * @return {@code true} if found
     */
    @Override
    public boolean containsComic(Comic comic) {
        for (ComicElement ce : elements) {
            if (ce.containsComic(comic)) return true;
        }
        return false;
    }

    /**
     * Returns all direct {@link Comic} leaves in this collection.
     * Composite children (Publishers, Series, Volumes) are not traversed here;
     * use {@link #search(String)} with an empty query to retrieve all issues.
     *
     * @return list of directly held Comic objects
     */
    public List<Comic> getComics() {
        List<Comic> comics = new ArrayList<>();
        for (ComicElement element : elements) {
            if (element instanceof Comic c) {
                comics.add(c);
            }
        }
        return comics;
    }

        /**
     * Replaces any element in the list that is identity-equal to {@code target}
     * with {@code replacement}. Used by grade/slab commands to swap a raw Comic
     * for its decorated wrapper (or a GradeDecorator for a SlabDecorator).
     *
     * @param target      the element currently in the list
     * @param replacement the element to put in its place
     * @return true if the replacement was made
     */
    public boolean replaceElement(Object target, ComicElement replacement) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) == target) {   // identity, not equals()
                elements.set(i, replacement);
                return true;
            }
        }
        return false;
    }

    /**
     * Walks the element list looking for a ValueDecorator whose unwrapped
     * base Comic matches the given comic. Returns the decorator if found,
     * or null if the comic is stored raw (undecorated).
     *
     * Used by SlabComicCommand to locate the GradeDecorator it needs to
     * wrap in a SlabDecorator.
     *
     * @param comic the underlying comic to search for
     * @return the outermost decorator wrapping that comic, or null
     */
    public ComicBookValue findDecorator(Comic comic) {
        for (ComicElement element : elements) {
            if (element instanceof ValueDecorator vd && vd.getWrapped().equals(comic)) {
                return vd;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // ComicElement implementation (Composite pattern)
    // -------------------------------------------------------------------------

    /**
     * Searches the entire collection hierarchy for comics matching the query.
     * Each child element's {@code search()} method is called recursively,
     * and results are aggregated into a single array.
     *
     * @param query the search term (case-insensitive, partial match)
     * @return array of all matching {@link ComicElement} leaves
     */
    @Override
    public ComicElement[] search(String query) {
        List<ComicElement> results = new ArrayList<>();
        for (ComicElement element : elements) {
            ComicElement[] childResults = element.search(query);
            Collections.addAll(results, childResults);
        }
        return results.toArray(ComicElement[]::new);
    }

    /**
     * Returns the total number of issues across the entire collection hierarchy.
     * For each child element, its issue count is added recursively.
     *
     * @return total issue count
     */
    @Override
    public int getIssueCount() {
        int count = 0;
        for (ComicElement element : elements) {
            count += element.getIssueCount();
        }
        return count;
    }

    /**
     * Returns the aggregate monetary value of the entire collection hierarchy.
     * Values are summed recursively across all child elements.
     *
     * @return total collection value
     */
    @Override
    public double getValue() {
        double total = 0.0;
        for (ComicElement element : elements) {
            total += element.getValue();
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Display / browsing helpers
    // -------------------------------------------------------------------------

    /**
     * Returns a formatted summary string displaying collection-level statistics.
     * This is displayed at the top level while browsing the collection (Req. 4b).
     *
     * @return a multi-line summary of the collection
     */
    public String getSummary() {
        return String.format(
                "=== Personal Collection ===%n" +
                "Total Issues : %d%n" +
                "Total Value  : $%.2f%n",
                getIssueCount(), getValue()
        );
    }

    @Override
    public String toString() {
        return String.format("PersonalCollection[issues=%d, value=%.2f]",
                getIssueCount(), getValue());
    }
}
