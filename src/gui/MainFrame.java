package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;

import javax.swing.JTabbedPane;

import java.awt.Choice;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;

import javax.swing.Box;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import database.GradeManager;

import javax.swing.JSeparator;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtAssignment;
	private JTextField textField_1;
	
	private GradeManager grades = new GradeManager();
	private JTextField textField_2;
	private JTextField textField;

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
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		
		Choice choice = new Choice();
		panel.setLayout(new GridLayout(20, 1, 0, 0));
		
		JLabel lblAddAssignment = new JLabel("Add Assignment");
		lblAddAssignment.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblAddAssignment);
		
		Component rigidArea = Box.createRigidArea(new Dimension(200, 20));
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
				grades.addAssignment(txtAssignment.getText(), textField.getText(), textField_1.getText(), (String) comboBox.getSelectedItem()); 
			}
		});
		panel.add(btnAddAssignment);
		panel.add(choice);
		
		JLabel label = new JLabel("___________________________");
		panel.add(label);
		
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
				grades.addClass(textField_2.getText());
			}
		});
		panel.add(btnAddClass);
	}

}
