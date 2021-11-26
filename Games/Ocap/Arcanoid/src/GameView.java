import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;


import org.dvb.ui.DVBBufferedImage;

/**
 * Класс реализует отображение игрового процесса
 * @author Igor Maznitsa
 * @version 1.03
 */
public class GameView 
{
	private static final String IMAGE_BRDRLEFT = "gfx/brdrlft.gif";
	private static final String IMAGE_BRDRRIGHT = "gfx/brdrght.gif";
	private static final String IMAGE_BRDRTOP = "gfx/brdrtop.gif";
	private static final String IMAGE_PANEL = "gfx/panel.gif";
	
    /**
     * Картинка, содержащая изображение левого бордюра 
     */
	private static Image p_LeftBorderImage;

    /**
     * Картинка, содержащая изображение верхнего бордюра 
     */
	private static Image p_TopBorderImage;

    /**
     * Картинка, содержащая изображение правого бордюра 
     */
    private static Image p_RightBorderImage;

    /**
     * Картинка, содержащая изображение панели очков и шариков
     */
    private static Image p_PanelImage;
	
    /**
     * Бэкбуфер
     */
	private static DVBBufferedImage p_BackBuffer;
	
    /**
     * Графический контекст бэкбуфера
     */
	private static Graphics p_BackBufferGraphics;
	
    /**
     * Ширина игровой зоны
     */
	private static final int BACKBUFFERWIDTH = 480;

    /**
     * Высота игровой зоны
     */
	private static final int BACKBUFFERHEIGHT = 400;
	
    /**
     * Флаг, показывающий что требуется перерисовать весь экран
     */
	private static boolean lg_PaintAll;
	
    /**
     * Цвет фона для заливки областей на игровом поле 
     */
    private static final Color BACKGROUNDCOLOR = new Color(0x8CB500);
    
    /**
     * Счетчик количества областей для перерисовки 
     */
	public static int i_AreasForDrawingNumber;
	
    /**
     * Координаты областей для перерисовки
     */
    private static final long [] al_areasForDrawing = new long[32];
	
	/**
	 * Инициализация визуализатора
	 * @param _width ширина теневого буфера
	 * @param _height высота теневого буфера
	 * @throws Throwable порождается в случае проблем при инициализации
	 */
	public static final void initResources(int _width,int _height) throws Throwable
	{
		p_LeftBorderImage = Utils.loadImageFromResource(IMAGE_BRDRLEFT);
		p_RightBorderImage = Utils.loadImageFromResource(IMAGE_BRDRRIGHT);
		p_TopBorderImage = Utils.loadImageFromResource(IMAGE_BRDRTOP);
		p_PanelImage = Utils.loadImageFromResource(IMAGE_PANEL);
		
		p_BackBuffer = new DVBBufferedImage(BACKBUFFERWIDTH,BACKBUFFERHEIGHT);
		p_BackBufferGraphics = p_BackBuffer.getGraphics();
		
		ImageManager.init(true);
		
		lg_PaintAll = true;
	}

    /**
     * Освобождение ресурсов визуализатора
     *
     */
	public static final void releaseResources()
	{
			ImageManager.release();
            p_LeftBorderImage = null;
            p_RightBorderImage = null;
            p_TopBorderImage = null;
            p_PanelImage = null;
			if (p_BackBuffer!=null) p_BackBuffer.dispose();
	}

	public static final void showNotify()
	{
		lg_PaintAll = true;
	}
	
    public static final void gameAction(int _action,int _optionaldata)
    {
        try
        {
        switch(_action)
        {
            case Gamelet.GAMEACTION_UPDATEBALLAREA : addAreaForDrawing(XOFFSET_BALLSPANEL,YOFFSET_PANEL,PANELWIDTH,PANELHEIGHT);break;
            case Gamelet.GAMEACTION_ADDREFLECTIONLINE : drawFullReflectionLine();break;
            case Gamelet.GAMEACTION_UPDATEMAINGAMEFIELD : drawBlockAt(_optionaldata);break;
            case Gamelet.GAMEACTION_UPDATEREFLECTIONFIELD : drawReflectBlockAt(_optionaldata);break;
            case Gamelet.GAMEACTION_BEATTOROCKET : 
            {
                if (startup.i_Options_SoundLevel>0) SoundManager.playSound(SoundManager.SOUND_BALLBIT,startup.i_Options_SoundLevel);
            };break;
            case Gamelet.GAMEACTION_EXPLOSION : {
                if (startup.i_Options_SoundLevel>0) SoundManager.playSound(SoundManager.SOUND_EXPLOSION,startup.i_Options_SoundLevel);
            };break;
        }
        }catch(Throwable _thr)
        {
            _thr.printStackTrace();
        }
    }
    
	public static final void beforeGameIteration() throws Throwable
	{
		// заполняем оптимизатор значениями позиций спрайтов 
        Graphics p_g = p_BackBufferGraphics;
        ImageManager.p_DestinationGraphics = p_g;
		SpriteCollection [] ap_collection = Gamelet.ap_SpriteCollections;
		for(int li=0;li<ap_collection.length;li++)
		{
			//ap_collection[li].fillOptimizer(p_DrawOptimizer,false);
            clearBackgroundForSpriteCollection(ap_collection[li],p_g);
		}	

		//p_DrawOptimizer.pack();

//		System.out.println("p_DrawOptimizer = "+p_DrawOptimizer.i_StackPointer);
		
		// очищаем позиции спрайтов
		//clearBackground(p_DrawOptimizer);
	}
	
    public static final void clearBackgroundForSpriteCollection(SpriteCollection _collection,Graphics _g) throws Throwable
    {
        int i_offset = _collection.i_lastActiveSpriteOffset;
        
        final int[] ai_spriteData = _collection.ai_spriteDataArray;
        final short[] ash_spriteAnimationData = _collection.ash_spriteAnimationDataArray;

        _g.setColor(BACKGROUNDCOLOR);
        
        while(i_offset>=0)
        {
            final int i_listData = ai_spriteData[i_offset];
            int i_prev = (i_listData & SpriteCollection.MASK_LISTPREVINDEX) >>> SpriteCollection.SHR_LISTPREVINDEX;
            
            int i_dataOffset = ai_spriteData[i_offset + SpriteCollection.OFFSET_STATICDATAOFFSET] + SpriteCollection.ANIMATIONTABLEEOFFSET_WIDTH;
            int i_w = ash_spriteAnimationData[i_dataOffset++];
            int i_h = ash_spriteAnimationData[i_dataOffset];

            i_dataOffset = i_offset + SpriteCollection.OFFSET_SCREENX;
            int i_x = (ai_spriteData[i_dataOffset++] + 0x7F) >> 8;
            int i_y = (ai_spriteData[i_dataOffset] + 0x7F) >> 8;

            _g.fillRect(i_x,i_y,i_w,i_h);
            
            //_optimizer.addRectangleArea(i_x,i_y,i_w,i_h,_withOptimization);

            // производим восстановление тайлов игрового поля
            if (i_y<(Gamelet.I8_STARTGAMEFIELDY>>8))
            {
                int i_startcellx = i_x / (Gamelet.I8_CELLWIDTH>>8);
                int i_startcelly = i_y / (Gamelet.I8_CELLHEIGHT>>8);
            
                int i_endcellx = (i_x+i_w) / (Gamelet.I8_CELLWIDTH>>8);
                int i_endcelly = (i_y+i_h) / (Gamelet.I8_CELLHEIGHT>>8);
            
                int i_cellsw = (i_endcellx - i_startcellx)+1;
                int i_cellsh = (i_endcelly - i_startcelly)+1;

                if (i_startcellx+i_cellsw>Gamelet.FIELDCELLWIDTH)
                {
                    i_cellsw = Gamelet.FIELDCELLWIDTH - i_startcellx;
                }

                if (i_startcelly+i_cellsh>Gamelet.FIELDCELLHEIGHT)
                {
                    i_cellsh = Gamelet.FIELDCELLHEIGHT - i_startcelly;
                }
                
                int i_startareax = i_startcellx*(Gamelet.I8_CELLWIDTH>>8);
                int i_startareay = i_startcelly*(Gamelet.I8_CELLHEIGHT>>8);

                int i_startoffset = i_startcellx + i_startcelly*Gamelet.FIELDCELLWIDTH;
                
                final int [] ai_gamefieldsblocks = Gamelet.ai_GameField;
                
                while(i_cellsh>0)
                {
                    int i_xoffset = i_startoffset;
                    int i_strlen = i_cellsw;
                
                    int i_xcoord = i_startareax;
                
                    while(i_strlen>0)
                    {
                        int i_block = ai_gamefieldsblocks[i_xoffset++];
                        
                        //p_BackBufferGraphics.setColor(Color.blue);
                        //p_BackBufferGraphics.draw3DRect(i_xcoord,i_startareay,(Gamelet.I8_CELLWIDTH>>8),(Gamelet.I8_CELLHEIGHT>>8),true);

                        switch(i_block)
                        {
                            case Gamelet.BLOCK_TELEPORTIN_DOWN:
                            {
                                ImageManager.drawImage(MAP_TELE_IN,i_xcoord,i_startareay-(Gamelet.I8_CELLHEIGHT>>8));
                            };break;
                            case Gamelet.BLOCK_TELEPORTOUT_DOWN:
                            {
                                ImageManager.drawImage(MAP_TELE_OUT,i_xcoord,i_startareay-(Gamelet.I8_CELLHEIGHT>>8));
                            };break;
                            case Gamelet.BLOCK_BONUSGENERATOR_DOWN:
                            {
                                ImageManager.drawImage(MAP_BONUS_GEN,i_xcoord,i_startareay-(Gamelet.I8_CELLHEIGHT>>8));
                            };break;
                            case Gamelet.BLOCK_TELEPORT1IN :
                            case Gamelet.BLOCK_TELEPORT2IN :
                            {
                                ImageManager.drawImage(MAP_TELE_IN,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_TELEPORT1OUT :
                            case Gamelet.BLOCK_TELEPORT2OUT :
                            {
                                ImageManager.drawImage(MAP_TELE_OUT,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_BONUSGENERATOR :
                            {
                                ImageManager.drawImage(MAP_BONUS_GEN,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_NONE:
                            {
                                _g.fillRect(i_xcoord,i_startareay,Gamelet.I8_CELLWIDTH>>8,Gamelet.I8_CELLHEIGHT>>8);
                            };break;
                            case Gamelet.BLOCK_BOMBA :
                            {
                                ImageManager.drawImage(MAP_BLOCK_BOMB,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_IRON :
                            {
                                ImageManager.drawImage(MAP_BLOCK_IRON,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_NORMAL1 :
                            {
                                ImageManager.drawImage(MAP_BLOCK_YELLOW01,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_NORMAL2 :
                            {
                                ImageManager.drawImage(MAP_BLOCK_BLUE01,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_NORMAL3 :
                            {
                                ImageManager.drawImage(MAP_BLOCK_MAGNA01,i_xcoord,i_startareay);
                            };break;
                            case Gamelet.BLOCK_SAND :
                            {
                                ImageManager.drawImage(MAP_BLOCK_SAND,i_xcoord,i_startareay);
                            };break;
                        }

                        i_xcoord += (Gamelet.I8_CELLWIDTH>>8);
                        i_strlen--;
                    }
                    
                    i_startareay += (Gamelet.I8_CELLHEIGHT>>8);
                    
                    if (i_startareay>=(Gamelet.I8_STARTGAMEFIELDY>>8)) break;
                    
                    i_startoffset += Gamelet.FIELDCELLWIDTH;
                    i_cellsh--;
                }
            }
            
            // проверяем на взаимодействие с линией отражателей
            if(i_y+i_h>=Gamelet.YOFREFLECTIONLINE)
            {
                // попадает, значит отрисовываем
                final int [] ai_reflection = Gamelet.ai_ReflectionLine;

                int i_startcellx = i_x / (Gamelet.I8_CELLWIDTH>>8);
                int i_endcellx = (i_x+i_w) / (Gamelet.I8_CELLWIDTH>>8);
                
                int i_width = (i_endcellx-i_startcellx)+1;
                
                if (i_startcellx+i_width>Gamelet.FIELDCELLWIDTH)
                {
                    i_width = Gamelet.FIELDCELLWIDTH-i_startcellx;
                }
                
                // отрисовываем
                int i_xcoord = i_startcellx*(Gamelet.I8_CELLWIDTH>>8);
                while(i_width!=0)
                {
                    int i_block = ai_reflection[i_startcellx++];

                    if (i_block!=Gamelet.BLOCK_NONE)
                    {
                        // отрисовываем
                        ImageManager.drawImage(MAP_BLOCK_MIRROR,i_xcoord,Gamelet.YOFREFLECTIONLINE);
                    }
                    i_xcoord += (Gamelet.I8_CELLWIDTH>>8);
                    i_width--;
                }
            }
            
            if (i_prev == 0xFFFF) i_prev = -1;
            i_offset = i_prev;
        }
    }
    
    private static final void drawFullReflectionLine() throws Throwable
    {
        ImageManager.p_DestinationGraphics = p_BackBufferGraphics;
        final int [] ai_reflection = Gamelet.ai_ReflectionLine;

        p_BackBufferGraphics.setColor(BACKGROUNDCOLOR);
        
        p_BackBufferGraphics.fillRect(0,Gamelet.YOFREFLECTIONLINE,BACKBUFFERWIDTH,Gamelet.I8_CELLHEIGHT>>8);
        
        int i_startcellx = 0;
        int i_width = Gamelet.FIELDCELLWIDTH;
        
        // отрисовываем
        int i_xcoord = 0;
        while(i_width!=0)
        {
            int i_block = ai_reflection[i_startcellx++];

            if (i_block!=Gamelet.BLOCK_NONE)
            {
                // отрисовываем
                ImageManager.drawImage(MAP_BLOCK_MIRROR,i_xcoord,Gamelet.YOFREFLECTIONLINE);
            }
            i_xcoord += (Gamelet.I8_CELLWIDTH>>8);
            i_width--;
        }
    }
    
	public static final void afterGameIteration() throws Throwable
	{
		// отрисовываем ракетку
        // отрисовываем ракетку игрока
        SpriteCollection p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_PLAYERROCKET];
        p_sprCol.initIterator();
        ImageManager.p_DestinationGraphics = p_BackBufferGraphics;
        final Graphics p_g = p_BackBufferGraphics;
        
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;

            int i_spriteShield = 0;
            int i_spriteBaseOffset = 0;
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            int i_frame = p_sprCol.getFrameNumber(i_sprite);
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_PLAYERROCKETLONG:
                {
                	i_spriteShield = MAP_SHIELD_BIG;
                	i_spriteBaseOffset = 34;
                };break;
                case Gamelet.SPRITE_OBJ_PLAYERROCKETNORMAL:
                {
                	i_spriteShield = MAP_SHIELD_NORMAL;
                	i_spriteBaseOffset = 15;
                };break;
                case Gamelet.SPRITE_OBJ_PLAYERROCKETSHORT:
                {
                	i_spriteShield = MAP_SHIELD_SMALL;
                	i_spriteBaseOffset = 4;
                };break;
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                    ImageManager.drawImage(MAP_EXPLOSION1_01+(i_frame*7),i_x,i_y);
                    continue;
                }
                default:
                    System.out.println("Unknown type");
            }
            
            ImageManager.drawImage(i_spriteShield,i_x,i_y);
            ImageManager.drawImage(MAP_WAGON01+i_frame*7,i_x+i_spriteBaseOffset,i_y+5);
            
            //int i_h = p_sprCol.getWidthHeight(i_sprite);
            //int i_w = i_h>>>16;  
            //i_h &= 0xFFFF;
            //p_g.fillRect(i_x,i_y,i_w,i_h);
        }

        
        // отрисовываем взрывы
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_EXPLOSIONS];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;

            int i_map = 0;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                    switch(p_sprCol.getState(i_sprite))
                    {
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION1 :
                        {
                            i_map = MAP_EXPLOSION1_01;
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION2 :
                        {
                        	i_map = MAP_EXPLOSION2_01;   
                        };break;
                        case Gamelet.SPRITE_STATE_EXPLOSION_EXPLOSION3 :
                        {
                        	i_map = MAP_EXPLOSION3_01;
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

            ImageManager.drawImage(i_map+p_sprCol.getFrameNumber(i_sprite)*7,i_x,i_y);
            
            //int i_h = p_sprCol.getWidthHeight(i_sprite);
            //int i_w = i_h>>>16;  
            //i_h &= 0xFFFF;
            //p_g.fillRect(i_x,i_y,i_w,i_h);
        }
 
        // отрисовываем бонусы
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_BONUSES];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            int i_mapBonus = -1;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_BONUSES:
                {
                	switch(p_sprCol.getState(i_sprite))
                	{
                		case Gamelet.SPRITE_STATE_BONUSES_ADDBALL : i_mapBonus = MAP_BONUS_BALLS;break;
                		case Gamelet.SPRITE_STATE_BONUSES_LIFE :i_mapBonus = MAP_BONUS_LIFE;break;
                		case Gamelet.SPRITE_STATE_BONUSES_ADDSCORE  :i_mapBonus = MAP_BONUS_POINTS;break;
                		//case Gamelet.SPRITE_STATE_BONUSES_ : i_mapBonus = MAP_BONUS_DEATH;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BALLDECREASE : i_mapBonus = MAP_BONUS_SMALL_BALL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BALLSPEEDMINUS : i_mapBonus = MAP_BONUS_SLOW_BALL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_SMALLROCKET : i_mapBonus = MAP_BONUS_SMALL_PLATFORM;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BALLINCREASE : i_mapBonus = MAP_BONUS_BIG_BALL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BALLSPEEDPLUS  : i_mapBonus = MAP_BONUS_QUICK_BALL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BIGROCKET :i_mapBonus = MAP_BONUS_BIG_PLATFORM;break;
                		case Gamelet.SPRITE_STATE_BONUSES_MAGNET  :i_mapBonus = MAP_BONUS_MAGNET;break;
                		case Gamelet.SPRITE_STATE_BONUSES_NEXTLEVEL  :i_mapBonus = MAP_BONUS_NEXT_LEVEL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_BALLNORMAL  :i_mapBonus = MAP_BONUS_NORMAL_BALL;break;
                		case Gamelet.SPRITE_STATE_BONUSES_REFLECTIONLINE  :i_mapBonus = MAP_BONUS_MIRROR;break;
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
            
            ImageManager.drawImage(MAP_BONUS_BACKGROUND01+p_sprCol.getFrameNumber(i_sprite)*7,i_x,i_y);
            ImageManager.drawImage(i_mapBonus,i_x+15,i_y+15);
            
            //int i_h = p_sprCol.getWidthHeight(i_sprite);
            //int i_w = i_h>>>16;  
            //i_h &= 0xFFFF;
            
            //p_g.fillRect(i_x,i_y,i_w,i_h);
        }
        
        // отрисовываем мячик
        p_sprCol = Gamelet.ap_SpriteCollections[Gamelet.COLLECTIONID_PLAYERBALLS];
        p_sprCol.initIterator();
        while(true)
        {
            int i_sprite = p_sprCol.nextActiveSpriteOffset();
            if (i_sprite<0) break;
            
            int i_map = 0;
            
            switch(p_sprCol.getType(i_sprite))
            {
                case Gamelet.SPRITE_OBJ_BALL:
                {
                    switch(p_sprCol.getState(i_sprite))
                    {
                        case Gamelet.SPRITE_STATE_BALL_BIG :
                        {
                        	i_map = MAP_BALL_BIG01;
                        };break;
                        case Gamelet.SPRITE_STATE_BALL_NORMAL :
                        {
                        	i_map = MAP_BALL_NORMAL01;   
                        };break;
                        case Gamelet.SPRITE_STATE_BALL_SMALL :
                        {
                        	i_map = MAP_BALL_SMALL01;
                        };break;
                        default: System.err.println("unfnown state");    
                    }
                };break;
                case Gamelet.SPRITE_OBJ_EXPLOSION:
                {
                	i_map = MAP_EXPLOSION1_01;
                };break;
            }
            
            int i_y = p_sprCol.getScreenXY(i_sprite);
            int i_x = (short)(i_y>>>16);  
            i_y = (short)(i_y);
            
            ImageManager.drawImage(i_map+p_sprCol.getFrameNumber(i_sprite)*7,i_x,i_y);
            
            //int i_h = p_sprCol.getWidthHeight(i_sprite);
            //int i_w = i_h>>>16;  
            //i_h &= 0xFFFF;
            //p_g.fillRect(i_x,i_y,i_w,i_h);
        }
	}

    private static final void drawReflectBlockAt(int _offset) throws Throwable
    {
        int i_scrX = _offset * (Gamelet.I8_CELLWIDTH>>8);
        
        int i_block = Gamelet.ai_ReflectionLine[_offset];
        
        Graphics p_g = p_BackBufferGraphics;
        p_g.setColor(BACKGROUNDCOLOR);
        
        switch(i_block)
        {
            case Gamelet.BLOCK_NONE : 
            {
                p_g.fillRect(i_scrX,Gamelet.YOFREFLECTIONLINE,Gamelet.I8_CELLWIDTH>>8,Gamelet.I8_CELLHEIGHT>>8);
            };break;
            default:
            {
                ImageManager.drawImage(MAP_BLOCK_MIRROR,i_scrX,Gamelet.YOFREFLECTIONLINE);
            }
        }
    }
    
	private static final void drawBlockAt(int _offset) throws Throwable
	{
		int i_x = _offset % Gamelet.FIELDCELLWIDTH;
		int i_y = _offset / Gamelet.FIELDCELLWIDTH;
		
		int i_scrX = i_x * (Gamelet.I8_CELLWIDTH>>8);
		int i_scrY = i_y * (Gamelet.I8_CELLHEIGHT>>8);
		
		int i_block = Gamelet.ai_GameField[_offset];
        
        Graphics p_g = p_BackBufferGraphics;
        ImageManager.p_DestinationGraphics = p_g;
        p_g.setColor(BACKGROUNDCOLOR);
        
		switch(i_block)
		{
			case Gamelet.BLOCK_NONE : 
            {
                p_g.fillRect(i_scrX,i_scrY,Gamelet.I8_CELLWIDTH>>8,Gamelet.I8_CELLHEIGHT>>8);
            };break;
            case Gamelet.BLOCK_TELEPORTIN_DOWN:
            {
                ImageManager.drawImage(MAP_TELE_IN,i_scrX,i_scrY-(Gamelet.I8_CELLHEIGHT>>8));
            };break;
            case Gamelet.BLOCK_TELEPORTOUT_DOWN:
            {
                ImageManager.drawImage(MAP_TELE_OUT,i_scrX,i_scrY-(Gamelet.I8_CELLHEIGHT>>8));
            };break;
            case Gamelet.BLOCK_BONUSGENERATOR_DOWN:
            {
                ImageManager.drawImage(MAP_BONUS_GEN,i_scrX,i_scrY-(Gamelet.I8_CELLHEIGHT>>8));
            };break;
			case Gamelet.BLOCK_TELEPORT1IN :
			case Gamelet.BLOCK_TELEPORT2IN :
            {
                ImageManager.drawImage(MAP_TELE_IN,i_scrX,i_scrY);
            };break;
			case Gamelet.BLOCK_TELEPORT1OUT :
			case Gamelet.BLOCK_TELEPORT2OUT :
            {
                ImageManager.drawImage(MAP_TELE_OUT,i_scrX,i_scrY);
            };break;
			case Gamelet.BLOCK_BONUSGENERATOR :
            {
                ImageManager.drawImage(MAP_BONUS_GEN,i_scrX,i_scrY);
            };break;
			case Gamelet.BLOCK_BOMBA :
			{
				ImageManager.drawImage(MAP_BLOCK_BOMB,i_scrX,i_scrY);
			};break;
			case Gamelet.BLOCK_IRON :
			{
				ImageManager.drawImage(MAP_BLOCK_IRON,i_scrX,i_scrY);
			};break;
			case Gamelet.BLOCK_NORMAL1 :
			{
				ImageManager.drawImage(MAP_BLOCK_YELLOW01,i_scrX,i_scrY);
			};break;
			case Gamelet.BLOCK_NORMAL2 :
			{
				ImageManager.drawImage(MAP_BLOCK_BLUE01,i_scrX,i_scrY);
			};break;
			case Gamelet.BLOCK_NORMAL3 :
			{
				ImageManager.drawImage(MAP_BLOCK_MAGNA01,i_scrX,i_scrY);
			};break;
			case Gamelet.BLOCK_SAND :
			{
				ImageManager.drawImage(MAP_BLOCK_SAND,i_scrX,i_scrY);
			};break;
		}
		
	}
	
	public static final void afterInitGameStage() throws Throwable
	{
	    ImageManager.p_DestinationGraphics = p_BackBufferGraphics;
        clearBackground();

		// отрисовываем поле
		final int [] ai_gamefield = Gamelet.ai_GameField;

		
		int i_len = ai_gamefield.length;

		while(i_len>0)
		{
			drawBlockAt(--i_len);
		}
        
        drawFullReflectionLine();
        
		showNotify();
	}
	
	public static final void prepareDrawingList()
	{
		if (lg_PaintAll)
		{
			addAreaForDrawing(0,0,640,480);
			lg_PaintAll = false;
		}
		else
		{
			addAreaForDrawing(82,80,BACKBUFFERWIDTH,BACKBUFFERHEIGHT);
		}
	}

    private static final int XOFFSET_LEVELPANEL = 72; 
    private static final int XOFFSET_BALLSPANEL = 405; 
    private static final int YOFFSET_PANEL = 38; 
    private static final int PANELWIDTH = 164; 
    private static final int PANELHEIGHT = 42; 
    
	private static final void drawPanelLevel(Graphics _g) throws Throwable
	{
		ImageManager.p_DestinationGraphics = _g;
		
		_g.drawImage(p_PanelImage,XOFFSET_LEVELPANEL,YOFFSET_PANEL,null);
		
		ImageManager.drawImage(MAP_LEVEL,83,41);
		
		int i_level = Gamelet.i_StageID+1;
		
		int i_mapNum1 = i_level<10 ? MAP_DIGIT0 : MAP_DIGIT0+7*(i_level/10);
		int i_mapNum2 = MAP_DIGIT0+(i_level%10)*7;
		
		ImageManager.drawImage(i_mapNum1,175,42);
		ImageManager.drawImage(i_mapNum2,200,42);
	}
	
	private static final void drawPanelBalls(Graphics _g) throws Throwable
	{
		ImageManager.p_DestinationGraphics = _g;
		_g.drawImage(p_PanelImage,XOFFSET_BALLSPANEL,YOFFSET_PANEL,null);

		ImageManager.drawImage(MAP_LIFE_ICO,428,41);

		int i_att = Gamelet.i_PlayerAttemptionsNumber;
		
		int i_mapNum1 = i_att<10 ? MAP_DIGIT0 : MAP_DIGIT0+7*(i_att/10);
		int i_mapNum2 = MAP_DIGIT0+(i_att%10)*7;

		ImageManager.drawImage(MAP_DIGITX,475,47);

		ImageManager.drawImage(i_mapNum1,503,42);
		ImageManager.drawImage(i_mapNum2,528,42);
	}

	public static final void clearBackground() throws Throwable
	{
        p_BackBufferGraphics.setColor(BACKGROUNDCOLOR);
        p_BackBufferGraphics.fillRect(0,0,BACKBUFFERWIDTH,BACKBUFFERHEIGHT);
	}
	
	public static final void addAreaForDrawing(int _x,int _y,int _w,int _h)
	{
		long l_area = (((long)_x)<<48) | (((long)_y)<<32) | (((long)_w)<<16) | (long)_h;
		al_areasForDrawing[i_AreasForDrawingNumber++] = l_area;
	}
	
	public static final long getRepaintArea()
	{
		while(i_AreasForDrawingNumber>0)
		{
			return al_areasForDrawing[--i_AreasForDrawingNumber];
		}
		return -1L;
	}
	
	public static final void paint(Graphics _g,boolean _outputField) throws Throwable
	{
        Rectangle p_clipBounds = _g.getClipBounds();

		switch(p_clipBounds.x)
        {
            case 0:
            {
                //System.out.println("Draw full game screen");
                
                // full screen
                // отрисовываем бордюры
                _g.setColor(new Color((145<<16)|(182<<8)|7));
                _g.fillRect(0,0,640,480);
                _g.drawImage(p_LeftBorderImage,0,0,null);
                _g.drawImage(p_TopBorderImage,80,0,null);
                _g.drawImage(p_RightBorderImage,560,0,null);
                
                // отрисовываем панели
                drawPanelLevel(_g);
                drawPanelBalls(_g);

                // поле
                if (_outputField) _g.drawImage(p_BackBuffer,82,82,null);
            };break;
            case XOFFSET_BALLSPANEL :
            case XOFFSET_LEVELPANEL :
            {
                //System.out.println("PANELS "+p_clipBounds);
                _g.drawImage(p_TopBorderImage,80,0,null);
                // панель шариков
                drawPanelBalls(_g);
                // панель уровня
                drawPanelLevel(_g);
            };break;
            default:
            {
                if (_outputField) _g.drawImage(p_BackBuffer,82,82,null);
            }
        }
	}

    private static final int MAP_BALL_BIG01 = 0;
    private static final int MAP_BALL_BIG02 = 7;
    private static final int MAP_BALL_BIG03 = 14;
    private static final int MAP_BALL_BIG04 = 21;
    private static final int MAP_BALL_BIG05 = 28;
    private static final int MAP_BALL_BIG06 = 35;
    private static final int MAP_BALL_NORMAL01 = 42;
    private static final int MAP_BALL_NORMAL02 = 49;
    private static final int MAP_BALL_NORMAL03 = 56;
    private static final int MAP_BALL_SMALL01 = 63;
    private static final int MAP_BALL_SMALL02 = 70;
    private static final int MAP_BLOCK_BLUE01 = 77;
    private static final int MAP_BLOCK_BOMB = 84;
    private static final int MAP_BLOCK_IRON = 91;
    private static final int MAP_BLOCK_MAGNA01 = 98;
    private static final int MAP_BLOCK_MIRROR = 105;
    private static final int MAP_BLOCK_SAND = 112;
    private static final int MAP_BLOCK_YELLOW01 = 119;
    private static final int MAP_BONUS_BACKGROUND01 = 126;
    private static final int MAP_BONUS_BACKGROUND02 = 133;
    private static final int MAP_BONUS_BACKGROUND03 = 140;
    private static final int MAP_BONUS_BALLS = 147;
    private static final int MAP_BONUS_BIG_BALL = 154;
    private static final int MAP_BONUS_BIG_PLATFORM = 161;
    private static final int MAP_BONUS_DEATH = 168;
    private static final int MAP_BONUS_GEN = 175;
    private static final int MAP_BONUS_LIFE = 182;
    private static final int MAP_BONUS_MAGNET = 189;
    private static final int MAP_BONUS_MIRROR = 196;
    private static final int MAP_BONUS_NEXT_LEVEL = 203;
    private static final int MAP_BONUS_NORMAL_BALL = 210;
    private static final int MAP_BONUS_POINTS = 217;
    private static final int MAP_BONUS_QUICK_BALL = 224;
    private static final int MAP_BONUS_SLOW_BALL = 231;
    private static final int MAP_BONUS_SMALL_BALL = 238;
    private static final int MAP_BONUS_SMALL_PLATFORM = 245;
    public static final int MAP_DIGIT0 = 252;
    private static final int MAP_DIGIT1 = 259;
    private static final int MAP_DIGIT2 = 266;
    private static final int MAP_DIGIT3 = 273;
    private static final int MAP_DIGIT4 = 280;
    private static final int MAP_DIGIT5 = 287;
    private static final int MAP_DIGIT6 = 294;
    private static final int MAP_DIGIT7 = 301;
    private static final int MAP_DIGIT8 = 308;
    private static final int MAP_DIGIT9 = 315;
    private static final int MAP_DIGITX = 322;
    private static final int MAP_EXPLOSION1_01 = 329;
    private static final int MAP_EXPLOSION1_02 = 336;
    private static final int MAP_EXPLOSION1_03 = 343;
    private static final int MAP_EXPLOSION1_04 = 350;
    private static final int MAP_EXPLOSION1_05 = 357;
    private static final int MAP_EXPLOSION1_06 = 364;
    private static final int MAP_EXPLOSION1_07 = 371;
    private static final int MAP_EXPLOSION1_08 = 378;
    private static final int MAP_EXPLOSION2_01 = 385;
    private static final int MAP_EXPLOSION2_02 = 392;
    private static final int MAP_EXPLOSION2_03 = 399;
    private static final int MAP_EXPLOSION2_04 = 406;
    private static final int MAP_EXPLOSION2_05 = 413;
    private static final int MAP_EXPLOSION2_06 = 420;
    private static final int MAP_EXPLOSION3_01 = 427;
    private static final int MAP_EXPLOSION3_02 = 434;
    private static final int MAP_EXPLOSION3_03 = 441;
    private static final int MAP_EXPLOSION3_04 = 448;
    private static final int MAP_EXPLOSION3_05 = 455;
    private static final int MAP_EXPLOSION3_06 = 462;
    private static final int MAP_EXPLOSION3_07 = 469;
    private static final int MAP_EXPLOSION3_08 = 476;
    private static final int MAP_LEVEL = 483;
    private static final int MAP_LIFE_ICO = 490;
    private static final int MAP_SHIELD_BIG = 497;
    private static final int MAP_SHIELD_NORMAL = 504;
    private static final int MAP_SHIELD_SMALL = 511;
    private static final int MAP_TELE_IN = 518;
    private static final int MAP_TELE_OUT = 525;
    private static final int MAP_WAGON01 = 532;
    private static final int MAP_WAGON02 = 539;
    private static final int MAP_WAGON03 = 546;
    private static final int MAP_WAGON04 = 553; }
