package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.wyx.myinterface.PanelContentListener;
import com.wyx.util.PropUtil;

public class ShowFacePanel extends JPanel implements PanelContentListener{
	
	private static final long serialVersionUID = 1L;
	private static final String LOCAL_FACE_FRAMES_PATH;
	private static PropUtil prop = PropUtil.getInstance();
	private static final int RECO_IMAGE_WIDTH;
	private static final int RECO_IMAGE_HEIGHT;
//	private String localFaceFramesPath = "/home/hadoop/12/";
	private JScrollPane scrollPane ;
	
	private JPanel tmp = null;
//	private Timer timer = new Timer(2000, new ActionListener() {
//		
//		public void actionPerformed(ActionEvent e) {
//			System.out.println("时间组件触发。");
//			initTmpPanel();
//		}
//	});
	static {
		RECO_IMAGE_WIDTH = Integer.parseInt(prop.getProperty("RECO_IMAGE_WIDTH"));
		RECO_IMAGE_HEIGHT = Integer.parseInt(prop.getProperty("RECO_IMAGE_HEIGHT"));
		LOCAL_FACE_FRAMES_PATH = prop.getProperty("local_faceFramesPath");
	}
	
	public ShowFacePanel(){
		this.setPreferredSize(new Dimension(0, CustomJFrame.CUSTOM_HEIGHT/2));
//		this.setBackground(Color.RED);
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new BorderLayout());
		
		tmp = new JPanel();
		tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
//		tmp.setPreferredSize(new Dimension(0,CustomJFrame.CUSTOM_HEIGHT/2+100));
		//initTmpPanel();
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tmp);
		scrollPane.setPreferredSize(new Dimension(0, CustomJFrame.CUSTOM_HEIGHT/2));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		this.add(scrollPane,BorderLayout.CENTER);
		
	}
	/**
	 * 初始化面板
	 */
	public void initTmpPanel(){
		tmp.removeAll();
		tmp.repaint();
		tmp.updateUI();
//		scrollPane.updateUI();
		int height = 50;
		File dir = new File(LOCAL_FACE_FRAMES_PATH);
		if (dir.exists() && dir.isDirectory()) {
			File [] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getAbsolutePath();
				ImageIcon icon = new ImageIcon(filename);
				icon.setImage(icon.getImage().getScaledInstance(RECO_IMAGE_WIDTH,RECO_IMAGE_HEIGHT,Image.SCALE_DEFAULT)); 
				int num = CustomJFrame.CUSTOM_WIDTH/icon.getIconWidth();
				JLabel label = new JLabel(icon);
				label.setName(filename);
				label.setToolTipText("单击可查看大图");
				label.addMouseListener(new MouseAdapter() {
					
					@Override
					public void mouseClicked(MouseEvent e) {
						JLabel tmp = (JLabel)e.getSource();
						showImageByDialog(tmp.getName());
					}
				});
				tmp.add(label);
				if (i%num==0) {
					height+=icon.getIconHeight();
				}
			}
			tmp.setPreferredSize(new Dimension(0, height));
			tmp.repaint();
			tmp.updateUI();
			scrollPane.updateUI();
		}
	}
	
	/**
	 * 显示图像的实际大小
	 */
	public void showImageByDialog(String filename){
		ImageIcon icon = new ImageIcon(filename);

		final JDialog dialog = new JDialog();
		dialog.setSize(icon.getIconWidth(), icon.getIconHeight());
		dialog.setTitle("Press ‘Esc’ to Exit");
		dialog.setModal(true);
		dialog.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ESCAPE) {
					dialog.dispose();
				}
			}
		});
		
		JLabel label = new JLabel(icon);
		dialog.add(label);
		
		dialog.setVisible(true);
	}
	/**
	 * 显示检测到的人脸图像
	 */
	public void showFace() {
		initTmpPanel();
	}

	public void showMessage() {
		
	}
}