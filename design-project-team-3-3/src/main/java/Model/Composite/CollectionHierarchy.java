package Model.Composite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Model.Comic;
import Model.PersonalCollection;

/**
 * Builds and maintains the four-level composite hierarchy:
 *
 *   PersonalCollection
 *     └── Publishers          (grouped by comic.getPublisher())
 *           └── Series        (grouped by comic.getSeriesTitle())
 *                 └── Volumes (grouped by comic.getVolume())
 *                       └── Comic  (leaf)
 *
 * Rebuilt whenever the collection changes (add/remove/edit).
 * The PTUI uses this to drill down level by level; the raw
 * PersonalCollection remains the source of truth for all commands.
 */
public class CollectionHierarchy {

    // Top-level list of Publishers nodes, in alphabetical order.
    private final List<Publishers> publisherNodes = new ArrayList<>();

    /**
     * (Re)builds the entire tree from the current flat comic list.
     * Call this after any add/remove/edit operation.
     */
    public void rebuild(PersonalCollection collection) {
        publisherNodes.clear();

        // Use LinkedHashMap to preserve insertion (alphabetical) order.
        // publisher name  → Publishers node
        Map<String, Publishers> pubMap    = new LinkedHashMap<>();
        // publisher name + "|" + series title → Series node
        Map<String, Series>     seriesMap = new LinkedHashMap<>();
        // publisher name + "|" + series title + "|" + volume → Volumes node
        Map<String, Volumes>    volMap    = new LinkedHashMap<>();

        // Sort the flat list before grouping so every level ends up ordered.
        List<Comic> sorted = new ArrayList<>(collection.getComics());
        sorted.sort(
            java.util.Comparator
                .comparing(Comic::getPublisher,    String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Comic::getSeriesTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Comic::getVolume)
                .thenComparingInt(Comic::getIssue)
        );

        for (Comic comic : sorted) {
            String pubKey    = comic.getPublisher();
            String seriesKey = pubKey + "|" + comic.getSeriesTitle();
            String volKey    = seriesKey + "|" + comic.getVolume();

            // Publishers level
            if (!pubMap.containsKey(pubKey)) {
                Publishers pubNode = new Publishers(comic.getPublisher());
                pubMap.put(pubKey, pubNode);
                publisherNodes.add(pubNode);
            }

            // Series level
            if (!seriesMap.containsKey(seriesKey)) {
                Series seriesNode = new Series(comic.getSeriesTitle());
                seriesMap.put(seriesKey, seriesNode);
                pubMap.get(pubKey).addElement(seriesNode);
            }

            // Volumes level
            if (!volMap.containsKey(volKey)) {
                Volumes volNode = new Volumes(comic.getVolume());
                volMap.put(volKey, volNode);
                seriesMap.get(seriesKey).addElement(volNode);
            }

            // Comic leaf
            volMap.get(volKey).addElement(comic);
        }
    }

    /** Returns the top-level Publisher nodes (level 2 of the hierarchy). */
    public List<Publishers> getPublisherNodes() {
        return new ArrayList<>(publisherNodes);
    }

    /** Total issues across the whole hierarchy — shown at the collection level. */
    public int getTotalIssues() {
        return publisherNodes.stream().mapToInt(Publishers::getIssueCount).sum();
    }

    /** Total value across the whole hierarchy — shown at the collection level. */
    public double getTotalValue() {
        return publisherNodes.stream().mapToDouble(Publishers::getValue).sum();
    }
}