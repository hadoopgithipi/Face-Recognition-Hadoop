package com.wyx.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.wyx.myinterface.PanelContentListener;

/**
 * 控制面板
 * 包括  功能面板及显示检索信息面板
 * @author hadoop
 *
 */
public class ControlPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	//功能面板
	private FunctionPanel functionPanel;
	//显示检索信息面板
	private ResultMsgPanel resultMsgPanel;
	private PanelContentListener listener = null;
	
	/**
	 * 添加监听器
	 * @param listener
	 */
	public void addPanelContentListener(PanelContentListener listener){
		this.listener = listener;
	}
	
	public ControlPanel(){
		this.setPreferredSize(new Dimension(0,CustomJFrame.CUSTOM_HEIGHT/2-60));
//		this.setBackground(Color.BLUE);
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new BorderLayout(5,5));
		
		
		functionPanel = new FunctionPanel();
		this.add(functionPanel,BorderLayout.WEST);
		resultMsgPanel = new ResultMsgPanel();
		this.add(resultMsgPanel,BorderLayout.CENTER);
	}
	
	public void add(){
		functionPanel.addPanelContentListener(listener);
		functionPanel.addPanelContentListener(resultMsgPanel);
	}
}