package com.wyx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
/**
 * 操作配置文件config.properties的工具类
 * 单例设计模式创建工具类对象（饿汉式）
 * @author hadoop
 *
 */
public class PropUtil {
	//在类加载的时候就创建了该对象
	public static PropUtil instance = new PropUtil();
	private static Properties prop;
	
	public static PropUtil getInstance() {
		return instance;
	}
	/**
	 * 加载配置文件
	 */
	public PropUtil() {
		prop = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("加载配置文件失败");
		}
	}
	/**
	 * 根据给出的键返回相应的值
	 * @param key 键
	 * @return    相应的值
	 */
	public String getProperty(String key) {
		if (prop!=null){
			try {
				return new String(prop.getProperty(key).getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new RuntimeException("获取值异常......");
			}
		}
		return null;
	}
	
	
	public void setProperty(){
		
	}
}