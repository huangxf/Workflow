package test;

import java.util.Date;

import com.general.task.workflow.WorkflowEngine;
import com.general.task.workflow.dao.*;

public class Test {
	
	public static void main(String[] args){
		WorkflowDAOImp dao = new WorkflowDAOImp();
		WorkflowEngine engine = WorkflowEngine.init(dao);
		try{
			//engine.createWorkflow("0x0001",new Long((new Date()).getTime()).toString());
			WorkflowEngine.init(dao);
			engine.load("0x0001","1345841204125");
			engine.start();
			engine.save();
		}catch(Exception e){
			System.out.println("Something is wrong:" + e.getMessage());
			e.printStackTrace();
		}
	}

}
