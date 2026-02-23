import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class DatabaseTest {
    private String url;
    private String username;
    private String password;

    private static class DbConfig {
        String db_host;
        long   db_port;
        String db_name;
        String username;
        String password;
    }

    public void loadConfig() throws FileNotFoundException {
        Gson gson = new Gson();
        DbConfig config = gson.fromJson(new FileReader("db.json"), DbConfig.class);

        this.url      = "jdbc:postgresql://" + config.db_host + ":" + config.db_port + "/" + config.db_name;
        this.username = config.username;
        this.password = config.password;
    }

    @Test  
    public void sqlLoadDataTest() throws FileNotFoundException {
        DatabaseTest dt = new DatabaseTest();
        dt.loadConfig();

        try (Connection con = DriverManager.getConnection(dt.url, dt.username, dt.password);
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM users")) { // Use try-with-resources

                while (rs.next()) {
                username = rs.getString("username");
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertEquals(username, "Tony Tailpipe");
    }
}
