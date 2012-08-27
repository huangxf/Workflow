package com.databasetool.transsupport;

import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import sun.jdbc.rowset.CachedRowSet;

public class DBTools {
	 private String JNDI = "";

	  private DataSource ds = null;

	  private Context ctx = null;

	  private Connection conn = null;

	  private Statement stmt = null;

	  private Vector sqlVector = new Vector(5);

	  private static Logger log = Logger.getLogger(DBTools.class);

	  public DBTools()
	  {
	  }

	  public DBTools(String jndi)
	  {
	    this.JNDI = jndi;
	  }

	  public CachedRowSet getResultBySelect(String strSql)
	    throws DBException
	  {
	    CachedRowSet crs = null;
	    ResultSet rs = null;
	    try {
	      this.ctx = new InitialContext();
	      this.ds = ((DataSource)this.ctx.lookup(this.JNDI));

	      crs = new CachedRowSet();

	      this.conn = this.ds.getConnection();

	      this.stmt = this.conn.createStatement();

	      rs = this.stmt.executeQuery(strSql);
	      crs.populate(rs);
	    }
	    catch (SQLException e) {
	      log.info("getResultBySelect : " + e.getMessage());
	      throw new DBException(e.getMessage());
	    }
	    catch (NamingException fe) {
	      log.info("naming ex : " + fe.getMessage());
	      throw new DBException(fe.getMessage());
	    }
	    finally {
	      try {
	        rs.close(); } catch (Exception localException) {
	      }
	      try {
	        this.stmt.close(); } catch (Exception localException1) {
	      }
	      try {
	        this.conn.close();
	      } catch (Exception localException2) {
	      }
	    }
	    return crs;
	  }




	  public int getSqlCount(String strSql) throws DBException
	  {
	    int recnum = 0;

	    CachedRowSet crs = null;
	    StringBuffer tempSql = new StringBuffer("");
	    try {
	      tempSql.append("select count(1) from ");
	      tempSql.append(strSql.substring(strSql.indexOf("from") + 4));
	      crs = getResultBySelect(tempSql.toString());
	      if (crs.next()) {
	        recnum = crs.getInt(1);
	      }
	      else
	        recnum = 0;
	    }
	    catch (Exception e)
	    {
	      throw new DBException(e.getMessage().replace('\n', ' '));
	    }
	    return recnum;
	  }

	  public void addSql(String strSql)
	  {
	    this.sqlVector.add(strSql);
	  }

	  public boolean executeSql() throws DBException {
	    int[] res = new int[1];
	    try
	    {
	      this.ctx = new InitialContext();
	      this.ds = ((DataSource)this.ctx.lookup(this.JNDI));
	      this.conn = this.ds.getConnection();
	      this.stmt = this.conn.createStatement();
	      this.conn.setAutoCommit(false);
	      for (int i = 0; i < this.sqlVector.size(); i++) {
	        this.stmt.addBatch(this.sqlVector.get(i).toString());
	      }
	      
	      res = this.stmt.executeBatch();
	      this.conn.setAutoCommit(true);
	    }
	    catch (Exception fe)
	    {
	      log.error("executeSql出现错误：" + fe.getMessage());
	      throw new DBException(fe.getMessage().replace('\n', ' '));
	    }
	    finally {
	      if (this.sqlVector != null)
	        this.sqlVector.clear();
	      try
	      {
	        this.stmt.close();
	      } catch (Exception localException1) {
	      }
	      try {
	        this.conn.close();
	      } catch (Exception localException2) {
	      }
	    }
	    return true;
	  }

	  public String getColumnValue(String strSql)
	    throws DBException
	  {
	    String tempStr = "";

	    CachedRowSet crs = getResultBySelect(strSql);
	    try
	    {
	      if (crs.next())
	        tempStr = crs.getString(1) == null ? "" : crs.getString(1);
	    }
	    catch (SQLException fe)
	    {
	      throw new DBException(fe.getMessage().replace('\n', ' '));
	    }
	    return tempStr;
	  }

	  public Connection getConnection()
	  {
	    Connection conn = null;
	    try {
	      InitialContext ctx = new InitialContext();
	      DataSource ds = (DataSource)ctx.lookup(this.JNDI);
	      conn = ds.getConnection();
	    }
	    catch (Exception fe) {
	      log.error("getConnection出现错误：" + fe.getMessage());
	      try {
	        conn.close();
	      } catch (Exception localException1) {
	      }
	    }
	    return conn;
	  }

	  public Connection getConnection(String jname)
	  {
	    try
	    {
	      this.ctx = new InitialContext();
	      this.ds = ((DataSource)this.ctx.lookup(jname));
	      this.conn = this.ds.getConnection();
	    }
	    catch (Exception fe) {
	      log.error("getConnection出现错误：" + fe.getMessage());
	      try {
	        this.conn.close();
	      } catch (Exception localException1) {
	      }
	    }
	    return this.conn;
	  }

	  public Connection getConnection(String ip, String sid, String oraport, String username, String password) {
	    try {
	      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	      this.conn = DriverManager.getConnection("jdbc:oracle:thin:@" + ip + ":" + oraport + ":" + "sid", username, password);
	    }
	    catch (Exception fe) {
	      log.error("getConnection出现错误：" + fe.getMessage());
	      try {
	        this.conn.close();
	      } catch (Exception localException1) {
	      }
	    }
	    return this.conn;
	  }

	  public String getSequence(String sequenceName)
	    throws DBException
	  {
	    String sequenceID = null;
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("Select " + sequenceName + ".nextval from dual");

	    String seqId = getColumnValue(buffer.toString());
	    return seqId;
	  }







	  public String doFunction(String functionName, String parameterss, String sign) throws DBException {
	    String para = "";
	    DBTools transUtil = new DBTools();

	    String returnValue = "";

	    Connection conn = null;
	    ResultSet rs = null;
	    CallableStatement call = null;

	    StringBuffer strCall = new StringBuffer("{ ? = call ");
	    strCall.append(functionName).append("(");

	    String parameters = parameterss;
	    while (parameters.indexOf(sign) >= 0)
	    {
	      para = parameters.substring(0, parameters.indexOf(sign));
	      parameters = parameters.substring(parameters.indexOf(sign) + 1);
	      strCall.append("?,");
	    }
	    strCall = new StringBuffer(strCall.substring(0, strCall.lastIndexOf(",")));
	    strCall.append(")}");

	    log.info(strCall.toString());
	    try {
	      conn = transUtil.getConnection();

	      call = conn.prepareCall(strCall.toString());

	      call.registerOutParameter(1, 12);
	      parameters = parameterss;
	      int i = 1;
	      while (parameters.indexOf(sign) >= 0)
	      {
	        para = parameters.substring(0, parameters.indexOf(sign));
	        parameters = parameters.substring(parameters.indexOf(sign) + 1);
	        i++;
	        log.info("parameters=" + parameters + ";i=" + i + ";para=" + para);
	        call.setString(i, para);
	      }
	      rs = call.executeQuery();
	      returnValue = call.getString(1);
	    }
	    catch (Exception fe) {
	      throw new DBException(fe.getMessage());
	    }
	    finally {
	      try {
	        if (conn != null)
	          conn.close();
	      }
	      catch (Exception fe) {
	        throw new DBException(fe.getMessage());
	      }
	    }
	    return returnValue;
	  }

	  public String doProc(String functionName, String parameterss, String sign) throws DBException
	  {
	    String para = "";
	    DBTools transUtil = new DBTools();

	    String returnValue = "";

	    boolean success = false;

	    Connection conn = null;

	    CallableStatement call = null;

	    StringBuffer strCall = new StringBuffer("{ ? = call ");
	    strCall.append(functionName).append("(");

	    String parameters = parameterss;
	    while (parameters.indexOf(sign) >= 0)
	    {
	      para = parameters.substring(0, parameters.indexOf(sign));
	      parameters = parameters.substring(parameters.indexOf(sign) + 1);
	      strCall.append("?,");
	    }
	    strCall = new StringBuffer(strCall.substring(0, strCall.lastIndexOf(",")));
	    strCall.append(")}");

	    log.info(strCall.toString());
	    try {
	      conn = transUtil.getConnection();

	      call = conn.prepareCall(strCall.toString());

	      call.registerOutParameter(1, 12);
	      parameters = parameterss;
	      int i = 1;
	      while (parameters.indexOf(sign) >= 0)
	      {
	        para = parameters.substring(0, parameters.indexOf(sign));
	        parameters = parameters.substring(parameters.indexOf(sign) + 1);
	        i++;
	        log.info("parameters=" + parameters + ";i=" + i + ";para=" + para);
	        call.setString(i, para);
	      }

	      success = call.execute();
	      returnValue = call.getString(1);
	    }
	    catch (Exception fe) {
	      throw new DBException(fe.getMessage());
	    }
	    finally {
	      try {
	        if (conn != null)
	          conn.close();
	      }
	      catch (Exception fe) {
	        throw new DBException(fe.getMessage());
	      }
	    }
	    return returnValue;
	  }


	  public String getSeq(String pseqname) throws DBException {
		  DBTools transUtil = new DBTools();

	    String returnValue = "";

	    boolean success = false;

	    Connection conn = null;

	    CallableStatement call = null;

	    StringBuffer strCall = new StringBuffer("{ ? = call ");
	    strCall.append(pseqname).append("(?)}");

	    log.info(strCall.toString());
	    try {
	      conn = transUtil.getConnection();

	      call = conn.prepareCall(strCall.toString());

	      call.registerOutParameter(1, 12);
	      call.registerOutParameter(2, 12);
	      call.setString(2, "a");

	      success = call.execute();
	      returnValue = call.getString(2);
	    }
	    catch (Exception fe) {
	      throw new DBException(fe.getMessage());
	    }
	    finally {
	      try {
	        if (conn != null)
	          conn.close();
	      }
	      catch (Exception fe) {
	        throw new DBException(fe.getMessage());
	      }
	    }
	    return returnValue;
	  }
	  
	  public void close(){}

}





