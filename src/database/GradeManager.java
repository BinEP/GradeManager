package database;

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
		return (String[][]) scores.getMatchingRows("CLASS", yourClass).toArray();
	}
	
	public void removeAssignment(String assignmentName) {
		
	}
	
	public String[] getClasses() {
		return (String[]) classes.selectData().toArray();
	}
	
	public String[][] getAllAssignments() {
		return (String[][]) scores.selectData().toArray();
	}
	
	public String[][] getAssignmentsSorted(boolean up, String... colsSortBy) {
		return (String[][]) scores.sortBy(up, colsSortBy).toArray();
	}
}