package com.general.task.workflow.web.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.*;

import javax.sql.RowSet;

import sun.jdbc.rowset.CachedRowSet;

import com.databasetool.transsupport.DBTools;
import com.general.reflection.ReflectionTools;
import com.general.task.workflow.*;


public class WorkflowDAOImp implements WorkflowDAO{

	@Override
	public void create(String wftid,String params,String userid) throws Exception {
		//DatabaseTools util = new DatabaseTools("localhost","1433","eamis","sa","123456");
		DBTools util = new DBTools("java:/zhyeamDS");
		//CachedRowSet rs = util.getResultBySelect("SELECT * FROM wf_template where wftid = '"+wftid+"'");

		//模板表中的各个字段
		String actorfun = util.getColumnValue("SELECT actorfun FROM wf_template where wftid = '"+wftid+"'");
		//需要插入的主键
		String wfiid = new Long(new Date().getTime()).toString();
		String creator = userid;
		
		
		
		//通过模板流程创建实例流程
		StringBuffer insSQL = new StringBuffer("INSERT INTO WF_INSTANCE (wfiid,wftid,creator,status,actorfun,params,inputtime,lastmodify) VALUES('");
		insSQL.append(wfiid).append("','");
		insSQL.append(wftid).append("','");
		insSQL.append(creator).append("','");
		insSQL.append("0").append("','");
		insSQL.append(actorfun).append("','");
		insSQL.append(params).append("',");
		insSQL.append("getDate()").append(",'");
		insSQL.append(new Long(new Date().getTime()).toString()).append("')");
		util.addSql(insSQL.toString());
		
		//通过模板节点创建实例节点
		CachedRowSet rs = util.getResultBySelect("SELECT * FROM wf_nodetemplate where wftid = '"+wftid+"'");
		
		while(rs!=null && rs.next()){
			String niid = new Long(new Date().getTime()).toString();
			String actor = this.getNodeActors(wftid, rs.getString("actor"));
			insSQL = new StringBuffer("INSERT INTO WF_NODEINSTANCE (niid,nid,wfiid,actor,isactive,isskippable,nodetype,status,inputtime,modifytime) VALUES('");
			insSQL.append(niid).append("','");
			insSQL.append(rs.getString("nid")).append("','");
			insSQL.append(wfiid).append("','");
			insSQL.append(actor).append("','");
			insSQL.append("0").append("','"); //isactive
			insSQL.append("0").append("','"); //isskippable
			insSQL.append("0").append("','"); //nodetype
			insSQL.append("0").append("',"); //status
			insSQL.append("getDate()").append(","); 
			insSQL.append("getDate()").append(")"); 
			
			System.out.println("执行sql:"+insSQL.toString());
			util.addSql(insSQL.toString());
			
			Thread.sleep(10);
			
		}

		 
		
		try{
			util.executeSql();
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			util.close();
		}
		
	}
	
	

	
	 public NodeTemplate loadNodeTemplate(String pkvalue) throws Exception{ 
		 //TODO implment Dao code here.
		 DBTools transUtil = new DBTools("java:/zhyeamDS");
		NodeTemplate item =  new NodeTemplate();
		try{
			String nid = transUtil.getColumnValue("SELECT nid FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setNid(nid);

			String wftid = transUtil.getColumnValue("SELECT wftid FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setWftid(wftid);

			String nodename = transUtil.getColumnValue("SELECT nodename FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setNodename(nodename);
			
			String nodetype = transUtil.getColumnValue("SELECT nodetype FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setNodetype(nodetype);


			String nextnodelist = transUtil.getColumnValue("SELECT nextnodelist FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setNextnodelist(nextnodelist);

			String actor = transUtil.getColumnValue("SELECT actor FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setActor(actor);
		
			String auditurl = transUtil.getColumnValue("SELECT auditurl FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setAuditurl(auditurl);	

			String inputtime = transUtil.getColumnValue("SELECT inputtime FROM WF_NODETEMPLATE WHERE nid= '" + pkvalue +"'");
			item.setInputtime(inputtime);
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}

		return item;
	 }
	 
	 /**
	  * 将日志对象写入到数据库
	  * @param wfLogList
	  * @param transUtil
	  * @throws Exception
	  */
	void writeLog(ArrayList<WfLog> wfLogList,DBTools transUtil) throws Exception{
		String wflid = new Long(new Date().getTime()).toString();
		if(wfLogList == null || wfLogList.size() == 0)
			return;
		Iterator it = wfLogList.iterator();
		while(it.hasNext()){
			WfLog log = (WfLog)it.next();
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO wf_log (wflid,niid,operation,operator,note,operatetime,ifdel) VALUES('");
		sql.append(log.getWflid()).append("','");
		sql.append(log.getNiid()).append("','");
		sql.append(log.getOperation()).append("','");
		sql.append(log.getOperator()).append("','");
		sql.append(log.getNote()).append("',");
		sql.append("getDate()").append(",'");
		sql.append("0").append("')");
		
		System.out.println("执行SQL:"+sql.toString());
		transUtil.addSql(sql.toString());
		}

	
	}

	@Override
	public void save(WorkflowEngine engine) throws Exception{
		if(engine==null)
			return;
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		
		String currentDBUpdateTime = transUtil.getColumnValue("SELECT lastmodify FROM wf_instance WHERE wfiid = '"+engine.getWfInstance().getWfiid()+"'");
		if(!currentDBUpdateTime.equals(engine.getWfInstance().getLastmodify())){
			throw new Exception("该流程在当前操作期间状态发生了改变，请重新进行流程操作!");
		}
		
		String currentTime = new Long(new Date().getTime()).toString();
		engine.getWfInstance().setLastmodify(currentTime);
		
		//更新流程实例表
		saveObj(engine.getWfInstance(),"WF_INSTANCE","wfiid",transUtil);
		
		ArrayList<NodeInstance> nodeInstanceList = engine.getWfInstance().getNodeInstanceList();
		Iterator it = nodeInstanceList.iterator();
		while(it.hasNext()){
			NodeInstance node = (NodeInstance)it.next();
			System.out.println("存储实例节点:"+node.getNiid());
			saveObj(node,"WF_NODEINSTANCE","niid",transUtil);
		}
		
		//写入工作流操作日志
		writeLog(engine.getWfLogList(),transUtil);

		
		try{
			transUtil.executeSql();
			engine.getWfLogList().clear(); //存储后清空日志
		}catch(Exception e){
			engine.getWfInstance().setLastmodify(currentDBUpdateTime);
			throw new Exception("在存储工作流实例(id:"+engine.getWfInstance().getWfiid()+")到数据库的过程中发生了问题:"+e.toString());
		}finally{
			transUtil.close();
		}
	}
	
	/**
	 * 生成需要存储对象的SQL，并暂时加入到数据库事务里
	 * @param obj
	 * @param tablename
	 * @param pkname
	 * @param util
	 * @throws Exception
	 */
	void saveObj(Object obj,String tablename, String pkname,DBTools util) throws Exception {
		// TODO Auto-generated method stub
		if(obj == null || tablename == null || util == null){
			System.out.println("对象为空或表明为空或主属性为空或者数据工具对象为空.");
			return;
		}
			
		HashMap<String,String> result = ReflectionTools.getMemberMap(obj);
		String pkvalue = result.get(pkname); //根据主属性名称取得主属性的值
		
		Iterator it = result.entrySet().iterator();
		while(it.hasNext()){
			   StringBuffer sbf = new StringBuffer();
			   Map.Entry entry = (Map.Entry) it.next();
			   Object key = entry.getKey();
			   Object val = entry.getValue(); 
			   sbf.append("UPDATE ").append(tablename).append(" SET ").append(key).append("='").append(val).append("' WHERE ");
			   sbf.append(pkname).append("=").append("'").append(pkvalue).append("'");
			   System.out.println("执行SQL:"+sbf.toString());
			   util.addSql(sbf.toString());
		}
		
	}

	@Override
	public WfTemplate loadTemplate(String pkvalue) throws Exception {
		// TODO Auto-generated method stub
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		WfTemplate item =  new WfTemplate();
		try{
		String wftid = transUtil.getColumnValue("SELECT wftid FROM WF_TEMPLATE WHERE wftid= '" + pkvalue +"'");
		item.setWftid(wftid);

		String name = transUtil.getColumnValue("SELECT name FROM WF_TEMPLATE WHERE wftid= '" + pkvalue +"'");
		item.setName(name);

		String description = transUtil.getColumnValue("SELECT description FROM WF_TEMPLATE WHERE wftid= '" + pkvalue +"'");
		item.setDescription(description);

		String actorfun = transUtil.getColumnValue("SELECT actorfun FROM WF_TEMPLATE WHERE wftid= '" + pkvalue +"'");
		item.setActorfun(actorfun);

		String inputtime = transUtil.getColumnValue("SELECT inputtime FROM WF_TEMPLATE WHERE wftid= '" + pkvalue +"'");
		item.setInputtime(inputtime);
		
		//初始化NodeTemplateList列表
		item.setNodeTemplateList(new ArrayList<NodeTemplate>());
		
		CachedRowSet rs = transUtil.getResultBySelect("SELECT * FROM WF_NODETEMPLATE WHERE wftid = '" + wftid +"'");
		while(rs!=null && rs.next()){
			String nid = (String)rs.getString("nid");
			NodeTemplate nodeTemplate = loadNodeTemplate(nid);
			item.getNodeTemplateList().add(nodeTemplate);
		}
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}

		return item;

	}
	
	public WfInstance loadInstance(String wftid,String params) throws Exception{
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		WfInstance temp = null;
		try{
			String wfiid = transUtil.getColumnValue("SELECT wfiid FROM WF_INSTANCE WHERE wftid = '"+wftid+"' AND params = '"+params+"'");
			temp = loadInstance(wfiid);
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}
		return temp;
	}

	@Override
	public WfInstance loadInstance(String pkvalue) throws Exception {
		// TODO Auto-generated method stub
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		WfInstance item =  new WfInstance();
		try{
		String wfiid = transUtil.getColumnValue("SELECT wfiid FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setWfiid(wfiid);

		String wftid = transUtil.getColumnValue("SELECT wftid FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setWftid(wftid);

		String creator = transUtil.getColumnValue("SELECT creator FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setCreator(creator);

		String actorfun = transUtil.getColumnValue("SELECT actorfun FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setActorfun(actorfun);

		String params = transUtil.getColumnValue("SELECT params FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setParams(params);

		String status = transUtil.getColumnValue("SELECT status FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setStatus(status);

		String inputtime = transUtil.getColumnValue("SELECT inputtime FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setInputtime(inputtime);

		String lastmodify = transUtil.getColumnValue("SELECT lastmodify FROM WF_INSTANCE WHERE wfIid= '" + pkvalue +"'");
		item.setLastmodify(lastmodify);
		
		//初始化NodeInstanceList列表
		item.setNodeInstanceList(new ArrayList<NodeInstance>());
		
		CachedRowSet rs = transUtil.getResultBySelect("SELECT * FROM WF_NODEINSTANCE WHERE wfiid = '" + wfiid +"'");
		while(rs!=null && rs.next()){
			String niid = (String)rs.getString("niid");
			NodeInstance nodeInstance = loadNodeInstance(niid);
			item.getNodeInstanceList().add(nodeInstance);
		}
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}

		return item;

	}

	@Override
	public NodeInstance loadNodeInstance(String pkvalue)
			throws Exception {
		// TODO Auto-generated method stub
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		NodeInstance item =  new NodeInstance();
		try{
		String niid = transUtil.getColumnValue("SELECT niid FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setNiid(niid);

		String nid = transUtil.getColumnValue("SELECT nid FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setNid(nid);

		String wfiid = transUtil.getColumnValue("SELECT wfiid FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setWfiid(wfiid);

		String actor = transUtil.getColumnValue("SELECT actor FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setActor(actor);

		String isactive = transUtil.getColumnValue("SELECT isactive FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setIsactive(isactive);

		String isskippable = transUtil.getColumnValue("SELECT isskippable FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setIsskippable(isskippable);
		
		String prvnode = transUtil.getColumnValue("SELECT prvnode FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setPrvnode(prvnode);
		
		String nextnode = transUtil.getColumnValue("SELECT nextnode FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setNextnode(nextnode);
		
		String nodetype = transUtil.getColumnValue("SELECT nodetype FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setNodetype(nodetype);

		String status = transUtil.getColumnValue("SELECT status FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setStatus(status);

		String inputtime = transUtil.getColumnValue("SELECT inputtime FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setInputtime(inputtime);

		String modifytime = transUtil.getColumnValue("SELECT modifytime FROM WF_NODEINSTANCE WHERE niid= '" + pkvalue +"'");
		item.setModifytime(modifytime);
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}

		return item;		
	}
	

	 public WfLog loadWfLog(String pkvalue) throws Exception{ 
		 //TODO implment Dao code here.
		DBTools transUtil = new DBTools("java:/zhyeamDS");
		WfLog item =  new WfLog();
		try{
		String wflid = transUtil.getColumnValue("SELECT wflid FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setWflid(wflid);

		String niid = transUtil.getColumnValue("SELECT niid FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setNiid(niid);

		String operation = transUtil.getColumnValue("SELECT operation FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setOperation(operation);

		String note = transUtil.getColumnValue("SELECT note FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setNote(note);

		String operator = transUtil.getColumnValue("SELECT operator FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setOperator(operator);

		String operatetime = transUtil.getColumnValue("SELECT operatetime FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setOperatetime(operatetime);

		String ifdel = transUtil.getColumnValue("SELECT ifdel FROM WF_LOG WHERE wflid= '" + pkvalue +"'");
		item.setIfdel(ifdel);
		}catch(Exception e){
			throw new Exception(e.toString());
		}finally{
			transUtil.close();
		}

		return item;
	 }
	 

	@Override
	public void delete() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	 @Override	 
	 public String getNodeActors(String wftid,String actor) throws Exception {
		 DBTools transUtil = new DBTools("java:/zhyeamDS");
		 try{
			 	String actorFun = transUtil.getColumnValue("SELECT actorFun FROM wf_template where wftid = '"+wftid+"'");
			 	Class clazz = Class.forName(actorFun);
			 	Method method = clazz.getMethod("getActorList", String.class);
			 	String actorList = (String)method.invoke(null, actor);
			 	return actorList;
		 }catch(Exception e){
			 throw new Exception(e.toString());
		 }finally{
			 transUtil.close();
		 }
	}

}
