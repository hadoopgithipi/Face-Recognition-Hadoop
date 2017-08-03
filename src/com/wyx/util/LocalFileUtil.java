package com.wyx.util;

import java.io.File;
/**
 * 本地目录工具类
 * 创建文件等
 * @author hadoop
 *
 */
public class LocalFileUtil {
	//不允许创建该类的对象
	private LocalFileUtil(){}
	/**
	 * 判断输入是否为文件
	 * @return 是文件 返回true  否则返回false
	 */
	public static boolean isFile(String path){
		File file = new File(path);
		if (file.exists()&&file.isFile()) {
			return true;
		}
		return false;
	}
	/**
	 *	创建目录 
	 * @param path 路径
	 * @return 创建成功返回true  否则返回false
	 */
	public static boolean mkdirDir(String path){
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return file.mkdirs();
		}
		return true;
	}
	/**
	 * 删除源文件
	 * @param srcFile 源文件路径
	 * @return 删除成功返回true 否则返回false
	 */
	public static boolean deleteSrcFile(String srcFile){
		File file = new File(srcFile);
		return file.delete();
	}
	
	/**
	 * 删除源目录
	 * @param srcDir 源目录路径
	 * @return  删除成功  返回true  否则返回false
	 */
	public static boolean deleteSrcDir(String srcDir){
		return deleteSrcDir(new File(srcDir));
	}
	
	/**
	 * 删除源目录
	 * @param srcDir 源目录路径
	 * @return  删除成功  返回true  否则返回false
	 */
	public static boolean deleteSrcDir(File dir){
		File [] files = dir.listFiles();
		for (File file2 : files) {
			if (file2.isDirectory()) {
				deleteSrcDir(file2.getAbsolutePath());
			}
			if (file2.isFile()) {
				deleteSrcFile(file2.getAbsolutePath());
			}
		}
		return dir.delete();
	}
//	public static void main(String[] args) {
//		System.out.println(deleteSrcDir("/home/hadoop/wyx"));
//	}
//	
}
