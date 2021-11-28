// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;

public class RActionEvent
{
	int id;
	RComponent compo;
	
	public int getID()
	{
		return id;
	}
	
	public RComponent getComponent()
	{
		return compo;
	}
	
	public RActionEvent(RComponent cmp,int ID)
	{
		id = ID;
		compo = cmp;
	}
	
}
