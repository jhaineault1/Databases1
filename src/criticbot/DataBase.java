package criticbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class DataBase {
	
	Connection conn;
	
	public DataBase(String username, String password)
	{
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3300/Criticbot";
			this.conn = DriverManager.getConnection(connectionUrl, username, password);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		return this.conn;
	}
	
	public int insert(String sql, HashMap<String, String> dataSet)
	{
		int result = 0;
		try {
			PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sql);
			
			int parameterIndex = 1;
			for (String key : dataSet.keySet()) {
				if (dataSet.get(key).matches("\\d+")) {
					stmt.setInt(parameterIndex, Integer.parseInt(dataSet.get(key)));
				} else if (dataSet.get(key).matches("\\d+\\.\\d+")) {
					stmt.setDouble(parameterIndex, Double.parseDouble(dataSet.get(key)));
				} else {
					stmt.setString(parameterIndex, dataSet.get(key));
				}
				parameterIndex++;
			}
			stmt.executeQuery();
		} catch (Exception e) {
			Utils.debug_log(sql);
			e.printStackTrace();
			System.exit(0);
		}
		
		return result;
	}	
}
