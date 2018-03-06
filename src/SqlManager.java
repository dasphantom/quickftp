import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlManager {

    Connection conn = null;

    public void connectSQL() {

        try {
            // db parameters
            String url = "jdbc:sqlite:quickftp.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    public  void disconnectSQL()
    {
        try {
            conn.close();

            System.out.println("Connection to SQLite has been aborted");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
