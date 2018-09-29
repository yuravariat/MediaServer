package haifa.university.mysql;
import com.mysql.jdbc.Connection;

import haifa.university.mediaserver.common.AppLogger;
import haifa.university.mediaserver.common.AppSettingsManager;

import java.sql.*;
/**
 * @desc A singleton database access class for MySQL
 * @author Yuri
 */
public final class MySQLConnector implements AutoCloseable {
    public Connection conn;
    public MySQLConnector() {
    	Conect();
    }
    @Override public void close() throws Exception {
    	if(this.conn!=null){
	    	this.conn.close();
	    	this.conn = null;
    	}
	}
    private void Conect(){
    	try {
        	MySQLConnection conn = AppSettingsManager.getInstance().getSettings().dmozConnection;
        	
            String url= "jdbc:mysql://" + conn.getHost() + ":"+conn.getPort()+"/";
            Class.forName(conn.getDriver()).newInstance();
            this.conn = (Connection)DriverManager.getConnection(
            		url + conn.getDbName() + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10&useUnicode=true&characterEncoding=UTF-8",
            		conn.getUserName(),conn.getPassword());
        }
        catch (Exception sqle) {
            sqle.printStackTrace();
            
            AppLogger.getInstance().writeLog("MySQLConnector() error", " Exception=" + sqle.getMessage() + 
					"stack trace = " + sqle.getStackTrace(),
                    AppLogger.LogLevel.ERROR);
        }
    }
    /**
     *
     * @param query String The query to be executed
     * @return a ResultSet object containing the results or null if not available
     * @throws SQLException
     */
    public ResultSet query(String query){
    	Statement statement;
    	ResultSet res = null;
		try {
			statement = conn.createStatement();
			res = statement.executeQuery(query);
		} catch (SQLException e) {
			AppLogger.getInstance().writeLog("MySQLConnector ResultSet query error", " Exception=" + e.getMessage() + 
					"stack trace = " + e.getStackTrace(),
                    AppLogger.LogLevel.ERROR);
			e.printStackTrace();
		}
        return res;
    }
    /**
     * @desc Method to insert data to a table
     * @param insertQuery String The Insert query
     * @return boolean
     * @throws SQLException
     */
    public int insert(String insertQuery) {
    	Statement statement;
    	int result = 0;
		try {
			statement = conn.createStatement();
			result = statement.executeUpdate(insertQuery);
		} catch (SQLException e) {
			AppLogger.getInstance().writeLog("MySQLConnector int insert error", " Exception=" + e.getMessage() + 
					"stack trace = " + e.getStackTrace(),
                    AppLogger.LogLevel.ERROR);
			e.printStackTrace();
		}
        return result;
    }
    public int[] runBatch(String sql) {
    	Statement statement;
    	int[] result = null;
		try {
			statement = conn.prepareStatement(sql);
			result = statement.executeBatch();
		} catch (SQLException e) {
			AppLogger.getInstance().writeLog("MySQLConnector int[] runBatch error", " Exception=" + e.getMessage() + 
					"stack trace = " + e.getStackTrace(),
                    AppLogger.LogLevel.ERROR);
			e.printStackTrace();
		}
        return result;
    }
}
