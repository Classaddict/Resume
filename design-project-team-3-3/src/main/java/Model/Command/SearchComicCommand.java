package Model.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Model.Comic;
import Model.ComicElement;
import Model.Decorator.ValueDecorator;
import Model.PersonalCollection;

public class SearchComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private String query;
    private final boolean exactMatch;
    private final SortOrder sortOrder;

    /**
     * Enum representation of the two methods of sorting in Comix.
     */
    public enum SortOrder {
        SERIES_VOLUME_ISSUE,
        PUBLICATION_DATE
    }

    /**
     * Full constructor.
     *
     * @param collection the personal collection to search
     * @param userID     the current user
     * @param query      the search term
     * @param exactMatch true for exact matching, false for partial (default)
     * @param sortOrder  how to order the results
     */
    public SearchComicCommand(PersonalCollection collection, int userID, String query, boolean exactMatch, SortOrder sortOrder) {
        this.collection = collection;
        this.userID     = userID;
        this.query      = query;
        this.exactMatch = exactMatch;
        this.sortOrder  = sortOrder;
    }

    /**
     * Convenience constructor — partial match, default sort order.
     * Keeps existing call sites in CommandController working unchanged.
     */
    public SearchComicCommand(PersonalCollection collection, int userID, String query) {
        this(collection, userID, query, false, SortOrder.SERIES_VOLUME_ISSUE);
    }

    @Override
    public String performAction() {
        // 1. Collect all comics from the collection (unwrapping decorators)
        List<Comic> allComics = collectAllComics();

        // 2. Filter by query
        List<Comic> results = filter(allComics);

        if (results.isEmpty()) {
            return "No comics found matching: \"" + query + "\"";
        }

        // 3. Sort results
        results.sort(buildComparator());

        // 4. Format output
        return formatResults(results);
    }

    /**
     * Pulls every Comic out of the collection, unwrapping any decorators
     * so we always compare against the Comic's actual field values.
     */
    private List<Comic> collectAllComics() {
        List<Comic> comics = new ArrayList<>();
        for (ComicElement element : collection.getElements()) {
            if (element instanceof Comic c) {
                comics.add(c);
            } else if (element instanceof ValueDecorator vd) {
                comics.add(vd.getWrapped());
            } else {
                // Composite node (Publishers/Series/Volumes) — recurse via search(null)
                for (ComicElement child : element.search(null)) {
                    if (child instanceof Comic c) comics.add(c);
                }
            }
        }
        return comics;
    }

    /**
     * Applies either exact or partial, case-insensitive matching
     * against: series title, principal characters, creator names, description.
     */
    private List<Comic> filter(List<Comic> comics) {
        if (query == null || query.isBlank()) return new ArrayList<>(comics);

        String lowerQuery = query.toLowerCase();
        List<Comic> results = new ArrayList<>();

        for (Comic c : comics) {
            if (exactMatch ? matchesExact(c, lowerQuery) : matchesPartial(c, lowerQuery)) {
                results.add(c);
            }
        }
        return results;
    }

    private boolean matchesPartial(Comic c, String lowerQuery) {
        return c.getSeriesTitle().toLowerCase().contains(lowerQuery)
            || c.getCreators().toLowerCase().contains(lowerQuery)
            || c.getDescription().toLowerCase().contains(lowerQuery)
            || c.getCharacters().stream()
                .anyMatch(ch -> ch.toLowerCase().contains(lowerQuery));
    }

    private boolean matchesExact(Comic c, String lowerQuery) {
        return c.getSeriesTitle().equalsIgnoreCase(lowerQuery)
            || c.getCreators().equalsIgnoreCase(lowerQuery)
            || c.getDescription().equalsIgnoreCase(lowerQuery)
            || c.getCharacters().stream()
                .anyMatch(ch -> ch.equalsIgnoreCase(lowerQuery));
    }

    private Comparator<Comic> buildComparator() {
        return switch (sortOrder) {
            case PUBLICATION_DATE -> Comparator.comparing(
                    Comic::getPublicationDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case SERIES_VOLUME_ISSUE -> Comparator
                    .comparing(Comic::getSeriesTitle, String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(Comic::getVolume)
                    .thenComparingInt(Comic::getIssue);
        };
    }

    private String formatResults(List<Comic> results) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Search results for \"%s\" (%d found):%n", query, results.size()));
        sb.append("─".repeat(60)).append("\n");
        for (Comic c : results) {
            sb.append(String.format("  %-35s Vol.%-3d #%-5d  $%.2f%n",
                    c.getSeriesTitle(),
                    c.getVolume(),
                    c.getIssue(),
                    c.getValue()));
        }
        return sb.toString();
    }
}
