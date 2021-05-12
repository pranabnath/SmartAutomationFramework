package com.auto.pkg.testcases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	private Connection connection;
	public Connection getConnection() {
		return connection;
	}

	public DBUtil(String host,String port,String sid,String userName, String password) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String oracleDriver = "jdbc:oracle:thin:@";
		try {
			String connectionString = oracleDriver+host+":"+port+":" + sid ;
			connection = DriverManager.getConnection(connectionString,userName,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
