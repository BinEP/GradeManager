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

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -3136693642689296249L;
	private JPanel contentPane;
	private JTextField txtAssignment;
	private JTextField textField_1;

	private GradeManager grades = new GradeManager();
	private JTextField textField_2;
	private JTextField textField;
	private JTable scoreTable;

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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		makeTables(tabbedPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(20, 1, 0, 0));

		makeSideBar(panel);
	}

	private void makeTables(JTabbedPane tabbedPane) {

		scoreTable = new JTable();
		scoreTable.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
			}
		});

		String[] colHeaders = { "Total Points", "Scored Points", "Out Of",
				"Percent", "" };
		
		JPanel scoreWrapper = new JPanel();
		
		JScrollPane scorePane = makeClassScoreTable("All");
		JScrollPane sumPane = makeSumTable(colHeaders, "All");
		
		scoreWrapper.add(scorePane);
		scoreWrapper.add(sumPane);

		tabbedPane.addTab("All", null, scoreWrapper, null);

		for (String s : grades.getClasses()) {
			
			scoreWrapper = new JPanel();
			
			scorePane = makeClassScoreTable(s);
			sumPane = makeSumTable(colHeaders, s);
			
			scoreWrapper.add(scorePane);
			scoreWrapper.add(sumPane);

			tabbedPane.addTab(s, null, scoreWrapper, null);
		}
	}

	private JScrollPane makeClassScoreTable(String s) {
		scoreTable = new JTable(grades.getClassAssignments(s),
				grades.getAssignmentHeaders());
		JScrollPane scorePane = new JScrollPane(scoreTable);
		return scorePane;
	}

	private JScrollPane makeSumTable(String[] colData, String s) {
		if (s.equals("All")) s = "%";
		int scoreTotal = grades.getColumnTotal("POINTS", "CLASS LIKE '" + s + "'", true);
		int possTotal = grades.getColumnTotal("OUTOF", "CLASS LIKE '" + s + "'", true);
		double percent = getPercent(scoreTotal, possTotal);
		String[][] rowData = { { "Total", "" + scoreTotal, "" + possTotal,
				"" + percent + "%", "" } };
		JTable sumTable = new JTable(rowData, colData);
		JScrollPane sumPane = new JScrollPane(sumTable);
		return sumPane;
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

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(grades.getClasses()));
		comboBox.setSelectedIndex(0);
		panel.add(comboBox);

		JButton btnAddAssignment = new JButton("Add Assignment");
		btnAddAssignment.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addAssignment(comboBox);
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
				addClass();
			}
		});
		panel.add(btnAddClass);

	}

}
