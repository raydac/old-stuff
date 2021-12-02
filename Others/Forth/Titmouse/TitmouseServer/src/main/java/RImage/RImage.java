package RImage;

import java.awt.*;  
import java.awt.image.*;  
import java.util.*; 
import java.io.*;
			
public class RImage
{
	public byte [] ImageArray; // Массив с изображением

	int ImageWidth,ImageHeight; // Ширина и высота изображения

	int BorderColor = 0; // Цвет границы при закраске
	
	byte CurrentColor=0; // Текущий цвет пера
	int LastX=0; // Последняя использованная координата по X
	int LastY=0; // Последняя использованная координата по Y
	
	byte [] CurrentFont = new byte [2048];
	public RImagePalette Palette ;

	public int LineWidth = 0;
	
	public short GetImageWidth()
	{
		return (short)ImageWidth;	
	}
	
	public short GetImageHeight()
	{
		return (short)ImageHeight;	
	}
	
	public void InitImage(int Width,int Height,int FillValue)
	{
		ImageArray = new byte[Width * Height];
		for (int int1=0; int1<(Width*Height); int1++) ImageArray[int1] = (byte)FillValue;
		ImageWidth = Width;
		ImageHeight = Height;
	}

	// Устанавливает цвет
	public void SetColor(int index)
	{
		CurrentColor = (byte) index;
	}

	// Считывает точку
	public int GetPoint(int X, int Y)
	{
		LastX=X; LastY=Y;
		if ((X<0) || (Y<0)) return CurrentColor;
		if ((X<ImageWidth) && (Y<ImageHeight))
		{
			return (int)ImageArray[Y*ImageWidth+X]; 
		}
		return CurrentColor; 
	}
	
	protected void MainSetPoint(int X,int Y)
	{
		if ((X<0) || (Y<0)) return;
		if ((X<ImageWidth) && (Y<ImageHeight))
		{
			ImageArray[Y*ImageWidth+X]=CurrentColor; 
		}
	}

	protected void MainSetPointC(int X,int Y,int C)
	{
		if ((X<0) || (Y<0)) return;
		if ((X<ImageWidth) && (Y<ImageHeight))
		{
			ImageArray[Y*ImageWidth+X]=(byte)C; 
		}
	}
	
	// Рисует точку
	public void SetPoint(int X, int Y)
	{
		LastX=X; LastY=Y;
		if (LineWidth>0) SetPoint3(X,Y);
		else MainSetPoint(X,Y);
	}

	// Обрабатывает отрисовку точек размером больше 1
	void SetPoint3(int X,int Y)
	{
		int xstart,xend,ystart,yend;
		xstart = X-LineWidth;
		xend = X+LineWidth;
		ystart = Y-LineWidth;
		yend = Y+LineWidth;
		for (int xx=xstart;xx<=xend;xx++)
			for (int yy=ystart;yy<=yend;yy++)
				MainSetPoint(xx,yy);
	}	
	
	public void SetPoint2(int X, int Y)
	{
		if (LineWidth>0) SetPoint3(X,Y); else 
			MainSetPoint(X,Y);
	}
	
	// Прямоугольник
	public void Rectangle (int X1,int Y1,int X2,int Y2)
	{
		int xd,yd;
		if (X2<X1) xd = -1; else xd = 1;
		if (Y2<Y1) yd = -1; else yd = 1;
		for(int x=X1;x!=X2;x+=xd) { SetPoint(x,Y1); SetPoint(x,Y2); }
		for(int y=Y1;y!=Y2;y+=yd) { SetPoint(X1,y); SetPoint(X2,y); }			
	}

	// Закрашенный прямоугольник
	public void FillRectangle (int X1,int Y1,int X2,int Y2)
	{
		int xd,yd;
		if (X2<X1) xd = -1; else xd = 1;
		if (Y2<Y1) yd = -1; else yd = 1;
		for(int y=Y1;y!=Y2;y+=yd)
		{
			for(int x=X1;x!=X2;x+=xd) SetPoint(x,y);
		}
	}

	// Прямоугольник с закругленными краями
	public void RectangleRounded (int X1,int Y1,int X2, int Y2, int R)
	{
		int startx,endx,starty,endy,x1,y1;
		startx = Math.min(X1,X2)+R;
		endx = Math.max(X1,X2)-R;
		starty = Math.min(Y1,Y2)+R;
		endy = Math.max(Y1,Y2)-R;
		if ((startx>=endx)||(starty>=endy))
		{
			Ellipse(X1,Y1,X2,Y2); 	
			return;
		}
		
		for(x1=startx;x1<=endx;x1++) { SetPoint2(x1,Y1); SetPoint2(x1,Y2); }
		for(y1=starty;y1<=endy;y1++) { SetPoint2(X1,y1); SetPoint2(X2,y1); }

        int x,y,delta;
        y=R;
        delta=3-2*R;
        for (x=0;x<y; )
		{
			plot_circleRR1(x,y,startx,starty);
			plot_circleRR2(x,y,startx,endy);
			plot_circleRR3(x,y,endx,starty);
			plot_circleRR4(x,y,endx,endy);

			if (delta<0) delta+=4*x+6;
            else 
			{
				delta+=4*(x-y)+10;
				y--;
			}
                x++;
        }
        x=y;
		if (y!=0) 
		{ 
			plot_circleRR1(x,y,startx,starty);
			plot_circleRR2(x,y,startx,endy);
			plot_circleRR3(x,y,endx,starty);
			plot_circleRR4(x,y,endx,endy);
		}
	}

    void plot_circleRR1(int x,int y,int x_center,int y_center)
    {
		int startx,starty,endx,endy,x1,y1;
        starty=y;
        endy=(y+1);
        startx=x;
        endx=(x+1);

        for (x1=startx;x1<endx;++x1) SetPoint2(x_center-x1,y_center-y);
        for (y1=starty;y1<endy;++y1) SetPoint2(x_center-y1,y_center-x);
	}

    void plot_circleRR2(int x,int y,int x_center,int y_center)
    {
		int startx,starty,endx,endy,x1,y1;
        starty=y;
        endy=(y+1);
        startx=x;
        endx=(x+1);

        for (x1=startx;x1<endx;++x1) SetPoint2(x_center-x1,y+y_center);
        for (y1=starty;y1<endy;++y1) SetPoint2(x_center-y1,x+y_center);
	}
	
    void plot_circleRR3(int x,int y,int x_center,int y_center)
    {
		int startx,starty,endx,endy,x1,y1;
        starty=y;
        endy=(y+1);
        startx=x;
        endx=(x+1);

        for (x1=startx;x1<endx;++x1) SetPoint2(x1+x_center,y_center-y);
        for (y1=starty;y1<endy;++y1) SetPoint2(y1+x_center,y_center-x);
	}

    void plot_circleRR4(int x,int y,int x_center,int y_center)
    {
		int startx,starty,endx,endy,x1,y1;
        starty=y;
        endy=(y+1);
        startx=x;
        endx=(x+1);

        for (x1=startx;x1<endx;++x1) SetPoint2(x1+x_center,y+y_center);
        for (y1=starty;y1<endy;++y1) SetPoint2(y1+x_center,x+y_center);
	}

	// Производит закраску области
	public void Fill(int x,int y, int color)
	{
		BorderColor = color;
		LineFill(x,y,1,x,y); 	
	}
	
	int LineFill(int x, int y, int dir, int PrevXl, int PrevXr)
	{
		int int_1;
		int xl = x;
		int xr = x;
		int c;
		
		do c = GetPoint(--xl,y);
		while ((c!=BorderColor)&&(c!=CurrentColor));
		do c = GetPoint(++xr,y);
		while ((c!=BorderColor)&&(c!=CurrentColor));
		xl++; xr--;
		
		for(int_1=xl; int_1<=xr; int_1++) MainSetPoint(int_1,y);
		
		for(x=xl; x<=xr; x++)
		{
			c = GetPoint(x,y+dir);
			if ((c!=BorderColor)&&(c!=CurrentColor)) x=LineFill(x,y+dir,dir,xl,xr); 
		}
		for (x=xl;x<PrevXl; x++)
		{
			c = GetPoint(x,y-dir);
			if ((c!=BorderColor)&&(c!=CurrentColor)) x=LineFill(x,y-dir,-dir,xl,xr); 
		}
		for (x=PrevXr;x<xr; x++)
		{
			c = GetPoint(x,y-dir);
			if ((c!=BorderColor)&&(c!=CurrentColor)) x=LineFill(x,y-dir,-dir,xl,xr); 
		}
		return xr;
	}
	
	
	// Рисует линию
	public void Line(int X1, int Y1, int X2, int Y2)
	{
			int dx = Math.abs(X2-X1);
			int dy = Math.abs(Y2-Y1);
			int sx=X2>=X1?1:-1;
			int sy=Y2>=Y1?1:-1;
			if (dy<=dx)
			{
				int d = (dy<<1)-dx;
				int d1 = dy<<1;
				int d2 = (dy-dx)<<1;

				SetPoint2(X1,Y1);
				
				for (int x=X1+sx,y=Y1, i=1; i<=dx; i++, x+=sx)
				{
					if (d>0) { d += d2; y += sy; }
					else d += d1;
					SetPoint2(x,y);
				}
			
			}
			else
			{
				int d = (dx << 1) - dy;
				int d1 = dx << 1;
				int d2 = (dx - dy) << 1;
				SetPoint(X1,Y1);
				for (int x=X1, y=Y1+sy, i=1; i<=dy; i++, y+=sy)
				{
					if (d>0) { d += d2; x += sx; }
					else d += d1;
					SetPoint2(x,y);
				}
			}
		LastX = X2; LastY = Y2;
	}
	
	// Рисует линию
	public void LineFrom(int X, int Y)
	{
		Line(LastX,LastY,X,Y); 
	}

	double asp_ratio=1; 
	
	// Рисует эллипс вписанный в прямоугольник
	public void Ellipse(int x1,int y1,int x2,int y2)
	{
			int WorkingX,WorkingY,Threshold,XAdjust,YAdjust;
			WorkingX = Math.min(x1,x2);
			WorkingY = Math.max(x1,x2);
			x1 = WorkingX;
			x2 = WorkingY;
			WorkingX = Math.min(y1,y2);
			WorkingY = Math.max(y1,y2);
			y1 = WorkingX;
			y2 = WorkingY;
			int A = (x2-x1)/2;
			int B = (y2-y1)/2;
			int X = (x1+x2)/2;
			int Y = (y2+y1)/2;
			int ASquared = A*A;
			int BSquared = B*B;

			SetPoint2(X,Y+B);
			SetPoint2(X,Y-B);
			
			WorkingX=0;
			WorkingY=0;
			XAdjust=0;
			YAdjust=ASquared*2*B;
			Threshold = ASquared/4-ASquared*B;
			for(;;)
			{
				Threshold+=XAdjust+BSquared;
				if (Threshold>=0)
				{
					YAdjust-=ASquared*2;
					Threshold-=YAdjust;
					WorkingY--;
				}
						
			XAdjust+=BSquared*2;
			WorkingX++;

			if (XAdjust>=YAdjust) break;

			SetPoint2(X+WorkingX,y1-WorkingY);
			SetPoint2(X-WorkingX,y1-WorkingY);
			SetPoint2(X+WorkingX,y2+WorkingY);
			SetPoint2(X-WorkingX,y2+WorkingY);
			}

			WorkingX=A;
			WorkingY=0;
			XAdjust=BSquared*2*A;
			YAdjust=0;
			Threshold=BSquared/4-BSquared*A;
			
			SetPoint2(X+A,Y);
			SetPoint2(X-A,Y);
	
			for(;;)
			{
				Threshold+=YAdjust+ASquared;
				if (Threshold>=0)
				{
					XAdjust-=BSquared*2;
					Threshold = Threshold-XAdjust;
					WorkingX--;
				}
				
				YAdjust+=ASquared*2;
				WorkingY++;
				if (YAdjust>XAdjust) break;
				SetPoint2(X+WorkingX,Y-WorkingY);
				SetPoint2(X-WorkingX,Y-WorkingY);
				SetPoint2(X+WorkingX,Y+WorkingY);
				SetPoint2(X-WorkingX,Y+WorkingY);
			}
	}

	
	// Рисует окружность
	public void Circle(int x_center, int y_center, int radius)
	{
            int x,y,delta;
            y=radius;
            delta=3-2*radius;
            for (x=0;x<y; )
			{
				plot_circle(x,y,x_center,y_center);
                if (delta<0) delta+=4*x+6;
                else 
				{
					delta+=4*(x-y)+10;
					y--;
				}
                x++;
            }
            x=y;
            if (y!=0) plot_circle(x,y,x_center,y_center);
	}

    /* Функция печатает точки, определяющие окружность */
    void plot_circle(int x,int y,int x_center,int y_center)
    {
		int startx,starty,endx,endy,x1,y1;
        starty=y;
        endy=(y+1);
        startx=x;
        endx=(x+1);

        for (x1=startx;x1<endx;++x1) 
		{
			SetPoint2(x1+x_center,y+y_center);
			SetPoint2(x1+x_center,y_center-y);
			SetPoint2(x_center-x1,y+y_center);
			SetPoint2(x_center-x1,y_center-y);
        }

        for (y1=starty;y1<endy;++y1)
		{
			SetPoint2(y1+x_center,x+y_center);
			SetPoint2(y1+x_center,y_center-x);
            SetPoint2(x_center-y1,x+y_center);
            SetPoint2(x_center-y1,y_center-x);
        }
	}

	// Выводит текст
	public void DrawText(int X, int Y, String Text,byte Color)
	{
	
	}

	// Загружает внешнее изображение
	public void LoadImage(String AImageName) throws Exception  
	{
		int int_1,int_2,int_3;
		FileInputStream fis = new FileInputStream(AImageName);

		// Считываем и проверяем метку файла
		int_1 = fis.read();
		int_2 = fis.read();
		if ((int_1!='R')||(int_2!='H')) throw new Exception("Error handle of image  file!");
		
		// Считываем версию
		int_1 = fis.read();
		if (int_1!=1) throw new Exception("Error version of image  file!");
				
		// Считываем размеры изображения (старший-младший)
		// Считываем ширину и высоту файла
		int_3 = fis.read();
		int_1 = fis.read();
		int_1 = (int_3 <<8) | int_1;
		int_3 = fis.read();
		int_2 = fis.read();
		int_2 = (int_3 <<8) | int_2;
		
		InitImage (int_1,int_2,0);
		
		// Считываем изображение
		if (fis.read(ImageArray,0,int_1*int_2)!=(int_1*int_2)) throw new Exception("Error size of image  file!");
		
		fis.close(); 
	}
	
	// Загружает требуемый фонт
	public void LoadFont(String AFontName)
	{
		
	}

	// Выводим одну картинку в другую
	public void Draw (RImage img, int x, int y)
	{
		int llx,lly;
		int lw = img.GetImageWidth();
		int lh = img.GetImageHeight();
		for (int lx=0; lx<lw; lx++)
			for (int ly=0; ly<lh; ly++)
			{
				llx = lx+x; lly = ly+y;
				if ((llx<0) || (lly<0)) return;
				if ((llx<ImageWidth) && (lly<ImageHeight))
				{
					ImageArray[lly*ImageWidth+llx]=(byte)img.GetPoint(lx,ly); 
				}
			}
	}

	// Выводим одну картинку в другую с учетом прозрачности
	public void DrawTransparent (RImage img, int x, int y)
	{
		int llx,lly;
		int tmpcolor;
		int lw = img.GetImageWidth();
		int lh = img.GetImageHeight();
		for (int lx=0; lx<lw; lx++)
			for (int ly=0; ly<lh; ly++)
			{
				llx = lx+x; lly = ly+y;
				if ((llx<0) || (lly<0)) return;
				if ((llx<ImageWidth) && (lly<ImageHeight))
				{
					tmpcolor = (byte)img.GetPoint(lx,ly); 
					if (tmpcolor!=CurrentColor)						
						ImageArray[lly*ImageWidth+llx]=(byte)tmpcolor; 
				}
			}
	}
	
	public RImage()
	{
		Palette = new RImagePalette();
		Palette.DefaultPalette(); 
	}
	
}
