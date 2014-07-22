package com.example.tracing.paint;

import java.util.ArrayList;


public class paintStack {
	
	private ArrayList<paintPath> list ; 
	
	public ArrayList<paintPath> getList() {
		return list;
	}


	int now;

	public paintStack() {
		list = new ArrayList<paintPath>(); 
		now = 0;
	}
	
	public void top(){
		if (now>0) now--;
		
	}
	
	public paintPath pop(){
		paintPath p = null;
		if (now>0) 
			{
			 p = list.get(now);
			 list.remove(now);
			}
		
		top();
		
		return p;
				
	}
	
	public void Push(paintPath p){
		now++;
		list.add(p);
	}
	
	
	
	
}
