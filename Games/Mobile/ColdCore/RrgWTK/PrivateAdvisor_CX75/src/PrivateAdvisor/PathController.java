package PrivateAdvisor;

public class PathController
{
    public short[] as_pathArray;
    private int i_initOffset;
    private int i_currentOffset;
    public Sprite p_sprite;
    private int i_length;
    private int i_curLength;
    private int i8_dx;
    private int i8_dy;
    private int i8_targetX;
    private int i8_targetY;
    private int i8_curX;
    private int i8_curY;
    private boolean lg_completed;

    public int i8_centerX;
    public int i8_centerY;

    protected boolean lg_back;

    private static final int POINT_DATA_SIZE = 3;


    public PathController()
    {
        lg_completed = true;
    }

    public void deactivate()
    {
        lg_completed = true;
    }

    public final int getCurrentPointIndex()
    {
        return i_curLength;
    }

    private final void calculateDifferents(boolean _speedPrev)
    {
        int i_steps = 0;
        if (_speedPrev) i_steps = as_pathArray[i_currentOffset++];
        i8_targetX = (int) (as_pathArray[i_currentOffset++] << 8);
        i8_targetY = (int) (as_pathArray[i_currentOffset++] << 8);
        if (!_speedPrev) i_steps = as_pathArray[i_currentOffset++];

        i8_dx = (i8_targetX - i8_curX) / i_steps;
        i8_dy = (i8_targetY - i8_curY) / i_steps;

        i8_targetX = i8_curX + i8_dx * i_steps;
        i8_targetY = i8_curY + i8_dy * i_steps;
    }

    public final void initPath(int _centerX, int _centerY, Sprite _sprite, short[] _pathArray, int _offset, int _initPathPoint, int _pathLength)
    {
        i8_centerX = _centerX;
        i8_centerY = _centerY;
        i_initOffset = _offset;

        p_sprite = _sprite;
        as_pathArray = _pathArray;
        i_length = as_pathArray[i_initOffset++];
        if (_pathLength > 0)
            i_length = _pathLength;
        else if (_initPathPoint > 0)
            i_length -= _initPathPoint;
        short w_acc = as_pathArray[i_initOffset++];
        i_initOffset = i_initOffset + _initPathPoint * POINT_DATA_SIZE;
        resetPath();
        calculateDifferents(true);
        lg_back = false;

        p_sprite.setMainPointXY(i8_centerX + i8_curX, i8_centerY + i8_curY);
    }

    public final void resetPath()
    {
        i_curLength = 0;
        lg_back = false;
        lg_completed = false;
        i8_curX = as_pathArray[i_initOffset] << 8;
        i8_curY = as_pathArray[i_initOffset + 1] << 8;
        i_currentOffset = i_initOffset + (POINT_DATA_SIZE - 1);
    }

    public final boolean isCompleted()
    {
        return lg_completed;
    }

    public final boolean processStep()
    {
        if (lg_completed) return false;

        i8_curX += i8_dx;
        i8_curY += i8_dy;

        p_sprite.setMainPointXY(i8_centerX + i8_curX, i8_centerY + i8_curY);

        if (i8_curX == i8_targetX && i8_curY == i8_targetY)
        {
            i_curLength++;
            if (i_curLength == i_length)
                lg_completed = true;
            else
                calculateDifferents(true);
            return true;
        }
        return false;
    }
}
