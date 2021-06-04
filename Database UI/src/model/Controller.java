package model;
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
	
	/**
	 * Connection to the database
	 */
	private SQLConnection database;
	
	/**
	 * Data behind left list
	 */
	private DefaultListModel<String> leftList;
	
	/**
	 * Data behind right lists
	 */
	private ArrayList<DefaultListModel<String>> rightLists;
	
	/**
	 * Column name of left list
	 */
	private String leftTitle;
	
	/**
	 * Column name of right lists
	 */
	private ArrayList<String> rightTitles;
	
	/**
	 * Selected values in each list
	 */
	private String leftSelected, rightSelected;
	
	/**
	 * Stack for keeping track of columns for back action
	 */
	private Stack<String> titleStack;
	
	/**
	 * Stack for keeping track of selections for back action
	 */
	private Stack<String> selectStack;
	
	/**
	 * Stack for keeping track of selection indices for back action
	 */
	private Stack<Integer> indexStack;
	
	/**
	 * The index for which right table is currently being viewed/modified
	 */
	private int selectedIndex;
	
	/**
	 * The column names of attributes for the selected object
	 */
	private ArrayList<String> selectedObjectAttributes;
	
	/**
	 * The attribute details for each column of the selected object
	 */
	private ArrayList<String> selectedObjectDetails;
	
	/**
	 * Boolean for checking if the database is connected
	 */
	private boolean connected;
	
	/**
	 * Constructor for the controller object.
	 */
	public Controller() {
		//SQL Connection code
		database = new SQLConnection();
		connected = database.isConnected();
		
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
	
	/**
	 * Getter for the connection status;
	 * @return True if connected to database, false otherwise
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Setter for the index of the right column
	 * @param index the index
	 */
	public void setSelectedIndex(int index) {
		selectedIndex = index;
	}
	
	/**
	 * Setter for the left selected value
	 * @param selection
	 */
	public void setLeftSelection(String selection) {
		leftSelected = selection;
	}
	
	/**
	 * Setter for the right selected value
	 * @param selection
	 */
	public void setRightSelection(String selection) {
		rightSelected = selection;
	}
	
	/**
	 * Getter for the index of the right column
	 * @return the selected index
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	/**
	 * Getter for the value selected by the left list
	 * @return left selected value
	 */
	public String getLeftSelection() {
		return leftSelected;
	}
	/**
	 * Getter for the value selected by the right list
	 * @return right selected value
	 */
	public String getRightSelection() {
		return rightSelected;
	}
	
	/**
	 * Getter for the column name of the left list;
	 * @return left selected value
	 */
	public String getLeftTitle() {
		return leftTitle;
	}
	
	/**
	 * Getter for the column name of the right list given an index;
	 * @param index the index
	 * @return left selected value
	 */
	public String getRightTitle(int index) {
		return  rightTitles.size() > 0 ? rightTitles.get(index) : null;
	}
	
	/**
	 * The list of values for the given left column
	 * @return DefaultListModel a list of the items
	 */
	public DefaultListModel<String> getLeftList(){
		return leftList;
	}
	
	/**
	 * The list of values for the given right column at the provided index
	 * @param index the index
	 * @return DefaultListModel a list of the items
	 */
	public DefaultListModel<String> getRightList(int index){
		return  rightLists.size() > 0 ? rightLists.get(index) : null;
	}
	
	/**
	 * Get the list of all the lists
	 * @return list of all right lists
	 */
	public ArrayList<DefaultListModel<String>> getRightLists(){
		return rightLists;
	}
	
	/**
	 * Getter for object attributes
	 * @return an Arraylist of the columns
	 */
	public ArrayList<String> getSelectedAttributes(){
		return selectedObjectAttributes;
	}
	
	/**
	 * Getter for object details
	 * @return an Arraylist of the column values
	 */
	public ArrayList<String> getSelectedDetails(){
		return selectedObjectDetails;
	}
	
	/**
	 * Method for getting all the attributes of the selected object
	 */
	public void getDetails() {
		try {
			queryDetails();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Query the details for an object in a designated table
	 * @throws SQLException 
	 */
	private void queryDetails() throws SQLException {
		
		selectedObjectAttributes = new ArrayList<String>();
		selectedObjectDetails = new ArrayList<String>();
		
		//Get the column name of the 'Name' column
		ResultSet pickSet = database.query("SELECT *"
				+ " FROM " + rightTitles.get(selectedIndex) );
		ResultSetMetaData pickMetadata = pickSet.getMetaData();
		String pickNameCol = "";
		for(int i = 1; i <= pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						
				pickNameCol = col;
				break;
				}
		}

		ResultSet resultSet = database.query("SELECT * FROM " + rightTitles.get(selectedIndex) 
									+ " WHERE " + pickNameCol + " = '" + rightSelected + "'");
		ResultSetMetaData metadata = resultSet.getMetaData();
		
		
		ArrayList<Integer> IDloc = new ArrayList<Integer>(); //For avoiding displaying ID's
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
			
			String s = metadata.getColumnLabel(i);
			if(s.contains("ID")) {
				IDloc.add(i);
			}
			else {
				selectedObjectAttributes.add(s);
			}
			
		}
		while (resultSet.next()) {
			for(int i = 1; i <= metadata.getColumnCount(); i++) {
				if(!IDloc.contains(i)) {
					selectedObjectDetails.add(resultSet.getString(i));
				}
				
			}
        }
		
	}
	
	/**
	 * Method for updating the attributes in the database
	 * @param values the values to update to
	 */
	public void update(ArrayList<String> values) {
		try {
			updateQuery(values);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the attributes in the database
	 * @param values the values to update to
	 * @throws SQLException
	 */
	public void updateQuery(ArrayList<String> values) throws SQLException{
			
		String updatePairs = "";
		String nameLabel = "";
		for(int i = 0; i < selectedObjectAttributes.size(); i++) {
			String attribute = selectedObjectAttributes.get(i);
			boolean inqoutes = false;
			if(attribute.contains("Name")) {
				nameLabel =  attribute;
				inqoutes = true;
			}
			if(attribute.equals("RightAscension") || attribute.equals("Declination") 
					|| attribute.equals("DateDiscovered")) {
				inqoutes = true;
			}
			
			String value = values.get(i);
			
			
			if(value.equals("")) {
				continue;
			}
			
			value = inqoutes? "'" + value + "'" : value;
			
			if(i == 0) {
				updatePairs += attribute + " = " + value;
			}
			else {
				updatePairs += ", " + attribute + " = " + value;
			}
			
			
		}
		
		String id;
		switch(rightTitles.get(selectedIndex)) {
			case "GalaxyCluster":
				id = "ClusterID";
				break;
			case "GalaxyGroup":
				id = "GroupID";
				break;
			case "SolarSystem":
				id = "SystemID";
				break;
			case "NaturalSatellite":
				id = "SatelliteID";
				break;
			default:
				id = rightTitles.get(selectedIndex) + "ID";
				break;
		}
		
		ResultSet pickSet = database.query("SELECT " + id
											+ " FROM " + rightTitles.get(selectedIndex)
											+ " WHERE " + nameLabel + " = '" + rightSelected + "'");

		pickSet.next();
		String theID = pickSet.getString(1);
		database.update("UPDATE " + rightTitles.get(selectedIndex) 
						+ " SET " + updatePairs +" WHERE " + id + " = " + theID);
	}
	
	/**
	 * Insert values into the designated table
	 * @param name the Name of the object to insert
	 */
	public void insert(String name) {
		try {
			insertQuery(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Insertion call to database
	 * @param name value to insert
	 * @throws SQLException
	 */
	private void insertQuery(String name) throws SQLException{
		//Get the column name of the 'Name' column
		ResultSet pickSet = database.query("SELECT *"
				+ " FROM " + leftTitle);
		ResultSetMetaData pickMetadata = pickSet.getMetaData();
		String pickNameCol = "";
		ArrayList<String> IDList = new ArrayList<String>();
		for(int i = 1; i <= pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						
				pickNameCol = col;
				break;
			}
			
			if(col.contains("ID")) {
				IDList.add(col);
			}
			
		}
		
		//Build ID retrieval string
		String selectedIDs = "";
		selectedIDs += IDList.get(0);
		for(int i = 1; i < IDList.size(); i++) {
			selectedIDs += ", " + IDList.get(i);
		}
	
		ResultSet resultSet = database.query("SELECT " + selectedIDs +" FROM " + leftTitle + " WHERE " + pickNameCol + " = " + "'" + leftSelected + "'");
		ResultSetMetaData metadata = resultSet.getMetaData();
		
		
		String IDs = "";
		while (resultSet.next()) {
			for(int i = 1; i <= metadata.getColumnCount(); i++) {
				if(i==1) {
					IDs += (resultSet.getString(i));
				}
				else {
					IDs += (", " + resultSet.getString(i));
				}
				
			}
			
        }
		
		
		//Get the column name of the 'Name' column
		pickSet = database.query("SELECT *"
				+ " FROM " + rightTitles.get(selectedIndex) );
		pickMetadata = pickSet.getMetaData();
		String pickInsertNameCol = "";
		for(int i = 1; i <= pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						
				pickInsertNameCol = col;
				break;
				}
		}
		
		if(rightTitles.get(selectedIndex).contains("Satellite")) {
			
			database.update("INSERT INTO Satellite" + "(" + selectedIDs +")"
					+ " VALUES(" + IDs + ")");
			
			
			pickSet = database.query("SELECT SatelliteID from Satellite");
			String id = "1";
			while(pickSet.next()) {
				id = pickSet.getString("SatelliteID");
			}
			database.update("INSERT INTO " + rightTitles.get(selectedIndex) + "(SatelliteID, " + pickInsertNameCol + ", " + selectedIDs +")"
					+ " VALUES(" + id + ", '" + name +"', " + IDs + ")");
			
		}
		else {
			database.update("INSERT INTO " + rightTitles.get(selectedIndex) + "(" + pickInsertNameCol + ", " + selectedIDs +")"
							+ " VALUES('" + name +"', " + IDs + ")");
		}
	}
	
	
	/**
	 * Method for deleting a row
	 */
	public void delete() {
		try {
			deleteQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		reloadTables();
	}
	
	/**
	 * Deletion call to database
	 * @throws SQLException
	 */	
	private void deleteQuery() throws SQLException{
		
		ResultSet pickSet = database.query("SELECT *"
				+ " FROM " + rightTitles.get(selectedIndex));
		ResultSetMetaData pickMetadata = pickSet.getMetaData();
		String pickNameCol = "";
		for(int i = 1; i <= pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						
				pickNameCol = col;
				break;
			}
		}
		
		
		if(rightTitles.get(selectedIndex).contains("Satellite")) {
			
			pickSet = database.query("SELECT SatelliteID"
					+ " FROM  " + rightTitles.get(selectedIndex) 
					+ " WHERE " + pickNameCol + " = '" + rightSelected + "'");
			
			String id = "1";
			while(pickSet.next()) {
				id = pickSet.getString(1);
			}
			
			database.update("DELETE FROM " + rightTitles.get(selectedIndex) 
			+ " WHERE " + pickNameCol + " = '" + rightSelected + "'");
			
			database.update("DELETE FROM Satellite WHERE SatelliteID = " +id);
		}
		else {
			database.update("DELETE FROM " + rightTitles.get(selectedIndex) 
			+ " WHERE " + pickNameCol + " = '" + rightSelected + "'");
		}
		
		
	}
	
	/**
	 * Reload the tables with the values from the database on the updated chosen parameters
	 */
	public void reloadTables() {
		try {
			leftListQuery();
			rightListQuery();
			rightSelected = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Query to populate the left list with values from the data base for the given column
	 * @throws SQLException
	 */
	private void leftListQuery() throws SQLException {
		ResultSet resultSet = database.query("SELECT * FROM " + leftTitle);
		ResultSetMetaData metadata = resultSet.getMetaData();
		
		String nameCol = "";
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
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
	
	/**
	 * Query to populate the right list(s) with values from the data base for the given column(s)
	 * @throws SQLException
	 */
	private void rightListQuery() throws SQLException {
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
			case "NaturalSatellite":
				id = "SatelliteID";
				break;
			default:
				id = leftTitle + "ID";
				break;
		}
		
		ResultSet pickSet = database.query("SELECT *"
											+ " FROM " + leftTitle);
		ResultSetMetaData pickMetadata = pickSet.getMetaData();
		String pickNameCol = "";
		for(int i = 1; i <= pickMetadata.getColumnCount(); i++) {
			String col = pickMetadata.getColumnLabel(i);
			if(col.contains("Name")) { 						
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
			for(int i = 1; i <= metadata.getColumnCount(); i++) {
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
	
	/**
	 * Method for shifting the focus to down to a child object and its children
	 */
	public void moveInto() {
		
		String rightTitle = rightTitles.get(selectedIndex);
		indexStack.push(selectedIndex);
		titleStack.push(leftTitle);
		selectStack.push(leftSelected);
		leftTitle = rightTitle;
		rightTitles = getChildren(rightTitle);
		reloadTables();
	}
	
	/**
	 * Method for shifting back from the child to its parent
	 */
	public void moveBack() {
		
		leftTitle = titleStack.pop();
		selectedIndex = indexStack.pop();
		rightTitles = getChildren(leftTitle);
		rightSelected = leftSelected;
		leftSelected = selectStack.pop();
		reloadTables();
	}
	/**
	 * Helper method to make dealing with multi-child DB objects easier
	 * Lets the controller know which object has what children
	 * @param parent the table name
	 * @return a list of all possible child tables
	 */
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
			
		}
		return children;
	}
	/**
	 * Method for knowing if the currently selected object has possible children
	 * @return True if children exist, false otherwise
	 */
	public boolean hasMoreDepth() {
		return hasChildren(rightTitles.get(selectedIndex));
	}
	/**
	 * Helper method for knowing if there is more depth to an object hierarchy .
	 * @param parent the object name
	 * @return True if the given object has children, false otherwise
	 */
	private boolean hasChildren(String parent) {
		return !(parent.equals("Nebula") || parent.equals("RogueObject") 
				|| parent.equals("Star") || parent.equals("BlackHole")
				|| parent.equals("NaturalSatellite")
				|| parent.equals("Comet") || parent.equals("Asteroid"));
	}
	
}
