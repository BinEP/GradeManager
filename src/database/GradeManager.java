package database;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.JTable;

public class GradeManager {

	DatabaseManagement classes;
	DatabaseManagement scores;
	
	public GradeManager() {
		String[][] fields = {{"ID", "INT"}, {"NAME", "TEXT"}};
		classes = new DatabaseManagement(DatabaseManagement.CLASSES_DB, fields);
		String[][] fields1 = {{"ID", "INT"}, {"ASSIGNMENT", "TEXT"}, {"POINTS", "INT"}, {"OUTOF", "INT"}, {"CLASS", "TEXT"}};
		scores = new DatabaseManagement(DatabaseManagement.SCORES_DB, fields1);
	}
	
	public void addClass(String newClass) {
		classes.insertInfo(newClass);
	}
	
	public void addAssignment(String assignmentName, String score, String possiblePoints, String theClass) {
		scores.insertInfo(assignmentName, score, possiblePoints, theClass);
	}
	
	public void updateScoreInfo(String id, String column, String newValue) {
		scores.updateInfo(id, column, newValue);
	}
	
	public String[][] getClassAssignments(String yourClass) {
		if (yourClass.equals("All")) return getAllAssignments();
		return toListNormalArray(scores.getMatchingRows("CLASS", yourClass));
	}
	
	public void removeAssignment(String assignmentName) {
		
	}
	
	public String[] getClasses() {
		return getIndexOfArrays(toListNormalArray(classes.selectData()), 1);
	}
	
	public ArrayList<String> getClassesList() {
		ArrayList <String> classList = new ArrayList<String>();
		for (String c : getIndexOfArrays(toListNormalArray(classes.selectData()), 1)) {
			classList.add(c);
		}
		return classList;
	}
	
	public String[] getAssignmentHeaders() {
		return scores.getColumnNames();
	}
	
	public String[][] getAllAssignments() {
		return toListNormalArray(scores.selectData());
	}
	
	public int getColumnTotal(String col, String theClass) {
		if (theClass.equals("All")) theClass = "%";
		String[] allScores = toNormalArray(scores.getColumnData(col, "CLASS LIKE '" + theClass
				+ "'"));
		int sum = 0;
		for (String s : allScores) {
			sum += Integer.parseInt(s);
		}
		return sum;
	}
	
	public int getNumOfAssignments(String theClass) {
		if (theClass.equals("all")) return getAllAssignments().length;
		return getClassAssignments(theClass).length;
	}
	
	public String[][] getAssignmentsSorted(boolean up, String... colsSortBy) {
		return toListNormalArray(scores.sortBy(up, colsSortBy));
	}
	
	private String[] toNormalArray(ArrayList<String> arr) {
		String[] newArr = new String[arr.size()];
		int index = 0;
		for (String s : arr) {
			newArr[index] = s;
			index++;
		}
		return newArr;
	}
	
	private String[][] toListNormalArray(ArrayList<String[]> arr) {
		if (arr == null || arr.size() == 0) return new String[0][0];
		String[][] newArr = new String[arr.size()][arr.get(0).length];
		int index = 0;
		for (String[] s : arr) {
			newArr[index] = s;
			index++;
		}
		return newArr;
	}
	
	private String[] getIndexOfArrays(String[][] arr, int index) {
		String[] newArr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			newArr[i] = arr[i][index];
		}
		return newArr;
	}
	
	public int nextScoreID() {
		return scores.newID();
	}
	
	public int nextClassID() {
		return classes.newID();
	}
	
	public static void main(String[] args) {
		GradeManager gm = new GradeManager();
		for (String s : gm.getAssignmentHeaders()) {
			System.out.println(s);
		}
		for (String s[] : gm.getAllAssignments()) {
			for (String t : s) {
				System.out.print(t + "\t");
			}
			System.out.println();
		}
		System.out.println(gm.getColumnTotal("POINTS", "All"));
		System.out.println(gm.getColumnTotal("OUTOF", "All"));
	}
}
