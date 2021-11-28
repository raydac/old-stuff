// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

public interface RMPSListener 
{
	public void mps_mouseDown(boolean lb,int x,int y);
	public void mps_mouseUp(int x,int y);
	public void mps_mouseDrag(int x,int y);
}
