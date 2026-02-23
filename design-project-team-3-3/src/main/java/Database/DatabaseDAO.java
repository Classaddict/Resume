package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Model.Comic;

/**
 * Handles all read-only queries against the master comics database.
 * The master database is immutable — users cannot change its contents.
 */
public class DatabaseDAO {

    private final Connection connection;

    public DatabaseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Searches the master database for comics matching the given query.
     * Matches case-insensitively against series title, story title,
     * publisher name, and creators. Partial matches are supported.
     *
     * @param query the search term; null or blank returns all comics
     * @return list of matching Comics
     */
    public List<Comic> search(String query, String sortBy) throws SQLException {
    
    String orderBy = switch (sortBy) {
        case "volume"  -> "ORDER BY v.volume_number";
        case "issue"   -> "ORDER BY i.issue_number";
        case "date"    -> "ORDER BY i.release_date";
        default        -> "ORDER BY s.title, v.volume_number, i.issue_number";
    };

    String sql = """
        SELECT p.name       AS publisher,
               s.title      AS series_title,
               v.volume_number,
               i.issue_number,
               i.story_title,
               i.release_date,
               i.creators
        FROM   comics c
        JOIN   publishers p ON c.publisher_id = p.publisher_id
        JOIN   series     s ON c.series_id    = s.series_id
        JOIN   volumes    v ON c.volume_id    = v.volume_id
        JOIN   issues     i ON c.issue_id     = i.issue_id
        WHERE  (? IS NULL OR ? = '')
           OR  LOWER(s.title)       LIKE LOWER(?)
           OR  LOWER(p.name)        LIKE LOWER(?)
           OR  LOWER(i.creators)    LIKE LOWER(?)
           OR  LOWER(i.story_title) LIKE LOWER(?)
        """ + orderBy; 
    List<Comic> results = new ArrayList<>();
    String likeQuery = "%" + (query == null ? "" : query) + "%";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, query);
        ps.setString(2, query);
        ps.setString(3, likeQuery);
        ps.setString(4, likeQuery);
        ps.setString(5, likeQuery);
        ps.setString(6, likeQuery);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRowToComic(rs));
            }
        }
    }
    return results;
    }

    /** Returns every distinct publisher name in the master database. */
    public List<String> browsePublishers() throws SQLException {
        List<String> publishers = new ArrayList<>();
        String sql = "SELECT name FROM publishers ORDER BY name";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) publishers.add(rs.getString("name"));
        }
        return publishers;
    }

    /** Returns all series titles for a given publisher name. */
    public List<String> browseSeries(String publisherName) throws SQLException {
        List<String> series = new ArrayList<>();
        String sql = """
            SELECT s.title FROM series s
            JOIN publishers p ON s.publisher_id = p.publisher_id
            WHERE p.name = ?
            ORDER BY s.title
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, publisherName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) series.add(rs.getString("title"));
            }
        }
        return series;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Comic mapRowToComic(ResultSet rs) throws SQLException {
        String publisher   = rs.getString("publisher");
        String seriesTitle = rs.getString("series_title");
        int    volume      = rs.getInt("volume_number");

        // issue_number is TEXT in the DB (handles "32B", "5.1A" etc.)
        // We store it as the issue field; parse to int where possible
        String issueText = rs.getString("issue_number");
        int    issue     = parseIssueNumber(issueText);

        Date   sqlDate   = rs.getDate("release_date");
        LocalDate pubDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        Comic comic = new Comic(publisher, seriesTitle, volume, issue, pubDate);
        comic.setStoryTitle(rs.getString("story_title") != null
                ? rs.getString("story_title") : "");
        comic.setCreators(rs.getString("creators") != null
                ? rs.getString("creators") : "");
        return comic;
    }

    /** Parses an issue string like "32", "32B", "5.1" — returns the integer part. */
    private int parseIssueNumber(String issueText) {
        if (issueText == null || issueText.isBlank()) return 0;
        String digits = issueText.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        try { return Integer.parseInt(digits); }
        catch (NumberFormatException e) { return 0; }
    }
}