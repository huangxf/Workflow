package test;

import java.lang.reflect.Field;

import com.general.reflection.ReflectionTools;
import com.general.task.workflow.WfTemplate;
import com.general.task.workflow.WorkflowEngine;
import com.general.task.workflow.dao.WorkflowDAOImp;

public class ReflectionTest {
	
	
	public static void main(String[] args) throws Exception{
		WorkflowDAOImp dao = new WorkflowDAOImp();
		WorkflowEngine engine = WorkflowEngine.init(dao,"0000000002");
		WorkflowEngine.init(dao,"0000000002");
		engine.load("0x0001","1345719266062");
		WfTemplate template = engine.getWfTemplate();
		
		System.out.println(ReflectionTools.getValue(template, "name"));
		

		
		
	}

}
