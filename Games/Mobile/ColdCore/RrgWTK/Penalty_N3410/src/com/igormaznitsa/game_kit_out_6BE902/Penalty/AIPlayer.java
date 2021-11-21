package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class AIPlayer
{
    public int getAIPlayerMove(int _mode, int _submode, int _param)
    {
        int i_value = 0;
        switch (_mode)
        {
            case Penalty_SB.MODE_GOALKEEPER:
                {
                    switch (_submode)
                    {
                        case Penalty_SB.SUBMODE_SELECTSIDE:
                            {
                                int i_in = Penalty_SB.p_rnd.getInt(30);
                                if (i_in <= 10)
                                    i_value = Penalty_SB.SIDE_LEFT;
                                else if (i_in <= 20)
                                    i_value = Penalty_SB.SIDE_CENTER;
                                else
                                    i_value = Penalty_SB.SIDE_RIGHT;
                            }
                            ;
                            break;
                        case Penalty_SB.SUBMODE_SELECTCORNER:
                            {
                                switch (Penalty_SB.p_rnd.getInt(8))
                                {
                                    case 0:
                                        i_value = Penalty_SB.CORNER_CENTERCENTER;
                                        break;
                                    case 1:
                                        i_value = Penalty_SB.CORNER_CENTERDOWN;
                                        break;
                                    case 2:
                                        i_value = Penalty_SB.CORNER_CENTERUP;
                                        break;
                                    case 3:
                                        i_value = Penalty_SB.CORNER_LEFTCENTER;
                                        break;
                                    case 4:
                                        i_value = Penalty_SB.CORNER_LEFTDOWN;
                                        break;
                                    case 5:
                                        i_value = Penalty_SB.CORNER_LEFTUP;
                                        break;
                                    case 6:
                                        i_value = Penalty_SB.CORNER_RIGHTCENTER;
                                        break;
                                    case 7:
                                        i_value = Penalty_SB.CORNER_RIGHTDOWN;
                                        break;
                                    case 8:
                                        i_value = Penalty_SB.CORNER_RIGHTUP;
                                        break;
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case Penalty_SB.MODE_PLAYER:
                {
                    switch (_submode)
                    {
                        case Penalty_SB.SUBMODE_SELECTCORNER:
                            {
                                switch (_param)
                                {
                                    case Penalty_SB.SIDE_LEFT:
                                        {
                                            switch (Penalty_SB.p_rnd.getInt(5))
                                            {
                                                case 0:
                                                    i_value = Penalty_SB.CORNER_CENTERCENTER;
                                                    break;
                                                case 1:
                                                    i_value = Penalty_SB.CORNER_CENTERDOWN;
                                                    break;
                                                case 2:
                                                    i_value = Penalty_SB.CORNER_CENTERUP;
                                                    break;
                                                case 3:
                                                    i_value = Penalty_SB.CORNER_RIGHTCENTER;
                                                    break;
                                                case 4:
                                                    i_value = Penalty_SB.CORNER_RIGHTDOWN;
                                                    break;
                                                case 5:
                                                    i_value = Penalty_SB.CORNER_RIGHTUP;
                                                    break;
                                            }
                                        }
                                        ;
                                        break;
                                    case Penalty_SB.SIDE_CENTER:
                                        {
                                            switch (Penalty_SB.p_rnd.getInt(8))
                                            {
                                                case 0:
                                                    i_value = Penalty_SB.CORNER_CENTERCENTER;
                                                    break;
                                                case 1:
                                                    i_value = Penalty_SB.CORNER_CENTERDOWN;
                                                    break;
                                                case 2:
                                                    i_value = Penalty_SB.CORNER_CENTERUP;
                                                    break;
                                                case 3:
                                                    i_value = Penalty_SB.CORNER_LEFTCENTER;
                                                    break;
                                                case 4:
                                                    i_value = Penalty_SB.CORNER_LEFTDOWN;
                                                    break;
                                                case 5:
                                                    i_value = Penalty_SB.CORNER_LEFTUP;
                                                    break;
                                                case 6:
                                                    i_value = Penalty_SB.CORNER_RIGHTCENTER;
                                                    break;
                                                case 7:
                                                    i_value = Penalty_SB.CORNER_RIGHTDOWN;
                                                    break;
                                                case 8:
                                                    i_value = Penalty_SB.CORNER_RIGHTUP;
                                                    break;
                                            }
                                        }
                                        ;
                                        break;
                                    case Penalty_SB.SIDE_RIGHT:
                                        {
                                            switch (Penalty_SB.p_rnd.getInt(5))
                                            {
                                                case 0:
                                                    i_value = Penalty_SB.CORNER_CENTERCENTER;
                                                    break;
                                                case 1:
                                                    i_value = Penalty_SB.CORNER_CENTERDOWN;
                                                    break;
                                                case 2:
                                                    i_value = Penalty_SB.CORNER_CENTERUP;
                                                    break;
                                                case 3:
                                                    i_value = Penalty_SB.CORNER_LEFTCENTER;
                                                    break;
                                                case 4:
                                                    i_value = Penalty_SB.CORNER_LEFTDOWN;
                                                    break;
                                                case 5:
                                                    i_value = Penalty_SB.CORNER_LEFTUP;
                                                    break;
                                        }
                                        ;
                                        break;
                                }
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }

        return i_value;
    }
}
