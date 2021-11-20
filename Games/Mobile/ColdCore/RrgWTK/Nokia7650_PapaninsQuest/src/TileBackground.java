
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class TileBackground
{
  private int i_slider_max_x = 0xffff;
  private int i_slider_max_y = 0xffff;

  private int i_slide_step_x = 0xffff;
  private int i_slide_step_y = 0xffff;


  private int bsWIDTH, bsHEIGHT, bs_X_FRAMES, bs_X_LAST_FRAME, bs_X_POS, LOC_X//, OFS_X
                 , bs_Y_FRAMES, bs_Y_LAST_FRAME, bs_Y_POS, LOC_Y//, OFS_Y
          , CELL_SIZE;
  private int center_x,center_y;

  private Image BackScreen;
  private Image p_BlockImage;
  private Graphics gBackScreen;

  private byte[] ab_roomArray;
  private int i_roomArrayWidth,i_roomArrayHeight,i_roomArrayLength;

  private int FLOOR_COLOR=0xffffff;
  public int i_viewCoordX,i_viewCoordY;

    public TileBackground(int _width, int _height, int _cellWidth, int _cellHeight)
    {

        CELL_SIZE = _cellWidth;
        bs_X_FRAMES = (_width + _cellWidth -1) / _cellWidth + 2;       //amount necessary cells + half-view buffer + shadow
        bs_Y_FRAMES = (_height + _cellHeight - 1) / _cellHeight + 2;  // -//-
        bsWIDTH = bs_X_FRAMES * CELL_SIZE;
        bsHEIGHT = bs_Y_FRAMES * CELL_SIZE;
//        OFS_X = (_width / CELL_SIZE)>>1;
//        OFS_Y = (_height / CELL_SIZE)>>1;

        center_x = _width>>1;
        center_y = _height>>1;

        BackScreen=Image.createImage(bsWIDTH ,bsHEIGHT);
        gBackScreen=BackScreen.getGraphics();
    }



    public void drawImageToGraphics(Graphics _g, int _x, int _y)
    {
        synchronized (BackScreen)
        {

 int i = LOC_X-/*i_viewCoordX%CELL_SIZE*/(i_viewCoordX&0xf)+_x, j = LOC_Y-/*i_viewCoordY%CELL_SIZE*/(i_viewCoordY&0xf)+_y;
   _g.drawImage(BackScreen,i,j,0);
   _g.drawImage(BackScreen,i,j-bsHEIGHT,0);
   _g.drawImage(BackScreen,i-bsWIDTH,j,0);
   _g.drawImage(BackScreen,i-bsWIDTH,j-bsHEIGHT,0);


        }
    }

    public void setGameRoomArray(int _roomWidth, byte[] _roomArray)
    {
        synchronized (BackScreen)
        {
            i_roomArrayWidth = _roomWidth;
            i_roomArrayLength = _roomArray.length;
            i_roomArrayHeight = (i_roomArrayLength + _roomWidth-1)/_roomWidth;
            ab_roomArray = _roomArray;
        }
    }

    public void setBlockImage(Image _image)
    {
        synchronized (BackScreen)
        {
            p_BlockImage = _image;
        }
    }

    public void setXY(int _xx, int _yy)
    {
        synchronized (BackScreen)
        {
      //gBackScreen.setColor(FLOOR_COLOR);
      //gBackScreen.fillRect(0,0,bsWIDTH,bsHEIGHT);

      i_viewCoordX = _xx;
      i_viewCoordY = _yy;

      bs_X_POS = bs_X_FRAMES-1;           /// WARNING: now  OFS_X  and  OFS_Y  actually longer by 1 !!!
      bs_Y_POS = bs_Y_FRAMES-1;           ///          (in oth.w. we should _decrease_ some values on 1)
      bs_X_LAST_FRAME = _xx/CELL_SIZE;
      bs_Y_LAST_FRAME = _yy/CELL_SIZE;
      LOC_X = 0;//center_x - OFS_X*CELL_SIZE;//-CELL_SIZE>>2;
      LOC_Y = 0;//center_y - OFS_Y*CELL_SIZE;//-CELL_SIZE>>2;


      int from_x = bs_X_LAST_FRAME/*-OFS_X*/, from_y = bs_Y_LAST_FRAME/*-OFS_Y*/;
      int to_x=from_x+bs_X_FRAMES, to_y=from_y+bs_Y_FRAMES;
      int _y = -CELL_SIZE,  _x;


            for (int y = from_y; y < to_y; y++)
     {
         _x = -CELL_SIZE;
  _y+=CELL_SIZE;
                for (int x = from_x; x < to_x; x++)
                {
          _x+=CELL_SIZE;
   //drawCell(x,y,_x,_y);
   drawCell((x==to_x-1?bs_X_LAST_FRAME-1:x),
                                 (y==to_y-1?bs_Y_LAST_FRAME-1:y),_x,_y);

                }
     }
        }
    }

    private void repaintHorizontalLine(int index,int bs_position){
       int bsx = bs_X_POS, px = bs_X_LAST_FRAME-1,
           dx = bsx <<4/* * CELL_SIZE*/,dy = (bs_position)<<4/* * CELL_SIZE*/;
    //gBackScreen.setColor(FLOOR_COLOR);
    //gBackScreen.fillRect(0,dy,bsWIDTH,CELL_SIZE);

    for (int i = 0 ; i <bs_X_FRAMES ; i++){
      drawCell(px,index,dx,dy);
      bsx++; dx+=CELL_SIZE; px++;
      if(bsx>=bs_X_FRAMES){
        bsx-=bs_X_FRAMES;
        dx-=bsWIDTH;
      }
    }
    }

    private void repaintVerticalLine(int index,int bs_position){
       int bsy = bs_Y_POS, py = bs_Y_LAST_FRAME-1,
           dy = bsy<<4 /* * CELL_SIZE*/,dx = (bs_position)<<4 /* * CELL_SIZE*/;
    //gBackScreen.setColor(FLOOR_COLOR);
    //gBackScreen.fillRect(dx,0,CELL_SIZE,bsHEIGHT);

    for (int i = 0 ; i <bs_Y_FRAMES ; i++){
      drawCell(index,py,dx,dy);
      bsy++; dy+=CELL_SIZE; py++;
      if(bsy>=bs_Y_FRAMES){
        bsy-=bs_Y_FRAMES;
        dy-=bsHEIGHT;
      }
    }
    }

    public void updateBackScreen(int _x, int _y){

        if(i_slide_step_x != i_slider_max_x)
        {
          if (i_slide_step_x > i_slider_max_x)
             i_slide_step_x --;
            else
              i_slide_step_x ++;
        }
        if(i_slide_step_y != i_slider_max_y)
        {
          if (i_slide_step_y > i_slider_max_y)
             i_slide_step_y --;
            else
              i_slide_step_y ++;
        }

        int bx = _x - i_viewCoordX;
        int by = _y - i_viewCoordY;

        i_viewCoordX += Math.min(i_slide_step_x, Math.max(-i_slide_step_x,bx));
        i_viewCoordY += Math.min(i_slide_step_y, Math.max(-i_slide_step_y,by));

        bx=i_viewCoordX>>4/* /CELL_SIZE */;
        by=i_viewCoordY>>4/* /CELL_SIZE */;

 if(by!=bs_Y_LAST_FRAME){
   int dy = by-bs_Y_LAST_FRAME;
   if(dy>0){
    for (int i = 0 ; i <dy ; i++) {
      repaintHorizontalLine(bs_Y_LAST_FRAME + bs_Y_FRAMES-1,bs_Y_POS);
      if(++bs_Y_POS >= bs_Y_FRAMES) bs_Y_POS -= bs_Y_FRAMES;
      bs_Y_LAST_FRAME++;
    }
   } else {
    for (int i = 0 ; i >dy ; i--) {
      if(--bs_Y_POS < 0) bs_Y_POS += bs_Y_FRAMES;
      bs_Y_LAST_FRAME--;
      repaintHorizontalLine(bs_Y_LAST_FRAME -1 ,bs_Y_POS);
    }
   }
   LOC_Y -= dy<<4/* *CELL_SIZE*/;
   if(LOC_Y>=bsHEIGHT)LOC_Y-=bsHEIGHT;
     else
       if(LOC_Y<0)LOC_Y+=bsHEIGHT;
 } else {
          _y = LOC_Y-(_y&0xf); //LOC_Y-_y%CELL_SIZE
   if(_y>=bsHEIGHT)LOC_Y-=bsHEIGHT;
     else
       if(_y<0)LOC_Y+=bsHEIGHT;
 }

 if(bx!=bs_X_LAST_FRAME){
   int dx = bx-bs_X_LAST_FRAME;
   if(dx>0){
    for (int i = 0 ; i <dx ; i++) {
      repaintVerticalLine(bs_X_LAST_FRAME + bs_X_FRAMES-1,bs_X_POS);
      if(++bs_X_POS >= bs_X_FRAMES) bs_X_POS -= bs_X_FRAMES;
      bs_X_LAST_FRAME++;
    }
   } else {
    for (int i = 0 ; i >dx ; i--) {
      if(--bs_X_POS < 0) bs_X_POS += bs_X_FRAMES;
      bs_X_LAST_FRAME--;
      repaintVerticalLine(bs_X_LAST_FRAME/* - OFS_X*/ -1 ,bs_X_POS);
    }
   }
   LOC_X -= dx<<4/* *CELL_SIZE*/;
   if(LOC_X>=bsWIDTH)LOC_X-=bsWIDTH;
     else
       if(LOC_X<0)LOC_X+=bsWIDTH;
 } else {
          _x = LOC_X-(_x&0xf); //LOC_X-_x%CELL_SIZE
   if(_x>=bsWIDTH)LOC_X-=bsWIDTH;
     else
       if(_x<0)LOC_X+=bsWIDTH;
 }
    }


    public void TB_changeArrayCell(int _cellx, int _celly, byte _newvalue)
    {
      if(_cellx>=0 && _celly>=0 && _cellx<i_roomArrayWidth && _celly<i_roomArrayHeight){
         ab_roomArray[_celly*i_roomArrayWidth+_cellx] = _newvalue;

         int dx = _cellx - bs_X_LAST_FRAME+bs_X_POS+1-bs_X_FRAMES,
             dy = _celly - bs_Y_LAST_FRAME+bs_Y_POS+1-bs_Y_FRAMES;
//		       dx+=bs_X_POS+OFS_X+1;
//		       dy+=bs_Y_POS+OFS_Y+1;
//             if(dx>=bs_X_FRAMES)dx-=bs_X_FRAMES;
//		        else if(dx<0){dx+=bs_X_FRAMES;System.out.println("Catcha!!!! X");}
//             if(dy>=bs_Y_FRAMES)dy-=bs_Y_FRAMES;
//		         else if(dy<0){dy+=bs_Y_FRAMES;System.out.println("Catcha!!!! Y");}

//         System.out.println(dx+":"+dy);
             dx = (dx<<4);
             dy = (dy<<4);
//         System.out.println(dx+":"+dy);
             if (dx<0)dx+=bsWIDTH;
             if (dy<0)dy+=bsHEIGHT;

             gBackScreen.setClip(dx,dy,CELL_SIZE,CELL_SIZE);
      gBackScreen.drawImage(p_BlockImage,dx - ((_newvalue&7)<<4), dy - ((_newvalue&0xf8)<<1),0);
      }
    }




    private void drawCell(int x, int y ,int _x,int _y){
           int qd=30; //IMG_BACK_TILE;
           int ofs;

        if( x >= 0 && x <i_roomArrayWidth && (ofs = y*i_roomArrayWidth+x) >= 0 &&  ofs< i_roomArrayLength)
    qd = ab_roomArray[ofs];

          gBackScreen.setClip(_x,_y,CELL_SIZE,CELL_SIZE);
   gBackScreen.drawImage(p_BlockImage,_x - ((qd&7)<<4), _y - ((qd&0xf8)<<1),0);
    }
    public void setSlideSpeed(int _x,int _y)
    {
        i_slider_max_x = _x;
        i_slider_max_y = _y;

        i_slide_step_x = _x;
        i_slide_step_y = _y;
    }
    public void setSliderSpeed(int _x,int _y)
    {
        i_slider_max_x = _x;
        i_slider_max_y = _y;
    }

}
