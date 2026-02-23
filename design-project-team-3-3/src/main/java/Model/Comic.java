package Model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents an individual comic book in the COMIX system.
 *
 * Roles in design patterns:
 *   - Composite pattern: Leaf (implements ComicElement)
 *   - Decorator pattern: Concrete Component (implements ComicBookValue)
 *
 * A Comic may be wrapped by a GradeDecorator and/or a SlabDecorator
 * to dynamically adjust its value without modifying this class.
 */
public class Comic implements ComicElement, ComicBookValue {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** The publisher of the comic (e.g. "Marvel", "Image"). */
    private String publisher;

    /** The series title of the comic (e.g. "The Magnificent Ms. Marvel"). */
    private String seriesTitle;

    /** The story/issue title within the series. */
    private String storyTitle;

    /** The volume number of the series. Defaults to 1 when not specified. */
    private int volume;

    /** The issue number within the volume. */
    private int issue;

    /** The publication date of this issue. */
    private LocalDate publicationDate;

    /**
     * The names of creators associated with this comic (writers, artists, etc.).
     * Optional – may be empty.
     */
    private String creators;

    /**
     * The principal characters appearing in this comic.
     * Optional – may be empty.
     */
    private List<String> characters;

    /**
     * A free-text description of the comic.
     * Optional – may be empty.
     */
    private String description;

    /**
     * The monetary value of the comic in its base (ungraded) state.
     * Optional – defaults to 0.0.
     */
    private double value;

    /** Whether this comic has been graded (grade is stored in a GradeDecorator wrapper). */
    private boolean graded;

    /** Whether this comic has been slabbed (requires graded == true). */
    private boolean slabbed;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new Comic with the minimum required attributes.
     * Optional fields default to empty / zero values.
     *
     * @param publisher       the publisher name
     * @param seriesTitle     the series title
     * @param volume          the volume number (use 1 when unspecified)
     * @param issue           the issue number
     * @param publicationDate the publication date
     */
    public Comic(String publisher, String seriesTitle, int volume,
                 int issue, LocalDate publicationDate) {
        this.publisher = publisher != null ? publisher : "";
        this.seriesTitle = seriesTitle != null ? seriesTitle : "";
        this.storyTitle = "";
        this.volume = volume;
        this.issue = issue;
        this.publicationDate = publicationDate;
        this.creators = "";
        this.characters = new ArrayList<>();
        this.description = "";
        this.value = 0.0;
        this.graded = false;
        this.slabbed = false;
    }

    // -------------------------------------------------------------------------
    // ComicBookValue implementation (Decorator pattern – Concrete Component)
    // -------------------------------------------------------------------------

    /**
     * Returns the base monetary value of this comic.
     * When wrapped in a GradeDecorator or SlabDecorator, the decorator
     * calls this method and adjusts the result before returning it.
     *
     * @return the raw (unmodified) value stored on this comic
     */
    @Override
    public double totalValue() {
        return value;
    }

    // -------------------------------------------------------------------------
    // ComicElement implementation (Composite pattern – Leaf)
    // -------------------------------------------------------------------------

    /**
     * Searches this comic's searchable fields (series title, principal characters,
     * creator names, description) for the given query string.
     * Matching is case-insensitive. Partial matches are supported by default;
     * pass an exact-match flag via the caller when needed.
     *
     * @param query the search term to match against
     * @return an array containing this comic if it matches, or an empty array
     */
    @Override
    public ComicElement[] search(String query) {
        if (query == null || query.isBlank()) {
            return new ComicElement[]{this};
        }

        String lowerQuery = query.toLowerCase();

        boolean matchesSeriesTitle  = seriesTitle.toLowerCase().contains(lowerQuery);
        boolean matchesStoryTitle   = storyTitle.toLowerCase().contains(lowerQuery);
        boolean matchesCreators     = creators.toLowerCase().contains(lowerQuery);
        boolean matchesDescription  = description.toLowerCase().contains(lowerQuery);
        boolean matchesCharacters   = characters.stream()
                .anyMatch(c -> c.toLowerCase().contains(lowerQuery));
        boolean matchesPublisher    = publisher.toLowerCase().contains(lowerQuery);

        if (matchesSeriesTitle || matchesStoryTitle || matchesCreators
                || matchesDescription || matchesCharacters || matchesPublisher) {
            return new ComicElement[]{this};
        }

        return new ComicElement[0];
    }

    /**
     * Returns the number of issues represented by this leaf node (always 1).
     *
     * @return 1
     */
    @Override
    public int getIssueCount() {
        return 1;
    }

    /**
     * Returns the value of this comic as reported by {@link #totalValue()}.
     * Composite nodes aggregate this value across their children.
     *
     * @return the total value of this comic
     */
    @Override
    public double getValue() {
        return totalValue();
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher != null ? publisher : "";
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle != null ? seriesTitle : "";
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle != null ? storyTitle : "";
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getCreators() {
        return creators;
    }

    public void setCreators(String creators) {
        this.creators = creators != null ? creators : "";
    }

    public List<String> getCharacters() {
        return new ArrayList<>(characters);
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters != null ? new ArrayList<>(characters) : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    /** Sets the raw base value of this comic (used before any grading/slabbing). */
    public void setValue(double value) {
        this.value = value;
    }


    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public boolean isSlabbed() {
        return slabbed;
    }

    /**
     * Marks this comic as slabbed. A comic cannot be slabbed unless it is graded.
     *
     * @param slabbed true to mark as slabbed
     * @throws IllegalStateException if attempting to slab an ungraded comic
     */
    public void setSlabbed(boolean slabbed) {
        if (slabbed && !this.graded) {
            throw new IllegalStateException(
                    "A comic cannot be slabbed unless it is graded.");
        }
        this.slabbed = slabbed;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
                "[Comic] %s - %s Vol.%d #%d (%s) | Creator(s): %s | Value: $%.2f%s%s",
                publisher, seriesTitle, volume, issue,
                publicationDate != null ? publicationDate.toString() : "N/A",
                creators.isBlank() ? "N/A" : creators,
                totalValue(),
                graded  ? " [Graded]"  : "",
                slabbed ? " [Slabbed]" : ""
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Comic other)) return false;
        return volume == other.volume
                && issue == other.issue
                && publisher.equals(other.publisher)
                && seriesTitle.equals(other.seriesTitle);
    }

    @Override
    public int hashCode() {
        int result = publisher.hashCode();
        result = 31 * result + seriesTitle.hashCode();
        result = 31 * result + volume;
        result = 31 * result + issue;
        return result;
    }

    @Override
    public boolean containsComic(Comic comic) {
        return this.equals(comic);
    }
}
