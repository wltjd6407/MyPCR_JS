package com.mypcr.timer;

import java.io.IOException;
import java.util.TimerTask;

import com.codeminders.hidapi.HIDDevice;
import com.mypcr.beans.Action;
import com.mypcr.beans.State;
import com.mypcr.beans.TxAction;
import com.mypcr.beans.RxAction;
import com.mypcr.handler.Handler;
import com.mypcr.ui.MainUI;
import com.mypcr.ui.ProgressDialog;

public class GoTimer extends TimerTask
{
	public static final int TIMER_DURATION	=	200;
	public static final int TIMER_NUMBER	=	0x01;
	
	private HIDDevice 		m_Device = null;
	private MainUI 	  		m_Handler	= null;
	private TxAction		m_TxAction	= null;
	private RxAction		m_RxAction 	= null;
	private Action[]		m_Actions = null;
	private String			m_preheat = null;
	private int				m_index = 0;
	private int				m_protocol_length = 0;
	ProgressDialog 			m_dialog = null;
	
	public GoTimer(HIDDevice device, Action[] actions, String preheat, MainUI handler)
	{
		m_Device = device;
		m_TxAction = new TxAction();
		m_RxAction = new RxAction();
		m_Handler = handler;
		m_preheat = preheat;
		m_Actions = actions; 
		m_protocol_length = m_Actions.length;
		m_dialog = new ProgressDialog(m_Handler, "PCR Protocol Transmitting...", m_protocol_length);
		Thread TempThread = new Thread()
		{
			public void run()
			{
				m_dialog.setModal(true);
				m_dialog.setVisible(true);
			}
		};
		TempThread.start();
	}

	@Override
	public void run() 
	{
		m_dialog.setProgressValue(m_index);
		
		if( m_index < m_protocol_length )
		{
			try
			{
				
				byte[] readbuffer = new byte[64];
				m_Device.read(readbuffer);
				m_RxAction.set_Info(readbuffer);
				
				int time = (m_RxAction.getTime_H()&0xFF)*256 + (m_RxAction.getTime_L()&0xFF);
				System.out.println(m_index + "===" + m_RxAction.getReqLine());
				System.out.println( m_Actions[m_index].getLabel() + "===" + m_RxAction.getLabel());
				System.out.println(m_RxAction.getTemp() + "===" + Integer.parseInt(m_Actions[m_index].getTemp()));
				System.out.println(time + "===" + Integer.parseInt(m_Actions[m_index].getTime()));
				
				if( m_Actions[m_index].getLabel().equals(m_RxAction.getLabel() + "") && m_index == m_RxAction.getReqLine() 
						&& m_RxAction.getTemp() == Integer.parseInt(m_Actions[m_index].getTemp()) && m_index == m_RxAction.getReqLine()){
					m_index++;
					System.out.println("test");
				}
				else{
					m_Device.write( m_TxAction.Tx_TaskWrite(m_Actions[m_index].getLabel(), m_Actions[m_index].getTemp(), m_Actions[m_index].getTime(), m_preheat, m_index));
					System.out.println("test2");
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				System.out.println("456");
				byte[] readbuffer = new byte[64];
				m_Device.read(readbuffer);
				m_RxAction.set_Info(readbuffer);
				if(m_protocol_length == m_RxAction.getTotal_Action())
				{
					try
					{
						Thread.sleep(10);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					if(m_RxAction.getState() == State.RUN)
					{
						m_Handler.OnHandleMessage(Handler.MESSAGE_TASK_WRITE_END, null);
						this.cancel();
						Thread TempThread = new Thread()
						{
							public void run()
							{
								try
								{
									Thread.sleep(1000);
								}catch(InterruptedException e)
								{
									e.printStackTrace();
								}
								m_dialog.setVisible(false);
							}
						};
						TempThread.start();
					}
					else
						m_Device.write( m_TxAction.Tx_Go() );
				}
				else
					m_Device.write( m_TxAction.Tx_TaskEnd() );
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
