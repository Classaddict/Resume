package Model;

/**
 * Component interface for the Composite pattern used to represent
 * the COMIX collection hierarchy:
 *
 *   PersonalCollection → Publishers → Series → Volumes → Comic (Leaf)
 *
 * Every node in the hierarchy – whether a single Comic or an entire
 * collection – must be able to report its issue count, its aggregate
 * value, and respond to a search query.
 */
public interface ComicElement {

    /**
     * Searches this element (and, for composites, all children recursively)
     * for comics whose searchable fields contain the given query string.
     *
     * Searchable fields are: series title, principal characters,
     * creator names, and description. Matching is case-insensitive.
     *
     * @param query the search term; partial matches are supported
     * @return an array of matching {@link ComicElement}s (leaf Comics)
     */
    ComicElement[] search(String query);

    /**
     * Returns the total number of individual issues represented by this element.
     * For a leaf Comic this is always 1; for composites it is the sum of
     * their children's issue counts.
     *
     * @return the number of issues at or below this node in the hierarchy
     */
    int getIssueCount();

    /**
     * Returns the aggregate monetary value of this element.
     * For a leaf Comic this delegates to {@link ComicBookValue#totalValue()};
     * for composites it is the sum of all children's values.
     *
     * @return the total value at or below this node
     */
    double getValue();

    boolean containsComic(Comic comic);
}
