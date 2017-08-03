package com.wyx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.log4j.Logger;

public class VideoFrameUtil {
	
	private static String outputPath = null;
	private Logger logger = Logger.getLogger(VideoFrameUtil.class);
	
	public VideoFrameUtil(String outputPath){
		VideoFrameUtil.outputPath = outputPath;
	}
	public VideoFrameUtil(){
		
	}
	/**
	 * 将视频解析为图片
	 * @return
	 */
	public boolean getFramesToImages(String path){
		LocalFileUtil.mkdirDir(outputPath);
		if (!LocalFileUtil.isFile(path)) {
			logger.error(".avi video input path is false");
			throw new RuntimeException(".avi格式文件路径输入错误");
		}
		if (process(path)) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean process(String filename){
		//获得帧速率
		int fps =Integer.parseInt( PropUtil.getInstance().getProperty("fps"));
		
		
		int tmp = new Random().nextInt(100);
		String command = "ffmpeg -i "+filename+" -r"+" "+fps+" -f image2"+" "+outputPath+File.separator+"test"+tmp+"%d.jpg";
		logger.info("video to frames command is :"+command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			printStreams(new BufferedReader(new InputStreamReader(process.getInputStream())));
			printStreams(new BufferedReader(new InputStreamReader(process.getErrorStream())));
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	/**
	 * 打印流
	 * @param reader
	 */
	public void printStreams(BufferedReader reader){
		String line = null;
		try {
			while ((line = reader.readLine())!=null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
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
	/**
	 * 得到视频解析为总图片的数量
	 * @return 视频解析为总图片的数量
	 */
	public static int getAllFramesCount(){
		int size = 0;
		File file = new File(outputPath);
		if (file.exists()&&file.isDirectory()) {
			size = file.listFiles().length;
		}
		return size;
	}
}