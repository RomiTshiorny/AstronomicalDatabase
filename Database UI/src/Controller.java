import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.DefaultListModel;

/**
 * This is the Controller class that serves as the bridge between the UI and the database.
 * @author Romi Tshiorny
 * 
 */
public class Controller {
	
	private SQLConnection database;
	private DefaultListModel<String> leftList;
	private ArrayList<DefaultListModel<String>> rightLists;
	private String leftTitle;
	private ArrayList<String> rightTitles;
	private String leftSelected, rightSelected;
	private Stack<String> titleStack;
	private Stack<String> selectStack;
	private Stack<Integer> indexStack;
	private int selectedIndex;
	
	public Controller() {
		//SQL Connection code
		database = new SQLConnection();
		
		rightLists = new ArrayList<DefaultListModel<String>>();
		indexStack = new Stack<Integer>();
		
		titleStack = new Stack<String>();
		selectStack = new Stack<String>();
		leftTitle = "GalaxyCluster";
		rightTitles = new ArrayList<String>();
		rightTitles.add("GalaxyGroup");
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
	public void queryDetails() {
		
	}
	public void insert() {
		System.out.println("Insert into "+ rightTitles.get(selectedIndex)); //TODO add insertion functionality
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
	public void setSelectedIndex(int index) {
		selectedIndex = index;
	}
	public void setLeftSelection(String selection) {
		leftSelected = selection;
	}
	public void setRightSelection(String selection) {
		rightSelected = selection;
	}
	public int getSelectedIndex() {
		return selectedIndex;
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
	public String getRightTitle(int index) {
		return  rightTitles.size() > 0 ? rightTitles.get(index) : null;
	}
	public DefaultListModel<String> getLeftList(){
		return leftList;
	}
	public DefaultListModel<String> getRightList(int index){
		return  rightLists.size() > 0 ? rightLists.get(index) : null;
	}
	public ArrayList<DefaultListModel<String>> getRightLists(){
		return rightLists;
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

		rightLists.clear();
		int index = 0;
		for(String rightTitle:rightTitles) {
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
			rightLists.add(new DefaultListModel<String>());
			while (resultSet.next()) {
				rightLists.get(index).addElement(resultSet.getString(nameCol));
	            					
	        }
			index++;
			
		}
		
		
		
	}
	public void moveInto() {
		
		String rightTitle = rightTitles.get(selectedIndex);
		indexStack.push(selectedIndex);
		titleStack.push(leftTitle);
		selectStack.push(leftSelected);
		leftTitle = rightTitle;
		rightTitles = getChildren(rightTitle);
		reloadTables();
	}
	public void moveBack() {
		
		leftTitle = titleStack.pop();
		selectedIndex = indexStack.pop();
		rightTitles = getChildren(leftTitle);
		rightSelected = leftSelected;
		leftSelected = selectStack.pop();
		reloadTables();
	}
	//Helper method to make dealing with multi-child DB objects easier
	private ArrayList<String> getChildren(String parent){
		ArrayList<String> children = new ArrayList<String>();
		if(parent.equals("GalaxyCluster")) {
			children.add("GalaxyGroup");
		}
		else if(parent.equals("GalaxyGroup")) {
			children.add("Galaxy");
		}
		else if(parent.equals("Galaxy")) {
			children.add("SolarSystem");
			children.add("Nebula");
			children.add("RogueObject");
		}
		else if(parent.equals("SolarSystem")) {
			children.add("Star");
			children.add("Planet");
			children.add("Asteroid");
			children.add("Comet");
			children.add("BlackHole");
			
		}
		else if(parent.equals("Planet")) {
			children.add("NaturalSatellite");
			children.add("ArtificialSatellite");
			
		}
		return children;
	}
	public boolean hasMoreDepth() {
		return hasChildren(rightTitles.get(selectedIndex));
	}
	//Helper method for knowing if there is more depth to a branch.
	private boolean hasChildren(String parent) {
		return !(parent.equals("Nebula") || parent.equals("RogueObject") 
				|| parent.equals("Star") || parent.equals("BlackHole")
				|| parent.equals("NaturalSatellite") || parent.equals("ArtificialSatellite")
				|| parent.equals("Comet") || parent.equals("Asteroid"));
	}
	
}
