import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Stack;

import javax.swing.DefaultListModel;

public class Controller {
	
	private SQLConnection database;
	private DefaultListModel<String> leftList, rightList;
	private String leftTitle, rightTitle;
	private String leftSelected, rightSelected;
	private Stack<String> titleStack;
	private Stack<String> selectStack;
	
	public Controller() {
		//SQL Connection code
		database = new SQLConnection();
		
		titleStack = new Stack<String>();
		selectStack = new Stack<String>();
		leftTitle = "GalaxyCluster";
		rightTitle = "GalaxyGroup";
		leftSelected = null;
		rightSelected = null;
		
		try {
			leftListQuery();
			leftSelected = leftList.get(0);
			rightListQuery();
			rightSelected = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void reloadTables() {
		try {
			leftListQuery();
			rightListQuery();
			rightSelected = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void setLeftSelection(String selection) {
		leftSelected = selection;
	}
	public void setRightSelection(String selection) {
		rightSelected = selection;
	}
	public String getLeftSelection() {
		return leftSelected;
	}
	public String getRightSelection() {
		return rightSelected;
	}
	public String getLeftTitle() {
		return leftTitle;
	}
	public String getRightTitle() {
		return rightTitle;
	}
	public DefaultListModel<String> getLeftList(){
		return leftList;
	}
	public DefaultListModel<String> getRightList(){
		return rightList;
	}
	public void leftListQuery() throws SQLException {
		ResultSet resultSet = database.query("SELECT * FROM " + leftTitle);
		ResultSetMetaData metadata = resultSet.getMetaData();
		
		String nameCol = "";
		for(int i = 1; i < metadata.getColumnCount(); i++) {
			String col = metadata.getColumnLabel(i);
			if(col.contains("Name")) {
				nameCol = col;
				break;
			}
		}
		leftList = new DefaultListModel<String>();
		while (resultSet.next()) {
//			System.out.print(resultSet.getString("ClusterName") + " ");
//			System.out.println();
			leftList.addElement(resultSet.getString(nameCol));
            					
        }
	}
	public void rightListQuery() throws SQLException {
		String id;
		switch(leftTitle) {
			case "GalaxyCluster":
				id = "ClusterID";
				break;
			case "GalaxyGroup":
				id = "GroupID";
				break;
			case "SolarSystem":
				id = "SystemID";
				break;
			default:
				id = leftTitle + "ID";
				break;
		}
		
		ResultSet pickSet = database.query("SELECT *"
											+ " FROM " + leftTitle);
		ResultSetMetaData pickMetadata = pickSet.getMetaData();
		String pickNameCol = "";
		for(int i = 1; i < pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						//Might be an issue when joining? First one would always be chosen.
				pickNameCol = col;
				break;
			}
		}

		ResultSet resultSet = database.query("SELECT *"
											+ " FROM " + rightTitle
											+ " JOIN " + leftTitle + " ON " + leftTitle + "." + id + " = " + rightTitle + "." + id
											+ " WHERE " + pickNameCol + " = " + "'" + leftSelected + "'");
		ResultSetMetaData metadata = resultSet.getMetaData();
		
		String nameCol = "";
		for(int i = 1; i < metadata.getColumnCount(); i++) {
			String col = metadata.getColumnLabel(i);
			if(col.contains("Name")) { 						//Might be an issue when joining? First one would always be chosen.
				nameCol = col;
				break;
			}
		}
		rightList = new DefaultListModel<String>();
		while (resultSet.next()) {
//			System.out.print(resultSet.getString("ClusterName") + " ");
//			System.out.println();
			rightList.addElement(resultSet.getString(nameCol));
            					
        }
	}
	public void moveInto() {
		titleStack.push(leftTitle);
		selectStack.push(leftSelected);
		leftTitle = rightTitle;
		if(rightTitle.equals("GalaxyGroup")) {
			rightTitle = "Galaxy";
		}
		reloadTables();
	}
	public void moveBack() {
		rightTitle = leftTitle;
		leftTitle = titleStack.pop();
		rightSelected = leftSelected;
		leftSelected = selectStack.pop();
		reloadTables();
	}
	
}
