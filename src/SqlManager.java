import java.sql.*;

public class SqlManager {

    Connection conn = null;

    public void connectSQL() {

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

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS login (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	host text, \n"
                + "	user text \n"
                + ");";

        try {
                Statement stmt = conn.createStatement();
                // create a new table
                stmt.execute(sql);
            }
            catch(SQLException e)
            {
                System.out.println("createSQL error = " +  e.getMessage());
            }

    }

    public void insertSQL (String host, String user)
    {
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
