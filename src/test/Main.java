package test;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.codeminders.hidapi.HIDManager;
import com.hidapi.CallbackDeviceChange;
import com.hidapi.DeviceChange;
import com.hidapi.HidClassLoader;

public class Main implements DeviceChange 
{
	static
	{
		if( !HidClassLoader.LoadLibrary() )
		{
			JOptionPane.showMessageDialog(null, "This os is not supported");
		}
	}
	private CallbackDeviceChange deviceChange = null;
	private HIDManager manager = null;
	public Main()
	{
		try {
			manager = HIDManager.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deviceChange = CallbackDeviceChange.getInstance(manager, this);
		deviceChange.setSerialNumber("MyPCR333333");
		deviceChange.start();
	}
	
	private boolean statusFlag = true;
	public void OnMessage(int MessageType, Object data, int firmwareVersion) {
		String count = (String)data;
		
		switch (MessageType) {
		case CONNECTED:
			if(count.equals("1"))
			{
				System.out.println("�����");
				statusFlag = true;
			}
			break;
		case DISCONNECTED:
			if( statusFlag )
			{
				System.out.println("����ȵ�");
				statusFlag = !statusFlag;
			}
			break;
		}		
	}
	
	
	
	

	public static void main(String[] args) throws FileNotFoundException 
	{
		/*
		Main m = new Main();
		while(true);
		*/
	
		Action[] actions = new Action[20];
		ArrayList<String> list = new ArrayList<String>();
		int lines = 0;
		String path = "test.txt";
		BufferedReader in ;
		try {
			in = new BufferedReader(new FileReader(path));
			String line = null;
			while( (line = in.readLine()) != null)
			{
				list.add(line);
				
			}
			in.close();
			String first = list.get(0);
			String last = list.get(list.size()-1);
			if(first.contains("%PCR%") && last.contains("%END"))
			{
				for(int i=1; i<=list.size()-2; i++)
				{
					String aclists[] = list.get(i).split("\t");
					actions[lines] = new Action(aclists[0], aclists[1], aclists[2]);
					lines++;
				}
				System.out.println("�ùٸ� ���������Դϴ�.");
				
				for(int i=0; i<lines; i++)
				{
					System.out.println(String.format("label=%s, temp=%s, time=%s",actions[i].label, actions[i].temp, actions[i].time));
				}
			}
			else
				System.out.println("�߸��� ���������Դϴ�.");
			
			int time = 0;
			for(int i=0; i<lines; i++)
			{
				time += Integer.parseInt(actions[i].time);
			}
			calcTime(time);
			runProtocol(actions, lines);
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("���� ����");
		}
		
	}

	public static void calcTime(int time)
	{
		int s = 0, m = 0;
		for(int i=0; i<time; i++)
		{
			s++;
			if( s == 60 )
			{
				s = 0;
				m++;
			}
		}
		System.out.println("======================================");
		System.out.println(String.format("%02d:%02d", m, s));
		System.out.println("======================================");
	}
	
	public static void runProtocol(Action[] actions, int lines)
	{
		int fl = 1;
		System.out.println("======================================");
		for(int i=0; i<lines; i++)
		{
			
			if(!actions[i].label.equals("GOTO"))
			{
				System.out.println(String.format("%d - %s", fl++, actions[i].label));
			}
			else
			{
				int no = Integer.parseInt(actions[i].temp);
				int cnt = Integer.parseInt(actions[i].time);
				for(int k=0; k<cnt; k++)
				{
					for(int j=no-1; j<i; j++)
					{
						System.out.println(String.format("%d - %s", fl++, actions[j].label));
					}
				}
			}
		}
		System.out.println("======================================");
	}
}
