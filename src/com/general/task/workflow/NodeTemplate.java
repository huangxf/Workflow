package com.general.task.workflow;

public class NodeTemplate {
	String nid = null;
	String wftid = null;
	String nodename = null;
	String nodetype = null;
	String nextnodelist = null;
	String auditurl = null;
	String actor = null;
	String inputtime = null;

	public String getNid() {
		return this.nid;
	}

	public void setNid(String Nid) {
		this.nid = Nid;
	}

	public String getWftid() {
		return this.wftid;
	}

	public void setWftid(String Wftid) {
		this.wftid = Wftid;
	}

	public String getNodename() {
		return this.nodename;
	}

	public void setNodename(String Nodename) {
		this.nodename = Nodename;
	}

	public String getNextnodelist() {
		return this.nextnodelist;
	}

	public void setNextnodelist(String Nextnodelist) {
		this.nextnodelist = Nextnodelist;
	}

	public String getActor() {
		return this.actor;
	}

	public void setActor(String Actor) {
		this.actor = Actor;
	}

	public String getInputtime() {
		return this.inputtime;
	}

	public void setInputtime(String Inputtime) {
		this.inputtime = Inputtime;
	}

	public String getAuditurl() {
		return auditurl;
	}

	public void setAuditurl(String auditurl) {
		this.auditurl = auditurl;
	}

	public String getNodetype() {
		return nodetype;
	}

	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	

}
