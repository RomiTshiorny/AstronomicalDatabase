import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.DefaultListModel;

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
//		public static void main(String args[]) {
//			
//			String connectionUrl = "jdbc:sqlserver://localhost\\ROMISQLSERVER";
//			
//			Properties info = new Properties();
//			info.put("user","sa");
//			info.put("password","romisqlpass");
//			info.put("database","Tshiorny_Romi_db");
//			try (Connection connection = DriverManager.getConnection(connectionUrl,info); Statement statement = connection.createStatement();){
//				System.out.println("Connection Successful");
//				
//
//				
//				String selectSQL = "SELECT TOP 10 * FROM Planet";
//				ResultSet resultSet = statement.executeQuery(selectSQL);
//				ResultSetMetaData metadata = resultSet.getMetaData();
//				for(int i = 1; i < metadata.getColumnCount(); i++) {
//					System.out.print(metadata.getColumnLabel(i) + " ");
//				}
//				System.out.println();
//				
//	
//				while (resultSet.next()) {
//					for(int i = 1; i < metadata.getColumnCount(); i++) {
//						System.out.print(resultSet.getString(i) + " ");
//					}
//					System.out.println();
//	                					
//	            }
//				
//			}
//			catch(SQLException e) {
//				e.printStackTrace();
//			}
//		}
}
