package database;

import java.util.ArrayList;

public class Tester {

	public Tester() {
	}
	
	public static void main(String[] args) {
		
		String[][] fields = {{"NAME", "TEXT"}};
		DatabaseManagement classes = new DatabaseManagement(DatabaseManagement.CLASSES_DB, fields[0]);
		String[][] fields1 = {{"ASSIGNMENT", "TEXT"}, {"POINTS", "INT"}, {"OUTOF", "INT"}, {"CLASS", "TEXT"}};
		DatabaseManagement scores = new DatabaseManagement(DatabaseManagement.SCORES_DB, fields1[0], fields1[1], fields1[2], fields1[3]);
		
		printDatabaseContents(classes, scores);
		
		classes.clearTable();
		scores.clearTable();
		
		printDatabaseContents(classes, scores);
		
		classes.insertInfo("English");
		classes.insertInfo("Science");
		classes.insertInfo("APUSH");
		
		scores.insertInfo("Assignment 1", "30", "30", "English");
		scores.insertInfo("Assignment 2", "30", "30", "Science");
		scores.insertInfo("Assignment 3", "30", "30", "APUSH");
		scores.insertInfo("Assignment 4", "30", "30", "APUSH");
		scores.insertInfo("Assignment 5", "30", "30", "English");
		scores.insertInfo("Assignment 6", "30", "30", "Science");
		scores.insertInfo("Assignment 7", "30", "30", "Science");
		
		printDatabaseContents(classes, scores);
		
		ArrayList<String[]> getClass = scores.getMatchingRows("CLASS", "Science");
		printClassResult(getClass, scores);
		
	}
	
	public static void printDatabaseContents(DatabaseManagement classes, DatabaseManagement scores) {
		System.out.println("Scores");

		for (String s : scores.getColumnNames()) {
			System.out.print(s + "\t");
		}
		System.out.println();
		
		for (String[] s : scores.selectData()) {
			for (String s1 : s) {
				System.out.print(s1 + "\t");
			}	
		}
		System.out.println();

		System.out.println("Classes");
		
		for (String s : classes.getColumnNames()) {
			System.out.print(s + "\t");
		}
		System.out.println();
		
		for (String[] s : classes.selectData()) {
			for (String s1 : s) {
				System.out.print(s1 + "\t");
			}
		}
		System.out.println();
	}
	
	public static void printClassResult(ArrayList <String[]> theClass, DatabaseManagement db) {
		
		System.out.println("Results");
		
		for (String s : db.getColumnNames()) {
			System.out.print(s + "\t");
		}
		System.out.println();
		
		for (String[] s : theClass) {
			for (String s1 : s) {
				System.out.print(s1 + "\t");
			}	
			System.out.println();
		}
		System.out.println();
	}
}
