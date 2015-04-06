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
		if (yourClass.equals("All")) return getAllAssignments();
		return toListNormalArray(scores.getMatchingRows("CLASS", yourClass));
	}
	
	public void removeAssignment(String assignmentName) {
		
	}
	
	public String[] getClasses() {
		return getIndexOfArrays(toListNormalArray(classes.selectData()), 0);
	}
	
	public ArrayList<String> getClassesList() {
		ArrayList <String> classList = new ArrayList<String>();
		for (String c : getIndexOfArrays(toListNormalArray(classes.selectData()), 0)) {
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
	
	public int getScoreTotal() {
		String[] allScores = toNormalArray(scores.getColumnData("POINTS"));
		int sum = 0;
		for (String s : allScores) {
			sum += Integer.parseInt(s);
		}
		return sum;
	}
	
	public int getPossTotal() {
		String[] allScores = toNormalArray(scores.getColumnData("OUTOF"));
		int sum = 0;
		for (String s : allScores) {
			sum += Integer.parseInt(s);
		}
		return sum;
	}
	
	public int getColumnTotal(String col, String theClass) {
		String[] allScores = toNormalArray(scores.getColumnData(col, "CLASS", theClass));
		int sum = 0;
		for (String s : allScores) {
			sum += Integer.parseInt(s);
		}
		return sum;
	}
	
	public int getColumnTotal(String col, String condition, boolean r) {
		String[] allScores = toNormalArray(scores.getColumnData(col, condition));
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
		System.out.println(gm.getScoreTotal());
		System.out.println(gm.getPossTotal());
	}
}
