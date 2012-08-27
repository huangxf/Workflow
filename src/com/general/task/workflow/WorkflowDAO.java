package com.general.task.workflow;

import java.util.ArrayList;

public interface WorkflowDAO {
	
	/***
	 * 创建并启动工作流
	 */
	public void create(String wftid,String params,String userid) throws Exception;
	
	
	
	/***
	 * 将工作流状态存入数据库
	 */
	public void save(WorkflowEngine engine) throws Exception;
	
	/***
	 * 从数据库中读取工作流模板
	 */
	public WfTemplate loadTemplate(String wftid) throws Exception;
	
	/***
	 * 从数据库中读取工作流实例
	 */
	public WfInstance loadInstance(String wfiid) throws Exception;
	public WfInstance loadInstance(String wftid,String params) throws Exception;
	
	/***
	 * 从数据库中读取工作流模板节点
	 */
	public NodeTemplate loadNodeTemplate(String nid) throws Exception;

	/***
	 * 从数据库中读取工作流实例节点
	 */
	public NodeInstance loadNodeInstance(String niid) throws Exception;
	
	/**
	 * 从数据库中读取工作流实例日志
	 */
	public WfLog loadWfLog(String wflid) throws Exception;
	
	/***
	 * 从数据库中删除工作流
	 */
	public void delete() throws Exception;
	
	/**
	 * 根据角色id取出相应的userid
	 * @param nodeTemplate
	 * @return
	 * @throws Exception
	 */
	public String getNodeActors(String wftid,String actor) throws Exception;

}
