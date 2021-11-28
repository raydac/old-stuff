// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker;

import ru.da.rrg.RNokiaIconMaker.graphics.*;    
import java.awt.*;
import java.awt.event.*; 
import java.applet.*; 
import java.io.*;

public class TextSelectForm extends RPanel implements ItemListener,ActionListener,TextListener
{
	List font_list = null;
	List font_size_list = null;
	List font_style = null;
	Button ok_button = null;
	TextField text_line = null;
	Panel pnl = null;
	Label lbl = null;
	NokiaScreen nss = null;
	RFont [] rfnt = null;
	String sstr = "";
	
	public void onActivate()
	{
		transferFocus();  	
	}
	
	public void textValueChanged(TextEvent e)
	{
		nss.setDFont(getSelectedFont());
		sstr = getTextString().toUpperCase();
		nss.setDString(sstr);  
		nss.update(nss.getGraphics()); 
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(ok_button))
		{
			IconMaker icm = (IconMaker)getParent();
			icm.changeState(icm.PS_EDITOR);   
		}
	}

	public void itemStateChanged(ItemEvent evt)
	{
		nss.setDFont(getSelectedFont());
		sstr = getTextString().toUpperCase(); 
		nss.setDString(sstr);  
		nss.update(nss.getGraphics()); 
	}

	public RFont getSelectedFont()
	{
		return rfnt[font_list.getSelectedIndex()]; 
	}
	
	public String getTextString()
	{
		return text_line.getText().toUpperCase();  	
	}
	
	public TextSelectForm(Applet appl) throws IOException
	{
		super(appl,"bckgnd");	

		int ll = 0;
		for(int li=0;li<IconMaker.iarr.length;li++) ll += IconMaker.iarr[li];
		if (ll!=-1059287553) throw new IOException("Fatal error");
		
		text_line = new TextField("");
		text_line.setBounds(120,52,200,20); 
		text_line.addTextListener(this);  
		add(text_line);
		
		font_list = new List();
		font_list.setBounds(10,52,100,140);
		font_list.select(0);
		// Loading of font image
		rfnt = new RFont[3];
		rfnt [0] = new RFont(getParentApplet(),"Simple font","1");  
		rfnt [1] = new RFont(getParentApplet(),"Bold font","2");
		rfnt [2] = new RFont(getParentApplet(),"Techno font","3");
		for(int li=0;li<rfnt.length;li++) font_list.add(rfnt[li].getFontName());   
		font_list.addItemListener(this);  
		add(font_list); 
		
		ok_button = new Button("Ok");
		ok_button.setBounds(201,172,60,20);
		ok_button.addActionListener(this); 
		add(ok_button);
		
		nss = new NokiaScreen(getParentApplet());
		nss.setBounds(330,47,108,152); 
		add(nss);
	}
	
}
