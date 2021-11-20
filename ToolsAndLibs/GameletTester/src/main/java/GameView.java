import java.awt.Color;
import java.awt.Graphics;

public class GameView
{
    private static final Color [] ap_colors = new Color[]{Color.YELLOW,Color.GRAY,Color.BLUE,Color.CYAN,Color.RED,Color.ORANGE,Color.MAGENTA,Color.BLACK,Color.DARK_GRAY};
    
    public static final void paintGame(Graphics _g)
    {
        _g.setColor(Color.white);
        _g.fillRect(0,0,startup.SCREEN_WIDTH,startup.SCREEN_HEIGHT);
        
        // отрисовываем поле
        int [] ai_cells = Gamelet.ai_GameField;
        int i_cellw = Gamelet.I8_CELLWIDTH>>8;
        int i_cellh = Gamelet.I8_CELLHEIGHT>>8;

        for(int ly=0;ly<40;ly++)
        {
            for(int lx=0;lx<Gamelet.FIELDCELLWIDTH;lx++)
            {
                _g.setColor(Color.GRAY);
                _g.drawRect(lx*i_cellw,ly*i_cellh,i_cellw,i_cellh);
            }
        }
        
        for(int ly=0;ly<Gamelet.FIELDCELLHEIGHT;ly++)
        {
            for(int lx=0;lx<Gamelet.FIELDCELLWIDTH;lx++)
            {
                int i_block = ai_cells[lx+ly*Gamelet.FIELDCELLWIDTH];
                if (i_block==0) continue;
                _g.setColor(ap_colors[i_block-1]);
                _g.fill3DRect(lx*i_cellw,ly*i_cellh,i_cellw,i_cellh,true);
            }
        }
        
        // отрисовываем массив отражателей
        for(int lx=0;lx<Gamelet.FIELDCELLWIDTH;lx++)
        {
            if (Gamelet.ai_ReflectionLine[lx]!=0)
            {
                _g.setColor(Color.blue);
                _g.fill3DRect(lx*i_cellw,Gamelet.YOFREFLECTIONLINE,i_cellw,i_cellh,true);
            }
        }
        
        // отрисовываем ракетку игрока
        SpriteCollection p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_PLAYERROCKET];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_PLAYERROCKETLONG:
                {
                    _g.setColor(Color.blue);
                };break;
                case Gamelet.SPRITE_OBJ_PLAYERROCKETNORMAL:
                {
                    _g.setColor(Color.orange);
                };break;
                case Gamelet.SPRITE_OBJ_PLAYERROCKETSHORT:
                {
                    _g.setColor(Color.green);
                };break;
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                    _g.setColor(Color.red);
                };break;
                default:
                    System.out.println("Unknown type");
            }
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            int i_h = p_sprCol.getWidthHeight(i_sprite);
            int i_w = i_h>>>16;  
            i_h &= 0xFFFF;
            
            _g.fillRect(i_x,i_y,i_w,i_h);
        }

        // отрисовываем взрывы
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_EXPLOSIONS];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                    switch(p_sprCol.getState(i_sprite))
                    {
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION1 :
                        {
                            _g.setColor(Color.RED);
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION2 :
                        {
                            _g.setColor(Color.GREEN);   
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION3 :
                        {
                            _g.setColor(Color.BLUE);
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION4 :
                        {
                            _g.setColor(Color.YELLOW);
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION5 :
                        {
                            _g.setColor(Color.CYAN);
                        };break;
                        default: System.err.println("unfnown state");    
                    }
                };break;
                default:
                {
                        System.out.println("Unknown type");
                }
            }

            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            int i_h = p_sprCol.getWidthHeight(i_sprite);
            int i_w = i_h>>>16;  
            i_h &= 0xFFFF;

            _g.fillRect(i_x,i_y,i_w,i_h);
        }
 
        // отрисовываем мегабонусы
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_MEGABLOCKS];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_MEGABLOCK:
                {
                           _g.setColor(Color.green);
                };break;
                default:
                {
                    System.out.println("Unknown type");
                }
            }
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            int i_h = p_sprCol.getWidthHeight(i_sprite);
            int i_w = i_h>>>16;  
            i_h &= 0xFFFF;
            
            _g.fill3DRect(i_x,i_y,i_w,i_h,true);
        }
        
        // отрисовываем бонусы
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_BONUSES];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_BONUSES:
                {
                           _g.setColor(Color.CYAN);
                };break;
                default:
                {
                    System.out.println("Unknown type");
                }
            }
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            int i_h = p_sprCol.getWidthHeight(i_sprite);
            int i_w = i_h>>>16;  
            i_h &= 0xFFFF;
            
            _g.fillRect(i_x,i_y,i_w,i_h);
        }
        
        // отрисовываем мячик
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_PLAYERBALLS];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_BALL:
                {
                    switch(p_sprCol.getState(i_sprite))
                    {
                        case Gamelet.SPRITE_STATE_BALL_BIG :
                        {
                            _g.setColor(Color.RED);
                        };break;
                        case Gamelet.SPRITE_STATE_BALL_NORMAL :
                        {
                            _g.setColor(Color.GREEN);   
                        };break;
                        case Gamelet.SPRITE_STATE_BALL_SMALL :
                        {
                            _g.setColor(Color.BLUE);
                        };break;
                        default: System.err.println("unfnown state");    
                    }
                };break;
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                    _g.setColor(Color.red.brighter());
                };break;
            }
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            int i_h = p_sprCol.getWidthHeight(i_sprite);
            int i_w = i_h>>>16;  
            i_h &= 0xFFFF;

            _g.fillRect(i_x,i_y,i_w,i_h);

            // рисуем путь мяча
            int i_index = i_sprite/SpriteCollection.SPRITEDATA_LENGTH;
            drawBallPath(_g,i_index<<3,Gamelet.ash_ballsPathsArray);
        }
        
        // рисуем точки мяча
        for(int li=0;li<3;li++)
        {
            int i_offset = (li*9)+4;
            int i_x =(Gamelet.ai_ball_pointsArray[i_offset++]+0x7F)>>8;
            int i_y =(Gamelet.ai_ball_pointsArray[i_offset++]+0x7F)>>8;
            
            
            _g.setColor(Color.BLACK);
            _g.fillOval(i_x-3,i_y-3,6,6);
            
            _g.drawString(""+li,i_x-4,i_y-4);
        }
    
        // точка пересечения уровня ракетки
        _g.setColor(Color.RED);

        _g.drawLine(0,Gamelet.YOFREFLECTIONLINE,Gamelet.MAINRESOLUTION_WIDTH,Gamelet.YOFREFLECTIONLINE);
        
        _g.fillOval(Gamelet.i_xCollided-5,Gamelet.i_yCollided-5,10,10);
        
        // вывод количества оставшихся шариков
        _g.setColor(Color.BLACK);
        _g.drawString("BALLS:"+Gamelet.i_PlayerAttemptionsNumber,3,10);
        _g.setColor(Color.ORANGE);
        _g.drawString("BALLS:"+Gamelet.i_PlayerAttemptionsNumber,2,9);
    }
    
    private static void drawBallPath(Graphics _g, int _offset , short [] _pathArray)
    {
        _offset += 2;
        int i_stX = _pathArray[_offset++];
        int i_stY = _pathArray[_offset++];
        _offset++;
        int i_enX = _pathArray[_offset++];
        int i_enY = _pathArray[_offset++];
        _g.setColor(Color.black);
        _g.drawLine(i_stX,i_stY,i_enX,i_enY);
    }
}
