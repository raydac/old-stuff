//#excludeif GAMEMODE!="VideoBall"
import ru.coldcore.game.Game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Canvas;

public class gm_VideoBall extends Game
{
    private static final int COLOR_PLAYER = 0x00FF00;
    private static final int COLOR_OPPONENT = 0xFF7777;
    private static final int COLOR_BALL = 0xFFFF00;
    private static final int COLOR_PLAYER_SCORE = 0x00A000;
    private static final int COLOR_OPPONENT_SCORE = 0xA03333;

    private static int i8_coeffW;
    private static int i8_coeffH;

    private static Image p_BackgroundImage;

    private static int i_CanvasWidth;
    private static int i_CanvasHeight;

    private static final int OFFSETY = 10;

    private static final int ROCKET_WIDTH = 20;
    private static final int ROCKET_HEIGHT = 6;

    private static final int OPPONENTY = OFFSETY - (ROCKET_HEIGHT >> 1);
    private static final int PLAYERY = IDEALHEIGHT - OFFSETY - (ROCKET_HEIGHT >> 1);

    private static final int BORDER_WIDTH = 5;
    private static final int ROCKETHORZSPEED = 8;
    private static final int BALLWIDTH = 8;
    private static final int BALLSPEED = 7;

    private static final int SEGMENTWIDTH = 5;
    private static final int SEGMENTHEIGHT = 5;
    private static final int LINEWIDTH = 2;
    private static final int LINEHEIGHT = 2;

    private static final int NUMBERWIDTH = (LINEWIDTH << 1) + SEGMENTWIDTH;
    private static final int NUMBERHEIGHT = (LINEHEIGHT * 3) + (SEGMENTHEIGHT << 1);

    private static final int SCORE_PLAYER_X = BORDER_WIDTH << 1;
    private static final int SCORE_PLAYER_Y = IDEALHEIGHT - (BORDER_WIDTH << 1) - NUMBERHEIGHT;
    private static final int SCORE_OPPONENT_X = IDEALWIDTH - (BORDER_WIDTH << 1) - (NUMBERWIDTH << 1) - LINEWIDTH;
    private static final int SCORE_OPPONENT_Y = BORDER_WIDTH << 1;

    private int i_BallX;
    private int i_BallY;
    private int i_OpponentX;
    private int i_PlayerX;
    private int i_BallDX;
    private int i_BallDY;

    private int i_Score_Player;
    private int i_Score_Opponent;

    private boolean lg_BallIn;

    private boolean lg_GradientOn;

    private int LEFTBORDER = BORDER_WIDTH;
    private int RIGHTBORDER = IDEALWIDTH - BORDER_WIDTH;

    public boolean initGameSession(int _canvasWidth, int _canvasHeight, int _i8coeffWidth, int _i8coeffHeight, int _colorsNumber,byte [] _gameData)
    {
        i8_coeffW = _i8coeffWidth;
        i8_coeffH = _i8coeffHeight;

        i_CanvasWidth = _canvasWidth;
        i_CanvasHeight = _canvasHeight;

        if (_colorsNumber <= 256)
            lg_GradientOn = false;
        else
            lg_GradientOn = true;

        // –азмеры объектов
        makeBackgroundImage();

        restartGameSession();

        if (_gameData[0]!=0)
        {
            // есть сохраненна€ игра
            i_BallDX = _gameData[1];
            i_BallDY = _gameData[2];
            i_BallX = _gameData[3];
            i_BallY = _gameData[4];
            i_PlayerX = _gameData[5];
            i_OpponentX = _gameData[6];
            i_Score_Opponent = _gameData[7];
            i_Score_Player = _gameData[8];

            lg_BallIn = _gameData[9] != 0;

            makeBackgroundImage();
        }

        return true;
    }

    private void processBall()
    {
        //int i_oldX = i_BallX;
        int i_oldY = i_BallY;

        i_BallX += i_BallDX;
        i_BallY += i_BallDY;

        if (lg_BallIn)
        {
            // проверка на прохождение центра
            if (i_BallX >= (IDEALWIDTH >> 1))
            {
                if (getRandomInt(100) > 50)
                {
                    i_BallDX = 0 - (BALLSPEED >> 1);
                }
                else
                {
                    i_BallDX = BALLSPEED >> 1;
                }

                if (getRandomInt(100) > 50)
                {
                    i_BallDY = 0 - (BALLSPEED >> 1);
                }
                else
                {
                    i_BallDY = (BALLSPEED >> 1);
                }
                lg_BallIn = false;
            }
        }
        else
        {
            // проверка на столкновение с вертикальной границей
            if (i_BallX <= LEFTBORDER)
            {
                i_BallX = LEFTBORDER;
                i_BallDX = 0 - i_BallDX;
            }
            else if (i_BallX + BALLWIDTH > RIGHTBORDER)
            {
                i_BallX = RIGHTBORDER - BALLWIDTH;
                i_BallDX = 0 - i_BallDX;
            }

            //ѕроверка на столкновение с  ракетками
            // оппонент
            if (!(i_BallY + BALLWIDTH < OPPONENTY || i_BallY > OPPONENTY + ROCKET_HEIGHT))
            {
                // проверка на столкновение с границами
                if (!(i_oldY + BALLWIDTH < OPPONENTY || i_oldY > OPPONENTY + ROCKET_HEIGHT))
                {
                    if ((i_BallY + (BALLWIDTH >> 1)) > (OPPONENTY + (ROCKET_HEIGHT >> 1)))
                        if (!(i_BallX > i_OpponentX + ROCKET_WIDTH || i_BallX + BALLWIDTH < i_OpponentX))
                        {
                            // вертикальна€ граница
                            if (i_BallDX < 0)
                                i_BallDX = BALLSPEED;
                            else
                                i_BallDX = 0 - BALLSPEED;

                            i_BallDX += (getRandomInt(BALLSPEED) - (BALLSPEED >> 1));

                            // выравниваем
                            if (i_BallX + (BALLWIDTH >> 1) > (i_OpponentX + (ROCKET_WIDTH >> 1)))
                            {
                                // ѕрава€
                                i_BallX = i_OpponentX + ROCKET_WIDTH + 1;
                            }
                            else
                            {
                                // Ћева€
                                i_BallX = i_OpponentX - BALLWIDTH - 1;
                            }
                        }
                }
                else
                {
                    // горизонтальна€ граница
                    if (!(i_BallX + BALLWIDTH < i_OpponentX || i_BallX > i_OpponentX + ROCKET_WIDTH))
                    {
                        if (i_BallDY < 0)
                            i_BallDY = BALLSPEED;
                        else
                            i_BallDY = 0 - BALLSPEED;

                        i_BallDY += (getRandomInt(BALLSPEED) - (BALLSPEED >> 1));

                        i_BallY = OPPONENTY + ROCKET_HEIGHT;
                    }
                }
            }
            else if (!(i_BallY + BALLWIDTH < PLAYERY || i_BallY > PLAYERY + ROCKET_HEIGHT))
            {
                // проверка на столкновение с границами
                if (!(i_oldY + BALLWIDTH < PLAYERY || i_oldY > PLAYERY + ROCKET_HEIGHT))
                {
                    if ((i_BallY + (BALLWIDTH >> 1)) < (PLAYERY + (ROCKET_HEIGHT >> 1)))
                        if (!(i_BallX > i_PlayerX + ROCKET_WIDTH || i_BallX + BALLWIDTH < i_PlayerX))
                        {
                            // вертикальна€ граница
                            if (i_BallDX < 0)
                                i_BallDX = BALLSPEED;
                            else
                                i_BallDX = 0 - BALLSPEED;

                            i_BallDX += (getRandomInt(BALLSPEED) - (BALLSPEED >> 1));

                            // выравниваем
                            if (i_BallX + (BALLWIDTH >> 1) > (i_PlayerX + (ROCKET_WIDTH >> 1)))
                            {
                                // ѕрава€
                                i_BallX = i_PlayerX + ROCKET_WIDTH + 1;
                            }
                            else
                            {
                                // Ћева€
                                i_BallX = i_PlayerX - BALLWIDTH - 1;
                            }
                        }
                }
                else
                {
                    // горизонтальна€ граница
                    if (!(i_BallX + BALLWIDTH < i_PlayerX || i_BallX > i_PlayerX + ROCKET_WIDTH))
                    {
                        if (i_BallDY < 0)
                            i_BallDY = BALLSPEED;
                        else
                            i_BallDY = 0 - BALLSPEED;
                        i_BallDY += (getRandomInt(BALLSPEED) - (BALLSPEED >> 1));

                        i_BallY = PLAYERY - BALLWIDTH;
                    }
                }
            }

            // проверка
            boolean lg_ballOut = false;
            if (i_BallY > IDEALHEIGHT)
            {
                // гол игроку
                i_Score_Opponent++;
                lg_ballOut = true;
            }
            else if (i_BallY + BALLWIDTH < 0)
            {
                // гол AI
                i_Score_Player ++;
                lg_ballOut = true;
            }

            if (lg_ballOut)
            {
                if (i_Score_Player == 100 || i_Score_Opponent == 100)
                {
                    restartGame();
                }
                else
                {
                    drawScoresOnBCKG();
                    initBallIn();
                }
            }

        }
    }

    private void processAI()
    {
        int i_cx = i_OpponentX + (ROCKET_WIDTH >> 1);
        int i_bx = i_BallX + (BALLWIDTH >> 1);

        // движение ракетки противника зависит от дальности м€ча
        boolean lg_move = false;
        if (i_BallY < 70 && i_BallDY < 0 && getRandomInt(8) != 3) lg_move = true;

        if (Math.abs(i_cx - i_bx) >= ROCKETHORZSPEED && lg_move)
        {
            if (i_cx < i_bx) i_OpponentX += ROCKETHORZSPEED;
            else if (i_cx > i_bx) i_OpponentX -= ROCKETHORZSPEED;

            if (i_OpponentX < LEFTBORDER) i_OpponentX = LEFTBORDER;
            else if (i_OpponentX + ROCKET_WIDTH > RIGHTBORDER) i_OpponentX = RIGHTBORDER - ROCKET_WIDTH;
        }
    }

    private void initBallIn()
    {
        i_BallX = 0 - (BALLWIDTH << 1);
        i_BallY = (IDEALWIDTH >> 1) - (BALLWIDTH >> 1);
        lg_BallIn = true;
        i_BallDX = BALLSPEED;
        i_BallDY = 0;
    }

    private void makeBackgroundImage()
    {
        p_BackgroundImage = Image.createImage(i_CanvasWidth, i_CanvasHeight);
        drawScoresOnBCKG();
    }

    public void restartGameSession()
    {
        int i_x = (IDEALWIDTH - ROCKET_WIDTH) >> 1;
        i_PlayerX = i_x;
        i_OpponentX = i_x;

        i_Score_Player = 0;
        i_Score_Opponent = 0;

        drawScoresOnBCKG();
        initBallIn();
    }

    public byte [] releaseGameSession()
    {
        p_BackgroundImage = null;

        byte [] ab_data = new byte[GAMEDATAMAXLEN];

        ab_data [0]=0x12;
        ab_data [1]=(byte)i_BallDX;
        ab_data [2]=(byte)i_BallDY;
        ab_data [3]=(byte)i_BallX;
        ab_data [4]=(byte)i_BallY;
        ab_data [5]=(byte)i_PlayerX;
        ab_data [6]=(byte)i_OpponentX;
        ab_data [7]=(byte)i_Score_Opponent;
        ab_data [8]=(byte)i_Score_Player;
        ab_data [9]= (byte)(lg_BallIn ? 1 : 0);

        return ab_data;
    }

    private void drawNumString(String _str, Graphics _g, int _x, int _y)
    {
        char [] ach_chars = _str.toCharArray();
        for (int li = 0; li < ach_chars.length; li++)
        {
            int i_num = ach_chars[li] - 0x30;
            _x += drawNumber(i_num, _g, _x, _y);
        }
    }

    private int drawNumber(int _number, Graphics _g, int _x, int _y)
    {
        final int i_segWidth = ((SEGMENTWIDTH * i8_coeffW) + 0x7F) >> 8;
        final int i_segHeight = ((SEGMENTHEIGHT * i8_coeffH) + 0x7F) >> 8;
        final int i_lineWidth = ((LINEWIDTH * i8_coeffW) + 0x7F) >> 8;
        final int i_lineHeight = ((LINEHEIGHT * i8_coeffH) + 0x7F) >> 8;

        boolean lg_seg1 = false;
        boolean lg_seg2 = false;
        boolean lg_seg3 = false;
        boolean lg_seg4 = false;
        boolean lg_seg5 = false;
        boolean lg_seg6 = false;
        boolean lg_seg7 = false;

        switch (_number)
        {
            case 0:
            {
                lg_seg1 = true;
                lg_seg2 = true;
                lg_seg5 = true;
                lg_seg6 = true;
                lg_seg7 = true;
                lg_seg4 = true;
            }
            ;
            break;
            case 1:
            {
                lg_seg7 = true;
                lg_seg4 = true;
            }
            ;
            break;
            case 2:
            {
                lg_seg1 = true;
                lg_seg4 = true;
                lg_seg3 = true;
                lg_seg5 = true;
                lg_seg6 = true;
            }
            ;
            break;
            case 3:
            {
                lg_seg1 = true;
                lg_seg4 = true;
                lg_seg3 = true;
                lg_seg7 = true;
                lg_seg6 = true;
            }
            ;
            break;
            case 4:
            {
                lg_seg2 = true;
                lg_seg3 = true;
                lg_seg4 = true;
                lg_seg7 = true;
            }
            ;
            break;
            case 5:
            {
                lg_seg1 = true;
                lg_seg2 = true;
                lg_seg3 = true;
                lg_seg7 = true;
                lg_seg6 = true;
            }
            ;
            break;
            case 6:
            {
                lg_seg1 = true;
                lg_seg2 = true;
                lg_seg3 = true;
                lg_seg7 = true;
                lg_seg6 = true;
                lg_seg5 = true;
            }
            ;
            break;
            case 7:
            {
                lg_seg1 = true;
                lg_seg4 = true;
                lg_seg7 = true;
            }
            ;
            break;
            case 8:
            {
                lg_seg1 = true;
                lg_seg2 = true;
                lg_seg3 = true;
                lg_seg4 = true;
                lg_seg5 = true;
                lg_seg6 = true;
                lg_seg7 = true;
            }
            ;
            break;
            case 9:
            {
                lg_seg1 = true;
                lg_seg2 = true;
                lg_seg3 = true;
                lg_seg4 = true;
                lg_seg6 = true;
                lg_seg7 = true;
            }
            ;
            break;
        }

        if (lg_seg1)
        {
            _g.fillRect(_x + i_lineWidth, _y, i_segWidth, i_lineHeight);
        }
        if (lg_seg2)
        {
            _g.fillRect(_x, _y + i_lineHeight, i_lineWidth, i_segHeight);
        }
        if (lg_seg3)
        {
            _g.fillRect(_x + i_lineWidth, _y + i_lineHeight + i_segHeight, i_segWidth, i_lineHeight);
        }
        if (lg_seg4)
        {
            _g.fillRect(_x + i_lineWidth + i_segWidth, _y + i_lineHeight, i_lineWidth, i_segHeight);
        }
        if (lg_seg5)
        {
            _g.fillRect(_x, _y + (i_lineHeight << 1) + i_segHeight, i_lineWidth, i_segHeight);
        }
        if (lg_seg6)
        {
            _g.fillRect(_x + i_lineWidth, _y + (i_lineHeight << 1) + (i_segHeight << 1), i_segWidth, i_lineHeight);
        }
        if (lg_seg7)
        {
            _g.fillRect(_x + i_lineWidth + i_segWidth, _y + (i_lineHeight << 1) + i_segHeight, i_lineWidth, i_segHeight);
        }

        return ((i_lineWidth << 1) + i_segWidth) + i_lineWidth;
    }

    private void drawScoresOnBCKG()
    {
        Graphics p_gr = p_BackgroundImage.getGraphics();

        //√радиентнта€ заливка
        if (lg_GradientOn)
        {
            int i_StartColor = 0x000000;
            int i_EndColor = 0x0000A0;

            int i_cX = i_CanvasWidth >> 1;
            int i_cY = i_CanvasHeight >> 1;

            int i_steps = Math.max(i_cX, i_cY);

            int i_w = 0;
            int i_h = 0;

            int i_dr = ((((i_EndColor >>> 16) & 0xFF) - ((i_StartColor >>> 16) & 0xFF)) << 8) / i_steps;
            int i_dg = ((((i_EndColor >>> 8) & 0xFF) - ((i_StartColor >>> 8) & 0xFF)) << 8) / i_steps;
            int i_db = (((i_EndColor & 0xFF) - (i_StartColor & 0xFF)) << 8) / i_steps;

            int i_r = ((i_StartColor >>> 16) & 0xFF) << 8;
            int i_g = ((i_StartColor >>> 8) & 0xFF) << 8;
            int i_b = ((i_StartColor & 0xFF) << 8);

            while (i_steps > 0)
            {
                p_gr.setColor((((i_r >> 8) & 0xFF) << 16) | (((i_g >> 8) & 0xFF) << 8) | ((i_b >> 8) & 0xFF));
                p_gr.drawRect(i_cX, i_cY, i_w, i_h);

                i_cX--;
                i_cY--;
                i_w += 2;
                i_h += 2;

                i_r += i_dr;
                i_g += i_dg;
                i_b += i_db;

                i_steps--;
            }
        }
        else
        {
            p_gr.setColor(0x0000A0);
            p_gr.fillRect(0, 0, i_CanvasWidth, i_CanvasHeight);
        }

        p_gr.setColor(0xFFFFFF);

        // Ѕордюры
        int i_brw = ((BORDER_WIDTH * i8_coeffW) + 0x7F) >> 8;
        int i_brh = ((BORDER_WIDTH * i8_coeffH) + 0x7F) >> 8;

        p_gr.drawRect(i_brw, i_brh, i_CanvasWidth - (i_brw << 1), i_CanvasHeight - (i_brh << 1));

        p_gr.drawLine(i_brw, i_CanvasHeight >> 1, i_CanvasWidth - i_brw, i_CanvasHeight >> 1);

        int i_cw = (50 * i8_coeffW + 0x7F) >> 8;
        int i_ch = (40 * i8_coeffH + 0x7F) >> 8;
        p_gr.drawArc((i_CanvasWidth >> 1) - (i_cw >> 1), (i_CanvasHeight >> 1) - (i_ch >> 1), i_cw, i_ch, 0, 360);

        String s_strPlayer = Integer.toString(i_Score_Player);
        if (s_strPlayer.length() == 1) s_strPlayer = '0' + s_strPlayer;
        String s_strOpponent = Integer.toString(i_Score_Opponent);
        if (s_strOpponent.length() == 1) s_strOpponent = '0' + s_strOpponent;

        // ќтрисовка очков
        p_gr.setColor(COLOR_OPPONENT_SCORE);

        int i_x = ((SCORE_OPPONENT_X * i8_coeffW) + 0x7F) >> 8;
        int i_y = ((SCORE_OPPONENT_Y * i8_coeffH) + 0x7F) >> 8;
        drawNumString(s_strOpponent, p_gr, i_x, i_y);

        p_gr.setColor(COLOR_PLAYER_SCORE);

        i_x = ((SCORE_PLAYER_X * i8_coeffW) + 0x7F) >> 8;
        i_y = ((SCORE_PLAYER_Y * i8_coeffH) + 0x7F) >> 8;
        drawNumString(s_strPlayer, p_gr, i_x, i_y);
    }

    public boolean processGameSessionStep(int _pressedKey)
    {
        switch (_pressedKey)
        {
            case Canvas.LEFT :
            {
                if (i_PlayerX > LEFTBORDER)
                {
                    i_PlayerX -= ROCKETHORZSPEED;
                    if (i_PlayerX < LEFTBORDER) i_PlayerX = LEFTBORDER;
                }
            }
            ;
            break;
            case Canvas.RIGHT :
            {
                if (i_PlayerX + ROCKET_WIDTH < RIGHTBORDER)
                {
                    i_PlayerX += ROCKETHORZSPEED;
                    if (i_PlayerX + ROCKET_WIDTH > RIGHTBORDER) i_PlayerX = RIGHTBORDER - ROCKET_WIDTH;
                }
            }
            ;
            break;
        }

        processAI();

        processBall();

        return true;
    }

    public void paintGameSessionField(Graphics _g)
    {
        _g.drawImage(p_BackgroundImage, 0, 0, 0);

        // оппонент
        _g.setColor(COLOR_OPPONENT);
        _g.fillRect((i_OpponentX * i8_coeffW + 0x7F) >> 8, (OPPONENTY * i8_coeffH + 0x7F) >> 8, (ROCKET_WIDTH * i8_coeffW + 0x7F) >> 8, (ROCKET_HEIGHT * i8_coeffH + 0x7F) >> 8);

        // игрок
        _g.setColor(COLOR_PLAYER);
        _g.fillRect((i_PlayerX * i8_coeffW + 0x7F) >> 8, (PLAYERY * i8_coeffH + 0x7F) >> 8, (ROCKET_WIDTH * i8_coeffW + 0x7F) >> 8, (ROCKET_HEIGHT * i8_coeffH + 0x7F) >> 8);

        // м€чик
        _g.setColor(COLOR_BALL);
        _g.fillRect((i_BallX * i8_coeffW + 0x7F) >> 8, (i_BallY * i8_coeffH + 0x7F) >> 8, (BALLWIDTH * i8_coeffW + 0x7F) >> 8, (BALLWIDTH * i8_coeffH + 0x7F) >> 8);
    }
}
