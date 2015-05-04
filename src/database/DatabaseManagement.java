package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DatabaseManagement {

	public static final String CLASSES_DB = "classes.db";
	public static final String SCORES_DB = "scores.db";

	private Connection database;
	private String tableName;
	private String db;
	private int uniqueID = 1;
	private String[][] fields;

	public DatabaseManagement(String database, String[]... fields) {
		this.db = database;
		fields = newFields(fields);
		this.fields = fields;
		tableName = (database.equals(CLASSES_DB)) ? "CLASSES" : "SCORES";
		connect();
	}

	public void connect() {
		try {
			if (database == null || database.isClosed()) {
				connectCommand();
				 newTable();
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void connectCommand() throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");
		database = DriverManager.getConnection("jdbc:sqlite:grades/" + db);
		database.setAutoCommit(false);
		System.out.println("Opened database successfully");
	}
	
	private String[][] newFields(String[][] fields) {
		String[][] newFields = new String[fields.length + 1][2];
		for (int i = 0; i < fields.length; i++) {
			newFields[i] = fields[i];
		}
		String[] f = {"DATEBACK", "TEXT"};
		newFields[newFields.length - 1] = f;
		return newFields;
	}

	public void newTable() {
		try {
			newTableCommand();
		} catch (SQLException e) {
			System.out.println("Table created already");
//			e.printStackTrace();
		}
	}

	private void newTableCommand() throws SQLException {

		connect();
		String sql = "SELECT * FROM sqlite_master WHERE type='table' AND name='"
				+ tableName + "';";
		Statement newTableCommand = database.createStatement();

			sql = "CREATE TABLE " + tableName + " "
					+ "(ID INT PRIMARY KEY      NOT NULL";
					
					for (String[] s : fields)
						sql += " , " + s[0] + "        " + s[1] + "      NOT NULL";
					sql += ")";
		newTableCommand.executeUpdate(sql);
		newTableCommand.close();
		database.commit();
		closeConnections();
	}

	public void insertInfo(String... info) {
		try {
			insertInfoCommand(info);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertInfoCommand(String[] info) throws SQLException {

		uniqueID = selectData().size() + 1;
		connect();

		Statement insertDataCommand = database.createStatement();
		String sql = "INSERT INTO " + tableName + " (ID";

		for (String[] s : Arrays.copyOfRange(fields, 1, fields.length))
			sql += "," + s[0];
		sql += ")  VALUES (" + uniqueID;

		for (int i = 0; i < info.length; i++) {
			String ifQuote = ifTextField(i) ? "'" : "";
			sql += " , " + ifQuote + info[i] + ifQuote;
		}
		
		SimpleDateFormat date = new SimpleDateFormat("MM:dd:yyy");
		sql += " , '" + date.format(new Date()) +"');";
		
		uniqueID++;
		insertDataCommand.executeUpdate(sql);
		insertDataCommand.close();
		
		database.commit();
		closeConnections();
		System.out.println("Records created successfully");
	}

	private boolean ifTextField(int i) {
		return fields[i + 1][1].equals("TEXT");
	}
	
	public void updateInfo(String id, String column, String newValue) {
		try {
			updateInfoCommand(id, column, newValue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateInfoCommand(String id, String column, String newValue) throws SQLException {
		String sql = "UPDATE SCORES SET " + column + " = '" + newValue + "' WHERE ID = " + id + ";";
		Statement updateInfoCommand = database.createStatement();
		updateInfoCommand.executeUpdate(sql);
		updateInfoCommand.close();
		database.commit();
		closeConnections();
		System.out.println("Value Updated successsfully");
	}
	
	public void deleteInfo(String id) {
		try {
			deleteInfoCommand(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteInfoCommand(String id) throws SQLException {
		String sql = "DELETE FROM SCORES WHERE ID = " + id + ";";
		Statement updateInfoCommand = database.createStatement();
		updateInfoCommand.executeUpdate(sql);
		updateInfoCommand.close();
		database.commit();
		closeConnections();
		System.out.println("Value Updated successsfully");
	}

	public ArrayList<String[]> selectData() {
		try {
			return selectDataCommand();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ArrayList<String[]> selectDataCommand() throws SQLException {

		connect();
		ArrayList<String[]> results = new ArrayList<String[]>();
		Statement selectData = database.createStatement();
		ResultSet resultData = selectData.executeQuery("SELECT * FROM " + tableName + ";");

		while (resultData.next()) {
			String[] scores = new String[fields.length];
			for (int i = 0; i < scores.length; i++) {
				scores[i] = resultData.getString(fields[i][0]);
			}
			results.add(scores);
		}

		resultData.close();
		selectData.close();
		closeConnections();
		return results;
	}

//	public void sortData() {
//		try {
//			sortDataCommand();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void sortDataCommand() throws SQLException {
//
//		connect();
//		Statement sortData = database.createStatement();
//		sortData.executeQuery("SELECT * FROM " + tableName
//				+ " ORDER BY SCORE, " + fields[0] + " ASC");
//
//		sortData.close();
//		database.commit();
//		closeConnections();
//	}
	
	public void clearTable() {
		try {
			clearTableCommand();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void clearTableCommand() throws SQLException {
		Statement clearTableCommand = database.createStatement();
		
		String sql = "DELETE FROM " + tableName;
		clearTableCommand.executeUpdate(sql);
		clearTableCommand.close();
		database.commit();
	}
	
	public ArrayList<String[]> getMatchingRows(String col, String toMatch) {
		try {
			return getMatchingRowsCommand(col, toMatch);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<String[]> getMatchingRowsCommand(String col, String toMatch) throws SQLException {
		Statement matchRowsCommand = database.createStatement();
		
		String sql = "SELECT * FROM " + tableName + " WHERE " + col + " =\"" + toMatch + "\"";
		
		ResultSet matchingRows = matchRowsCommand.executeQuery(sql);
		
		ArrayList<String[]> results = new ArrayList<String[]>();
		while (matchingRows.next()) {
			String[] scores = new String[fields.length];
			for (int i = 0; i < scores.length; i++) {
				scores[i] = matchingRows.getString(fields[i][0]);
			}
			results.add(scores);
		}
		
		matchingRows.close();
		matchRowsCommand.close();
		database.commit();
		return results;
	}
	
	public String[] getColumnNames() {
		String[] columnNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			columnNames[i] = fields[i][0];
		}
		return columnNames;
	}
	
	public ArrayList<String> getColumnData(String col) {
		try {
			return getColumnDataCommand(col);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<String> getColumnDataCommand(String col) throws SQLException {
		Statement matchRowsCommand = database.createStatement();
		
		String sql = "SELECT " + col + " FROM " + tableName;
		ResultSet matchingRows = matchRowsCommand.executeQuery(sql);
		
		ArrayList<String> results = new ArrayList<String>();
		while (matchingRows.next()) {
			results.add(matchingRows.getString(col));
		}
		
		matchingRows.close();
		matchRowsCommand.close();
		database.commit();
		return results;
	}
	
	public ArrayList<String> getColumnData(String col, String condition) {
		try {
			return getColumnDataCommand(col, condition);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<String> getColumnDataCommand(String col, String condition) throws SQLException {
		Statement matchRowsCommand = database.createStatement();
		
		String sql = "SELECT " + col + " FROM " + tableName + " WHERE " + condition;
		ResultSet matchingRows = matchRowsCommand.executeQuery(sql);
		
		ArrayList<String> results = new ArrayList<String>();
		while (matchingRows.next()) {
			results.add(matchingRows.getString(col));
		}
		
		matchingRows.close();
		matchRowsCommand.close();
		database.commit();
		return results;
	}
	
	public ArrayList<String[]> sortBy(boolean up, String... col) {
		try {
			return sortByCommand(up, col);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ArrayList<String[]> sortByCommand(boolean up, String[] col) throws SQLException {

		connect();
		Statement customSort = database.createStatement();
		String sql = "SELECT * FROM " + tableName + " ORDER BY ";
		for (String s : col) 
			sql += s + ", ";
		sql = sql.substring(0, sql.length() - 3);
		sql += (up) ? "ASC" : "DESC";
		
		ResultSet sortedRows = customSort.executeQuery(sql);
		
		ArrayList<String[]> results = new ArrayList<String[]>();
		while (sortedRows.next()) {
			String[] scores = new String[fields.length];
			for (int i = 0; i < scores.length; i++) {
				scores[i] = sortedRows.getString(fields[i][0]);
			}
			results.add(scores);
		}
		
		sortedRows.close();
		customSort.close();
		database.commit();
		
		closeConnections();
		return results;
	}
	
	public int newID() {
		uniqueID = selectData().size() + 1;
		return uniqueID;
	}

	public void closeConnections() {
	}
}
