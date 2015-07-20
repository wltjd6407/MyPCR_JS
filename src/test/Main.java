package test;

import java.awt.event.ActionListener;
import java.io.IOException;

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
	
	
	
	

	public static void main(String[] args) 
	{
		Main m = new Main();
		while(true);
	}

}
