package server.sql;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Connect {
    private static Connection con = null;
    private static final String url=  "jdbc:mysql://localhost:3306/CHAT";
    private static final String username = "Logger";
    private static final String password = "Logger@123";


    public static Connection connectToDB() {
        try {
            con = DriverManager.getConnection(url,username,password);
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
            return con;
        }
    }
}
