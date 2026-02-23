package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Comic;
import Model.PersonalCollection;

/**
 * Facade that coordinates the two DAOs and owns the JDBC connection.
 * This is the only database-aware class that the command layer touches —
 * CommandController and the PTUI never import java.sql directly.
 */
public class CollectionService {

    private final Connection       connection;
    private final DatabaseDAO      databaseDAO;
    private final PersonalCollectionDAO personalDAO;

    public CollectionService(Connection connection) {
        this.connection  = connection;
        this.databaseDAO = new DatabaseDAO(connection);
        this.personalDAO = new PersonalCollectionDAO(connection);
    }

    // -------------------------------------------------------------------------
    // User / session
    // -------------------------------------------------------------------------

    /**
     * Looks up a user by username and returns their user_id,
     * or -1 if no such user exists.
     */
    public int findUserID(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        }
        return -1;
    }

    /** Returns every username in the users table, for the login menu. */
    public List<String> listUsers() throws SQLException {
        List<String> names = new ArrayList<>();
        try (Statement st  = connection.createStatement();
             ResultSet rs  = st.executeQuery("SELECT username FROM users ORDER BY username")) {
            while (rs.next()) names.add(rs.getString("username"));
        }
        return names;
    }

    // -------------------------------------------------------------------------
    // Master database pass-through
    // -------------------------------------------------------------------------

    public List<Comic> searchDatabase(String query,String sortBy) throws SQLException {
        return databaseDAO.search(query,sortBy);
    }

    public List<String> browsePublishers() throws SQLException {
        return databaseDAO.browsePublishers();
    }

    public List<String> browseSeries(String publisher) throws SQLException {
        return databaseDAO.browseSeries(publisher);
    }

    // -------------------------------------------------------------------------
    // Personal collection — load on login, persist on every mutation
    // -------------------------------------------------------------------------

    /**
     * Loads the user's collection from the database into the provided
     * in-memory PersonalCollection, which the CommandController then owns.
     */
    public void loadCollection(int userID, PersonalCollection collection) throws SQLException {
        List<Comic> saved = personalDAO.loadCollection(userID);
        for (Comic c : saved) {
            collection.addComic(c);
        }
    }

    public void addComic(int userID, Comic comic) throws SQLException {
        personalDAO.addComic(userID, comic);
    }

    public void removeComic(int userID, Comic comic) throws SQLException {
        personalDAO.removeComic(userID, comic);
    }

    public void updateComic(int userID, Comic comic) throws SQLException {
        personalDAO.updateComic(userID, comic);
    }

    public void updateGrade(int userID, Comic comic, int grade) throws SQLException {
        personalDAO.updateGrade(userID, comic, grade);
    }

    public void updateSlabbed(int userID, Comic comic, boolean slabbed) throws SQLException {
        personalDAO.updateSlabbed(userID, comic, slabbed);
    }

    /** Closes the underlying JDBC connection. Call this on application exit. */
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}