package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.googlecode.javacv.FrameGrabber.Exception;
import com.wyx.face.FaceRecognizer;
import com.wyx.util.PropUtil;

/**
 * 人脸注册窗体
 * 通过输入用户名、选择是视频文件注册还是通过摄像头进行注册
 * @author hadoop
 *
 */
public class RegisterFaceDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RegisterFaceDialog.class);
	private static PropUtil prop = PropUtil.getInstance();
	//true 表示从视频中获取人脸并进行注册，false 表示从摄像头中获取人脸并进行注册
	public static boolean flag = true;
	private static final int DIALOG_WIDTH ;
	private static final int DIALOG_HEIGHT;
	static {
		DIALOG_WIDTH = Integer.parseInt(prop.getProperty("DIALOG_WIDTH"));
		DIALOG_HEIGHT = Integer.parseInt(prop.getProperty("DIALOG_HEIGHT"));
	}
	public RegisterFaceDialog() {
		this.setBounds(CustomJFrame.point.x+5, 465, DIALOG_WIDTH, DIALOG_HEIGHT);
		this.setModal(true);
		this.setUndecorated(true);
		this.setLayout(null);
		this.setResizable(false);
		
		initContentPane();
		
		this.setVisible(true);
	}
	
	private JTextField username = new JTextField();
	private JPanel contentPanel = new JPanel();
	/**
	 * 初始化内容面板
	 */
	public void initContentPane() {
		contentPanel.setLayout(new BorderLayout(5,5));
		
		initTopPanel();
		initCenterPanel();
		initBottomPanel();
		
		this.setContentPane(contentPanel);
		
	}
	/**
	 * 初始化顶层面板
	 */
	public void initTopPanel(){
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		JPanel tmp = new JPanel();
		//用户名标签
		JLabel label = new JLabel("用户名");
		tmp.add(label);
		//用户名文本框
		username.setPreferredSize(new Dimension(100, 20));
		tmp.add(username);
		topPanel.add(tmp, BorderLayout.NORTH);
		
		tmp = new JPanel();
		tmp.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
		tmp.setLayout(new BorderLayout());
		ButtonGroup group = new ButtonGroup();
		JRadioButton radio1 = new JRadioButton("从视频中获取人脸并进行注册");
		radio1.setSelected(true);
		radio1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				flag = true;				
			}
		});

		JRadioButton radio2 = new JRadioButton("从摄像头中获取人脸并进行注册");
		radio2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				flag = false;
			}
		});
		
		group.add(radio1);
		group.add(radio2);
		
		tmp.add(radio1,BorderLayout.NORTH);
		tmp.add(radio2,BorderLayout.CENTER);
		topPanel.add(tmp, BorderLayout.CENTER);
		
		contentPanel.add(topPanel, BorderLayout.NORTH);
		
	}
	/**
	 * 初始化中心面板
	 */
	public void initCenterPanel(){
		Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
		JPanel centerPanel = new JPanel();
		//确定按钮
		JButton ok = new JButton("确定");
		ok.setCursor(cursor);
		ok.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				click_ok_action();
			}
		});
		centerPanel.add(ok);
		JButton cancel = new JButton("取消");
		cancel.setCursor(cursor);
		cancel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				click_cancel_action();
			}
		});
		centerPanel.add(cancel);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
	}
	/**
	 * 初始化底部面板
	 */
	public void initBottomPanel(){
		JTextArea textArea = new JTextArea(
				"如果想删除已注册的用户：\n1.将data中的ForTraining.txt打开，清空并保存" +
				"\n2.将data中的facedata.xml用记事本打开，清空，\n留下第一行<?xml version=?>并保存"
				);
		textArea.setEditable(false);
		textArea.setPreferredSize(new Dimension(220,80));
		contentPanel.add(textArea, BorderLayout.SOUTH);
	}
	/**
	 * 点击确定按钮  事件
	 */
	public void click_ok_action() {
		String name = username.getText().trim();
		boolean isExists = false;
		if (!name.equals("")) {
			FaceRecognizer faceRecognizer = new FaceRecognizer();
			List<String>personNames = faceRecognizer.getList();
			for (String personName : personNames) {
				if (name.equals(personName)) {
					JOptionPane.showMessageDialog(null, "该用户已经注册过！");
					isExists = true;
					break;
				}
			}
			if (!isExists) {//该用户没有被注册过
				try {
					if (faceRecognizer.register(name)) {
						logger.info("注册成功");
						JOptionPane.showMessageDialog(null, "注册成功");
					}else{
						logger.info("注册失败");
						JOptionPane.showMessageDialog(null, "注册失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("注册失败");
				}
			}
		}else{
			JOptionPane.showMessageDialog(null, "用户名不能为空，请检查用户名！");
		} 
	}

	/**
	 * 点击取消按钮 事件
	 */
	public void click_cancel_action() {
		this.dispose();
	}
//	public static void main(String[] args) {
//		new RegisterFaceWindow();
//	}
}
