package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.wyx.util.PropUtil;
/**
 * 人机主界面
 * 包括显示面板、视频加载面板、控制面板
 * @author hadoop
 *
 */
public class CustomJFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private Image logo = new ImageIcon(this.getClass().getResource("logo.jpg")).getImage();
	private ShowFacePanel show;
	private ControlPanel control;
	private VideoLoadPanel loadPanel;
	
	public static Point point = null;
	
	private static PropUtil prop = PropUtil.getInstance();
	public static final int CUSTOM_WIDTH ;
	public static final int CUSTOM_HEIGHT ;
	static {
		CUSTOM_WIDTH = Integer.parseInt(prop.getProperty("CUSTOM_WIDTH"));
		CUSTOM_HEIGHT = Integer.parseInt(prop.getProperty("CUSTOM_HEIGHT"));
	}
	public CustomJFrame(){
		//设置标题
		this.setTitle("基于Hadoop 人脸识别系统");
		//设置图标
		this.setIconImage(logo);
		//设置大小
		this.setSize(CUSTOM_WIDTH, CUSTOM_HEIGHT);
		//设置大小不可改变
		this.setResizable(false);
		//设置默认的关闭方式，退出时关闭程序
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置相对位置
		this.setLocationRelativeTo(null);
		//设置布局为空
		this.setLayout(new BorderLayout(5,5));
		//窗体总是置上
//		this.setAlwaysOnTop(true);
		//显示面板
		show = new ShowFacePanel();
		this.add(show,BorderLayout.NORTH);
		//视频加载面板
		loadPanel = new VideoLoadPanel();
		this.add(loadPanel,BorderLayout.CENTER);
		//控制面板
		control = new ControlPanel();
		control.addPanelContentListener(show);
		control.add();
		this.add(control,BorderLayout.SOUTH);
		
		//显示窗体
		this.setVisible(true);
		point = this.getLocationOnScreen();
	}
	
	public static void main(String[] args) {
		new CustomJFrame();
	}
}