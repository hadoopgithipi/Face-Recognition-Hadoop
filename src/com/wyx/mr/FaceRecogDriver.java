package com.wyx.mr;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import com.wyx.util.HdfsUtil;
import com.wyx.util.PropUtil;
/**
 *作业驱动器 
 * @author hadoop
 *
 */
public class FaceRecogDriver extends Configured implements Tool{
	private static PropUtil prop = PropUtil.getInstance();
	private static final String MR_INPUT_PATH; 
	private static final String MR_OUTPUT_PATH ;
	static {
		MR_INPUT_PATH = prop.getProperty("mrInputPath");
		MR_OUTPUT_PATH = prop.getProperty("mrOutputPath");
	}
	{
		HdfsUtil.deleteSrcDir(MR_OUTPUT_PATH);		
	}
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf());
		job.setJarByClass(FaceRecogDriver.class);
		job.setJobName("基于Hadoop的人脸识别系统");
		
		FileInputFormat.setInputPaths(job, new Path(MR_INPUT_PATH));
		FileOutputFormat.setOutputPath(job, new Path(MR_OUTPUT_PATH));
		
		job.setMapperClass(FaceRecogMapper.class);
		job.setCombinerClass(FaceRecogReducer.class);
		job.setReducerClass(FaceRecogReducer.class);
		
//		job.setNumReduceTasks(4);
		
		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
				
		return job.waitForCompletion(true)?0:1;
	}
}