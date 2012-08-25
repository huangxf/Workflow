package com.general.task.workflow;

public class WfLog{
	String wflid =null;
	String niid =null;
	String operation =null;
	String note =null;
	String operator =null;
	String operatetime =null;
	String ifdel =null;
	public String getWflid(){
		return this.wflid;
	}
	public void setWflid(String Wflid){
		this.wflid = Wflid;
	}
	public String getNiid(){
		return this.niid;
	}
	public void setNiid(String Wfiid){
		this.niid = Wfiid;
	}
	public String getOperation(){
		return this.operation;
	}
	public void setOperation(String Operation){
		this.operation = Operation;
	}
	public String getNote(){
		return this.note;
	}
	public void setNote(String Note){
		this.note = Note;
	}
	public String getOperator(){
		return this.operator;
	}
	public void setOperator(String Operator){
		this.operator = Operator;
	}
	public String getOperatetime(){
		return this.operatetime;
	}
	public void setOperatetime(String Operatetime){
		this.operatetime = Operatetime;
	}
	public String getIfdel(){
		return this.ifdel;
	}
	public void setIfdel(String Ifdel){
		this.ifdel = Ifdel;
	}
}
