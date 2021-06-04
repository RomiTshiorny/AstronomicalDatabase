package model;
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

	
		private Boolean connected;
		/**
		 * Connection to database
		 */
		private Connection connection;
		
		/**
		 * Database query
		 */
		private Statement statement;
		
		/**
		 * Constructor for the connection object
		 */
		public SQLConnection() {
			
			connected = false;
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
					connected = true;
					
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			
			
		}
		/**
		 * Method for queries to the database
		 * @param sql the SQL statement to query the database with
		 * @return the ResultSet from the query
		 * @throws SQLException
		 */
		public ResultSet query(String sql) throws SQLException {
			return statement.executeQuery(sql);
		}
		
		/**
		 * Method for updating the database
		 * @param sql the SQL statement to update the database with
		 * @return the int returned by the update
		 * @throws SQLException
		 */
		public int update(String sql) throws SQLException{
			return statement.executeUpdate(sql);
		}
		
		/**
		 * Method for checking connection success
		 * @return True if connection was successful, false otherwise.
		 */
		public boolean isConnected() {
			return connected;
		}
}
