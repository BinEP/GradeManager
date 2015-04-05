package database;

import java.util.ArrayList;

public class GradeManager {

	DatabaseManagement classes;
	DatabaseManagement scores;
	
	public GradeManager() {
		String[][] fields = {{"NAME", "TEXT"}};
		classes = new DatabaseManagement(DatabaseManagement.CLASSES_DB, fields[0]);
		String[][] fields1 = {{"ASSIGNMENT", "TEXT"}, {"POINTS", "INT"}, {"OUTOF", "INT"}, {"CLASS", "TEXT"}};
		scores = new DatabaseManagement(DatabaseManagement.SCORES_DB, fields1[0], fields1[1], fields1[2], fields1[3]);
	}
	
	public void addClass(String newClass) {
		classes.insertInfo(newClass);
	}
	
	public void addAssignment(String assignmentName, String score, String possiblePoints, String theClass) {
		scores.insertInfo(assignmentName, score, possiblePoints, theClass);
	}
	
	public String[][] getClassAssignments(String yourClass) {
		return toListNormalArray(scores.getMatchingRows("CLASS", yourClass));
	}
	
	public void removeAssignment(String assignmentName) {
		
	}
	
	public String[] getClasses() {
		return getIndexOfArrays(toListNormalArray(classes.selectData()), 0);
	}
	
	public String[][] getAllAssignments() {
		return toListNormalArray(scores.selectData());
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
}
