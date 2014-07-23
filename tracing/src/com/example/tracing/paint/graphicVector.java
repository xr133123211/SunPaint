package com.example.tracing.paint;

public class graphicVector {
	float x;
	float y;
	
	
	public graphicVector(float i,float j) {
		// TODO Auto-generated constructor stub
		x=i;
		y=j;
	}
	
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	
	public graphicVector prep()
	{
		if (x==0) return new graphicVector(1, 0);
		if (y==0) return new graphicVector(0, 1);
		
		float ansX=(float)Math.sqrt(1/((1/(x*x))+(1/(y*y))))/x;
		float ansY=(float)Math.sqrt(1/((1/(x*x))+(1/(y*y))))/-y;
		return new graphicVector(ansX, ansY);
	}
	public float length()
	{
		return (float)Math.sqrt(x*x+y*y);
	}
	

}
