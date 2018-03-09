import java.sql.*;

public class SqlManager {

    Connection conn = null;

    public void connectSQL() {

        //method to connect to sqlite server

        String url = "jdbc:sqlite:quickftp.db";


        try {

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }

    }

    public void disconnectSQL(){

        //method to disconnect sqlite server

        try {
            conn.close();

            System.out.println("Connection to SQLite has been aborted");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public  void createSQL() {

        // SQL statement for creating a new table if not already made
        String sql = "CREATE TABLE IF NOT EXISTS login (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	host text, \n"
                + "	user text \n"
                + ");";

        try {
                Statement stmt = conn.createStatement();

                stmt.execute(sql);
            }
            catch(SQLException e)
            {
                System.out.println("createSQL error = " +  e.getMessage());
            }

    }

    public void insertSQL (String host, String user)
    {
        //method to store the login in the db

        String sql = "INSERT INTO login(host,user) VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, host);
            pstmt.setString(2, user);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] fetchSQL ()
    {
        //method to load previously stored login

        String sql = "SELECT host, user FROM login";
        Statement stmt = null;
        ResultSet rs = null;
        String [] Result = new String [2];

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            Result [0] = rs.getString("host");
            Result [1] = rs.getString("user");
            }

            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        return Result;

    }
}
