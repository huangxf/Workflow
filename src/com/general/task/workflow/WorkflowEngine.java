package com.general.task.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	 * 工作流引擎的初始化函数，用于返回一个工作流引擎对象
	 * @return WorkflowEngine
	 */
	public static WorkflowEngine init(WorkflowDAO wfDAO){
		WorkflowEngine workflowEngine = new WorkflowEngine();
		workflowEngine.workflowDAO = wfDAO;
		workflowEngine.wfTemplate = null;
		workflowEngine.wfInstance = null;
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
		
	}
	
	/**
	 * 存储当前的工作流信息到数据库
	 */
	public void save() throws Exception{
		workflowDAO.save(this);
	}
	
	

	public void forward(int step){} //向前推进工作流节点
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
	}; //销毁工作流
	
	

}
