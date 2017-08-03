package com.wyx.util;

import java.io.File;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/**
 * 将本地文件上传到hdfs或者将hdfs上的文件下载到本地
 * @author hadoop
 *
 */
public class HdfsUtil {
	private HdfsUtil(){};
	private static Configuration conf = new Configuration();
	
	static {
		conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	}
	/**
	 * 将hdfs上的文件下载到本地
	 * @param srcPath 源路径
	 * @param destPath 目的路径
	 */
	public static void downloadFromHdfs(String srcPath,String destPath){
		downloadFromHdfs(new Path(srcPath), new Path(destPath));
	}
	/**
	 * 将hdfs上的文件下载到本地
	 * @param srcPath 源路径
	 * @param destPath 目的路径
	 */
	public static void downloadFromHdfs(Path srcPath,Path destPath){
		try {
			FileSystem hdfs = FileSystem.get(new URI(srcPath.toString()),conf);
			hdfs.copyToLocalFile(srcPath, destPath);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 将本地文件上传到Hdfs
	 * @param srcPath 源路径
	 * @param destPath 目的路径
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public static void putFileToHdfs(String srcPath,String destPath) throws Exception{
		putFileToHdfs(new Path(srcPath), new Path(destPath));
	}
	/**
	 * 将本地文件上传到Hdfs
	 * @param srcPath 源路径
	 * @param destPath 目的路径
	 * @throws Exception 
	 */
	public static void putFileToHdfs(Path srcPath,Path destPath) throws Exception{
		try {
			FileSystem hdfs = FileSystem.get(new URI(destPath.toString()),conf);
//			hdfs.setVerifyChecksum(false);
			File file = new File(srcPath.toString());
			if (file.isDirectory()) {
				File [] files = file.listFiles();
				for (File tmp : files) {
					hdfs.copyFromLocalFile(false, true, new Path(srcPath+File.separator+tmp.getName()), destPath);
				}
			}else {
				hdfs.copyFromLocalFile(false, true, srcPath, destPath);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 创建dfs 目录
	 * @param path  dfs目录
	 * @return 创建成功返回true  否则返回false
	 */
	public static boolean mkdirDir(String path){
		try {
			FileSystem hdfs = FileSystem.get(new URI(path),conf);
			return hdfs.mkdirs(new Path(path));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	/**
	 * 删除分布式文件系统上的文件
	 * @param srcFile 源文件
	 * @return 删除成功返回true  否则返回false
	 */
	public static boolean deleteSrcFile(String srcFile){
		FileSystem hdfs;
		try {
			hdfs = FileSystem.get(new URI(srcFile),conf);
			return hdfs.delete(new Path(srcFile), true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	/**
	 * 删除分布式文件系统上的文件
	 * @param srcDir 源目录
	 * @return 删除成功返回true 否则返回false
	 */
	public static boolean deleteSrcDir(String srcDir){
		FileSystem hdfs;
		try {
			hdfs = FileSystem.get(new URI(srcDir), conf);
			if (!hdfs.exists(new Path(srcDir))) {
				return true;
			}
			return hdfs.delete(new Path(srcDir), true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
//		putFileToHdfs("/home/hadoop/frames/", "/user/hadoop/");
//		downloadFromHdfs("/user/hadoop/image/logo.jpg","/home/hadoop/local_frames/" );
//		System.out.println(deleteSrcDir("/user/hadoop/faceRecog"));
		
	}
}
