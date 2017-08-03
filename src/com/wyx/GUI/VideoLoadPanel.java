package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.wyx.util.LocalFileUtil;
import com.wyx.util.PropUtil;
/**
 * 视频加载面板
 * 视频加载功能，获取视频文件路径，供后面对视频的格式的转换、图片截取
 * @author hadoop
 *
 */
public class VideoLoadPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(VideoLoadPanel.class);
	// 进度条
	private JProgressBar progress;
	/**
	 * 保存打开的视频文件的路径,后面对视频的操作需要用
	 */
	public static Path path;
	//加载视频文件时临时存放的路径
	private String videoBakPath = PropUtil.getInstance().getProperty("videoBakPath");
	//设置点击功能按钮的标志
	public static int flag = -1;
	Timer timer = new Timer(20, new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			loadingVideo();
		}
	});
	public VideoLoadPanel(){
		this.setPreferredSize(new Dimension(0, 60));
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setLayout(new BorderLayout(5,5));
		
		JLabel title = new JLabel("选择要加载的视频文件");
		this.add(title,BorderLayout.WEST);
		progress = new JProgressBar();
		progress.setStringPainted(true);
//		progress.setBorderPainted(false);
		this.add(progress,BorderLayout.CENTER);
		
		initSelectButton();
	}
	/**
	 * 初始化加载视频按钮
	 */
	public void initSelectButton() {
		JButton select = new JButton("...");
		select.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//选择要加载的视频文件
				selectVideo();
			}
		});
		this.add(select,BorderLayout.EAST);
	}
	/**
	 * 选择要加载的视频文件
	 */
	public void selectVideo(){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI & MP4 ", "avi", "mp4");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	path = new Path(chooser.getSelectedFile().getAbsolutePath());
	    	logger.info("you chose to open this file: "+path);
	    	if (LocalFileUtil.mkdirDir(videoBakPath)) {
	    		new Thread(new ReadFile()).start();
	    		//启动
	    		logger.info("start loading the video file....");
	    		timer.start();
			}else {
				logger.error("mkdir directory incompletly");
				return;
			}
	    }
	}
	/**
	 * 加载视频文件
	 */
	public void loadingVideo(){
		String filename = videoBakPath+path.getName();
		long maxBytes = new File(path.toString()).length();
		long currentBytes = new File(filename).length();
		progress.setValue((int)currentBytes);
		if (currentBytes>=maxBytes) {
			timer.stop();
			logger.info("videofile loading completely!");
			//删除临时文件
			new File(filename).delete();
			//改变标志值
			flag = 0;
		}
	}
	
	/**
	 * 将视频文件进行备份
	 * @author hadoop
	 *
	 */
	private class ReadFile implements Runnable{
		private BufferedInputStream reader ;
		private BufferedOutputStream write;
		
		public ReadFile(){
			try {
				reader = new BufferedInputStream(new FileInputStream(new File(path.toString())));
				write = new BufferedOutputStream(new FileOutputStream(new File(videoBakPath+new File(path.toString()).getName())));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				logger.error("get file exception");
				throw new RuntimeException("获取文件异常");
			}
		}
		public void run() {
			byte [] bs = new byte[1024*1024*10];
			int len = -1;
			try {
				while ((len =reader.read(bs))!=-1) {
					write.write(bs);
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("read and write file exception");
				throw new RuntimeException("读写文件异常");
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
				if (write!=null) {
					try {
						write.close();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						write = null;
					}
				}
			}
		}
	}
}