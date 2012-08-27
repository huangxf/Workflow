package com.general.task.workflow.dao;

import com.huangxf.databasetools.DatabaseTools;

public class GetActorList {

	public static String getActorList(String actor) throws Exception{
		DatabaseTools util = new DatabaseTools("localhost","1433","organ","sa","123456");
		try{
			String userid = "";
			
			userid = util.getColumnValue("SELECT userid FROM v_user_role WHERE roleid = '"+actor+"' and rolename like '%设备%'");
			return userid;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("在取得用户角色的时候发生了错误！");
		}finally{
			util.close();
		}
	}
}
