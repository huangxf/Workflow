package com.general.task.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import com.general.reflection.*;

public class WfInstance{
	String wfiid =null;
	String wftid =null;
	String creator =null;
	String actorfun =null;
	String params =null;
	String status =null;
	String inputtime =null;
	String lastmodify =null;
	ArrayList<NodeInstance> NodeInstanceList;
	public String getWfiid(){
		return this.wfiid;
	}
	public void setWfiid(String Wfiid){
		this.wfiid = Wfiid;
	}
	public String getWftid(){
		return this.wftid;
	}
	public void setWftid(String Wftid){
		this.wftid = Wftid;
	}
	public String getCreator(){
		return this.creator;
	}
	public void setCreator(String Creator){
		this.creator = Creator;
	}
	public String getActorfun(){
		return this.actorfun;
	}
	public void setActorfun(String Actorfun){
		this.actorfun = Actorfun;
	}
	public String getParams(){
		return this.params;
	}
	public void setParams(String Params){
		this.params = Params;
	}
	public String getStatus(){
		return this.status;
	}
	public void setStatus(String Status){
		this.status = Status;
	}
	public String getInputtime(){
		return this.inputtime;
	}
	public void setInputtime(String Inputtime){
		this.inputtime = Inputtime;
	}
	public String getLastmodify(){
		return this.lastmodify;
	}
	public void setLastmodify(String Lastmodify){
		this.lastmodify = Lastmodify;
	}
	public ArrayList<NodeInstance> getNodeInstanceList() {
		return NodeInstanceList;
	}
	public void setNodeInstanceList(ArrayList<NodeInstance> nodeInstanceList) {
		NodeInstanceList = nodeInstanceList;
	}
	
	public NodeInstance findFirst(WfTemplate wfTemplate) throws Exception{
		NodeTemplate firstNodeTemplate = wfTemplate.findFirst(); //在模板节点中取得第一个节点
		NodeInstance firstNodeInstance = null;
		String niid = null;
		String nid = null;
		if(firstNodeTemplate != null){
			nid = firstNodeTemplate.getNid();
			firstNodeInstance = findNodeByAttr("nid",nid); //根据模板节点中的nid来查找实例中的第一个节点
		}
		
		return firstNodeInstance;
	}
	
	
	/**
	 * 根据指定的属性和值来查找工作流实例下的节点
	 * @param attrName //指定的属性
	 * @param value //指定的值
	 * @return
	 */
	public NodeInstance findNodeByAttr(String attrName,String value) throws Exception{
		NodeInstance node = null;
		int count = 0;
		if(this.getNodeInstanceList() == null)
			return node;
		
		Iterator<NodeInstance> it = this.getNodeInstanceList().iterator();
		while(it.hasNext()){
			NodeInstance temp = it.next();
			if(ReflectionTools.getValue(temp, attrName) .equals(value) ){
				count++;
				node = temp;
			}
		}
		
		if(count > 1)
			throw new Exception("该流程实例(id:"+this.getWftid()+")中包含多于1个的符合该id的节点！");
		else
			return node;
	}
	
	
}
