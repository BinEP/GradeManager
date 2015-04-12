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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JSeparator;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MainFrame extends JFrame implements CellEditorListener, KeyListener {

	private static final long serialVersionUID = -3136693642689296249L;
	private final JPanel contentPane = new JPanel();
	private final JTextField txtAssignment = new JTextField();
	private final JTextField outOfField = new JTextField();

	private final GradeManager grades = new GradeManager();
	private final JTextField classField = new JTextField();
	private final JTextField scoreField = new JTextField();
	private final JComboBox<String> classSelectBox = new JComboBox<String>();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final HashMap<String, JTable> assignmentTables = new HashMap<String, JTable>();
	private final HashMap<String, JTable> sumTables = new HashMap<String, JTable>();

	private final String[] colHeaders = grades.getAssignmentHeaders();

	private void addAssignment() {
		grades.addAssignment(txtAssignment.getText(), scoreField.getText(),
				outOfField.getText(), (String) classSelectBox.getSelectedItem());
	}

	private void addClass() {
		grades.addClass(classField.getText());
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
				classSelectBox.setSelectedItem(title);
			}
		});
	}

	private void setupOuterPane() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}

	private void makePanes(JTabbedPane tabbedPane) {
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
		TableColumnModel tm = scoreTable.getColumnModel();
		tm.removeColumn(tm.getColumn(0));
		
		scoreTable.putClientProperty("terminateEditOnFocusLost", true);
		scoreTable.setAutoCreateColumnsFromModel(false);
		scoreTable.addKeyListener(this);
		scoreTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		assignmentTables.put(className, scoreTable);
	}
	
	private void makeNewSumTable(JTable tab, String s) {
		setupSumTable(tab, s);
		TableColumnModel tm = tab.getColumnModel();
		tm.removeColumn(tm.getColumn(0));
		tm.removeColumn(tm.getColumn(tm.getColumnCount() - 1));
		tab.setAutoCreateColumnsFromModel(false);
		
		tab.setEnabled(false);
		sumTables.put(s, tab);
	}
	
	private void setupClassTable(JTable tab, String className) {
		DefaultTableModel model = new DefaultTableModel(
				grades.getClassAssignments(className), grades.getAssignmentHeaders());
		
		tab.setModel(model);
		addTableListener(tab);
		
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
		
		
	}

	private void addAssignmentUpdateTables(boolean assignment) {

		if (assignment) {
			addAssignment();
			String className = (String) classSelectBox.getSelectedItem();
			refreshTables(className);
			
		} else {
			addClass();
			addPanesToClassTabs(classField.getText());
			classSelectBox.setModel(new DefaultComboBoxModel<String>(grades
					.getClasses()));
		}
	}
	
	private void refreshTables(String className) {
		refreshClassTables(className);
		refreshSumTables(className);
	}
	
	private void refreshTables() {
		ArrayList < String > theClasses = grades.getClassesList();
		theClasses.add("All");
		for (String className : theClasses) {
			refreshClassTables(className);
			refreshSumTables(className);
		}
	}
	
	private void refreshClassTables(String className) {
		setupClassTable(assignmentTables.get(className), className);
		setupClassTable(assignmentTables.get("All"), "All");
	}

	private void refreshSumTables(String className) {
		setupSumTable(sumTables.get(className), className);
		setupSumTable(sumTables.get("All"), "All");
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

		addLabel(panel, "Add Assignment");

		addRigid(panel);

		JLabel lblAssignmentName = new JLabel("Assignment Name");
		lblAssignmentName.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAssignmentName);
		addLabel(panel, "Assignment Name");

		txtAssignment.setText("assignment");
		panel.add(txtAssignment);
		txtAssignment.setColumns(10);

		addRigid(panel);

		JLabel lblScore = new JLabel("Score");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblScore);
		addLabel(panel, "Add Assignment");

		scoreField.setMinimumSize(new Dimension(10, 28));
		scoreField.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(scoreField);
		scoreField.setColumns(4);

		JLabel lblOutOf = new JLabel("Out Of");
		lblOutOf.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblOutOf);
		addLabel(panel, "Add Assignment");

		panel.add(outOfField);
		outOfField.setColumns(10);

		addRigid(panel);

		
		classSelectBox.setModel(new DefaultComboBoxModel<String>(grades.getClasses()));
		try {
			classSelectBox.setSelectedIndex(0);
		} catch (IllegalArgumentException e) {
			System.out.println("No Classes");
		}
		panel.add(classSelectBox);

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
		addLabel(panel, "Add Assignment");

		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_1);

		JLabel lblClass = new JLabel("Class");
		panel.add(lblClass);
		addLabel(panel, "Add Assignment");
		
		panel.add(classField);
		classField.setColumns(10);

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

	private void addLabel(JPanel panel, String label) {
		JLabel lblAddAssignment = new JLabel(label);
		lblAddAssignment.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAddAssignment);
	}

	private void addRigid(JPanel panel) {
		Component rigidArea = Box.createRigidArea(new Dimension(150, 20));
		panel.add(rigidArea);
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
	
	private void deleteRows(JTable tab) {
		int[] rows = tab.getSelectedRows();
		for (int i : rows) {
			String id = (String) tab.getModel().getValueAt(i, 0);
			grades.deleteAssignment(id);
		}
		refreshTables();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getComponent());
		JTable tab = (JTable) e.getComponent();
		if (tab.getEditingRow() == -1) {
			System.out.println("Key Pressed");
			deleteRows(tab);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}