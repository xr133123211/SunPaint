package com.example.tracing.paint;

import java.util.Calendar;

public class paintPosition {
	
	float x,y;
	float width;
	Calendar time;
	
	public paintPosition(float a,float b){
		x = a;
		y = b;
		time = Calendar.getInstance();
		//System.out.println(time.getTimeInMillis());
	}

	public float getX() {
		return x;
	}

	
	public float getY() {
		return y;
	}

	public void SetXY(float i,float j) {
		x=i;
		y=j;
	}
	
	
	public void setwidth(float w)
	{
		width=w;
	}
	
	public float getwidth()
	{
		return width;
	}
	
	public Calendar getTime() {
		return time;
	}


}
