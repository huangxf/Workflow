package com.huangxf.databasetools;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * 
 * 数据库工具类，用于提供在编程中可能用到的各种数据库方法。
 * @author huangxf xiaofei_huang@hotmail.com
 * 
 *
 */

public class DatabaseTools{
	
	private Connection connection;
	private Statement statement;
	private ArrayList<String> sqlList;
	private DataSource dataSource;
	private Context context;
	private String jndi;
	
	
	
	/**
	 * 数据库工具类的构造函数，用于对数据库工具类进行初始化工作。需要提供相应参数。
	 * 
	 * @param server 数据库服务器地址
	 * @param port 数据库服务器端口 
	 * @param database 数据库名称
	 * @param username 登陆用户名
	 * @param password 登陆密码
	 * @throws Exception 返回异常
	 */
	
	public DatabaseTools(String server,String port,String database,String username,String password) throws Exception{
		String dbURL = "";
        if(port.equals("")) port = "1433";
		if(database.trim().equals("")){
            dbURL= "jdbc:sqlserver://"+server+":"+port;
        }else{
              dbURL="jdbc:sqlserver://"+server+":"+port+" ;databasename="+database;
        }
		
		System.out.println("数据库连接字:"+dbURL);
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(dbURL,username,password);
        System.out.println("建立连接成功!"); 
        
        statement = connection.createStatement();
        sqlList = new ArrayList<String>();
	}
	
	public DatabaseTools(String jndi){
		try{
			this.jndi = jndi;
			this.context = new InitialContext();
			this.dataSource = (DataSource)context.lookup(jndi);
			this.connection = dataSource.getConnection();
			this.statement = connection.createStatement();
			System.out.println("由JNDI:"+this.jndi+"创建连接成功");
		}catch(Exception e){
			System.out.println("由JNDI:"+this.jndi+"创建连接失败:"+e.toString());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将需要执行的sql语句序列存入队列(主要是删除，修改，插入操作)
	 * 
	 * @param sql 需要执行的sql语句
	 */
	public void addSql(String sql){
		sqlList.add(sql);
	}
	
	
	/**
	 * 一次性执行sql队列中的所有sql语句
	 * 
	 * @throws SQLException
	 */
	public void excuteSql() throws SQLException{
		try{
		Iterator it = sqlList.iterator();
		this.connection.setAutoCommit(false);
		while(it.hasNext()){
			String sql = (String)it.next();
			System.out.println("执行sql语句:"+sql);
			statement.execute(sql);
		}
		this.connection.commit();
		this.connection.setAutoCommit(true);
		sqlList.clear();
		}catch(Exception e){
			this.connection.rollback();
			throw new SQLException(e.toString());
		}
	}
	
	/**
	 * 根据sql语句返回某一列的值(String)
	 * @param sql 需要执行的sql语句
	 * @return 返回查询出来的列值
	 * @throws SQLException
	 */
	public String getColumnValue(String sql) throws SQLException{
		if(sql== null || sql.equals("")) return null;
		Statement queryStatement = connection.createStatement();
		ResultSet rs = queryStatement.executeQuery(sql);
		
		if(rs == null) {
			System.out.print("通过执行sql语句["+sql+"]没有找到结果");
			return null;
		}
		
		//rs.beforeFirst();
		rs.next();
		String returnValue = rs.getString(1); 
		rs.close();
		queryStatement.close();
		return returnValue;
	}
	
	/**
	 * 根据sql语句查询并返回相应的数据集
	 * 
	 * @param sql 需要查询的sql语句
	 * @return 返回符合sql查询条件的数据集
	 * @throws SQLException
	 */
	public ResultSet getResultBySelect(String sql) throws SQLException{
		if(sql== null || sql.equals("")) return null;
		ResultSet rs = statement.executeQuery(sql);
		
		if(rs == null) {
			System.out.print("通过执行sql语句["+sql+"]没有找到结果");
			return null;
		}
		
		return rs;
	}
	
	/**
	 * 关闭当前使用的数据库连接线程
	 * @throws SQLException
	 */
	public void close() throws SQLException{
		if(connection != null)
			connection.close();
	}
	
	/**
	 * 根据sql语句，返回当前sql语句查询出的数据集的列名
	 * @param sql 查询的sql语句
	 * @return 返回一个String类型的ArrayList，包含了满足sql语句查询结果的数据集列名的集合。
	 * @throws SQLException
	 */
	public ArrayList<String> getMetaData(String sql) throws SQLException {
		if(sql == null || sql.equals(""))
			return null;
		
		ArrayList<String> metaData = new ArrayList<String>();
		
		ResultSet rs = getResultBySelect(sql);
		
		if(rs==null)
			return null;
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		int columnCount = rsmd.getColumnCount();
		for(int i=1;i<=columnCount;i++){
			metaData.add(rsmd.getColumnName(i));
		}
		
		
		return metaData;
	}
	


		

	


}
