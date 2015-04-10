import java.sql.*;

public class Database {

    private Connection connection = null;
    private PreparedStatement pst = null;

    private Connection connect() throws SQLException {
        connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/short");
        return connection;
    }

    public String get(int id) throws SQLException {

        Connection db = connect();
        try {
            pst = db.prepareStatement("SELECT url FROM urls WHERE id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } finally {
            if (pst != null) {
                pst.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public int save(String url) throws SQLException {
        Connection db = connect();
        try {
            pst = db.prepareStatement("INSERT INTO urls(url) VALUES(?) RETURNING id");
            pst.setString(1, url);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            if (pst != null) {
                pst.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return 0;
    }

    public void setup() throws SQLException {
        Connection db = connect();
        try {
            Statement stmt = db.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS urls (\n" +
                    "    id serial PRIMARY KEY, \n" +
                    "    url VARCHAR(2000)\n" +
                    ");";
            stmt.executeUpdate(sql);
            stmt.close();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
