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
				System.out.println("연결되");
				statusFlag = true;
			}
			break;
		case DISCONNECTED:
			if( statusFlag )
			{
				System.out.println("연결안됨");
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
				for(int i=1; i<list.size()-2; i++)
				{
					String aclists[] = list.get(i).split("\t");
					actions[lines] = new Action(aclists[0], aclists[1], aclists[2]);
					lines++;
				}
				System.out.println("올바른 프로토콜입니다.");
				
				for(int i=0; i<lines; i++)
				{
					System.out.println(String.format("label=%s, temp=%s, time=%s",actions[i].label, actions[i].temp, actions[i].time));
				}
			}
			else
				System.out.println("잘못된 프로토콜입니다.");
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("파일 없음");
		}
		
	}

}
