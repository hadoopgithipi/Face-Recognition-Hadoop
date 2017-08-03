package com.wyx.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.wyx.face.FaceRecognizer;
import com.wyx.mr.FaceRecogDriver;
import com.wyx.myinterface.PanelContentListener;
import com.wyx.util.HdfsUtil;
import com.wyx.util.LocalFileUtil;
import com.wyx.util.PropUtil;
import com.wyx.util.SingleFaceRecognizer;
import com.wyx.util.VideoConvertUtil;
import com.wyx.util.VideoFrameUtil;

public class FunctionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	//获取日志对象
	private Logger logger = Logger.getLogger(FunctionPanel.class);
	//
	private JLabel selected = null;
	//设置点击功能按钮标志
	private int flag = 0;
	//人脸识别 表示false;人脸检索true
	public static boolean isRecog = true;
	//获取配置文件对象
	private PropUtil prop = PropUtil.getInstance();
	
	//是否删除源文件 
	private boolean delLocalSrcFile = Boolean.parseBoolean(prop.getProperty("delLocalSrcFile"));
	private boolean delDfsSrcFile = Boolean.parseBoolean(prop.getProperty("delDfsSrcFile"));
	
	public static String startTime = "";
	public static String endTime = "";
	
	private List<PanelContentListener> listeners = new ArrayList<PanelContentListener>();
	
	/**
	 * 添加监听器
	 * @param listener
	 */
	public void addPanelContentListener(PanelContentListener listener){
		listeners.add(listener);
	}
	
	public FunctionPanel(){
		this.setPreferredSize(new Dimension(CustomJFrame.CUSTOM_WIDTH/2,0));
		this.setBorder(BorderFactory.createTitledBorder("功能区"));
//		this.setBackground(Color.MAGENTA);
		this.setLayout(new GridLayout(5,1));
		
		initRegisterFacelb();
		initSwitchFormatlb();
		initVideoToImageslb();
		initUpLoadToHdfslb();
		initImagesRetrieval();
	}
	/**
	 * 人脸注册
	 * 通过检索视频进行注册、通过摄像头进行注册
	 */
	public void initRegisterFacelb(){
		JLabel label = new JLabel("人脸注册");
		this.add(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel)e.getSource();
				setLableUI(label);//设置点击后JLable的外观
				logger.info("face register is waiting......");
				registerFaceAction();//人脸注册事件
			}
		});
	}
	
	/**
	 * 转换格式
	 */
	public void initSwitchFormatlb(){
		JLabel label = new JLabel("视频格式转换->AVI");
		
		this.add(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel)e.getSource();
				setLableUI(label);//设置点击后JLable的外观
				logger.info("video format is switched to AVI");
				switchFormatAction();
			}
		});
	}
	
	/**
	 * 将视频转化为图片
	 */
	public void initVideoToImageslb(){
		JLabel label = new JLabel("视频转化为图片");
		this.add(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel)e.getSource();
				setLableUI(label);//设置点击后JLable的外观
				logger.info("video is cut apart frames ");
				videoToFramesAction();
			}
		});
	}
	
	/**
	 * 将生成的图片上传至HDFS
	 */
	public void initUpLoadToHdfslb(){
		JLabel label = new JLabel("将图片上传至HDFS");
		this.add(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel)e.getSource();
				setLableUI(label);//设置点击后JLable的外观
				logger.info("frames is uploaded to dfs");
				copyFramesToHdfsAction();
			}
		});
	}
	/**
	 * 检索图像
	 */
	public void initImagesRetrieval(){
		JLabel label = new JLabel("开始检索图像");
		this.add(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setLableUI((JLabel)e.getSource());
				logger.info("start retriavaling face frames");
				int result = JOptionPane.showConfirmDialog(null, "是否启用特定检索？","特定检索", JOptionPane.YES_NO_OPTION);
				if (result==JOptionPane.YES_OPTION) {
					isRecog = false;
					new SpecificSearchDialog();
					
					FaceRecognizer faceRecognizer = SingleFaceRecognizer.getInstance();
					faceRecognizer.recongizeFormImage(SpecificSearchDialog.imagePath);
					
				}else {
					isRecog = true;
				}
				retriavalAction();
			}
		});
	}

	/**
	 * 注册人脸事件
	 */
	public void registerFaceAction(){
		
		new RegisterFaceDialog();
	}
	/**
	 * 转换格式事件
	 * 
	 * 转换格式后视频的输出路径==视频输入路径
	 */
	public void switchFormatAction(){
		if (VideoLoadPanel.flag!=0) {//还未加载文件
			logger.error("firstly load the video file");
			JOptionPane.showMessageDialog(null, "请先加载视频文件");
			resetLableUI();//恢复JLable以前的外观
			return;
		}else {//成功加载视频文件
			//获得视频输入路径
			String videoInputPath = VideoLoadPanel.path.toString();
			logger.info("video input path is: "+videoInputPath);
			if (videoInputPath.lastIndexOf(".avi")>-1) {//.avi文件
				logger.info("the video file is .avi file,don't need to switch format");
				JOptionPane.showMessageDialog(null, "这是一个.avi文件");
				flag = 1;
			}else {//非.avi文件
				VideoConvertUtil convert = new VideoConvertUtil(videoInputPath);
				if (convert.processAVI()) {//格式转换成功
					if (delLocalSrcFile) {
						if (LocalFileUtil.deleteSrcFile(videoInputPath)) {
							logger.info("delete local the video file: "+videoInputPath);
						}
					}
					flag = 1;
					logger.info("switch video format is completely ");
					JOptionPane.showMessageDialog(null, "格式转换成功");
				}else{//格式转换失败
					logger.info("switch video format is incompletely");
					JOptionPane.showMessageDialog(null, "格式转换失败");
					return;
				}
			}
		}
	}
	/**
	 * 视频转化为图片事件
	 */
	public void videoToFramesAction(){

		if (flag!=1) {//请先转换视频格式
			logger.error("firstly switch the video format");
			JOptionPane.showMessageDialog(null, "请先转换视频格式");
			resetLableUI();//恢复JLable以前的外观
			return;
		}else {//已经转换格式
			//获得原始视频输入路径
			//  /home/hadoop/Videos/tree.mp4
			String videoInputPath = VideoLoadPanel.path.toString();
			videoInputPath = videoInputPath.substring(0, videoInputPath.lastIndexOf("."))+".avi";
			logger.info(".avi video input path :"+videoInputPath);
			String framesLocalPath = prop.getProperty("local_framesPath");
			File file = new File(framesLocalPath);
			if (file.exists()) {
				LocalFileUtil.deleteSrcDir(file);
			}
			logger.info("frames local path: "+framesLocalPath);
			
			VideoFrameUtil videoFrameUtil = new VideoFrameUtil(framesLocalPath);
			if (videoFrameUtil.getFramesToImages(videoInputPath)) {
				logger.info("video to frames is completely!");
				JOptionPane.showMessageDialog(null, "视频成功转换为图片");
				flag = 2;
			}else{
				logger.error("video to frames is incompletely");
				JOptionPane.showMessageDialog(null, "视频转换为图片失败");
				return ;
			}
		}
	}
	/**
	 * 将图片上传到Hdfs事件
	 */
	public void copyFramesToHdfsAction(){
		if (flag!=2) {//请先将视频解析为图片
			logger.error("firstly video to frames");
			JOptionPane.showMessageDialog(null, "请先将视频解析为图片");
			resetLableUI();//恢复JLable以前的外观
			return;
		}else {//已经将视频解析为图片
			String inputPath = prop.getProperty("local_framesPath");
			String outputPath = prop.getProperty("dfs_framesPath");
			//删除后重新创建
			if (HdfsUtil.deleteSrcDir(outputPath)) {
				if (HdfsUtil.mkdirDir(outputPath)) {
					try {
						HdfsUtil.putFileToHdfs(inputPath, outputPath);
					} catch (Exception e) {
						LocalFileUtil.deleteSrcFile(prop.getProperty("local_faceFramesPath")+"/test23.jpg.crc");
						JOptionPane.showMessageDialog(null, "图片上传至HDFS失败,请重新操作");
						return;
					}
					logger.info("frames upload dfs is completely!");
					JOptionPane.showMessageDialog(null, "图片成功上传至HDFS");
					if (delLocalSrcFile) {
						if (LocalFileUtil.deleteSrcDir(inputPath)) {//删除源目录
							logger.info("delete local frames path completely");
						}
					}
					flag = 3;
				}
			}
		}
	}
	/**
	 * 检索图像事件
	 */
	public void retriavalAction(){
		if (flag!=3) {//请先将图片上传到Hdfs
			logger.error("firstly upload all frames");
			JOptionPane.showMessageDialog(null, "请先图像上传到Hdfs");
			resetLableUI();//恢复JLable以前的外观
			return;
		}else {//已经将图像上传到Hdfs
			try {
				// 0表示检索结束   1表示检索失败
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
				startTime = sdf.format(new Date());
				/**
				 * 在运行MapReduce作业之前首先将本地存放人脸图像的目录删除，
				 * 否则在控制面板显示的时候会得不到自己想要的结果
				 */
				File file = new File(prop.getProperty("local_faceFramesPath"));
				if (file.exists()) {
					LocalFileUtil.deleteSrcDir(file);
				}
				//如果时人脸检索，不是人脸识别，先识别提供的人脸图像
//				if (!isRecog){ 
//					FaceRecognizer faceRecognizer = SingleFaceRecognizer.getInstance();
//					faceRecognizer.recongizeFormImage(SpecificSearchDialog.imagePath);
//				}
				
				int isOk = ToolRunner.run(new FaceRecogDriver(), null);
				if (isOk == 0) {
					//将搜索的人的姓名重置
					FaceRecognizer.personName = "";
					logger.info("retriavaling face frames is over");
					endTime = sdf.format(new Date());
					JOptionPane.showMessageDialog(null, "检索完毕");
					if (listeners.size()==2) {
						logger.info("start showing face frames");
						//显示检测到的人脸图像
						listeners.get(0).showFace();
						logger.info("showing face frames is over");
						logger.info("start showing result messages");
						//显示检测信息
						listeners.get(1).showMessage();
						logger.info("showing result messages is over");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置点击JLabel标签的外观
	 * @param source
	 */
	public void setLableUI(JLabel source){
		if (selected!=source) {
			if( selected != null ){
				selected.setOpaque( false ) ;
				selected.setForeground( Color.BLACK ) ;
			}
			selected = source;
			selected.setOpaque( true ) ;
			selected.setBackground(new Color(57, 105, 137));
			selected.setForeground( Color.WHITE ) ;
		}
	}
	/**
	 * 恢复JLable外观
	 */
	public void resetLableUI(){
		if (selected!=null) {
			selected.setOpaque(false);
			selected.setForeground(Color.BLACK);
			selected = null;
		}
	}
}