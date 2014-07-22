package com.example.tracing.paint;

import java.util.ArrayList;

import android.graphics.Color;

public class paintPath {

	private int opacity;
	private int intColor;
	
	ArrayList<paintPosition> list ;
	
	public paintPath(){
		
		opacity = 255;
		intColor = Color.BLACK;
		list = new ArrayList<paintPosition>();
	}
	
	public ArrayList<paintPosition> getList() {
		return list;
	}

	public void addPosition(paintPosition p){
		list.add(p);
		
	}
	
	
	public int getOpacity() {
		return opacity;
	}
	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}
	public int getIntColor() {
		return intColor;
	}
	public void setIntColor(int intColor) {
		this.intColor = intColor;
	}
	
	
	
	

	
}
