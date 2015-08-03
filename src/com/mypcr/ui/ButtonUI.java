package com.mypcr.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mypcr.beans.Action;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.Functions;
import com.mypcr.handler.Handler;
import com.mypcr.timer.NopTimer;

public class ButtonUI implements ActionListener
{
	private static ButtonUI instance = null;
	public static final int BUTTON_START	=	0x00;
	public static final int BUTTON_STOP		=	0x01;
	public static final int BUTTON_PROTOCOL	=	0x02;
	
	private static JFrame m_Parent = null;
	public JPanel m_Panel = null;
	private JButton m_Button_Start = null;
	private JButton m_Button_Stop = null;
	private JButton m_Button_ReadProtocol = null;
	//private JButton m_Button_Exit = null;
	
	private ButtonUI()
	{
		// �ǳ��� ����� ũ�� ����
		m_Panel = new JPanel();
		m_Panel.setBackground(UIConstant.BACKGROUND_COLOR);
		m_Panel.setBounds(20, 340, UIConstant.MYPCR_WIDTH - 50, 40);
		
		// ��ư ���� �� ĸ�� �޾��ֱ�
		m_Button_Start = new JButton(UIConstant.BUTTON_START_TEXT);
		m_Button_Stop = new JButton(UIConstant.BUTTON_STOP_TEXT);
		m_Button_ReadProtocol = new JButton(UIConstant.BUTTON_READPROTOCOL_TEXT);
	//	m_Button_Exit = new JButton(UIConstant.BUTTON_EXIT_TEXT);
		
		// ��ư �̺�Ʈ ������ ���
		m_Button_Start.addActionListener(this);
		m_Button_Stop.addActionListener(this);
		m_Button_ReadProtocol.addActionListener(this);
	//	m_Button_Exit.addActionListener(this);
		
		// �ǳڿ� ��ư�� �־��ش�. 
		m_Panel.add(m_Button_Start);
		m_Panel.add(m_Button_Stop);
		m_Panel.add(m_Button_ReadProtocol);
	//	m_Panel.add(m_Button_Exit);
		
		// ��ư ��Ȱ��ȭ �ʱ�ȭ ó��
		setEnable(BUTTON_START, false);
		setEnable(BUTTON_STOP, false);
		setEnable(BUTTON_PROTOCOL, false);
	}
	
	public static ButtonUI getInstance( JFrame parent )
	{
		m_Parent = parent;
		if( instance == null )
			instance = new ButtonUI();
		return instance;
	}
	
	public JPanel getPanel()
	{
		return m_Panel;
	}
	
	public void setEnable(int button, boolean bool)
	{
		switch( button )
		{
			case BUTTON_START:
				m_Button_Start.setEnabled(bool);
				break;
			case BUTTON_STOP:
				m_Button_Stop.setEnabled(bool);
				break;
			case BUTTON_PROTOCOL:
				m_Button_ReadProtocol.setEnabled(bool);
				break;
		}
	}
	
	public boolean isEnable(int button)
	{
		switch( button )
		{
			case BUTTON_START:
				return m_Button_Start.isEnabled();
			case BUTTON_STOP:
				return m_Button_Stop.isEnabled();
			case BUTTON_PROTOCOL:
				return m_Button_ReadProtocol.isEnabled();
		}
		
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object event = e.getSource();
		
		if( event == m_Button_Start )
		{
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_START_PCR, null);
		}
		else if( event == m_Button_Stop )
		{
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_STOP_PCR, null);
		}
		else if( event == m_Button_ReadProtocol )
		{
			Action[] actions = null;
			actions = Functions.ReadProtocolbyDialog( m_Parent );
			// Parent �� �޽����� ������, �޽����� ��ȿ�� ���δ� Parent�� �ñ��.
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_READ_PROTOCOL, actions);
		}
		//else if( event == m_Button_Exit )
		//{
			
		//}
	}
}
