package com.mypcr.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;
import com.hidapi.CallbackDeviceChange;
import com.hidapi.DeviceChange;
import com.hidapi.DeviceConstant;
import com.hidapi.HidClassLoader;
import com.mypcr.beans.Action;
//import com.mypcr.bootloader.BootLoader;
import com.mypcr.constant.Constants;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.PCR_Task;
import com.mypcr.handler.Handler;
//import com.mypcr.server.parser.ServerParser;
import com.mypcr.timer.GoTimer;
import com.mypcr.timer.NopTimer;
import com.mypcr.tools.Resolution;

/**
 * ó���� ��Ÿ���� UI�� ���� Ŭ�����̴�.
 * JFrame�� ��ӹް� {@link Main} Ŭ�������� ȣ���Ͽ� ����ȴ�.
 * @author YJ
 *
 */
public class MainUI extends JFrame implements Handler, DeviceChange, KeyListener
{
	/**
	 * JFrame�� �⺻ serialVersionUID �� ����.(������ ���������ϴ�.)
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Device ����Ʈ, Device ���� ���� ������ ����ϴ� ��ü
	 */
	private HIDManager m_Manager = null;
	/**
	 * {@link HIDManager} �� OpenById �Լ��� ���� ��ġ�� ����Ǹ�, ����� ��ġ�� �����Ѵ�.
	 * �� ��ü�� ��ġ�� Write, Read ����� �����ϰ� ��ſ��� Blocking, Non-Blocking ó���� �����Ѵ�.
	 * @see HIDManager#openById(int, int, String)
	 * @see HIDManager#openByPath(String)
	 */
	private HIDDevice m_Device = null;
	/**
	 * ����� �ϰ��� �ϴ� ��ġ�� ������ ��ȭ�� �ǽð����� üũ�Ͽ� �˷��ִ� �ݹ��Լ� ������ �ϴ� Ŭ�����̴�.
	 * ���°� �ٲ� ��쿡 ������ �ޱ� ���ؼ��� {@link DeviceChange} �������̽� �ν��Ͻ��� �Ű������� �Ѱ���� �Ѵ�.
	 * Connected, Disconnect ���¸� �޾ƿ� �� �ִ�.
	 * @see DeviceChange#OnMessage(int, Object)
	 */
	private CallbackDeviceChange m_Callback_DeviceChange = null;
	/**
	 * Frame�� ������ҵ��� ��� �ִ� Swing Panel �̴�.
	 * �� Panel �� add �Լ��� �̿��Ͽ� Swing ������Ʈ���� ���ϸ� Frame�� ǥ�õȴ�.
	 */
	private JPanel m_Panel = null;
	/**
	 * �о�� �������� ���� �̸�, ���� �ð� ǥ���ϴ� �κ��� �����ϴ� UI�̴�.
	 * @see ProtocolText
	 */
	private ProtocolText m_ProtocolText = null;
	/**
	 * ���� �������(�ø����ȣ), Chamber �µ�, LID Heater �µ��� ǥ���ϰ�, Preheat ���� ���� �ϴ� UI �̴�.
	 * @see StatusText
	 */
	private StatusText m_PCRStatusText = null;
	/**
	 * �������� ���Ϸκ��� �о�� ���������� List Control�� �����ϴ� UI�̴�.
	 * @see ProtocolList
	 */
	private ProtocolList m_ProtocolList = null;
	/**
	 * Start, Stop, Read Protocol ��ư�� ���� UI�̴�.
	 * @see ButtonUI
	 */
	private ButtonUI m_ButtonUI = null;

	private JTextField m_LidText = null;
	/**
	 * Protocol List�� ����� Action���� �����ϴ� �迭�̴�.
	 * Action�� Label, Temp, Time, Remaining Time ���� ������ �ִ�.
	 * @see Action
	 */
	private Action[] m_ActionList = null;
	/**
	 * PCR �� �����ϱ� ���� ��ɵ��� ������ �ִ� ��ü�̴�.
	 * ��ü Ŭ�������� ���� �߿��� ������ �Ѵ�.
	 * @see PCR_Task
	 */
	private PCR_Task m_PCRTask = null;
	/**
	 * PCR�� ���� ���¸� ��Ÿ���� �÷���.
	 * ����Ǹ� true, ������ �����Ǹ� false.
	 */
	private boolean IsConnected = false;
	/**
	 * Protocol ������ �о����� Ȯ���ϴ� �÷���.
	 * �о����� true, ���� ���� ���ų� ������ ����� false.
	 */
	private boolean IsProtocolRead = false;
	/**
	 * ������ ������ �ִ��� Ȯ���ϴ� �÷���.
	 * ������ ���� ������ true, ������ false.
	 * ������ ���� ������ ó�� ������ ���� �Ǳ� ������, ó�� ������ ���� �Ǳ� ������
	 * true�� �Ǵ� ���� ���α׷��� ó�� ������ ��츸 �ش��Ѵ�.
	 * PCR�� ���� ���¿��� ������ ���� ���� ��쿡 PCR�� �������̶�� PCR�� ���������� �о����
	 */
	public boolean IsNoStop = true;
	
	private String currentVersion = null;
	
	// LED Control 
	private JLabel ledBlue, ledRed, ledGreen;
	private URL url_blueOff = getClass().getClassLoader().getResource("ledBLow.png");
	private URL url_blueOn = getClass().getClassLoader().getResource("ledBHigh.png");
	private URL url_greenOff = getClass().getClassLoader().getResource("ledGLow.png");
	private URL url_greenOn = getClass().getClassLoader().getResource("ledGHigh.png");
	private URL url_redOff = getClass().getClassLoader().getResource("ledRLow.png");
	private URL url_redOn = getClass().getClassLoader().getResource("ledRHigh.png");
	private ImageIcon icon_blueOff, icon_blueOn, icon_greenOff, icon_greenOn, icon_redOn, icon_redOff;
	
	public void bLEDOn(){
		ledBlue.setIcon(icon_blueOn);
	}
	
	public void rLEDOn(){
		ledRed.setIcon(icon_redOn);
	}

	public void gLEDOn(){
		ledGreen.setIcon(icon_greenOn);
	}
	
	public void bLEDOff(){
		ledBlue.setIcon(icon_blueOff);
	}
	
	public void rLEDOff(){
		ledRed.setIcon(icon_redOff);
	}

	public void gLEDOff(){
		ledGreen.setIcon(icon_greenOff);
	}
	
	/**
	 * MainUI �⺻ �������̴�.
	 * private �������� �����ڸ� ȣ���� �� ������ �Ǿ� �ְ�, getInstance() �Լ��� ���Ͽ� �ν��Ͻ��� ������ �� �ִ�.(Singleton ���).
	 * ���������� init() �Լ��� ȣ���Ͽ� UI�� �ʱ�ȭ ��Ų��.
	 */
	private String serialNumber = null;
	
	public MainUI()
	{
		init();
	}
	/**
	 * UI�� �ʱ�ȭ �ϰų�, ��ü���� �ʱ�ȭ �ϴ� ������ �Ѵ�.
	 * MainUI() �����ڿ����� ȣ���� �� �ִ�. �� �ѹ��� ȣ��ȴ�.
	 */
	private void init()
	{
		// �������� ũ�� ����
		setBounds((Resolution.X * 2/5), Resolution.Y/4 ,UIConstant.MYPCR_WIDTH ,UIConstant.MYPCR_HEIGHT);
		// Ÿ��Ʋ ����
		setTitle("MyPCR version 3.2");

		// ����� ���α׷� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// �ִ�ȭ ����
		setResizable(false);
		
		// title icon ����
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());

		// ���� ������Ʈ�� ������� Panel
		// ���̾ƿ��� ������ǥ�� ����ϱ� ���� null�� ����
		m_Panel = new JPanel();
		m_Panel.setLayout(null);
		m_Panel.setBackground(UIConstant.BACKGROUND_COLOR);

		// 3���� GroupBox�� title
		String[] titles = { "Serial Number", "CHAMBER", "LID HEATER" };

		/** ������Ʈ ���� **/
		m_ProtocolText = new ProtocolText();
		m_PCRStatusText = StatusText.getInstance(UIConstant.GROUP_SIZE, titles);
		m_ProtocolList = ProtocolList.getInstance();
		m_ButtonUI = ButtonUI.getInstance( this );
		m_LidText = new JTextField();
		m_LidText.setLayout(null);
		m_LidText.setBounds(310, 55, 40, 20);
		m_LidText.setText("104");
		m_LidText.addKeyListener(this);
		
		// �ΰ� �߰�
		JLabel labelLogo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("logo.jpg")));
		labelLogo.setBounds(100, 385, 182, 37);
		
		// for bootloader mode
		labelLogo.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				if( e.getClickCount() == 3 && currentVersion != null ){
					String res = JOptionPane.showInputDialog(null, "Please input admin password for bootloader", "Admin Mode(Firmware V" + currentVersion + ")", JOptionPane.OK_CANCEL_OPTION);
					
					if( res != null ){
						if( res.equals(Constants.ADMIN_PASSWORD) ){
							if( m_ButtonUI.isEnable(ButtonUI.BUTTON_START) )
								OnHandleMessage(MESSAGE_STOP_PCR, null);
							Thread tempThread = new Thread(){
								public void run(){
									try {
										Thread.sleep(1000);
										OnMessage(DISCONNECTED, null, 0);
										Thread.sleep(1000);
										m_Device.write( m_PCRTask.m_TxAction.Tx_BootLoader() );
									}catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							};
							tempThread.start();
						}else
							JOptionPane.showMessageDialog(null, "Wrong password!", "Admin Mode", JOptionPane.WARNING_MESSAGE);
					}
						
				}
			}
		});

		// LED added
		icon_blueOff = new ImageIcon(url_blueOff); 
		icon_blueOn  = new ImageIcon(url_blueOn);
		icon_greenOff = new ImageIcon(url_greenOff);
		icon_greenOn = new ImageIcon(url_greenOn);
		icon_redOff = new ImageIcon(url_redOff);
		icon_redOn = new ImageIcon(url_redOn);
		
		ledBlue = new JLabel(icon_blueOff);
		ledBlue.setBounds(310, 1, 22, 29);
		ledRed = new JLabel(icon_redOff);
		ledRed.setBounds(332, 1, 22, 29);
		ledGreen = new JLabel(icon_greenOff);
		ledGreen.setBounds(354, 1, 22, 29);

		m_Panel.add(m_ProtocolText);
		m_Panel.add(m_PCRStatusText);
		m_Panel.add(m_ProtocolList.getPane());
		m_Panel.add(m_ButtonUI.getPanel());
		m_Panel.add(m_LidText);

		// 150509 logo and led added
		m_Panel.add(labelLogo);
		m_Panel.add(ledBlue);
		m_Panel.add(ledGreen);
		m_Panel.add(ledRed);
		/** ������Ʈ ���� **/

		// �ǳ��� ���� �����ӿ� ����
		add(m_Panel);

		// 150507 ȭ�鿡 UI �� ���� ���� ��ġ Ȯ���� ���� �ϱ� ���� ó��
		// ȭ�鿡 ���̵���
		// setVisible(true);

		// Device ���� üũ�� �ݹ� �Լ� ����
		try
		{
			// DeviceManager �ν��Ͻ� ����
			m_Manager = HIDManager.getInstance();
			// Device ���� ���¸� ǥ�����ִ� �ݹ��Լ� ���
			m_Callback_DeviceChange = CallbackDeviceChange.getInstance(m_Manager, this);
			m_Callback_DeviceChange.setDaemon(true);
			m_Callback_DeviceChange.start();
		}catch(IOException e)
		{
			e.printStackTrace();
		}

		// MyPCR ���� ����� ��� �ִ� ��ü�� �ν��Ͻ��� ����
		m_PCRTask = PCR_Task.getInstance(this);
	}

	/**
	 * main �Լ����� UI�� ���� ���� �� ó�� ȣ���ϴ� �Լ�.
	 * Singleton ����� getInstance�� ���� �Լ��̴�.
	 * ����� ���� Run�� �� ��︮�� ������ Run���� ���Ͽ���.
	 * @return MainUI�� instance ���� �����Ѵ�.
	 */
	
	// 150507 yj ������ singleton ��� ����
	public void Run()
	{
		setVisible(true);
	}
	
	/**
	 * ProtocolList ��ü�� �����Ѵ�.
	 * @see ProtocolList
	 */
	public ProtocolList getProtocolList()
	{
		return m_ProtocolList;
	}
	/**
	 * ProtocolText ��ü�� �����Ѵ�.
	 * @see ProtocolText
	 */
	public ProtocolText getProtocolText()
	{
		return m_ProtocolText;
	}
	/**
	 * StatusText ��ü�� �����Ѵ�.
	 * @see StatusText
	 */
	public StatusText getStatusText()
	{
		return m_PCRStatusText;
	}
	/**
	 * ButtonUI ��ü�� �����Ѵ�.
	 * @see ButtonUI
	 */
	public ButtonUI getButtonUI()
	{
		return m_ButtonUI;
	}
	/**
	 * �������� ����Ʈ�� ǥ�õ� ���������� ���� Action �迭�� �����Ѵ�.
	 */
	public Action[] getActionList()
	{
		return m_ActionList;
	}
	/**
	 * HIDManager �� ���Ͽ� ������ HIDDevice ��ü�� �����Ѵ�.
	 * ����Ǿ� ���� ��(Not null), �ٸ� Ŭ�������� �� �Լ��� ���Ͽ� PCR ��ġ�� ����� �� �� �ִ�.
	 * @see HIDManager
	 */
	public HIDDevice getDevice()
	{
		return m_Device;
	}
	/**
	 * PCR_Task ��ü�� �����Ѵ�.
	 * @see PCR_Task
	 */
	public PCR_Task getPCR_Task()
	{
		return m_PCRTask;
	}
	/**
	 * �� �����κ��� �߿��� ������ üũ�ϰ�, �߿��� ������Ʈ�� �� �� �ֵ��� �ϴ� �Լ�.
	 * PCR�� ������ �� ���¿��� �� ó���� �ѹ��� üũ�մϴ�.
	 * �߿��� ������ �������� �޾ƿ� ��, �� ������ PCR ������ ������ Ȯ�� �� �ٸ��� �������κ��� ���ο� �߿�� �޾Ƽ�
	 * PCR�� ���ε� ��ŵ�ϴ�. ���ε尡 �Ϸ�Ǹ� PCR�� Reset ��ŵ�ϴ�.
	 * �� ������ ���������� ������ �� ���ٴ� �޽����� ���ɴϴ�.
	 * �� ����� ����� �����Ϸ��� �ٸ� BootLoader ���α׷��� �����Ͻʽÿ�.
	 * @see BootLoader
	 */
	
	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
		m_Callback_DeviceChange.setSerialNumber(serialNumber);
	}
	
	public String getSerialNumber(){
		return serialNumber;
	}
	
	private void connectToDevice(int firmwareVersion){
		try
		{
			if( m_Device != null )
			{
				m_Device.close();
				m_Device = null;
			}
			
			m_Device = m_Manager.openById(DeviceConstant.VENDOR_ID, DeviceConstant.PRODUCT_ID, serialNumber);
			if( m_Device != null )
			{
				IsConnected = true;
				m_Device.disableBlocking();
				m_ButtonUI.setEnable(ButtonUI.BUTTON_START, true);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, true);
				m_PCRStatusText.setMessage(m_Device.getSerialNumberString(), 0);
				m_PCRTask.setTimer(NopTimer.TIMER_NUMBER);
				
				setTitle(m_Device.getSerialNumberString());
				setSerialNumber(m_Device.getSerialNumberString());
				
				gLEDOn();
				
				if( firmwareVersion == 0 )
					currentVersion = null;
				else
					currentVersion = ((firmwareVersion >> 8)&0xff) + "." + (firmwareVersion&0xff); 
				
				// 150506 YJ Firmware check disable
				// UpdateFromServer();
			}
			else
			{
				// ���� ���� ó��
				System.out.println("Fatal Error!");
				gLEDOff();
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Handler�� implements �� �޼ҵ�. �ٸ� Ŭ�����κ��� ���� Message�� �޴´�.
	 * @param MessageType int �޽��� ����
	 * @param data Object �޽����� ���� ���� ������
	 */
	@Override
	public void OnHandleMessage(int MessageType, Object data)
	{
		switch( MessageType )
		{
			// Protocol �� �о��ٴ� �޽��� �� ���
			case MESSAGE_READ_PROTOCOL:
				// Protocol ����Ʈ�� ����ִ�.
				Action[] actions = (Action[])data;
				// null �� ���� �������� ������ �߸� �ҷ��� ���
				if( actions == null )
					JOptionPane.showMessageDialog(this, "�ùٸ��� ���� Protocol �����Դϴ�.");
				else
				{
					// �÷��� �ʱ�ȭ
					IsProtocolRead = false;
					// action�� 0��° �迭�� ���̺��� null�� ���� �߸��� ������ ���.
					if( actions[0].getLabel() == null )
						return;
					// List�� ����ش�.
					m_ProtocolList.ResetContent();
					// ���� �������� ��ŭ insert ���ش�.
					for( Action action : actions )
						m_ProtocolList.InsertData(action);
					// �޾ƿ� �������ݵ��� ��������� �������ش�.
					m_ActionList = actions;
					// �о�� �������� ������ �̸��� ��ܿ� ǥ���Ѵ�.
					m_ProtocolText.setProtocolText(actions[0].getProtocolName());
					// �о����� �÷��� true
					IsProtocolRead = true;
				}
				break;
				// ��ŸƮ ��ư�� ������ ��.
			case MESSAGE_START_PCR:
				if( IsConnected )
				{
					// �ҷ��� �������� ������ ���� ��쿡�� ����
					if( IsProtocolRead )
					{
						rLEDOff();
						
						m_PCRTask.PCR_Start(m_LidText.getText());
						m_PCRTask.setTimer(GoTimer.TIMER_NUMBER);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_START, false);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, true);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, false);
					}
					else
					{
						JOptionPane.showMessageDialog(this, "�ҷ��� �������� ������ �����ϴ�.");
					}
				}
				break;
				// ��ž ��ư�� ������ ��
			case MESSAGE_STOP_PCR:
				if( IsConnected )
				{
					// ���� ó��
					m_PCRTask.Stop_PCR();
					m_ButtonUI.setEnable(ButtonUI.BUTTON_START, true);
					m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
					m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, true);
					// �÷��� ����
					IsNoStop = false;
					// Stop ������ �˸��� ���α׷��� ��
					final ProgressDialog dialog = new ProgressDialog(this, "Stoping this device...", 10);
					// ��޸��� ����� ���� ��� ��ȭ���ڸ� ���� ���� ������ ����
					Thread tempThread = new Thread()
					{
						public void run()
						{
							dialog.setModal(true);
							dialog.setVisible(true);
						}
					};
					tempThread.start();

					// 0.2�� ���� ���α׷����ٰ� 1ĭ�� �����ϵ��� 2���� ���� �ð��� �д�. ( �������� ���Ḧ ���� )
					Thread tempThread2 = new Thread()
					{
						public void run()
						{
							for(int i=1; i<=10; i++)
							{
								dialog.setProgressValue(i);
								try
								{
									Thread.sleep(200);
								}catch(InterruptedException e)
								{
									e.printStackTrace();
								}
							}

							dialog.setVisible(false);

							m_PCRTask.PCR_End();
						}
					};
					tempThread2.start();
				}
				break;
				// Start ����, �������ݵ��� ���� ���� ���� ��쿡 NOP Ÿ�̸Ӹ� ���� ��Ű�� ���� �޽���
			case MESSAGE_TASK_WRITE_END:
				try
				{
					Thread.sleep(300);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				// NopTimer ���۽�Ų��.
				m_PCRTask.setTimer(NopTimer.TIMER_NUMBER);
				break;
		}
	}

	@Override
	public void OnMessage(int MessageType, Object data, int firmwareVersion)
	{
		switch( MessageType )
		{
			case CONNECTED:
				String count = (String)data;
				if( count.equals("1") )
					connectToDevice(firmwareVersion);
				break;
			case DISCONNECTED:
				gLEDOff();
				IsConnected = false;
				m_ButtonUI.setEnable(ButtonUI.BUTTON_START, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, false);
				m_PCRTask.killTimer(NopTimer.TIMER_NUMBER);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE0, 0);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE1, 1);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE2, 2);
				break;
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// ���ڸ� �޵���
		char c = e.getKeyChar();

		if( !Character.isDigit(c) )
		{
			e.consume();
			return;
		}

		if( m_LidText.getText().length() == 3 )
			e.consume();
	}
}
