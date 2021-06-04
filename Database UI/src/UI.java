import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class UI extends JFrame{
	
	private final static int TABLE_WIDTH = 100;
	
	private JPanel base;
	private JButton back, addItem, forward;
	private JList<String> leftList, rightList;
	private JLabel leftTitle, rightTitle;
	private ArrayList<JList> rightLists;
	private ArrayList<JLabel> rightTitles;
	private Controller control;
	
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
	public void initialize() {
		setLayout(new BorderLayout());
		
		//base.setSize(400, 400);
		add(base,BorderLayout.CENTER);
		base.setLayout(new BorderLayout());
		
		addMenuBar();
		addLeftList();
		addRightList();
		addOptions();
	
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
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
				that.rightList.setSelectedIndex(selectedIndex);
				if(that.control.getLeftTitle().equals("GalaxyCluster")) {
					back.setEnabled(false);
				}
				
			}
		});
		
		addItem = new JButton("+");
		addItem.setToolTipText("Add new entry");
		addItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//that.leftList.removeElement(that.leftSelect);
				System.out.println(that.rightList.getSelectedValue());
				
			}
		});
		
		forward = new JButton(">");
		forward.setEnabled(false);
		forward.setToolTipText("Forward");
		forward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(that.rightList.getSelectedValue() != null) {
					int selectedIndex = that.rightList.getSelectedIndex();
					that.control.moveInto();
					that.reloadTables();
					that.leftList.setSelectedIndex(selectedIndex);
					that.control.setLeftSelection(that.leftList.getSelectedValue());
					that.control.reloadTables();
					that.reloadRightTable();
					that.rightList.clearSelection();
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
	private void addLeftList() {
		JPanel screen = new JPanel();
		screen.setLayout(new BorderLayout());
		leftList = new JList<>(control.getLeftList());

		UI that = this;
		leftList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					that.rightList.clearSelection();
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
		leftTitle = new JLabel(control.getLeftTitle(),JLabel.CENTER); 
		header.add(search,BorderLayout.NORTH);
		header.add(leftTitle,BorderLayout.SOUTH);
		
		screen.add(header,BorderLayout.NORTH);
		screen.add(scrollPane,BorderLayout.CENTER);
		base.add(screen,BorderLayout.WEST);
		
	}
	private void addRightList() {
		JPanel screen = new JPanel();
		screen.setLayout(new BorderLayout());
		
		rightList = new JList<>(control.getRightList());
		
		UI that = this;
		rightList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					that.control.setRightSelection(that.rightList.getSelectedValue());
					forward.setEnabled(true);
				}
				
			}
			
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(rightList);
		scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH,100));
		
		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());
		JTextField search = new JTextField();
		search.setToolTipText("Search list (No functionality yet)");
		search.setPreferredSize(new Dimension(TABLE_WIDTH,20));
		rightTitle = new JLabel(control.getRightTitle(),JLabel.CENTER); 
		header.add(search,BorderLayout.NORTH);
		header.add(rightTitle,BorderLayout.SOUTH);
		
		screen.add(header,BorderLayout.NORTH);
		screen.add(scrollPane,BorderLayout.CENTER);
		base.add(screen,BorderLayout.CENTER);
		
	}
	public void addMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu about = new JMenu("About");
		bar.add(about);
		add(bar,BorderLayout.NORTH);
	}
	public void reloadTables() {
		reloadLeftTable();
		reloadRightTable();
	}
	public void reloadLeftTable(){
		leftTitle.setText(control.getLeftTitle());
		leftList.setModel(control.getLeftList());
	}
	public void reloadRightTable(){
		rightTitle.setText(control.getRightTitle());
		rightList.setModel(control.getRightList());
	}
	public static void main(String args[]) {
		UI frame = new UI("App");
		frame.initialize();
	}
}
