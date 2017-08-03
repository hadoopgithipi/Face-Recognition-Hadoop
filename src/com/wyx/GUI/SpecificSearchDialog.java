package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.wyx.face.FaceRecognizer;
import com.wyx.util.PropUtil;

public class SpecificSearchDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RegisterFaceDialog.class);
	private static PropUtil prop = PropUtil.getInstance();
	//true 表示根据人名进行搜索，false 表示根据图像进行搜索
	public static boolean flag = false;
	public static String imagePath = "";
	
	private static final int DIALOG_WIDTH;
	private static final int DIALOG_HEIGHT ;
	static {
		DIALOG_WIDTH = Integer.parseInt(prop.getProperty("DIALOG_WIDTH"));
		DIALOG_HEIGHT = Integer.parseInt(prop.getProperty("DIALOG_HEIGHT"));
	}
	public SpecificSearchDialog() {
		this.setBounds(CustomJFrame.point.x+5, 465, DIALOG_WIDTH, DIALOG_HEIGHT);
		this.setModal(true);
		this.setUndecorated(true);
		this.setLayout(null);
		this.setResizable(false);
		
		initContentPane();
		
		this.setVisible(true);
	}

	private JLabel namelb = new JLabel("用户名");
	private JTextField userName = new JTextField();
	
	private JLabel imagelb = new JLabel("图  像");
	private JTextField userImage = new JTextField();
	private JButton select = new JButton("...");
	private JPanel contentPanel = new JPanel();
	/**
	 * 初始化内容面板
	 */
	public void initContentPane() {
		contentPanel.setLayout(new BorderLayout());
		
		setEnable();
		
		initTopPanel();
		initCenterPanel();
		initBottomPanel();
		
		this.setContentPane(contentPanel);
		
	}

	/**
	 * 初始化顶层面板
	 */
    public void initTopPanel() {
    	JPanel topPanel = new JPanel();
    	topPanel.setLayout(new GridLayout(2,1));
    	
    	ButtonGroup group = new ButtonGroup();
		JRadioButton radio1 = new JRadioButton("根据人名进行检索");
//		radio1.setSelected(true);
		radio1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flag = true;
				setEnable();
			}
		});
		radio1.setEnabled(false);
		//该功能未实现
		JRadioButton radio2 = new JRadioButton("根据图像进行检索");
		radio2.setSelected(true);
		radio2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flag = false;
				setEnable();
			}
		});
//		radio2.setEnabled(false);
		
		group.add(radio1);
		group.add(radio2);
		
		JPanel tmp = new JPanel();
    	tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
    	tmp.add(radio1);
    	tmp.add(namelb);
    	userName.setPreferredSize(new Dimension(100, 20));
    	tmp.add(userName);
    	topPanel.add(tmp);
    	
    	tmp = new JPanel();
    	tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
    	tmp.add(radio2);
    	tmp.add(imagelb);
    	userImage.setPreferredSize(new Dimension(100, 20));
    	tmp.add(userImage);
    	tmp.add(select);
    	select.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			imagePath = selectImageFile();
    			userImage.setText(imagePath);
    		}
    	});
    	topPanel.add(tmp);
    	contentPanel.add(topPanel,BorderLayout.NORTH);
	}
    
	/**
	 * 设置各个组件的显示
	 */
	public void setEnable(){
		if (flag) {
			namelb.setEnabled(true);
			userName.setEnabled(true);
			imagelb.setEnabled(false);
			userImage.setEnabled(false);
			select.setEnabled(false);
		}else {
			namelb.setEnabled(false);
			userName.setEnabled(false);
			imagelb.setEnabled(true);
			userImage.setEnabled(true);
			select.setEnabled(true);
		}
	}
    
    /**
     * 初始化中心面板
     */
    public void initCenterPanel(){
    	JPanel tmp = new JPanel();
    	JButton ok = new JButton("确定");
    	ok.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				click_ok_action();
			}
    	});
    	JButton cancel = new JButton("取消");
    	cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				click_cancel_action();
			}
    	});
    	tmp.add(ok);
    	tmp.add(cancel);
    	contentPanel.add(tmp,BorderLayout.CENTER);
    }
    
    /**
     * 初始化底部面板
     */
    public void initBottomPanel(){
		JTextArea textArea = new JTextArea(
				"说明：\n" +
				"根据人名进行检索: 可以根据你输入的人名，从视频中中查找出对应的图像\n" +
				"根据图像进行检索: 可以根据你选择的图像，从视频中调出相应的图像"
				);
		textArea.setPreferredSize(new Dimension(0,80));
		textArea.setEditable(false);
		contentPanel.add(textArea,BorderLayout.SOUTH);
    }

	/**
	 * 点击确定按钮  事件
	 */
	public void click_ok_action() {
		if (flag) {
			String name = userName.getText().trim();
			if (!name.equals("")) {
				FaceRecognizer.personName = name;
				this.dispose();
			}else{
				JOptionPane.showMessageDialog(null, "用户名为空，请检查用户名！");
			} 
		}else {
			String path = userImage.getText().trim();
			if (!path.equals("") && (path.endsWith(".jpg") || path.endsWith(".png"))) {
				imagePath = path;
				this.dispose();
			}else{
				JOptionPane.showMessageDialog(null, "请选择一个人脸图像！");
			}
					
		}
	}
	/**
	 * 选择图像文件
	 * @return 
	 */
	public String selectImageFile(){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG ", "jpg", "png");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filepath = chooser.getSelectedFile().getAbsolutePath();
	    	logger.info("You chose to open this file(register face): "+filepath);
	    	return filepath;
	    }
	    return null;
	}
	/**
	 * 点击取消按钮 事件
	 */
	public void click_cancel_action() {
		this.dispose();
	}
}
