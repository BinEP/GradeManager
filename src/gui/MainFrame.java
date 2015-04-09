package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;

import javax.swing.JTabbedPane;

import java.awt.Component;

import javax.swing.Box;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import database.GradeManager;

import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JSeparator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class MainFrame extends JFrame implements CellEditorListener {

	private static final long serialVersionUID = -3136693642689296249L;
	private JPanel contentPane;
	private JTextField txtAssignment;
	private JTextField textField_1;

	private GradeManager grades = new GradeManager();
	private JTextField textField_2;
	private JTextField textField;
	private JTable scoreTable;
	private JTable sumTable;
	private JComboBox<String> comboBox;
	private JTabbedPane tabbedPane;
	private HashMap<String, JTable> assignmentTables = new HashMap<String, JTable>();
	private HashMap<String, JTable> sumTables = new HashMap<String, JTable>();

	private String[] colHeaders = grades.getAssignmentHeaders();

	private void addAssignment() {
		grades.addAssignment(txtAssignment.getText(), textField.getText(),
				textField_1.getText(), (String) comboBox.getSelectedItem());
	}

	private void addClass() {
		grades.addClass(textField_2.getText());
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setupOuterPane();
		makePanes(tabbedPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(20, 1, 0, 0));
		makeSideBar(panel);
		
		addChangeToTabs();
	}

	private void addChangeToTabs() {
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				String title = sourceTabbedPane.getTitleAt(index);
				if (title == null)
					return;
				System.out.println("Tab changed to: " + title);
				comboBox.setSelectedItem(title);
			}
		});
	}

	private void setupOuterPane() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}

	private void makePanes(JTabbedPane tabbedPane) {

		scoreTable = new JTable();
		addClassTabs(colHeaders);
	}

	private void addClassTabs(String[] colHeaders) {
		
		ArrayList<String> classList = grades.getClassesList();
		classList.add(0, "All");
		
		for (String s : classList) {
			addPanesToClassTabs(s);
		}
	}

	private void addPanesToClassTabs(String s) {
		JPanel scoreWrapper;
		JScrollPane scorePane;
		JScrollPane sumPane;
		
		scoreWrapper = new JPanel();

		scorePane = makeClassScorePane(s);
		sumPane = makeClassSumPane(s);

		scoreWrapper.add(scorePane);
		scoreWrapper.add(sumPane);

		tabbedPane.addTab(s, null, scoreWrapper, null);
	}

	private JScrollPane makeClassScorePane(String className) {
		
		JTable scoreTable = new JTable();
		makeNewClassTable(scoreTable, className);
		JScrollPane scorePane = new JScrollPane(scoreTable);
		
		return scorePane;
	}
	
	private JScrollPane makeClassSumPane(String s) {
		JTable sumTable = new JTable();
		makeNewSumTable(sumTable, s);
		JScrollPane sumPane = new JScrollPane(sumTable);
		return sumPane;
	}

	private void makeNewClassTable(JTable scoreTable, String className) {
		setupClassTable(scoreTable, className);
		addTableListener(scoreTable);
		
		assignmentTables.put(className, scoreTable);
	}
	
	private void makeNewSumTable(JTable tab, String s) {
		setupSumTable(tab, s);
		sumTables.put(s, tab);
	}
	
	private void setupClassTable(JTable tab, String className) {
		DefaultTableModel model = new DefaultTableModel(
				grades.getClassAssignments(className), grades.getAssignmentHeaders());
		
		tab.setModel(model);
		TableColumnModel tm = tab.getColumnModel();
		tm.removeColumn(tm.getColumn(0));
		
		tab.putClientProperty("terminateEditOnFocusLost", true);
		tab.setAutoCreateColumnsFromModel(false);
	}

	private void setupSumTable(JTable tab, String className) {
		
		String s1 = className;
		if (className.equals("All")) 
			s1 = "%";
		
		int scoreTotal = grades.getColumnTotal("POINTS", s1);
		int possTotal = grades.getColumnTotal("OUTOF", s1);
		double percent = getPercent(scoreTotal, possTotal);
		
		String[][] rowData = { { "ID", "Total", "" + scoreTotal, "" + possTotal,
				"" + percent + "%", "" } };
		
		DefaultTableModel model = new DefaultTableModel(rowData, colHeaders);
		tab.setModel(model);
		
		TableColumnModel tm = tab.getColumnModel();
		tm.removeColumn(tm.getColumn(0));
		tab.setEnabled(false);
	}

	private void addAssignmentUpdateTables(boolean assignment) {

		if (assignment) {
			addAssignment();
			String className = (String) comboBox.getSelectedItem();
			refreshTables(className);
			
		} else {
			addClass();
			addPanesToClassTabs(textField_2.getText());
			comboBox.setModel(new DefaultComboBoxModel<String>(grades
					.getClasses()));
		}
	}
	
	private void refreshTables(String className) {
		refreshClassTables(className);
		refreshSumTables(className);
	}
	
	private void refreshTables() {
		for (String className : grades.getClasses()) {
			refreshClassTables(className);
			refreshSumTables(className);
		}
	}
	
	private void refreshClassTables(String className) {
		makeNewClassTable(assignmentTables.get(className), className);
		makeNewClassTable(assignmentTables.get("All"), "All");
	}

	private void refreshSumTables(String className) {
		makeNewSumTable(sumTables.get(className), className);
		makeNewSumTable(sumTables.get("All"), "All");
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		
	}

	private double getPercent(int scoreTotal, int possTotal) {
		double percent = Math.round((double) scoreTotal / (double) possTotal
				* 100.0 * 100.0) / 100.0;
		return percent;
	}

	private void makeSideBar(JPanel panel) {

		JLabel lblAddAssignment = new JLabel("Add Assignment");
		lblAddAssignment.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAddAssignment);

		Component rigidArea = Box.createRigidArea(new Dimension(150, 20));
		panel.add(rigidArea);

		JLabel lblAssignmentName = new JLabel("Assignment Name");
		lblAssignmentName.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAssignmentName);

		txtAssignment = new JTextField();
		txtAssignment.setText("assignment");
		panel.add(txtAssignment);
		txtAssignment.setColumns(10);

		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_2);

		JLabel lblScore = new JLabel("Score");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblScore);

		textField = new JTextField();
		textField.setMinimumSize(new Dimension(10, 28));
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textField);
		textField.setColumns(4);

		JLabel lblOutOf = new JLabel("Out Of");
		lblOutOf.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblOutOf);

		textField_1 = new JTextField();
		panel.add(textField_1);
		textField_1.setColumns(10);

		Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_3);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(grades.getClasses()));
		try {
			comboBox.setSelectedIndex(0);
		} catch (IllegalArgumentException e) {
			System.out.println("No Classes");
		}
		panel.add(comboBox);

		JButton btnAddAssignment = new JButton("Add Assignment");
		btnAddAssignment.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// addAssignment(comboBox);
				addAssignmentUpdateTables(true);
			}
		});
		panel.add(btnAddAssignment);

		JSeparator separator = new JSeparator();
		panel.add(separator);

		JLabel lblAddClass = new JLabel("Add Class");
		panel.add(lblAddClass);

		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_1);

		JLabel lblClass = new JLabel("Class");
		panel.add(lblClass);

		textField_2 = new JTextField();
		panel.add(textField_2);
		textField_2.setColumns(10);

		JButton btnAddClass = new JButton("Add Class");
		btnAddClass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// addClass();
				addAssignmentUpdateTables(false);
			}
		});
		panel.add(btnAddClass);
	}
	
	private void addTableListener(JTable table) {
		table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				System.out.println(e);
				int row = e.getFirstRow();
				int col = e.getColumn();
				DefaultTableModel model = (DefaultTableModel) e.getSource();
				String newValue = (String) (model.getValueAt(row, col));
				
				String id = (String) model.getValueAt(row, 0);
				String columnName = model.getColumnName(col);
				grades.updateScoreInfo(id, columnName, newValue);
				refreshTables();
			}
		});
	}
}
