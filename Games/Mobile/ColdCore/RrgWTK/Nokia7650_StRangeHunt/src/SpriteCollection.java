import java.io.*;

/**
 * �����, ��������� ��������� �������� � ��������� ������� ��� ������ � ���
 * (C) 2005 Raydac Research Group Ltd.
 *
 * @author ����� �������
 * @version 1.06 (28 Okt 2005)
 */
public class SpriteCollection
{
    /**
     * ����� ��� ��������� ������ ����� �� ������ ����
     */
    public static final int MASK_FRAMENUMBER = 0x7FFF;

    /**
     * ����� ��� ��������� ����� ���������� ������� �� ������ ����
     */
    private static final int MASK_SPRITEACTIVE = 0x80000000;

    /**
     * ����� ��� ��������� ������ ��������� �������
     */
    private static final int MASK_STATE = 0xFF;

    /**
     * ����� ��� ��������� ������ ���� �������
     */
    private static final int MASK_TYPE = 0xFF;

    /**
     * ����� ��� ��������� ����� �������� �������� ������� �� ������ ����
     */
    private static final int MASK_BACKANIMATION = 0x40000000;

    /**
     * ����� ��� ��������� ���������� ����� �� ����� ����� ������� �� ������ ����
     */
    private static final int MASK_NEXTFRAMEDELAY = 0x3FFF8000;

    /**
     * ����� ��� ��������� ����-��������� �������
     */
    private static final int MASK_TYPESTATE = 0xFFFF;

    /**
     * �������� ���������� ����� �� ����� ����� ������� � ����� ����
     */
    private static final int SHR_NEXTFRAMEDELAY = 15;

    /**
     * �������� ������ ���������� ����-��������� � ����� ����
     */
    private static final int SHR_NEXTTYPESTATE = 16;

    /**
     * �������� ������ ���� �������
     */
    private static final int SHR_TYPE = 8;

    /**
     * ������� ����� ������� ��������� � ������
     */
    public static final int SPRITE_ALIGN_CENTER = 0;

    /**
     * ������� ����� ������� ��������� �����
     */
    public static final int SPRITE_ALIGN_LEFT = 1;

    /**
     * ������� ����� ������� ��������� ������
     */
    public static final int SPRITE_ALIGN_RIGHT = 2;

    /**
     * ������� ����� ������� ��������� �����
     */
    public static final int SPRITE_ALIGN_TOP = 0x10;

    /**
     * ������� ����� ������� ��������� ����
     */
    public static final int SPRITE_ALIGN_DOWN = 0x20;

    /**
     * ����, ������������ ��� ������������ ����������� ��������
     */
    public static final int ANIMATION_CYCLIC = 0;

    /**
     * ����, ������������ ��� ������������ �������� � ���������� ���������� �����
     */
    public static final int ANIMATION_FROZEN = 1;

    /**
     * ����, ������������ ��� ������������ ����������� ��������
     */
    public static final int ANIMATION_PENDULUM = 2;

    /**
     * ��������� �������� ���������� �����, ��������� ��� ���� ������ � �������
     */
    public static final int SPRITEDATA_LENGTH = 10;

    /**
     * ��������� �������� ���������� ����, ��������� ��� ���������� ������ �������
     */
    public static final int SPRITEDATA_SAVED_LENGTH = 26;

    /**
     * ����, ������������, ��� ������ ������ ���� ��������� ��� ���������� ����� ������� �������� ��������
     */
    public static final int SPRITEBEHAVIOUR_RELEASE = 0xFF02;

    /**
     * ����, ������������, ��� ������ ���� ������� �������, ������������ � ���������� ����� �������� �������� ��� ��������� �������
     */
    public static final int SPRITEBEHAVIOUR_NOTIFY = 0xFF01;

    //-----------------------------�������� ��� ������� ��������--------------------------------------
    /**
     * �������� �� ������, ���������� ��������� �� ������ ������ ����������� ������� � ������, ���� -1 �� ��� ��������� ������
     */
    private static final int SPRITEDATAOFFSET_PREVLISTINDEX = 0;

    /**
     * �������� �� ������, ���������� ��������� �� ������ ������ ������������ ������� � ������, ���� -1 �� ��� ��������� ������
     */
    private static final int SPRITEDATAOFFSET_NEXTLISTINDEX = 1;


    /**
     * �������� �� ������, ���������� ��� �������, � ��� �� ��� � ��������� ������� � ������� �� ������ ������� ��� ���������� ����� �������� ��������
     * ������:
     * nextType<<24 | nextState<<16 | currentType<<8 | currentState
     * ���������� nextType<<8 | nextState ����� ��������� SPRITEBEHAVIOUR ����
     */
    public static final int SPRITEDATAOFFSET_OBJECTTYPESTATE = 2; // [���/��������� �� ��������� ��������]<<16 ������� ��� � ���������

    /**
     * �������� �� ������, �������� ������ �������� ������� � ���� ��� ����������
     * ������:
     * (sprite_active ? MASK_SPRITEACTIVE : 0x0)| ((backanimation_flag ? MASK_BACKANIMATION  : 0x0) | (current_animation_counter<<15) | current_frame
     */
    public static final int SPRITEDATAOFFSET_ANIMATIONDATA = 3; //SPRITE_ACTIVE | BACKANIMATION |  NEXTFRAMECOUNTER<<15 | FRAME

    /**
     * �������� �� ������ �� �������� � �������� ������� � ������� �������� ��������
     */
    public static final int SPRITEDATAOFFSET_STATICDATAOFFSET = 4;

    /**
     * �������� �� ������ �������, ��������� �������� ���������� X �������
     */
    public static final int SPRITEDATAOFFSET_SCREENX = 5;

    /**
     * �������� �� ������ �������, ��������� �������� ���������� Y �������
     */
    public static final int SPRITEDATAOFFSET_SCREENY = 6;

    /**
     * �������� �� ������ �������, ��������� ���������� X �������
     */
    public static final int SPRITEDATAOFFSET_MAINX = 7;

    /**
     * �������� �� ������ �������, ��������� ���������� Y �������
     */
    public static final int SPRITEDATAOFFSET_MAINY = 8;

    /**
     * �������� �� ������ �������, ��������� ������������ ���������� � �������
     */
    public static final int SPRITEDATAOFFSET_OPTIONALDATA = 9;


    //-----------------------------�������� ��� ������� ��������--------------------------------------
    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ������ �������
     */
    private static final int ANIMATIONTABLEEOFFSET_WIDTH = 0;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ������ �������
     */
    private static final int ANIMATIONTABLEEOFFSET_HEIGHT = 1;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� �������� ������� ���� �� ��� X �� �������� ������ ���� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX = 2;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� �������� ������� ���� �� ��� Y �� �������� ������ ���� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETY = 3;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ������ ������� ���� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEWIDTH = 4;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ������ ������� ���� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEHEIGHT = 5;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ���������� ������ �������� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_FRAMES = 6;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� �������� ����� ������� ��� �������� �������
     */
    private static final int ANIMATIONTABLEEOFFSET_ANIMATIONDELAY = 7;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� ���� ��������  �������
     */
    private static final int ANIMATIONTABLEEOFFSET_ANIMATIONTYPE = 8;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� �� ������� ����� �� �������� ������ ���� �� ��� X
     */
    private static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINX = 9;

    /**
     * �������� � ������� ������ �������� �� ������ �������� �������� �� ������� ����� �� �������� ������ ���� �� ��� Y
     */
    private static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINY = 10;

    /**
     * ���������� ������ ��������� �� ������ � ������� ��������
     */
    private short[] ash_spriteAnimationDataArray;

    /**
     * ���������� ������ ���������� �������� � ���������
     */
    private int i_SpritesNumber;

    /**
     * ���������� ������ �������� � ������ ���������� ����������� �������
     */
    public int i_lastInactiveSpriteOffset;

    /**
     * ���������� ������ �������� � ������ ���������� ��������� �������
     */
    public int i_lastActiveSpriteOffset;

    /**
     * ���������� ������ ������, ���������� ��������� ��������
     */
    public int[] ai_spriteDataArray;

    /**
     * ���������� ������ ������������� ���������
     */
    public int i_CollectionID;

    /**
     * ���������� ������ �������� � �������� ������� ��� �������� �������� ��������
     */
    private int i_lastIteratorSpriteOffset;

    /**
     * ������������� ��������� �������� ��������, ����� ������ ���� ������� ����� ������ ������� ��� ������ nextActiveSpriteOffset()
     */
    public final void initIterator()
    {
        i_lastIteratorSpriteOffset = i_lastActiveSpriteOffset;
    }

    /**
     * ��������� ���������� �������� �� ��������� �������� ��������
     *
     * @return �������� � ������� �������� ��� -1 ���� ��� ������ �������� ��������
     */
    public final int nextActiveSpriteOffset()
    {
        int i_result = i_lastIteratorSpriteOffset;
        if (i_result >= 0)
        {
            i_lastIteratorSpriteOffset = ai_spriteDataArray[i_result];
        }
        return i_result;
    }

    /**
     * ����������� ��������� ��������
     *
     * @param _collectionID    ���������� ������������� ���������
     * @param _spriteNumber    ���������� �������� � ���������
     * @param _spriteDataArray ��������� �� ������, ���������� ������ �� �������� ��������
     */
    public SpriteCollection(int _collectionID, int _spriteNumber, short[] _spriteDataArray)
    {
        i_CollectionID = _collectionID;
        ash_spriteAnimationDataArray = _spriteDataArray;
        i_SpritesNumber = _spriteNumber;

        ai_spriteDataArray = new int[SPRITEDATA_LENGTH * _spriteNumber];

        i_lastActiveSpriteOffset = -1;
        i_lastInactiveSpriteOffset = 0;

        releaseAllSprites();
    }

    /**
     * ������������� ���� �������� ��������
     */
    public final void releaseAllSprites()
    {
        final int[] ai_sprites = ai_spriteDataArray;
        i_lastActiveSpriteOffset = -1;

        int i_currentSpriteOffset = 0;
        int i_index = i_SpritesNumber;
        int i_prevOffset = -1;

        while (i_index != 0)
        {
            ai_sprites[i_currentSpriteOffset] = i_prevOffset;
            if (i_index == 1)
                ai_sprites[i_currentSpriteOffset + 1] = -1;
            else
                ai_sprites[i_currentSpriteOffset + 1] = i_currentSpriteOffset + SPRITEDATA_LENGTH;

            ai_sprites[i_currentSpriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = 0;

            i_prevOffset = i_currentSpriteOffset;
            i_currentSpriteOffset += SPRITEDATA_LENGTH;
            i_index--;
        }

        i_lastInactiveSpriteOffset = i_currentSpriteOffset-SPRITEDATA_LENGTH;
    }

    /**
     * �������� �������� ���������� ����������� �������
     *
     * @return �������� � ������� ��� -1 ���� ��� ���������� ��������
     */
    public final int getOffsetOfLastInactiveSprite()
    {
        return i_lastInactiveSpriteOffset;
    }

    /**
     * ���������������� ������ �� ��������� ��������
     *
     * @param _spriteOffset �������� � ������� ������ �������
     */
    public final void releaseSprite(int _spriteOffset)
    {
        int i_lastInactiveSprOffset = i_lastInactiveSpriteOffset;
        final int[] ai_sda = ai_spriteDataArray;

        // ������� ������ �� ������ ��������
        int i_prevActiveSpriteOffset = ai_sda[_spriteOffset];
        int i_nextActiveSpriteOffset = ai_sda[_spriteOffset + 1];
        if (i_prevActiveSpriteOffset >= 0)
        {
            ai_sda[i_prevActiveSpriteOffset + 1] = i_nextActiveSpriteOffset;
        }
        if (i_nextActiveSpriteOffset >= 0)
        {
            ai_sda[i_nextActiveSpriteOffset] = i_prevActiveSpriteOffset;
        }
        if (_spriteOffset == i_lastActiveSpriteOffset)
        {
            i_lastActiveSpriteOffset = i_prevActiveSpriteOffset;
        }

        // ��������� ������ � ������ ����������
        ai_sda[_spriteOffset] = i_lastInactiveSprOffset;
        ai_sda[_spriteOffset + 1] = -1;
        if (i_lastInactiveSprOffset >= 0)
        {
            ai_sda[i_lastInactiveSprOffset + 1] = _spriteOffset;
        }
        i_lastInactiveSpriteOffset = _spriteOffset;

        // ���������� ���� ���������� �������
        ai_sda[_spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = 0;
    }

    /**
     * ������� ���������� ����������� �������� ������ � ������ ������ �������, ������������ � ���������� ��������
     *
     * @param _spriteOffset �������� ������� � �������
     * @return ���������� ����������� �������� (width << 16) | height
     */
    public final int getSpriteWidthHeight(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_WIDTH;
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return (i_w << 16) | i_h;
    }

    /**
     * ������� ���������� ����������� �������� ������ � ������ ������� ���� �������, ������������ � ���������� �������� (�� I8)
     *
     * @param _spriteOffset �������� ������� � �������
     * @return ���������� ����������� �������� (hotzonewidth << 16) | hotzoneheight
     */
    public final int getSpriteHotzoneWidthHeight(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEWIDTH;
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return (i_w << 16) | i_h;
    }

    /**
     * ������� ���������� ����������� �������� �������� ������� ���� �������, ������������ � ���������� �������� (�� I8)
     *
     * @param _spriteOffset �������� ������� � �������
     * @return ���������� ����������� �������� (hotzoneXoffset << 16) | hotzoneYoffset
     */
    public final int getSpriteHotzoneXY(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_x = ash_spriteAnimationData[i_dataOffset++];
        int i_y = ash_spriteAnimationData[i_dataOffset];

        return (i_x << 16) | (i_y&0xFFFF);
    }

    /**
     * ������� ���������� ����������� �������� �������� ��������� �������, ������������ � ���������� ��������
     *
     * @param _spriteOffset �������� ������� � �������
     * @return ���������� ����������� �������� (X << 16) | Y
     */
    public final int getSpriteScreenXY(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        int i_dataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        int i_x = (ai_spriteData[i_dataOffset++] + 0x7F) >> 8;
        int i_y = (ai_spriteData[i_dataOffset] + 0x7F) >> 8;

        return (i_x << 16) |  (i_y & 0xFFFF);
    }

    /**
     * ������� ���������� ����������� �������� ���������� ������ � ������ ������ �������, ������������ � ���������� ��������
     *
     * @param _spriteOffset �������� ������� � �������
     * @return (xoffst << 48) | (yoffst << 32) | (width << 16) | (height)
     */
    public final long getSpriteHotspotCoordinates(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_x = ash_spriteAnimationData[i_dataOffset++];
        int i_y = ash_spriteAnimationData[i_dataOffset++];
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return ((long) i_x << 48) | ((long) i_y << 32) | ((long) i_w << 16) | (long) i_h;
    }

    /**
     * ������� ���������� ����������� �������� ������ �������� ����� � ���������� ������
     *
     * @param _spriteOffset �������� ������� � �������
     * @return (framenum << 16) | (cur_frame)
     */
    public final int getSpriteFrameInfo(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;

        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
        int i_frames = ash_spriteAnimationData[i_dataOffset+ANIMATIONTABLEEOFFSET_FRAMES];
        int i_curFrame = ai_spriteData[_spriteOffset+SPRITEDATAOFFSET_ANIMATIONDATA]&MASK_FRAMENUMBER;
        return (i_frames<<16)|i_curFrame;
    }

    /**
     * �������������� ������ � �������� ��������� � �������
     *
     * @param _objectType   ��� �������
     * @param _objectState  ��� ���������
     * @param _spriteOffset �������� �� ������ �������
     */
    public final void activateSprite(int _objectType, int _objectState, final int _spriteOffset)
    {
        int i_spriteOffset = _spriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short [] ash_ssda = ash_spriteAnimationDataArray;

        // ���������, ������� �� ��� ������, ���� ��� �� ������������, ����� ���������� �����������
        int i_offsetSpriteDataAnimation = _spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA;
        if ((ai_sda[i_offsetSpriteDataAnimation] & MASK_SPRITEACTIVE) == 0)
        {
            int i_lastActiveSprOffset = i_lastActiveSpriteOffset;

            // ������� ������ �� ������ ���������� � ��������� � ������ ��������
            int i_prevInactiveSpriteOffset = ai_sda[_spriteOffset];
            int i_nextInactiveSpriteOffset = ai_sda[_spriteOffset + 1];
            if (i_prevInactiveSpriteOffset >= 0)
            {
                ai_sda[i_prevInactiveSpriteOffset + 1] = i_nextInactiveSpriteOffset;
            }
            if (i_nextInactiveSpriteOffset >= 0)
            {
                ai_sda[i_nextInactiveSpriteOffset] = i_prevInactiveSpriteOffset;
            }
            if (_spriteOffset == i_lastInactiveSpriteOffset)
            {
                i_lastInactiveSpriteOffset = i_prevInactiveSpriteOffset;
            }

            // ��������� ������ � ������ ��������
            ai_sda[_spriteOffset] = i_lastActiveSprOffset;
            ai_sda[_spriteOffset + 1] = -1;
            if (i_lastActiveSprOffset >= 0)
            {
                ai_sda[i_lastActiveSprOffset + 1] = _spriteOffset;
            }
            i_lastActiveSpriteOffset = _spriteOffset;
        }

        // ��������� ��������� �� ��� � ���������
        i_spriteOffset += SPRITEDATAOFFSET_OBJECTTYPESTATE;

        // ���� STATICDATAOFFSET
        // ������� ������ �� �������� ��� ��������� �������
        int i_staticDataOffset = ash_ssda[(ash_ssda[_objectType]) + _objectState];
        int i_maxAnimationCounter = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

        // ���������� ��� � ���������
        int i_typeState = (_objectType << SHR_TYPE) | _objectState;
        ai_sda[i_spriteOffset++] = (i_typeState << SHR_NEXTTYPESTATE) | (i_typeState);
        // ������ �������� � ���� ����������
        ai_sda[i_spriteOffset++] = MASK_SPRITEACTIVE | (i_maxAnimationCounter << SHR_NEXTFRAMEDELAY);
        // ������ �� ����������� ������
        ai_sda[i_spriteOffset++] = i_staticDataOffset;

        // ����� �������� ��� �������� ��������
        i_spriteOffset++;
        i_spriteOffset++;
        int i_curMainX = ai_sda[i_spriteOffset++];
        int i_curMainY = ai_sda[i_spriteOffset];
        setMainPointXY(_spriteOffset, i_curMainX, i_curMainY);
    }

    /**
     * ������������ �������� ��� ���� �������� ��������
     */
    public final void processAnimationForActiveSprites()
    {
        int i_lastActive = i_lastActiveSpriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        while (true)
        {
            if (i_lastActive < 0) break;
            final int i_curOffset = i_lastActive;
            i_lastActive = ai_sda[i_lastActive];

            // ��������� ��������
            int i_animationData = ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA];
            int i_nextFrameCounter = (i_animationData >>> SHR_NEXTFRAMEDELAY) & ~MASK_NEXTFRAMEDELAY;
            if (i_nextFrameCounter == 0)
            {
                // ��������� ��������
                int i_staticDataOffset = ai_sda[i_curOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
                int i_curObjectStateType = ai_sda[i_curOffset + SPRITEDATAOFFSET_OBJECTTYPESTATE];
                int i_nextObjectStateType = i_curObjectStateType >>> SHR_NEXTTYPESTATE;
                i_curObjectStateType = i_curObjectStateType & MASK_TYPESTATE;
                int i_animationType = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONTYPE];
                int i_maxFrames = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_FRAMES];
                int i_animationDelay = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

                boolean lg_backMove = (i_animationData & MASK_BACKANIMATION) != 0;
                int i_currentFrame = i_animationData & MASK_FRAMENUMBER;

                i_nextFrameCounter = i_animationDelay;
                boolean lg_iterationFinished = false;

                switch (i_animationType)
                {
                    case ANIMATION_CYCLIC:
                    {
                        i_currentFrame++;
                        if (i_currentFrame >= i_maxFrames)
                        {
                            i_currentFrame = 0;
                            lg_iterationFinished = true;
                        }
                    }
                        ;
                        break;
                    case ANIMATION_FROZEN:
                    {
                        i_currentFrame++;
                        if (i_currentFrame >= i_maxFrames)
                        {
                            i_currentFrame = i_maxFrames - 1;
                            lg_iterationFinished = true;
                        }
                    }
                        ;
                        break;
                    case ANIMATION_PENDULUM:
                    {
                        if (lg_backMove)
                        {
                            i_currentFrame--;
                            if (i_currentFrame == 0)
                            {
                                lg_backMove = false;
                                lg_iterationFinished = true;
                            }
                        } else
                        {
                            i_currentFrame++;
                            if (i_currentFrame == i_maxFrames - 1)
                            {
                                lg_backMove = true;
                                lg_iterationFinished = true;
                            }
                        }
                    }
                        ;
                        break;
                }

                i_animationData = MASK_SPRITEACTIVE | (lg_backMove ? MASK_BACKANIMATION : 0x0) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY) | i_currentFrame;
                ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = i_animationData;

                // �������� �� ��������� ��������� �������
                if ((i_nextObjectStateType != i_curObjectStateType) && lg_iterationFinished)
                {
                    if ((i_nextObjectStateType >>> SHR_TYPE) == 0xFF)
                    {
                        i_nextObjectStateType &= MASK_STATE;

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_NOTIFY & MASK_STATE))!=0)
                        {
                            // ���������� � ���������� ��������
                            Gamelet.notifySpriteAnimationCompleted(i_CollectionID, i_curOffset);
                        }

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_RELEASE & MASK_STATE))!=0)
                        {
                            // ������� ������
                            releaseSprite(i_curOffset);
                        }
                    } else
                    {
                        // ��������� � ����� ��������� � ���
                        int i_newSpriteType = i_nextObjectStateType >>> SHR_TYPE;
                        int i_newSpriteState = i_nextObjectStateType & MASK_STATE;
                        activateSprite(i_newSpriteType, i_newSpriteState, i_curOffset);
                    }
                }
            } else
            {
                i_nextFrameCounter--;
                i_animationData = (i_animationData & ~MASK_NEXTFRAMEDELAY) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY);
                ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = i_animationData;
            }
        }
    }

    /**
     * ��������� ������� �� ������ � �������� ���������
     *
     * @param _spriteOffset �������� � ������� � �������
     * @return true ���� ������ �������, ����� false
     */
    public final boolean isSpriteActive(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] & MASK_SPRITEACTIVE) != 0;
    }

    /**
     * ���������� �������� ��������� �������� ������� � ������� ������
     * @param _index ������ �������
     * @return �������� ������� � �������
     */
    public static final int getOffsetForSpriteWithIndex(int _index)
    {
        return SPRITEDATA_LENGTH*_index;
    }

    /**
     * ����������� �������� ��������� ���� � ��������� �������, ������� ����� ��������� ��� �� ��������� ������� ������������ ��������
     *
     * @param _spriteOffset  �������� ������� � �������
     * @param _nextTypeState ��� � ��������� � ������� (���<<8)|���������, ��� �� ����� ���� �� ������� SPRITEBEHAVIOUR
     */
    public final void setSpriteNextTypeState(int _spriteOffset, int _nextTypeState)
    {
        int i_dataOffset = _spriteOffset + SPRITEDATAOFFSET_OBJECTTYPESTATE;
        int i_old = ai_spriteDataArray[i_dataOffset];
        i_old = (i_old & MASK_TYPESTATE) | (_nextTypeState<<SHR_NEXTTYPESTATE);
        ai_spriteDataArray[i_dataOffset] = i_old;
    }

     /**
     * ������ ��������� �������� � �����
     *
     * @param _outStream ����� ��� ������ ���������
     * @throws Exception ���������� � ������ ������ ������
     */
    public final void saveToStream(DataOutputStream _outStream) throws Exception
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH);
        int[] ai_dataArray = ai_spriteDataArray;

        _outStream.writeInt(i_lastActiveSpriteOffset);
        _outStream.writeInt(i_lastInactiveSpriteOffset);

        int i_offset = 0;

        while (i_offset < i_sprNumber)
        {
            _outStream.writeShort(ai_dataArray[i_offset++]); // prevsprite offset
            _outStream.writeShort(ai_dataArray[i_offset++]); // nextsprite offset

            _outStream.writeInt(ai_dataArray[i_offset++]); // object state
            _outStream.writeInt(ai_dataArray[i_offset++]); // animation data

            _outStream.writeShort(ai_dataArray[i_offset++]); // static data offset

            i_offset++; // screen x
            i_offset++; // screen y

            _outStream.writeInt(ai_dataArray[i_offset++]); // main x
            _outStream.writeInt(ai_dataArray[i_offset++]); // main y

            _outStream.writeInt(ai_dataArray[i_offset++]); // optional data
        }
        _outStream.flush();
    }

    /**
     * �������� ��������� �������� �� ������
     *
     * @param _inStream ����� ��� ������ ���������
     * @throws Exception ���������� � ������ ������ ������
     */
    public final void loadFromStream(DataInputStream _inStream) throws Exception
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH);
        final int[] ai_dataArray = ai_spriteDataArray;

        i_lastActiveSpriteOffset = _inStream.readInt();
        i_lastInactiveSpriteOffset = _inStream.readInt();

        int i_offset = 0;

        while (i_offset < i_sprNumber)
        {
            int i_sprOffset = i_offset;
            ai_dataArray[i_offset] = _inStream.readShort(); // prevsprite offset
            i_offset++;
            ai_dataArray[i_offset] = _inStream.readShort(); // nextsprite offset
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readInt(); // object state
            i_offset++;

            int i_anim = _inStream.readInt();
            ai_dataArray[i_offset] = i_anim; // animation data
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readUnsignedShort(); // static data offset
            i_offset++;

            i_offset++; // screen x
            i_offset++; // screen y

            int i_mx = _inStream.readInt();
            i_offset++;
            int i_my = _inStream.readInt();
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readInt(); // optional data
            i_offset++;

            if ((i_anim & MASK_SPRITEACTIVE)!=0)
            {
                setMainPointXY(i_sprOffset,i_mx,i_my);
            }
        }

    }

    /**
     * ���������� ������ ����� ������ � ������, ��������� ��� �������� ������ ��������� �������� � �������� ����������
     *
     * @param _spritesNumber ���������� ��������
     * @return ������ ������� � ������
     */
    public static final int getDataSize(int _spritesNumber)
    {
        return 8 + SPRITEDATA_SAVED_LENGTH * _spritesNumber;
    }

    /**
     * ��������� � �������� �� ������ ������ ������� �����
     * @param _sourceSpriteOffset �������� � �������������� �������
     * @param _destSpriteOffset �������� � �������� �������
     */
    public final void alignMainPoint(int _sourceSpriteOffset,int _destSpriteOffset)
    {
        final int[] ai_sda = ai_spriteDataArray;
        int i_off = _destSpriteOffset+SPRITEDATAOFFSET_MAINX;
        int i_mainX = ai_sda[i_off++];
        int i_mainY = ai_sda[i_off];
        setMainPointXY(_sourceSpriteOffset,i_mainX,i_mainY);
    }


    /**
     * ����������� ������ � �������� ���������
     *
     * @param _clonedSpriteOffset �������� �� ������� �������, ����������� ������������
     * @return �������� �� ������ �������-����� ��� -1 ���� �� ���� ��������� ��������
     */
    public final int cloneSprite(int _clonedSpriteOffset)
    {
        final int[] ai_sda = ai_spriteDataArray;

        int i_newSpriteOffset = i_lastInactiveSpriteOffset;
        if (i_newSpriteOffset < 0) return -1;

        // ������� ������ �� ������ ���������� � ��������� � ������ ��������
        int i_prevInactiveSpriteOffset = ai_sda[i_newSpriteOffset];
        int i_nextInactiveSpriteOffset = ai_sda[i_newSpriteOffset + 1];
        if (i_prevInactiveSpriteOffset >= 0)
        {
            ai_sda[i_prevInactiveSpriteOffset + 1] = i_nextInactiveSpriteOffset;
        }
        if (i_nextInactiveSpriteOffset >= 0)
        {
            ai_sda[i_nextInactiveSpriteOffset] = i_prevInactiveSpriteOffset;
        }
        i_lastInactiveSpriteOffset = i_prevInactiveSpriteOffset;

        // ��������� ������ � ������ ��������
        int i_lastActiveSprOffset = i_lastActiveSpriteOffset;
        ai_sda[i_newSpriteOffset] = i_lastActiveSprOffset;
        ai_sda[i_newSpriteOffset + 1] = -1;
        if (i_lastActiveSprOffset >= 0)
        {
            ai_sda[i_lastActiveSprOffset + 1] = i_newSpriteOffset;
        }
        i_lastActiveSpriteOffset = i_newSpriteOffset;

        System.arraycopy(ai_sda, _clonedSpriteOffset + 2, ai_sda, i_newSpriteOffset + 2, SPRITEDATA_LENGTH - 2);

        return i_newSpriteOffset;
    }

    /**
     * ���������� ��� ������� �� ��������
     * @param _spriteOffset �������� �������
     * @return ��� �������
     */
    public final int getSpriteType(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset+SPRITEDATAOFFSET_OBJECTTYPESTATE]>>>8) & 0xFF;
    }

    /**
     * ���������� ��������� ������� �� ��������
     * @param _spriteOffset �������� �������
     * @return ��������� �������
     */
    public final int getSpriteState(int _spriteOffset)
    {
        return ai_spriteDataArray[_spriteOffset+SPRITEDATAOFFSET_OBJECTTYPESTATE] & 0xFF;
    }

    /**
     * ����� ������, �������������� � �������� ��������, �������� ���������
     *
     * @param _spriteOffset     �������� �� ������������ ������� � �������� ���������
     * @param _spriteCollection �������� ��������� ��������
     * @param _initSpriteOffset �������� �������, � ����������� �������� ���� ���������� �������� (�������� ���� �� ������ � �����), ���� -1 �� �������� ���� � ������ ���������� ��������� �������
     * @return �������� �� ������� ��������������� ������� ��� -1 ���� ������� �� ������
     */
    public final int findCollidedSprite(int _spriteOffset, SpriteCollection _spriteCollection, int _initSpriteOffset)
    {
        boolean lg_ignoreThisOffset = _spriteCollection.i_CollectionID == i_CollectionID;

        int[] ai_dataArray = _spriteCollection.ai_spriteDataArray;

        short[] ash_staticData = ash_spriteAnimationDataArray;
        int i_indexOffset = _spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET;
        int i_dataOffset = ai_dataArray[i_indexOffset++];
        int i_ScrX = ai_dataArray[i_indexOffset++];
        int i_ScrY = ai_dataArray[i_indexOffset];

        i_indexOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_mainHotXoff = i_ScrX + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotYoff = i_ScrY + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotW = i_mainHotXoff + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotH = i_mainHotYoff + ((int)ash_staticData[i_indexOffset]<<8);

        ai_dataArray = ai_spriteDataArray;

        int i_lastActive;

        if (_initSpriteOffset<0)
            i_lastActive = i_lastActiveSpriteOffset;
        else
        {
            i_lastActive = ai_dataArray[_initSpriteOffset];
        }

        while (true)
        {
            if (i_lastActive < 0) break;

            if (lg_ignoreThisOffset && i_lastActive == _spriteOffset)
            {
                i_lastActive = ai_dataArray[i_lastActive];
                continue;
            }

            i_indexOffset = i_lastActive + SPRITEDATAOFFSET_STATICDATAOFFSET;
            i_dataOffset = ai_dataArray[i_indexOffset++];
            i_ScrX = ai_dataArray[i_indexOffset++];
            i_ScrY = ai_dataArray[i_indexOffset];
            i_dataOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;

            int i_cHotXoff = i_ScrX + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotYoff = i_ScrY + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotW = i_cHotXoff + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotH = i_cHotYoff + ((int)ash_staticData[i_dataOffset]<<8);

            if (i_mainHotW < i_cHotXoff || i_mainHotH < i_cHotYoff || i_cHotW < i_mainHotXoff || i_cHotH < i_mainHotYoff)
            {
                i_lastActive = ai_dataArray[i_lastActive];
            } else
            {
                return i_lastActive;
            }
        }
        return -1;
    }

    /**
     * ���������� ������������ ������ ��� �������
     * @param _spriteOffset �������� �������
     * @return ��������
     */
    public final int getOptionalData(int _spriteOffset)
    {
        return ai_spriteDataArray[_spriteOffset+SPRITEDATAOFFSET_OPTIONALDATA];
    }

    /**
     * ���������� ������������ ������ �������
     * @param _spriteOffset �������� �������
     * @param _value ��������
     */
    public final void setOptionalData(int _spriteOffset,int _value)
    {
        ai_spriteDataArray[_spriteOffset+SPRITEDATAOFFSET_OPTIONALDATA] = _value;
    }

    /**
     * �������� ���������� ������� ����� ��� ������� � �������� ���������
     *
     * @param _spriteOffset �������� ������� � �������
     * @param _deltaX       �������� �� ��� X ������� �����
     * @param _deltaY       �������� �� ��� Y ������� �����
     */
    public final void moveMainPointXY(int _spriteOffset, int _deltaX, int _deltaY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = (0-ash_ssda[i_staticDataOffset++])<<8;
        int i_offsetY = (0-ash_ssda[i_staticDataOffset])<<8;

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_MAINX;
        int i_mainX = ai_sda[i_staticDataOffset++] + _deltaX;
        int i_mainY = ai_sda[i_staticDataOffset] + _deltaY;

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = i_mainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = i_mainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = i_mainX;
        ai_sda[i_staticDataOffset++] = i_mainY;
    }


    /**
     * ��������� ���������� ������� ����� ��� ������� � �������� ���������
     *
     * @param _spriteOffset �������� ������� � �������
     * @param _i8newMainX     ����� �������� X ������� �����
     * @param _i8newMainY     ����� �������� Y ������� �����
     */
    public final void setMainPointXY(int _spriteOffset, int _i8newMainX, int _i8newMainY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = 0-(ash_ssda[i_staticDataOffset++]<<8);
        int i_offsetY = 0-(ash_ssda[i_staticDataOffset]<<8);

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = _i8newMainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = _i8newMainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = _i8newMainX;
        ai_sda[i_staticDataOffset++] = _i8newMainY;
    }

    /**
     * ����������� ������ �� �������� �������
     * @param _spriteOffset �������� �������
     * @param _i8x1 ���������� X ������� ����� ����� �������
     * @param _i8y1 ���������� Y ������� ����� ����� �������
     * @param _i8x2 ���������� X ������ ������ ����� �������
     * @param _i8y2 ���������� Y ������ ������ ����� �������
     * @param _screenCoords ������������ �������� ��������� ���� true � main ����� ���� false
     */
    public final void alignSpriteToArea(int _spriteOffset,int _i8x1,int _i8y1,int _i8x2,int _i8y2,boolean _screenCoords)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_deltaX = 0;
        int i_deltaY = 0;

        if (_screenCoords)
        {
            // �������� ����������
            int i_off = _spriteOffset+SPRITEDATAOFFSET_SCREENX;
            int i_sx = ai_sda[i_off++];
            int i_sy = ai_sda[i_off];

            int i_staticDataOffset = ai_sda[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET]+ANIMATIONTABLEEOFFSET_WIDTH;
            int i_w = ash_ssda[i_staticDataOffset++]<<8;
            int i_h = ash_ssda[i_staticDataOffset]<<8;

            if (i_sx<_i8x1)
            {
                i_deltaX = _i8x1 - i_sx;
            }
            else
            if (i_sx+i_w>_i8x2)
            {
                i_deltaX = _i8x2-(i_sx+i_w);
            }

            if (i_sy<_i8y1)
            {
                i_deltaY = _i8y1 - i_sy;
            }
            else
            if (i_sy+i_h>_i8y2)
            {
                i_deltaY = _i8y2 - (i_sy+i_h);
            }
        }
        else
        {
            // ���������� main point
            int i_off = _spriteOffset+SPRITEDATAOFFSET_MAINX;
            int i_mx = ai_sda[i_off++];
            int i_my = ai_sda[i_off];

            if (i_mx<_i8x1)
            {
                i_deltaX = _i8x1 - i_mx;
            }
            else
            if (i_mx>_i8x2)
            {
                i_deltaX = _i8x2 - i_mx;
            }

            if (i_my<_i8y1)
            {
                i_deltaY = _i8y1 - i_my;
            }
            else
            if (i_my>_i8y2)
            {
                i_deltaY = _i8y2 - i_my;
            }
        }

        if ((i_deltaX | i_deltaY)!=0)
        {
            moveMainPointXY(_spriteOffset,i_deltaX,i_deltaY);
        }
    }
}
