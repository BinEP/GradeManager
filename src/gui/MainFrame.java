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

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MainFrame extends JFrame implements CellEditorListener{

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

	private void addAssignment(JComboBox<String> comboBox) {
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		contentPane.add(tabbedPane, BorderLayout.CENTER);

		makeTables(tabbedPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(20, 1, 0, 0));

		makeSideBar(panel);

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				String title = sourceTabbedPane.getTitleAt(index);
				if (title == null)
					return;
				System.out.println("Tab changed to: " + title);
				// updateTable(sourceTabbedPane, index);

				comboBox.setSelectedItem(title);
			}
		});
	}

	private void makeTables(JTabbedPane tabbedPane) {

		scoreTable = new JTable();
		scoreTable.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
			}
		});

		addAllTabs(tabbedPane, colHeaders);
	}

	private void addAllTabs(JTabbedPane tabbedPane, String[] colHeaders) {
		JPanel scoreWrapper;
		JScrollPane scorePane;
		JScrollPane sumPane;
		ArrayList<String> classList = grades.getClassesList();
		classList.add(0, "All");
		for (String s : classList) {

			scoreWrapper = new JPanel();

			scorePane = makeClassScoreTable(s);
			sumPane = makeSumTable(s);

			scoreWrapper.add(scorePane);
			scoreWrapper.add(sumPane);

			tabbedPane.addTab(s, null, scoreWrapper, null);
		}
	}

	private JScrollPane makeClassScoreTable(String s) {
		scoreTable = new JTable(new DefaultTableModel(
				grades.getClassAssignments(s), grades.getAssignmentHeaders()));
		assignmentTables.put(s, scoreTable);
		scoreTable.putClientProperty("terminateEditOnFocusLost", true);
		scoreTable.getModel().addTableModelListener(new TableModelListener() {
		      public void tableChanged(TableModelEvent e) {
		         System.out.println(e);
		         
		      }
		    });
		TableColumnModel tm = scoreTable.getColumnModel();
		JScrollPane scorePane = new JScrollPane(scoreTable);
		return scorePane;
	}

	private JScrollPane makeSumTable(String s) {
		makeNewSumTable(s);
		JScrollPane sumPane = new JScrollPane(sumTable);
		return sumPane;
	}

	private JTable makeNewSumTable(String s) {
		String s1 = s;
		if (s.equals("All")) {
			s1 = "%";
		}
		int scoreTotal = grades.getColumnTotal("POINTS", "CLASS LIKE '" + s1
				+ "'", true);
		int possTotal = grades.getColumnTotal("OUTOF", "CLASS LIKE '" + s1
				+ "'", true);
		double percent = getPercent(scoreTotal, possTotal);
		String[][] rowData = { { "Total", "" + scoreTotal, "" + possTotal,
				"" + percent + "%", "" } };
		sumTable = new JTable(new DefaultTableModel(rowData, colHeaders));
		sumTable.setEnabled(false);
		sumTables.put(s, sumTable);
		return sumTable;
	}

	private void updateTables(boolean assignment) {
		// assignments

		if (assignment) {
			String className = (String) comboBox.getSelectedItem();
			SimpleDateFormat date = new SimpleDateFormat("MM:dd:yyy");
			String theDate = date.format(new Date());
			String[] rowData = { txtAssignment.getText(), textField.getText(),
					textField_1.getText(), (String) comboBox.getSelectedItem(),
					theDate };

			grades.addAssignment(txtAssignment.getText(), textField.getText(),
					textField_1.getText(), (String) comboBox.getSelectedItem());
			JTable table = assignmentTables.get(className);
			DefaultTableModel model = (DefaultTableModel) (table.getModel());

			model.addRow(rowData);

			table = assignmentTables.get("All");
			model = (DefaultTableModel) (table.getModel());

			model.addRow(rowData);

			table = sumTables.get(className);
			model = (DefaultTableModel) (table.getModel());
			if (className.equals("All"))
				className = "%";
			int scoreTotal = grades.getColumnTotal("POINTS", "CLASS LIKE '"
					+ className + "'", true);
			int possTotal = grades.getColumnTotal("OUTOF", "CLASS LIKE '"
					+ className + "'", true);
			double percent = getPercent(scoreTotal, possTotal);
			String[][] rowData1 = { { "Total", "" + scoreTotal, "" + possTotal,
					"" + percent + "%", "" } };
			model.setDataVector(rowData1, colHeaders);

			table = sumTables.get("All");
			model = (DefaultTableModel) (table.getModel());
			if (className.equals("All"))
				className = "%";
			scoreTotal = grades.getColumnTotal("POINTS", "CLASS LIKE '"
					+ className + "'", true);
			possTotal = grades.getColumnTotal("OUTOF", "CLASS LIKE '"
					+ className + "'", true);
			percent = getPercent(scoreTotal, possTotal);
			String[][] rowData2 = { { "Total", "" + scoreTotal, "" + possTotal,
					"" + percent + "%", "" } };
			model.setDataVector(rowData2, colHeaders);
			// table.setModel(model);

		} else {
			// classes
			String theNewClass = textField_2.getText();
			grades.addClass(theNewClass);

			JPanel scoreWrapper;
			JScrollPane scorePane;
			JScrollPane sumPane;

			scoreWrapper = new JPanel();

			scorePane = makeClassScoreTable(theNewClass);
			sumPane = makeSumTable(theNewClass);

			scoreWrapper.add(scorePane);
			scoreWrapper.add(sumPane);
			comboBox.setModel(new DefaultComboBoxModel<String>(grades
					.getClasses()));
			tabbedPane.addTab(theNewClass, null, scoreWrapper, null);
		}

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
				updateTables(true);
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
				updateTables(false);
			}
		});
		panel.add(btnAddClass);
	}
}
