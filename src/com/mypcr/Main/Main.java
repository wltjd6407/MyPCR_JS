package com.mypcr.Main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.plaf.ButtonUI;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;
import com.hidapi.CallbackDeviceChange;
import com.hidapi.DeviceChange;
import com.hidapi.DeviceConstant;
import com.hidapi.HidClassLoader;
import com.mypcr.beans.State;
import com.mypcr.beans.TxAction;
import com.mypcr.beans.RxAction;
import com.mypcr.ui.MainUI;

public class Main
{
	
	static
	{
		HidClassLoader.LoadLibrary();
	}
	public static void main(String args[])
	{
		MainUI main = new MainUI();
		main.Run();
		
	}
}
