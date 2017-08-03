package com.wyx.mr;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
/**
 * 自定义图片输入格式
 * 将一张图片作为一个整体不拆分
 * @author hadoop
 *
 */
public class ImageInputFormat extends FileInputFormat<NullWritable,BytesWritable>{
	
	@Override
	public RecordReader<NullWritable, BytesWritable> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException, InterruptedException {
		ImageRecordReader reader = new ImageRecordReader();
		reader.initialize(split, context);
		return reader;
	}

	/**
	 * 将一张图片作为一个整体  不拆分
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return false;
	}
}
