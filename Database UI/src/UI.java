
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * UI Class that is in charge of front-end visible to the user
 * @author Romi Tshiorny
 *
 */
public class UI extends JFrame{
	
	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long serialVersionUID = -3276763436766104098L;

	/**
	 * Width of selection lists for consistency
	 */
	private final static int TABLE_WIDTH = 100;
	
	/**
	 * Base background panel on which everything resides
	 */
	private JPanel base;
	
	/**
	 * Bottom panel with descriptions and buttons;
	 */
	private JPanel bottom;
	
	/**
	 * Panel with all descriptions
	 */
	private JPanel details;
	
	/**
	 * Panel for detail buttons
	 */
	JPanel detailButtons;
	
	/**
	 * Buttons for navigating UI
	 */
	private JButton back, addItem, forward, edit, delete;
	
	/**
	 * List for the current object we are viewing
	 */
	private JList<String> leftList;
	/**
	 * Label for type of the current object we are viewing
	 */
	private JLabel leftTitle;
	
	/**
	 * Arraylist of lists containing all objects that are navigable to
	 */
	private ArrayList<JList<String>> rightLists;
	
	/**
	 * Arraylist of labels containing all the titles of navigable objects
	 */
	private ArrayList<JLabel> rightTitles;
	
	/**
	 * Connection to the database
	 */
	private Controller control;
	
	
	/**
	 * Array of labels for the descriptions
	 */
	private ArrayList<String> propertyLabels;
	
	/**
	 * Description for each property.
	 */
	private ArrayList<String> propertyDescriptions;
	/**
	 * Constructor for the UI
	 * @param Title for user interface
	 */
	public UI(String title) {
		super(title);
		
		propertyLabels = new ArrayList<String>();
		propertyDescriptions = new ArrayList<String>();
		//How the UI will interact with the database
		control = new Controller();
		
		
		base = new JPanel();
		bottom = new JPanel();
		details = new JPanel();
		
		//Just to make the UI look nicer
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method that starts the drawing of all the frame objects
	 * Must be called after creation of object.
	 */
	public void initialize() {
		setResizable(false);
		setLayout(new BorderLayout());
		
		//base.setSize(400, 400);
		add(base,BorderLayout.CENTER);
		base.setLayout(new BorderLayout());
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
		
		addMenuBar();
		addLeftList();
		addRightLists();
		addOptions();
		addDescription();
		base.add(bottom,BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Helper Method to redraw the frame to include new or removed panels
	 */
	private void reinitialize() {
		setLayout(new BorderLayout());
		
		remove(bottom);
		remove(base);
		
		
		base = new JPanel();
		base.setLayout(new BorderLayout());
		add(base,BorderLayout.CENTER);

		bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
		
		
		addMenuBar();
		addLeftList();
		addRightLists();
		addOptions();
		addDescription();
		base.add(bottom,BorderLayout.SOUTH);
		
	
		pack();
		revalidate();
		repaint();
	}
	
	/**
	 * Repaint the details/descriptions
	 */
	private void resetDetails() {
		bottom.remove(details);
		bottom.remove(detailButtons);
		details = new JPanel();
		detailButtons = new JPanel();
		addDescription();
		
		revalidate();
		repaint();
		pack();
	}
	
	private void addDescription() {
		
		/*Description Text*/
		JPanel screen = new JPanel();
		screen.setLayout(new BorderLayout());
		
		JPanel descriptors = new JPanel();
		descriptors.setLayout(new BoxLayout(descriptors, BoxLayout.PAGE_AXIS));
		JPanel descriptions = new JPanel();
		descriptions.setLayout(new BoxLayout(descriptions, BoxLayout.PAGE_AXIS));
		
		
		for(String s:propertyLabels) {
			
			JTextArea field = new JTextArea(s + ":");
			field.setEnabled(false);
			descriptors.add(field);
		}
		
		ArrayList<JTextArea> entryFields = new ArrayList<JTextArea>();
		for(String s:propertyDescriptions) {
			JTextArea field = new JTextArea(s);
			entryFields.add(field);
			field.setEnabled(false);
			descriptions.add(field);
			
		}
		
		screen.add(descriptors,BorderLayout.WEST);
		screen.add(descriptions,BorderLayout.EAST);
		details.add(screen);
		bottom.add(details);
		
		/*Description Buttons*/
		
		detailButtons = new JPanel();
		edit = new JButton("Edit");
		edit.setEnabled(false);
		edit.addActionListener(new EditButtonListener(entryFields,edit));
		delete = new JButton("Delete");
		delete.setEnabled(false);
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.delete();
				int selectedIndex = leftList.getSelectedIndex();
				reloadTables();
				
				leftList.setSelectedIndex(selectedIndex);
				rightLists.get(control.getSelectedIndex()).setBackground(Color.LIGHT_GRAY);
				
				if(control.hasMoreDepth()) {
					forward.setEnabled(true);
				}
				back.setEnabled(true);
				addItem.setEnabled(true);
				
				control.getDetails();
				propertyLabels = new ArrayList<String>();
				propertyDescriptions = new ArrayList<String>();
				resetDetails();
				
			}
			
		});
		detailButtons.add(edit,BorderLayout.SOUTH);
		detailButtons.add(delete,BorderLayout.SOUTH);
		bottom.add(detailButtons);
	
	}
	
	/**
	 * Helper method to draw the navigation buttons
	 */
	private void addOptions() {
		
		UI that = this;
		
		JPanel screen = new JPanel();
		
		back = new JButton("<");
		back.setEnabled(false);
		back.setToolTipText("Back");
		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = that.leftList.getSelectedIndex();
				that.control.moveBack();
				that.reloadTables();
				that.leftList.setSelectedValue(that.control.getLeftSelection(), true);
				rightLists.get(control.getSelectedIndex()).setSelectedIndex(selectedIndex);
				rightLists.get(control.getSelectedIndex()).setBackground(Color.LIGHT_GRAY);
				
				control.setRightSelection(rightLists.get(control.getSelectedIndex()).getSelectedValue());
				if(that.control.getLeftTitle().equals("GalaxyCluster")) {
					back.setEnabled(false);
				}
				else {
					back.setEnabled(true);
				}
				forward.setEnabled(true);
				addItem.setEnabled(true);
				
				control.getDetails();
				propertyLabels = control.getSelectedAttributes();
				propertyDescriptions = control.getSelectedDetails();
				
				resetDetails();
				
				edit.setEnabled(true);
				delete.setEnabled(true);
				
			}
		});
		
		addItem = new JButton("+");
		addItem.setEnabled(false);
		addItem.setToolTipText("Add new entry");
		addItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter name of object to add to " + control.getLeftSelection());
				if(name != null && name.length() > 0) {
					control.insert(name);
					control.reloadTables();
					reloadTables();
					
					leftList.setSelectedValue(that.control.getLeftSelection(), true);
					control.setRightSelection(name);
					rightLists.get(control.getSelectedIndex()).setSelectedValue(name, true);
					rightLists.get(control.getSelectedIndex()).setBackground(Color.LIGHT_GRAY);
					
					if(control.hasMoreDepth()) {
						forward.setEnabled(true);
					}
					back.setEnabled(true);
					addItem.setEnabled(true);
					
					control.getDetails();
					propertyLabels = control.getSelectedAttributes();
					propertyDescriptions = control.getSelectedDetails();
					resetDetails();
					edit.setEnabled(true);
					delete.setEnabled(true);
					
				}
				
			}
		});
		
		forward = new JButton(">");
		forward.setEnabled(false);
		forward.setToolTipText("Forward");
		forward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(that.control.getRightSelection() != null) {
					int selectedIndex = rightLists.get(control.getSelectedIndex()).getSelectedIndex();			
					that.control.moveInto();
					that.reloadTables();
					that.leftList.setSelectedIndex(selectedIndex);
					that.control.setLeftSelection(that.leftList.getSelectedValue());
					that.control.reloadTables();
					that.reloadRightTable();
					that.back.setEnabled(true);
					that.forward.setEnabled(false);
					
					that.control.setRightSelection(null);
					
					propertyLabels = new ArrayList<String>();
					propertyDescriptions = new ArrayList<String>();
					resetDetails();
					
					addItem.setEnabled(false);
				}
			}
		});
		
		screen.add(back);
		screen.add(addItem);
		screen.add(forward);
		//bottom.add(screen,BorderLayout.CENTER);
		bottom.add(screen);
		
		
	}
	
	/**
	 * Helper method for drawing the left panel and list
	 */
	private void addLeftList() {
		JPanel screen = new JPanel();
		screen.setLayout(new BorderLayout());
		leftList = new JList<>(control.getLeftList());

		UI that = this;
		leftList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					for(JList<String> rightList: rightLists) {
						rightList.clearSelection();
					}
					
					forward.setEnabled(false);
					that.control.setLeftSelection(that.leftList.getSelectedValue());
					that.control.reloadTables();
					that.reloadRightTable();
					
					propertyLabels = new ArrayList<String>();
					propertyDescriptions = new ArrayList<String>();
					resetDetails();
				}
				
			}
			
		});
		leftList.setSelectedIndex(0);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(leftList);
		scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH,100));

		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());
		JTextField search = new JTextField();
		search.setToolTipText("Search list (No functionality yet)");
		search.setPreferredSize(new Dimension(TABLE_WIDTH,20));
		search.setEnabled(false);
		leftTitle = new JLabel(control.getLeftTitle(),JLabel.CENTER); 
		header.add(search,BorderLayout.NORTH);
		header.add(leftTitle,BorderLayout.SOUTH);
		
		screen.add(header,BorderLayout.NORTH);
		screen.add(scrollPane,BorderLayout.CENTER);
		
		//For consistency in the look
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		panel.add(screen);
		base.add(panel,BorderLayout.WEST);
		
	}
	
	/**
	 * Helper method for drawing the right panels and lists
	 */
	private void addRightLists() {
		
		rightTitles = new ArrayList<JLabel>();
		
		JPanel lists = new JPanel();
		lists.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		rightLists = new ArrayList<JList<String>>();
		for(DefaultListModel<String> rightList: control.getRightLists()) {
			rightLists.add(new JList<String>(rightList));
		}
		
		int index = 0;
		for(JList<String> rightList: rightLists) {
			rightList.addListSelectionListener(new ListListener(index,rightList));
			rightList.addMouseListener(new MouseListListener(index,rightList));
			
			JPanel screen = new JPanel();
			screen.setLayout(new BorderLayout());
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(rightList);
			scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH,100));
			
			JPanel header = new JPanel();
			header.setLayout(new BorderLayout());
			JTextField search = new JTextField();
			search.setToolTipText("Search list (No functionality yet)");
			search.setPreferredSize(new Dimension(TABLE_WIDTH,20));
			search.setEnabled(false);
			JLabel rightTitle = new JLabel(control.getRightTitle(index),JLabel.CENTER);
			rightTitles.add(rightTitle);
			header.add(search,BorderLayout.NORTH);
			header.add(rightTitle,BorderLayout.SOUTH);
			
			screen.add(header,BorderLayout.NORTH);
			screen.add(scrollPane,BorderLayout.CENTER);
			
			lists.add(screen);
			index++;
		}
		
		base.add(lists,BorderLayout.CENTER);
	}
	
	/**
	 * Helper method for drawing the top menu bar
	 */
	private void addMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu about = new JMenu("About");
		about.addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				JOptionPane.showMessageDialog(base,"TCSS 445 Database UI\n"
						+ "							Some functionality not fully implemented due to time constrains\n"
						+ "							Author: Romi Tshiorny", "About", JOptionPane.PLAIN_MESSAGE);
				
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		bar.add(about);
		add(bar,BorderLayout.NORTH);
	}
	
	/**
	 * Reloads the tables with the updated values in the controller
	 */
	private void reloadTables() {
		reinitialize();
		reloadLeftTable();
		reloadRightTable();
	}
	
	/**
	 * Helper to reload left table. Used by reloadTables()
	 */
	private void reloadLeftTable(){
		leftTitle.setText(control.getLeftTitle());
		leftList.setModel(control.getLeftList());
	}
	

	/**
	 * Helper to reload right table. Used by reloadTables()
	 */
	private void reloadRightTable(){
		
		int i = 0;
		for(JLabel rightTitle:rightTitles) {
			rightTitle.setText(control.getRightTitle(i));
			i++;
		}
		i = 0;
		for(JList<String> rightList: rightLists) {
			rightList.clearSelection();
			rightList.setModel(control.getRightList(i));
			i++;
		}
		
	}
	
	/**
	 * Private listener class for events that happen when selecting list objects
	 * @author Romi Tshiorny
	 */
	private class ListListener implements ListSelectionListener {

		private int theIndex;
		private JList<String> theList;
		public ListListener(int index,JList<String> list) {
			theIndex = index;
			theList = list;
			
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				control.setSelectedIndex(theIndex);
				control.setRightSelection(theList.getSelectedValue());
				if(control.hasMoreDepth()) {
					forward.setEnabled(true);
				}
				else {
					forward.setEnabled(false);
				}
				control.getDetails();
				
				propertyLabels = control.getSelectedAttributes();
				propertyDescriptions = control.getSelectedDetails();
				
				
				resetDetails();
				
				edit.setEnabled(true);
				delete.setEnabled(true);
			}
			
		}
		
	}
	/**
	 * Private listener class for events that happen when selecting list panels
	 * @author Romi Tshiorny
	 *
	 */
	private class MouseListListener implements MouseListener{

		private int theIndex;
		private JList<String> theList;
		public MouseListListener(int index, JList<String> list) {
			theIndex = index;
			theList = list;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			theList.setBackground(Color.LIGHT_GRAY);
			control.setSelectedIndex(theIndex);
			int index = control.getSelectedIndex();
			for(int i = 0; i<rightLists.size();i++) {
				if(i!=index) {
					rightLists.get(i).setBackground(Color.WHITE);
					rightLists.get(i).clearSelection();
				}
			}
			if(theList.getModel().getSize() <= 0) {
				forward.setEnabled(false);
				propertyLabels = new ArrayList<String>();
				propertyDescriptions = new ArrayList<String>();
				resetDetails();
			}
			else {
				
				if(control.hasMoreDepth()) {
					forward.setEnabled(true);
				}
				else {
					forward.setEnabled(false);
				}
			}
			
			
			
			addItem.setEnabled(true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
	}
	
	
	/**
	 * Private listener for events that happen on the edit button.
	 * @author Romi Tshiorny
	 *
	 */
	private class EditButtonListener implements ActionListener{

		private ArrayList<JTextArea> theFields;
		private JButton theButton;
		private boolean toggle;
		public EditButtonListener(ArrayList<JTextArea> fields, JButton button) {
			toggle = true;
			theFields=fields;
			theButton = button;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//On edit press
			if(toggle) {
				
				theButton.setText("Update");
				for(JTextArea field:theFields) {
					field.setEnabled(true);
				}

			}
			//On update press
			else {
				
				theButton.setText("Edit");
				for(JTextArea field:theFields) {
					field.setEnabled(false);
				}
				
				for(int i = 0; i < propertyLabels.size();i++) {
					System.out.println(propertyLabels.get(i) + " : " + theFields.get(i).getText());
				}
				
				
			}
			toggle = !toggle;
			
		}
		
	}
	/**
	 * Main method where program execution starts
	 * @param args
	 */
	public static void main(String args[]) {
		UI frame = new UI("App");
		frame.initialize();
	}
}
