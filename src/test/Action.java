package test;

public class Action 
{
	String label, temp, time;
	
	public Action()
	{
		
	}
	public Action(String label, String temp, String time)
	{
		this.label = label;
		this.temp = temp;
		this.time = time;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
