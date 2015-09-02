package utpsolver;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet resultSet = null;
	private boolean initialized = false;
	private PreparedStatement pstm = null;
	/*
	private String dbServer = "jdbc:mysql://localhost:3306/";//"jdbc:mysql://localhost/"
	private String dbname = "dissertation";
	private String user = "root";
	private String dbPassword = "mysql";
	*/
	//For departmental server
	private String dbServer = "jdbc:mysql://stusql.dcs.shef.ac.uk/";
	private String dbname = "team158";
	private String user = "team158";
	private String dbPassword = "9a5b309d";
	

	public DBConnection() {
		initConnection();
	}
	
	// create prepared statement 
	public PreparedStatement createPreparedStatement(String query)
			throws SQLException {
		if (!initialized) {
			initConnection();
		}
		pstm = con.prepareStatement(query);
		return pstm;
	}

	// execute query with prepared statement
	public ResultSet executeQuery(PreparedStatement query) {
		
		try {
			if (initialized) {
				resultSet = query.executeQuery();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	// execute query with string
	public ResultSet executeQuery(String query) {
		try {
			if (initConnection()) {			
				resultSet = stmt.executeQuery(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	// execute update query with prepared statement
	public int executeUpdate(PreparedStatement query) {
		int result = 0;
		try {
			if (initConnection()) {
				result = query.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// execute update query with string
	public int updateQuery(String query) {
		int result = 0;
		try {
			if (initConnection()) {
				result = stmt.executeUpdate(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// close all connection
	public void closeConnection() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (pstm != null) {
				pstm.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			initialized = false;
		}
	}

	private boolean initConnection() {
		if (!initialized) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbServer + dbname, user,
					dbPassword);
				stmt = con.createStatement();
				initialized = true;
			} catch (Exception e) {
				e.printStackTrace();
				//System.err.println("failed to start connection.:"+ e.getMessage());
				return false;
			}
		}
		return initialized;
	}
	
}

