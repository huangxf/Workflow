package test;

import java.util.Date;

import com.general.task.workflow.WorkflowEngine;
import com.general.task.workflow.dao.*;

public class Test {
	
	public static void main(String[] args){
		WorkflowDAOImp dao = new WorkflowDAOImp();
		WorkflowEngine engine = WorkflowEngine.init(dao,"0000000002");
		try{
			engine.createWorkflow("0x0001",new Long((new Date()).getTime()).toString());
			engine.load("0x0001","1346061722796");
			engine.start();
			engine.save();
			engine.forward();
			engine.save();
			engine.backward();
			engine.save();
			engine.stop();
			engine.save();
		}catch(Exception e){
			System.out.println("Something is wrong:" + e.getMessage());
			e.printStackTrace();
		}
	}

}
