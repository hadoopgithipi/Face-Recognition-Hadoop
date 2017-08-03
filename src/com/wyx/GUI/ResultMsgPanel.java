package com.wyx.GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wyx.myinterface.PanelContentListener;
import com.wyx.util.PropUtil;

public class ResultMsgPanel extends JPanel implements PanelContentListener{
	
	private static final long serialVersionUID = 1L;

	public ResultMsgPanel(){
		this.setPreferredSize(new Dimension(CustomJFrame.CUSTOM_WIDTH/2, 0));
		this.setBorder(BorderFactory.createTitledBorder("信息区"));
		this.setLayout(new GridLayout(5,1));
//		this.setBackground(Color.GREEN);
	}

	public void showFace() {}
	/**
	 * 显示检测信息
	 */
	public void showMessage() {
		this.removeAll();
		this.updateUI();
		JLabel label = new JLabel("检测的视频文件为： "+VideoLoadPanel.path.toString());
		label.setToolTipText(VideoLoadPanel.path.toString());
		this.add(label);
		
		label = new JLabel("正确识别出的人脸图像： "+getFaceFramesNum());
		this.add(label);
		
		String startTime = FunctionPanel.startTime;
		label = new JLabel("开始时间： "+startTime);
		this.add(label);
		
		String endTime = FunctionPanel.endTime;
		label = new JLabel("结束时间： "+endTime);
		this.add(label);
		
		label = new JLabel("检索时长： "+getUseTime(startTime, endTime));
		this.add(label);
		
		this.updateUI();

	}
	/**
	 * 得到人脸图像个数
	 * @return
	 */
	public int getFaceFramesNum(){
		String path = PropUtil.getInstance().getProperty("local_faceFramesPath");
		File dir = new File(path);
		if (dir.exists()) {
			return dir.listFiles().length;			
		}else {
			//根据人名未查询到相关的图像
			return 0;
		}
	}
	/**
	 * 获取总共用时
	 * @param start 开始时间
	 * @param end   结束时间
	 * @return		用时
	 */
	public String getUseTime(String start,String end){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		try {
			Date startDate = sdf.parse(start);
			Date endDate = sdf.parse(end);
			long useTime = (endDate.getTime()-startDate.getTime())/1000;
			return useTime+"秒";

		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("转换异常");
		}
	}
}