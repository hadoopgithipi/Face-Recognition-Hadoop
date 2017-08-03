package com.wyx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
/**
 * 转化格式 工具类
 * 统一转化为.avi格式的视频文件
 * @author hadoop
 *
 */
public class VideoConvertUtil {
	private String inputPath = "";
	private Logger logger = Logger.getLogger(VideoConvertUtil.class);
	public VideoConvertUtil(String inputPath){
		this.inputPath = inputPath;
	}
	/**
	 * 检查输入路径是否存在并且是否为文件
	 * @return
	 */
	public  boolean checkFile(){
		File file = new File(inputPath);
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	/**
	 * 检查文件格式
	 * @return 0代表 .avi
	 * 		   1代表 非.avi
	 */
	public int checkContentType(){
		String type = inputPath.substring(inputPath.lastIndexOf(".")+1, inputPath.length()).toLowerCase();
		if (type.equals("avi")) {
			return 0;//以.avi格式结尾的视频文件
		}else {
			return 1;//以非.avi格式结尾的视频文件
		}
	}
	/**
	 * 如果类型为非.avi格式的视频文件，将其转化为以.avi格式结尾的视频文件
	 * @return 转换成功返回true   否则返回false
	 */
	public boolean processAVI(){
		int type = checkContentType();
		if (checkFile()) {
			if (type==1) {
				File result = convertToAVI();//获取转化后的结果
				if (result==null || !result.exists()) {			   //结果为空  返回false
					return false;
				}
			}
			return true;			
		}else {
			return false;
		}
	}
	/**
	 * 将非.avi格式结尾的视频文件转化为.avi格式结尾的视频文件
	 * @return
	 */
	public File convertToAVI(){
		//    /home/hadoop/Videos/tree.mp4
		String outputPath = inputPath.substring(0, inputPath.lastIndexOf("."))+".avi";
		logger.info("video output path is: "+outputPath);
		String command = "ffmpeg -i "+inputPath+" "+outputPath;
		logger.info("switch format command is: "+command);
		
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			
			InputStream input = process.getInputStream();//输入流
			InputStream error = process.getErrorStream();//错误流
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
			printStream(inputReader);//打印输入流
			
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(error));
			printStream(errorReader);//打印错误流
			return new File(outputPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 打印流
	 * @param reader
	 */
	public void printStream(BufferedReader reader){
		String line = null;
		try {
			while ((line=reader.readLine())!=null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					reader = null;
				}
			}
		}
	}
	
//	public static void main(String[] args) {
//		VideoConvertUtil convert = new VideoConvertUtil("/home/hadoop/tree.mp4","/home/hadoop/");
//		if (convert.processAVI()) {
//			System.out.println("成功转化为.avi格式的文件");
//		}else {
//			System.out.println("转化为.avi格式的文件失败");
//		}
//	}
}
