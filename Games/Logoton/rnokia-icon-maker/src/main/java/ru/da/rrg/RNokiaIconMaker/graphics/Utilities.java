// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.applet.*;   
import java.util.*; 
import java.awt.*;
import java.io.*; 
import java.awt.event.*; 
import java.net.*;
import java.awt.image.*;

public class Utilities 
{
	public static Image changecolor(Image pri,Color ncolor)
	{
		int [] bfr = new int[pri.getWidth(null)*pri.getHeight(null)];
		int bckgcolor = ncolor.getRGB(); 
		int blckcolor = ColorEtaloner.black;
		
		PixelGrabber pxg = new PixelGrabber(pri,0,0,pri.getWidth(null),pri.getHeight(null),bfr,0,pri.getWidth(null));
		try
		{
			pxg.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return null;	
		}
		
		for(int li=0;li<bfr.length;li++)
		{
			if (!Utilities.checkColor(blckcolor,bfr[li])) bfr[li] = bckgcolor; 
		}
		
		Image primage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(pri.getWidth(null),pri.getHeight(null),bfr,0,pri.getWidth(null)));
		return primage;
	}
	
	
	public static void sendURLByteArray(Applet cls,String cmnd,byte[] arr,ActionListener lst) throws IOException
	{
	   try 
	   {
		    URL url = new URL(cls.getDocumentBase(),cmnd);

			/* System.out.println("Connection URL "+url); */
			
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);  
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.setRequestProperty ("Сontent-Transfer-Encoding","8BIT");
			connection.setRequestProperty ("MIME-Version","1.0");
			connection.setRequestProperty ("Content-Type","multipart/mixed");
			connection.setRequestProperty ("Content-length",String.valueOf(arr.length));   
			OutputStream ostr = connection.getOutputStream();

			ActionEvent ae = new ActionEvent(cls,0,"PRGRS"); 
			if (lst!=null) lst.actionPerformed(ae); 
			
			int stp = Math.round((float)arr.length/100f);
			int stp2 = stp*30;
			
			int cll = 0;
			for(int li=0;li<arr.length;li++) 
			{
				ostr.write(arr[li]);
				
				cll+=stp;
				if (cll>=stp2)
				{
					cll=0;
					ae = new ActionEvent(cls,Math.round(li/stp),"PRGRS"); 
					if (lst!=null) lst.actionPerformed(ae);
				}
			}

			DataInputStream inStream = new DataInputStream(connection.getInputStream());
		    
			/* System.out.println("===Server response==="); */
			String inputLine = null;
			while ((inputLine = inStream.readLine()) != null) 
			{
				/* System.out.println(inputLine); */
			}
			ostr.close();
			inStream.close();  
	   }
	    catch (MalformedURLException me) 
		{
			throw new IOException("Mailformed URL");
		} 
   	    catch (NoSuchElementException me) 
		{
			throw new IOException("Error response");
		} 
	}
	
	public static byte[] getURLResourceAsByteArray(Applet cls,String name) throws IOException
	{
		URL url = new URL(cls.getDocumentBase(),name); 
		
		URLConnection con = url.openConnection();
		InputStream istr = con.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		while (true)
		{
			int li = istr.read();
			if (li<0) break;
			bos.write(li);
		}
		
		return bos.toByteArray(); 
	}
	
	public static Image loadImageResource(Applet cmp,String name) throws IOException
	{
		name = "/resource/"+name+".gif";
		InputStream imgStream = cmp.getClass().getResourceAsStream(name);
		
		if (imgStream==null) throw new IOException(name); 
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = null;

		byte imageBytes[]=new byte[imgStream.available()];
		imgStream.read(imageBytes);
		img = tk.createImage(imageBytes);

		MediaTracker trcker = new MediaTracker(cmp);
		
		trcker.addImage(img,0);
		try
		{
			trcker.waitForID(0);
		}
		catch(InterruptedException exx)
		{
			return null;
		}
		
		trcker.removeImage(img); 
		
		return img;
	}
	
	public static boolean checkColor(int color1,int color2)
	{

		color1 = color1&0xFDFDFD;
		color2 = color2&0xFDFDFD;
		if (color1==color2) 
			return true; 
		else 
			return false;
	}

	public static Image createImage(Component cmp,int w,int h)
	{
		Image imm = cmp.createImage(w,h); 

		MediaTracker trcker = new MediaTracker(cmp);
		
		trcker.addImage(imm,0);
		try
		{
			trcker.waitForID(0);
		}
		catch(InterruptedException exx)
		{
			return null;
		}
		
		trcker.removeImage(imm); 
		
		return imm;
	}
	
	
}
