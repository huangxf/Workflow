package com.general.task.workflow;

public class NodeInstance{
	String niid =null;
	String nid =null;
	String wfiid =null;
	String actor =null;
	String isactive =null;
	String isskippable =null;
	String nodetype =null;
	String status =null;
	String inputtime =null;
	String modifytime =null;
	public String getNiid(){
		return this.niid;
	}
	public void setNiid(String Niid){
		this.niid = Niid;
	}
	public String getNid(){
		return this.nid;
	}
	public void setNid(String Nid){
		this.nid = Nid;
	}
	public String getWfiid(){
		return this.wfiid;
	}
	public void setWfiid(String Wfiid){
		this.wfiid = Wfiid;
	}
	public String getActor(){
		return this.actor;
	}
	public void setActor(String Actor){
		this.actor = Actor;
	}
	public String getIsactive(){
		return this.isactive;
	}
	public void setIsactive(String Isactive){
		this.isactive = Isactive;
	}
	public String getIsskippable(){
		return this.isskippable;
	}
	public void setIsskippable(String Isskippable){
		this.isskippable = Isskippable;
	}
	public String getNodetype(){
		return this.nodetype;
	}
	public void setNodetype(String Nodetype){
		this.nodetype = Nodetype;
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
	public String getModifytime(){
		return this.modifytime;
	}
	public void setModifytime(String Modifytime){
		this.modifytime = Modifytime;
	}
}
