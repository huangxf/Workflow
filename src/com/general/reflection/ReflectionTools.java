package com.general.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Java反射工具包
 * @author huangxf
 *
 */
public class ReflectionTools {
	
	
	/**
	 * 从一个类中取得属性的值，注意，该类的属性中一定要存在符合命名规范的getter才能使用
	 * @param obj 对象名称
	 * @param attr 需要查询的属性
	 * @return 返回属性的值
	 */
	public static String getValue(Object obj,String attr){
		String value = null;
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(int i = 0;i<fields.length;i++){
			String attrName = fields[i].getName();
			if(attrName.equals(attr) && fields[i].getType() == String.class){
				try {
					Method method = clazz.getMethod("get"+Uppercase(attrName), null);
					value = (String)method.invoke(obj,null);
				} catch (SecurityException e) {
					System.out.println("方法get"+Uppercase(attrName)+"不是public的方法，无法访问!");
					e.printStackTrace();
					break;
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					System.out.println("方法get"+Uppercase(attrName)+"不存在!");
					e.printStackTrace();
					e.printStackTrace();
					break;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					System.out.println("方法get"+Uppercase(attrName)+"参数错误!");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					System.out.println("方法get"+Uppercase(attrName)+"不是public的方法，无法访问!");
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					System.out.println("方法get"+Uppercase(attrName)+"异常抛出问题!");
					e.printStackTrace();
				}
				
			}
		}
		return value;
	}
	
	/**
	 * 将一个字符串的第一个字母转换成大写字母
	 * @param attrName
	 * @return
	 */
	public static String Uppercase(String attrName){
        if (attrName != null) {  
            StringBuffer sb = new StringBuffer(attrName);  
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
            return sb.toString();  
        } else {  
            return null;  
        }  
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 返回包含对象所有String属性的HashMap
	 * @param obj 需要抽取属性的对象
	 * @return 包含String属性的HashMap
	 * @throws Exception
	 */
	public static HashMap<String,String> getMemberMap(Object obj) throws Exception{
		HashMap<String,String> result = new HashMap<String,String>(1);
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(int i = 0;i<fields.length;i++){
			if(fields[i].getType() == String.class){
				result.put(fields[i].getName(), getValue(obj,fields[i].getName()));
			}
		}
		return result;
	}
}
