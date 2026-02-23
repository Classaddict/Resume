package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Model.Comic;

/**
 * Handles all reads and writes to the personal_collections table
 * for a specific user. This is the only place in the system that
 * mutates the database on behalf of the user.
 */
public class PersonalCollectionDAO {

    private final Connection connection;

    public PersonalCollectionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Loads all comics belonging to the given user from the database,
     * including their grade, slabbed status, and any overridden value.
     */
    public List<Comic> loadCollection(int userID) throws SQLException {
        String sql = """
            SELECT p.name          AS publisher,
                   s.title         AS series_title,
                   v.volume_number,
                   i.issue_number,
                   i.story_title,
                   i.release_date,
                   i.creators,
                   pc.grade,
                   pc.slabbed,
                   pc.comic_value,
                   pc.description,
                   pc.principal_characters
            FROM   personal_collections pc
            JOIN   issues     i  ON pc.issue_id     = i.issue_id
            JOIN   volumes    v  ON i.volume_id     = v.volume_id
            JOIN   series     s  ON v.series_id     = s.series_id
            JOIN   publishers p  ON s.publisher_id  = p.publisher_id
            WHERE  pc.user_id = ?
            ORDER BY s.title, v.volume_number, i.issue_number
            """;

        List<Comic> comics = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comics.add(mapRowToComic(rs));
                }
            }
        }
        return comics;
    }

    /**
     * Inserts a new comic entry into the user's personal collection.
     * Looks up the issue_id from the master comics table first.
     */
    public void addComic(int userID, Comic comic) throws SQLException {
        int issueID = findIssueID(comic);
        if (issueID == -1) {
            throw new SQLException("Comic not found in master database: " + comic);
        }

        String sql = """
            INSERT INTO personal_collections
              (user_id, issue_id, added_date, grade, slabbed, comic_value,
               description, principal_characters)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt    (1, userID);
            ps.setInt    (2, issueID);
            ps.setDate   (3, Date.valueOf(LocalDate.now()));
            ps.setObject (4, comic.isGraded() ? null : null, Types.SMALLINT); // grade set later
            ps.setBoolean(5, false);
            ps.setDouble (6, comic.getValue());
            ps.setString (7, comic.getDescription());
            ps.setString (8, String.join(", ", comic.getCharacters()));
            ps.executeUpdate();
        }
    }

    /**
     * Removes the comic from the user's personal collection.
     */
    public void removeComic(int userID, Comic comic) throws SQLException {
        int issueID = findIssueID(comic);
        if (issueID == -1) return;

        String sql = """
            DELETE FROM personal_collections
            WHERE user_id = ? AND issue_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, issueID);
            ps.executeUpdate();
        }
    }

    /**
     * Persists the grade for a comic already in the user's collection.
     */
    public void updateGrade(int userID, Comic comic, int grade) throws SQLException {
        int issueID = findIssueID(comic);
        if (issueID == -1) return;

        String sql = """
            UPDATE personal_collections
            SET    grade = ?
            WHERE  user_id = ? AND issue_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, grade);
            ps.setInt(2, userID);
            ps.setInt(3, issueID);
            ps.executeUpdate();
        }
    }

    /**
     * Persists the slabbed flag for a comic already in the user's collection.
     */
    public void updateSlabbed(int userID, Comic comic, boolean slabbed) throws SQLException {
        int issueID = findIssueID(comic);
        if (issueID == -1) return;

        String sql = """
            UPDATE personal_collections
            SET    slabbed = ?
            WHERE  user_id = ? AND issue_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, slabbed);
            ps.setInt    (2, userID);
            ps.setInt    (3, issueID);
            ps.executeUpdate();
        }
    }

    /**
     * Updates editable fields (description, principal characters, value)
     * for a comic in the user's collection.
     */
    public void updateComic(int userID, Comic comic) throws SQLException {
        int issueID = findIssueID(comic);
        if (issueID == -1) return;

        String sql = """
            UPDATE personal_collections
            SET    comic_value          = ?,
                   description          = ?,
                   principal_characters = ?
            WHERE  user_id = ? AND issue_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, comic.getValue());
            ps.setString(2, comic.getDescription());
            ps.setString(3, String.join(", ", comic.getCharacters()));
            ps.setInt   (4, userID);
            ps.setInt   (5, issueID);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Looks up the issue_id in the master database by matching publisher,
     * series title, volume number, and issue number.
     */
    private int findIssueID(Comic comic) throws SQLException {
        String sql = """
            SELECT i.issue_id
            FROM   issues i
            JOIN   volumes    v ON i.volume_id    = v.volume_id
            JOIN   series     s ON v.series_id    = s.series_id
            JOIN   publishers p ON s.publisher_id = p.publisher_id
            WHERE  p.name          = ?
              AND  s.title         = ?
              AND  v.volume_number = ?
              AND  i.issue_number  = ?
            LIMIT 1
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, comic.getPublisher());
            ps.setString(2, comic.getSeriesTitle());
            ps.setInt   (3, comic.getVolume());
            ps.setString(4, String.valueOf(comic.getIssue()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("issue_id");
            }
        }
        return -1;
    }

    private Comic mapRowToComic(ResultSet rs) throws SQLException {
        String publisher   = rs.getString("publisher");
        String seriesTitle = rs.getString("series_title");
        int    volume      = rs.getInt("volume_number");
        int    issue       = parseIssueNumber(rs.getString("issue_number"));
        Date   sqlDate     = rs.getDate("release_date");
        LocalDate pubDate  = sqlDate != null ? sqlDate.toLocalDate() : null;

        Comic comic = new Comic(publisher, seriesTitle, volume, issue, pubDate);
        comic.setStoryTitle (nvl(rs.getString("story_title")));
        comic.setCreators   (nvl(rs.getString("creators")));
        comic.setDescription(nvl(rs.getString("description")));

        // Restore value override if one was stored
        double storedValue = rs.getDouble("comic_value");
        if (!rs.wasNull()) comic.setValue(storedValue);

        // Restore grade/slabbed flags
        int grade = rs.getInt("grade");
        if (!rs.wasNull()) comic.setGraded(true);
        if (rs.getBoolean("slabbed")) comic.setSlabbed(true);

        // Restore principal characters
        String chars = rs.getString("principal_characters");
        if (chars != null && !chars.isBlank()) {
            comic.setCharacters(List.of(chars.split(",\\s*")));
        }

        return comic;
    }

    private int parseIssueNumber(String s) {
        if (s == null || s.isBlank()) return 0;
        String digits = s.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        try { return Integer.parseInt(digits); }
        catch (NumberFormatException e) { return 0; }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}