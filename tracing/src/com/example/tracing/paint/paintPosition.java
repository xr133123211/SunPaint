package com.example.tracing.paint;

import java.util.Calendar;

public class paintPosition {
	
	float x,y;
	Calendar time;
	
	public paintPosition(float a,float b){
		x = a;
		y = b;
		time = Calendar.getInstance();
	}

	public float getX() {
		return x;
	}

	
	public float getY() {
		return y;
	}

	
	public Calendar getTime() {
		return time;
	}


}
