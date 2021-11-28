// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.musicengine;

import java.applet.*; 
import java.io.*;
import java.awt.*; 
import java.net.*;

public class Util
{
	//--- added to provide work as local file reader
	public static byte [] loadFile(final File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException("Can't find file "+file);
		}
		int length =(int)file.length();
		final ByteArrayOutputStream result = new ByteArrayOutputStream(length);
		final FileInputStream inputStream = new FileInputStream(file);
		try {
			byte [] buffer = new byte[16384];
			while (Thread.currentThread().isAlive() && length > 0) {
				final int read = inputStream.read(buffer);
				if (read < 0) break;
				length -= read;
				result.write(buffer, 0, read);
			}
		}finally {
			inputStream.close();
		}
		return result.toByteArray();
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

	public static Image loadURLImageResource(Applet cmp,String name) throws IOException
	{
		byte imageBytes[]= getURLResourceAsByteArray(cmp,name);
		Toolkit tk = Toolkit.getDefaultToolkit(); 
		Image img = tk.createImage(imageBytes);

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
	
}