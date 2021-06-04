import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * This is the SQLConnection class that establishes a connection
 * to the database, and executes queries to that database.
 * @author Romi Tshiorny
 * 
 */
public class SQLConnection {

		private Connection connection;
		private Statement statement;
		public SQLConnection() {
			String connectionUrl = "jdbc:sqlserver://localhost\\ROMISQLSERVER";
			
			Properties info = new Properties();
			info.put("user","sa");
			info.put("password","romisqlpass");
			info.put("database","Tshiorny_Romi_db");
			
			
			try {
				connection = DriverManager.getConnection(connectionUrl,info);
				try {
					statement = connection.createStatement();
					System.out.println("Connection Successful");
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
		}
		public ResultSet query(String sql) throws SQLException {
			return statement.executeQuery(sql);
		}
}
