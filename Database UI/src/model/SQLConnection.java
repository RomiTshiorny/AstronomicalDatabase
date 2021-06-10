package model;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

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
			
			/**read credentials from a file**/
			String path,username,password,database;
			
			try {
				Scanner fileScanner = new Scanner(new File("connect.txt"));
				path = fileScanner.nextLine();
				username = fileScanner.nextLine();
				password = fileScanner.nextLine();
				database = fileScanner.nextLine();
				fileScanner.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				//Alternative if file can't be read
				path = "localhost\\";
				username = "username";
				password = "password";
				database = "database";
			}
			
			/**end of credential reading**/
			connected = false;
			String connectionUrl = "jdbc:sqlserver://" + path;
			
			Properties info = new Properties();
			info.put("user",username);
			info.put("password",password);
			info.put("database",database);
			
			
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
