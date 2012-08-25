package com.general.task.workflow;

import java.util.ArrayList;
import java.util.Iterator;

public class WfTemplate{
	String wftid =null;
	String name =null;
	String description =null;
	String actorfun =null;
	String inputtime =null;
	ArrayList<NodeTemplate> NodeTemplateList;
	public String getWftid(){
		return this.wftid;
	}
	public void setWftid(String Wftid){
		this.wftid = Wftid;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String Name){
		this.name = Name;
	}
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String Description){
		this.description = Description;
	}
	public String getActorfun(){
		return this.actorfun;
	}
	public void setActorfun(String Actorfun){
		this.actorfun = Actorfun;
	}
	public String getInputtime(){
		return this.inputtime;
	}
	public void setInputtime(String Inputtime){
		this.inputtime = Inputtime;
	}
	public ArrayList<NodeTemplate> getNodeTemplateList() {
		return NodeTemplateList;
	}
	public void setNodeTemplateList(ArrayList<NodeTemplate> nodeTemplateList) {
		NodeTemplateList = nodeTemplateList;
	}
	
	
	/**
	 * 查找流程模板第一个节点
	 * @return
	 * @throws Exception
	 */
	public NodeTemplate findFirst() throws Exception{
		NodeTemplate first = null;
		int count = 0;
		if(this.getNodeTemplateList() == null)
			return first;
		
		Iterator<NodeTemplate> it = this.getNodeTemplateList().iterator();
		while(it.hasNext()){
			NodeTemplate temp = it.next();
			if("0".equals(temp.getNodetype()) ){
				count++;
				first = temp;
			}
		}
		
		if(count > 1)
			throw new Exception("该流程模板(id:"+this.getWftid()+")中包含多于1个的起始节点！");
		else
			return first;
	}
	
	

	
	
}
