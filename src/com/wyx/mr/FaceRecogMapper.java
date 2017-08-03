package com.wyx.mr;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;

import com.wyx.face.FaceRecognizer;
import com.wyx.util.HdfsUtil;
import com.wyx.util.LocalFileUtil;
import com.wyx.util.PropUtil;
import com.wyx.util.SingleFaceRecognizer;

public class FaceRecogMapper extends Mapper<NullWritable, BytesWritable, Text, IntWritable>{
	//获得配置文件对象
	public PropUtil prop = PropUtil.getInstance();
	//人脸识别工具
	private FaceRecognizer faceRecognizer = SingleFaceRecognizer.getInstance();
	//获取日志对象
	private Logger logger = Logger.getLogger(FaceRecogMapper.class);
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		//获得输入分片
		FileSplit split = (FileSplit) context.getInputSplit();
		//获得图片在hdfs上的路径
		Path srcHdfsPath = split.getPath();	
		logger.info("dfs frames input path is: "+srcHdfsPath);
		//获得图片将保存到的本地路径
		String destLocalPath = prop.getProperty("local_framesPath");
		if (LocalFileUtil.mkdirDir(destLocalPath)) {
			logger.info("local frames output path is: "+destLocalPath);
			//将图片存放到本地路径                                                                    
			HdfsUtil.downloadFromHdfs(srcHdfsPath, new Path(destLocalPath));
			String filename = destLocalPath+File.separator+srcHdfsPath.getName();
			logger.info("local frames input path is: "+filename);
			//人脸识别
			faceRecognizer.recongizeFormImage(filename);
		}else {
			logger.info("local frame path is mkdired incompletely!");
			return;
		}
	}
	
	@Override
	protected void map(NullWritable key, BytesWritable value,Context context)
			throws IOException, InterruptedException {
		context.write(new Text("所有图像数量"), new IntWritable(1));
		context.write(new Text("人脸图像数量"), new IntWritable(1));
	}
}