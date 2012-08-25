package com.general.task.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.general.reflection.ReflectionTools;

public class WorkflowEngine {
	
	/**
	 * 私有化构造函数，不允许通过new创建对象
	 */
	private WorkflowEngine(){} 
	
	/***
	 * 数据库访问接口对象
	 */
	private WorkflowDAO workflowDAO;
	
	/**
	 * 工作流模板对象
	 */
	private WfTemplate wfTemplate; 
	
	/**
	 * 工作流实例对象
	 */
	private WfInstance wfInstance;
	
	/**
	 * 工作流日志操作列表
	 */
	private ArrayList<WfLog> wfLogList;
	
	/**
	 * 工作流引擎的初始化函数，用于返回一个工作流引擎对象
	 * @return WorkflowEngine
	 */
	public static WorkflowEngine init(WorkflowDAO wfDAO){
		WorkflowEngine workflowEngine = new WorkflowEngine();
		workflowEngine.workflowDAO = wfDAO;
		workflowEngine.wfTemplate = null;
		workflowEngine.wfInstance = null;
		workflowEngine.wfLogList = new ArrayList<WfLog>();
		return workflowEngine;
	}
	
	
	/***
	 * 从数据库中读取工作流数据
	 * @param wfiid 工作流模板id
	 * 
	 * @param params 工作流对应的业务数据参数
	 * @throws Exception
	 */
	public void load(String wftid,String params) throws Exception{
		//载入工作流实例对象
		this.wfInstance = workflowDAO.loadInstance(wftid,params);
		//载入工作流模板对象

		this.wfTemplate = workflowDAO.loadTemplate(wftid);
		//载入工作流实例节点
		//workflowInstance.setNodeInstanceList(workflowDAO.loadNodeInstance(wfiid));
		//载入工作流模板节点
		//workflowTemplate.setWorkflowNodeList(workflowDAO.loadNodeTemplate(wftid));
	}
	
	
	/**
	 * 根据工作流模板创建工作流实例
	 * @param wftid 工作流模板id
	 * @param params 与工作流实例相关的业务逻辑的参数(该参数会作为查询工作流实例的重要参数,因此要求不同的工作流逻辑该参数不能重复)
	 * @throws Exception 异常
	 */
	public void createWorkflow(String wftid,String params) throws Exception{
		workflowDAO.create(wftid,params);
	}
	
	/**
	 * 启动当前的工作流
	 * @throws Exception
	 */
	public void start() throws Exception{
		if(this.wfInstance == null)
			throw new Exception("当前的工作流引擎没有包含完整的工作流模板和实例,可能是由于初始化不正常的原因造成。");
		
		NodeInstance firstNode = wfInstance.findFirst(wfTemplate);
		if(firstNode == null){
			throw new Exception("当前流程实例(id:"+this.wfInstance.getWfiid()+")无法找到起始节点，请检查流程节点的配置!");
		}
		
		if(firstNode.getIsactive().equals("1")){
			throw new Exception("当前流程第一个节点已经启动,请不要重复启动流程!");
		}
		//设定当前的第一个节点为"激活"状态
		firstNode.setIsactive("1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		firstNode.setModifytime(sdf.format(new Date()));
		
		if(!"0".equals(wfInstance.getStatus()) && !"3".equals(wfInstance.getStatus())){ //已经启动或者未被驳回的流程不能启动
			throw new Exception("当前流程已经启动或者未被驳回,请不要重复启动流程!");
		}
		//设定工作流的状态为"已启动"
		this.wfInstance.setStatus("1");
		createLog(firstNode.getNiid(),"1","huangxf","");
		
	}
	
	/**
	 * 存储当前的工作流信息到数据库
	 */
	public void save() throws Exception{
		workflowDAO.save(this);
	}
	
	
	/**
	 * 创建一条工作流日志记录到日志列表
	 * @param niid 涉及的实例节点ID
	 * @param operation 涉及的操作类型
	 * @param operator 涉及的操作人
	 * @param note 涉及的该节点的审批意见
	 * @return 
	 */
	public void createLog(String niid,String operation,String operator,String note){
		String wflid = new Long(new Date().getTime()).toString();
		WfLog log = new WfLog();
		log.setWflid(wflid);
		log.setNiid(niid);
		log.setOperation(operation);
		log.setOperator(operator);
		log.setNote(note);
		String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		log.setOperatetime(time);
		log.setIfdel("0");
		this.getWfLogList().add(log);
	}
	
	
	/**
	 * 向前移动流程节点
	 */
	public int forward() throws Exception{
		int finishFlag = 0; //判断工作流是否结束的标志
		//判断流程实例是否未启动或者未开始，如果是就抛出异常
		String status = this.getWfInstance().getStatus();
		if("0".equals(status) || "2".equals(status) || "3".equals(status)){
			if("0".equals(status))
				throw new Exception("该流程节点还未启动");
			if("2".equals(status))
				throw new Exception("该流程节点已经被审批完成");
			if("3".equals(status))
				throw new Exception("该流程节点已经被驳回");
		}
			
		//找出流程实例中active的节点
		
		NodeInstance currentNode = null;
		currentNode = this.getWfInstance().findNodeByAttr("isactive", "1");
		
		if(currentNode == null)
			throw new Exception("该流程中当前没有节点处于激活状态,流程状态异常,请检查流程实例数据.");
		
		//从模板节点中找出active节点对应的下一个模板节点id
		String insCurNid = currentNode.getNid();
		String nextNid = this.getWfTemplate().findNodeByAttr("nid", insCurNid).nextnodelist;
		String finish = this.getWfTemplate().findNodeByAttr("nid", insCurNid).getNodetype();
		if("2".equals(finish))
			finishFlag = 1;
		else
			finishFlag = 0;
		
		//从实例节点中找出与下一模板节点对应的实例节点
		NodeInstance nextNodeInstance = this.getWfInstance().findNodeByAttr("nid", nextNid);
		
		//将当前节点的active置为0,将下一节点的active置为1,status置为已审批通过
		currentNode.setIsactive("0");
		currentNode.setNextnode(nextNodeInstance.getNiid());
		currentNode.setStatus("1");
		//非最后一个节点时，执行下一节点的操作
		if(finishFlag == 0){
			nextNodeInstance.setPrvnode(currentNode.getNiid());
			nextNodeInstance.setIsactive("1");
		}else{
			this.wfInstance.setStatus("2"); //设置流程的状态为审核通过
		}

		//保存日志
		createLog(currentNode.getNiid(),"2","huangxf","审批流程通过");
		return finishFlag;
	}
	public void forward(int step){} //向前推进工作流节点
	
	public int backward() throws Exception{
		int backFlag = 0; //判断驳回的标志
		//判断流程实例是否未启动或者未开始，如果是就抛出异常
		String status = this.getWfInstance().getStatus();
		if("0".equals(status) || "2".equals(status) || "3".equals(status)){
			if("0".equals(status))
				throw new Exception("该流程节点还未启动");
			if("2".equals(status))
				throw new Exception("该流程节点已经被审批完成");
			if("3".equals(status))
				throw new Exception("该流程节点已经被驳回");
		}
		//找出流程实例中active的节点
		
		NodeInstance currentNode = null;
		currentNode = this.getWfInstance().findNodeByAttr("isactive", "1");
		
		if(currentNode == null)
			throw new Exception("该流程中当前没有节点处于激活状态,流程状态异常,请检查流程实例数据.");
		
		NodeTemplate curTemplate = this.wfTemplate.findNodeByAttr("nid", currentNode.getNid());
		String nodeType = curTemplate.getNodetype();
		
		if("0".equals(nodeType))
				backFlag = 1;
		else
			backFlag = 0;
		
		currentNode.setStatus("2"); //当前节点为驳回
		currentNode.setIsactive("0"); //设定当前节点为非活动
		//找出active节点的上一个节点
		NodeInstance prevNode = null;
		if(backFlag == 0){ //如果当前节点不是首节点
			prevNode = this.getWfInstance().findNodeByAttr("niid", currentNode.prvnode);
			prevNode.setIsactive("1");
		}else{
			this.wfInstance.setStatus("3"); //设定工作流状态为已驳回
		}
		createLog(currentNode.getNiid(),"3","huangxf","审核驳回");
		
		return backFlag;
	}
	
	/**
	 * 工作流退回
	 * @param step
	 */
	public void backward(int step){}//向后驳回工作流节点
	public void skip(){} //跳过当前节点
	public void stop(){}//在当前节点结束工作流
	public void destroy(){}


	public WfTemplate getWfTemplate() {
		return wfTemplate;
	}


	public void setWfTemplate(WfTemplate wfTemplate) {
		this.wfTemplate = wfTemplate;
	}


	public WfInstance getWfInstance() {
		return wfInstance;
	}


	public void setWfInstance(WfInstance wfInstance) {
		this.wfInstance = wfInstance;
	}


	public ArrayList<WfLog> getWfLogList() {
		return wfLogList;
	}


	public void setWfLogList(ArrayList<WfLog> wfLogList) {
		this.wfLogList = wfLogList;
	}; 
	
	
	
	//销毁工作流
	
	

}
