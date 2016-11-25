package com.zzw.zwm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ���ݿ⣬ʹ��JDBC����MySQL�����ݿ�Ϊcool_dictionary��
 * ֻ�ṩ�˻�����query��execute������
 * @author zzw
 * @version 1.0
 */
public class MyDataBase {
	private static final String MYSQL_DRIVER="com.mysql.jdbc.Driver";
	private static final String MYSQL_URL="jdbc:mysql://localhost:3306"+
			"/cool_dictionary?user=root&password=262623"+
			"&useUnicode=true&characterEncoding=UTF8&useSSL=true";
	private Statement mStatement;
	private static MyDataBase mInstance;
	
	public static MyDataBase getInstance() throws 
	ClassNotFoundException, SQLException {
		if(mInstance==null){
			synchronized(MyDataBase.class){
				if(mInstance==null)
					mInstance=new MyDataBase();
			}
		}
		return mInstance;
	}
	
	private MyDataBase() throws 
	ClassNotFoundException, SQLException {
		// ����JDBC����
		Class.forName(MYSQL_DRIVER);
		// ���ӵ����ݿ�
		Connection connection=DriverManager.getConnection(MYSQL_URL);
		// ��ȡStatementʵ��
		mStatement=connection.createStatement();
	}
	
	public ResultSet query(String sql) throws SQLException{
		return mStatement.executeQuery(sql);
	}
	
	public boolean execute(String sql) throws SQLException{
		return mStatement.execute(sql);
	}
	
	public int executeReturnCount(String sql) throws SQLException{
		if(!mStatement.execute(sql))
			return mStatement.getUpdateCount();
		return 0;
	}
	
	public ResultSet executeReturnResult(String sql) throws SQLException{
		if(mStatement.execute(sql))
			return mStatement.getResultSet();
		return null;
	}
}
