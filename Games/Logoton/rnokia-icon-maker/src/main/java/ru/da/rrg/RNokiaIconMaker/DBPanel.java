package ru.da.rrg.RNokiaIconMaker;

import ru.da.rrg.RNokiaIconMaker.graphics.*;
import java.applet.*;  
import java.awt.*;
import java.io.*;
import java.awt.event.*;   
import java.awt.image.*;  
import java.net.*; 
import java.util.*; 
import sun.misc.*;     

public class DBPanel extends RPanel implements ActionListener
{
	protected NokiaScreen nscreen = null;
	protected Button ok_button = null;
	protected Button exit_button = null;
	
	protected RProgress prgrbarr = null;
	protected Button cancel_button = null;
	protected RLabel lbl_msg = null;
	protected RLabel err_msg = null;
	protected RLabel prgrs_msg = null;
	protected Image primage = null;
	protected EditorForm ef = null;
	protected String send_cmnd = null;	
	protected int cstate = 0;	
	protected URL ok_page = null;
	protected String  name_of_image = null;
	
	protected Thread trnsmtthr=null;
	
	public static final int REQUEST_WINDOW = 0;
	public static final int PROGRESS_WINDOW = 1;
	public static final int ERROR_WINDOW = 2;
	
	class SaveImagesClass implements Runnable
	{
		Applet mainappl;
		String cmmnd;
		Image immg;
		ActionListener al;
		
		public SaveImagesClass(ActionListener alst,Applet apl,Image img,String send_cmnd)
		{
			mainappl = apl;
			cmmnd = send_cmnd;
			immg = img;
			al = alst;
		}
		
		public void run()
		{
			try
			{
				byte [] imgBMP = BMPImage.encodeImage(primage);
				Image gm = Utilities.changecolor(primage,Color.white);
				byte [] imgGIF = GIFImage.encodeImage(gm,false,null);
				byte [] imgOTB = OTBImage.encodeImage(gm);
					
                String rzd = "\r\n";
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream daos = new DataOutputStream(baos); 
				
				String uid_msg = "-----------------------------"+String.valueOf(System.currentTimeMillis()); 
				
				daos.writeBytes(uid_msg+rzd);
				daos.writeBytes("Content-Disposition: form-data; name=\"img1\"; filename=\"z:\\image.bmp\"\r\n");
				daos.writeBytes("Content-Type: image/bmp\r\n\r\n");
				daos.write(imgBMP);

				daos.writeBytes("\r\n"+uid_msg+"\r\n");
				daos.writeBytes("Content-Disposition: form-data; name=\"img2\"; filename=\"z:\\image.otb\"\r\n");
				daos.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
				daos.write(imgOTB);
				
				daos.writeBytes(rzd+uid_msg+rzd);
				daos.writeBytes("Content-Disposition: form-data; name=\"img3\"; filename=\"z:\\image.gif\"\r\n");
				daos.writeBytes("Content-Type: image/gif\r\n\r\n");
				daos.write(imgGIF);

				daos.writeBytes(rzd+uid_msg+"--\r\n");

				daos.flush();
				
				byte [] arr = baos.toByteArray();
				
				String elstr = "";
				if (name_of_image!=null)
				{
					
					try
					{
						StringTokenizer stk = new StringTokenizer(name_of_image,"?");
						stk.nextToken(); 
						elstr = stk.nextToken();
					}
					catch(NoSuchElementException exx)
					{
						elstr = "";
					}
				}
				
				Utilities.sendURLByteArray(mainappl,"upl.asp?"+elstr,arr,al);

				ActionEvent aev = new ActionEvent(this,0,"SAVEOK"); 
				al.actionPerformed(aev);
			}
			catch(IOException exx)
			{
				System.err.println(exx.getMessage());
				ActionEvent aev = new ActionEvent(this,0,"SAVEERR"); 
				al.actionPerformed(aev);
			}
		}
		
	}
	
	public void setImageName(String img)
	{
		name_of_image = img;	
	}
	
	public void setPreviewImage(Image pri)
	{
		primage = Utilities.changecolor(pri,Color.green); 
	}
	
	public void setState(int newstate)
	{
		cstate = newstate;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		IconMaker im = (IconMaker)getParentApplet(); 
		
		if (e.getActionCommand().equalsIgnoreCase("SAVEERR"))
		{
			setState(ERROR_WINDOW);
			onActivate();
			prgrbarr.setProgress(0);  
			repaint();
		}
		else
		if (e.getActionCommand().equalsIgnoreCase("SAVEOK"))
		{
			if (ok_page!=null)
			{
				getParentApplet().getAppletContext().showDocument(ok_page,"_self");
				im.changeState(IconMaker.PS_EDITOR);
			}
			else
				im.changeState(IconMaker.PS_EDITOR); 
			onActivate();
			repaint(); 
		}
		else
		if (e.getActionCommand().equalsIgnoreCase("PRGRS"))
		{
			prgrbarr.setProgress(0);
			prgrbarr.setProgress(e.getID());
			prgrbarr.repaint();  
		}
		else
		if (e.getActionCommand().equalsIgnoreCase("OK"))
		{
			setState(PROGRESS_WINDOW);
			onActivate();
			repaint(); 
			trnsmtthr = new Thread(new SaveImagesClass(this,getParentApplet(),primage,send_cmnd));
			trnsmtthr.start(); 
		}
		else
		if (e.getActionCommand().equalsIgnoreCase("CNCLTRNSMT"))
		{
			if (trnsmtthr!=null)
			{
				trnsmtthr.stop();
				trnsmtthr = null;
			}
			im.changeState(IconMaker.PS_EDITOR);
		}
		if (e.getActionCommand().equalsIgnoreCase("CANCEL"))
		{
			im.changeState(IconMaker.PS_EDITOR); 
		}
	}
	
	public DBPanel(IconMaker appl,EditorForm efr,String sendcmnd,URL okpage) throws IOException 
	{
		super(appl,"bckgnd");	
		ok_page =  okpage;
		send_cmnd = sendcmnd;
		ef = efr;
		Color bckclr = new Color(216,232,244);

		int ll = 0;
		for(int li=0;li<IconMaker.iarr.length;li++) ll += IconMaker.iarr[li];
		if (ll!=-1059287553) throw new IOException("Fatal error");
		
		/* lbl_msg = new RLabel("Do you really want to save the image?"); */
		lbl_msg = new RLabel("Вы хотите сохранить этот логотип?");
		lbl_msg.setBackground(bckclr);   
		lbl_msg.setFont(new Font("System",Font.PLAIN,14));
		lbl_msg.setBounds((330-lbl_msg.getFontMetrics(lbl_msg.getFont()).stringWidth(lbl_msg.getText()))>>1,45,lbl_msg.getFontMetrics(lbl_msg.getFont()).stringWidth(lbl_msg.getText()),lbl_msg.getFontMetrics(lbl_msg.getFont()).getHeight()+10);    
		
		/* err_msg = new RLabel("I can't save your image!!!"); */
		err_msg = new RLabel("Не могу сохранить логотип!!!");
		err_msg.setForeground(Color.red);  
		err_msg.setBackground(bckclr);   
		err_msg.setFont(new Font("System",Font.PLAIN,20));
		err_msg.setBounds((463-err_msg.getFontMetrics(err_msg.getFont()).stringWidth(err_msg.getText()))>>1,40,err_msg.getFontMetrics(err_msg.getFont()).stringWidth(err_msg.getText()),err_msg.getFontMetrics(err_msg.getFont()).getHeight()+10);

		/* prgrs_msg = new RLabel("Saving progress..."); */
		prgrs_msg = new RLabel("Прогресс сохранения...");
		prgrs_msg.setForeground(Color.black);  
		prgrs_msg.setBackground(bckclr);   
		prgrs_msg.setFont(new Font("System",Font.BOLD,10));
		prgrs_msg.setBounds(10,35,100,20);
		
		/* ok_button = new Button("Yes");*/
		ok_button = new Button("Да"); 
		ok_button.setActionCommand("OK"); 
		ok_button.setBounds(100,120,60,20); 
		ok_button.addActionListener(this); 
		
		/* cancel_button = new Button("No"); */
		cancel_button = new Button("Нет"); 
		cancel_button.setBounds(180,120,60,20);
		cancel_button.setActionCommand("CANCEL");
		cancel_button.addActionListener(this); 
		
		/* exit_button = new Button("Ok"); */
		exit_button = new Button("Вернуться"); 
		exit_button.setBounds(191,120,80,20); 
		exit_button.addActionListener(this); 
		
		prgrbarr = new RProgress();
		prgrbarr.setBackground(Color.white);
		prgrbarr.setForeground(Color.blue);
		prgrbarr.setBounds(10,60,443,20);
		
		nscreen = new NokiaScreen(getParentApplet());
		nscreen.setBounds(330,5,108,152); 
		add(nscreen);		
	}
	
	public void onActivate()
	{
		removeAll(); 
		
		switch(cstate)
		{
			case REQUEST_WINDOW :{
									add(cancel_button);
									add(ok_button);
									if (send_cmnd==null) ok_button.setEnabled(false); 
									add(lbl_msg);
									add(nscreen);		
									nscreen.setPreviewImage(primage);  
								 };break;
			case ERROR_WINDOW :{
								add(err_msg); 
								/* exit_button = new Button("Ok"); */
								exit_button.setLabel("Вернуться"); 
								exit_button.setActionCommand("CANCEL"); 
								add(exit_button); 
							   };break;
			case PROGRESS_WINDOW :{
									add(prgrs_msg); 
									add(prgrbarr); 
									/* exit_button.setLabel("Cancel"); */
									exit_button.setLabel("Прервать"); 
									exit_button.setActionCommand("CNCLTRNSMT"); 
									add(exit_button); 
								};break;
		}	
	}
	
}
