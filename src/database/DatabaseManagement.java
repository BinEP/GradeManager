package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseManagement {

	public static final String CLASSES_DB = "classes.db";
	public static final String SCORES_DB = "scores.db";

	private Connection database;
	private Statement newTableCommand;
	private Statement insertDataCommand;
	private Statement selectData;
	private Statement sortData;
	private ResultSet resultData;
	private String tableName;
	private String db;
	private int uniqueID = 1;
	private String[][] fields;
	private boolean addDate = true;

	public DatabaseManagement(String database, String[]... fields) {
		this.db = database;
		fields = newFields(fields);
		this.fields = fields;
		tableName = (database.equals(CLASSES_DB)) ? "CLASSES" : "SCORES";
		connect();
	}
	
	public DatabaseManagement(String database, boolean addDate, String[]... fields) {
		this.db = database;
		setShowDate(addDate);
		if (addDate) {
			fields = newFields(fields);
		}
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
	
	public String[][] newFields(String[][] fields) {
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
		newTableCommand = database.createStatement();

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

		insertDataCommand = database.createStatement();
		String sql = "INSERT INTO " + tableName + " (ID";

		for (String[] s : fields)
			sql += "," + s[0];
		sql += ")         ";
		sql += "VALUES (" + uniqueID;

		for (int i = 0; i < info.length; i++) {
			String ifQuote = (fields[i][1].equals("TEXT")) ? "'" : "";
			sql += " , " + ifQuote + 
				info[i] + ifQuote;
		}
		if (addDate) {
			SimpleDateFormat date = new SimpleDateFormat("MM:dd:yyy");
			sql += " , '" + date.format(new Date()) +"'";
		}

		sql += ");";
		uniqueID++;
		insertDataCommand.executeUpdate(sql);

		insertDataCommand.close();
		database.commit();
		closeConnections();
		System.out.println("Records created successfully");
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
		selectData = database.createStatement();
		resultData = selectData.executeQuery("SELECT * FROM " + tableName + ";");

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

	public void sortData() {
		try {
			sortDataCommand();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void sortDataCommand() throws SQLException {

		connect();
		sortData = database.createStatement();
		sortData.executeQuery("SELECT * FROM " + tableName
				+ " ORDER BY SCORE, " + fields[0] + " ASC");

		sortData.close();
		database.commit();
		closeConnections();
	}
	
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
	
	public void setShowDate(boolean addDate) {
		this.addDate = addDate;
	}

	public void closeConnections() {
	}
}
