
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	 * Buttons for navigating UI
	 */
	private JButton back, addItem, forward;
	
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
	 * Constructor for the UI
	 * @param Title for user interface
	 */
	public UI(String title) {
		super(title);
		
		//How the UI will interact with the database
		control = new Controller();
		
		base = new JPanel();
		
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
		setLayout(new BorderLayout());
		
		//base.setSize(400, 400);
		add(base,BorderLayout.CENTER);
		base.setLayout(new BorderLayout());
		
		addMenuBar();
		addLeftList();
		addRightLists();
		addOptions();
		back.setEnabled(false);
	
		
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
		
		remove(base);
		
		base = new JPanel();
		add(base,BorderLayout.CENTER);
		base.setLayout(new BorderLayout());
		
		addMenuBar();
		addLeftList();
		addRightLists();
		addOptions();
	
		pack();
		revalidate();
		repaint();
	}
	
	/**
	 * Helper method to draw the navigation buttons
	 */
	private void addOptions() {
		
		UI that = this;
		
		JPanel screen = new JPanel();
		
		back = new JButton("<");
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
				forward.setEnabled(true);
				
			}
		});
		
		addItem = new JButton("+");
		addItem.setToolTipText("Add new entry");
		addItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.insert(); //TODO add insertion functionality
				
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
					
				}
			}
		});
		
		screen.add(back);
		screen.add(addItem);
		screen.add(forward);
		base.add(screen,BorderLayout.SOUTH);
		
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
			}
			else {
				
				if(control.hasMoreDepth()) {
					forward.setEnabled(true);
				}
				else {
					forward.setEnabled(false);
				}
			}
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
	 * Main method where program execution starts
	 * @param args
	 */
	public static void main(String args[]) {
		UI frame = new UI("App");
		frame.initialize();
	}
}
