package com.igormaznitsa.GameKit_FE652.Frog;

public class Flow
{
    public static final int TYPE_SINGLE = 0;
    public static final int TYPE_STREAM = 1;

    protected int _y;
    protected int _dir;
    protected int _type;
    protected int _speed;
    protected int _freq;

    protected Car[] _car_array;

    public Car[] getCarArrayp()
    {
        return _car_array;
    }

    public int processFlow(Car[] arr, int startindx)
    {
        Car _empt = null;
        boolean _generate = true;

        for (int li = 0; li < _car_array.length; li++)
        {
            Car _c = _car_array[li];
            if (_c._state == Car.STATE_INACTIVE)
            {
                _empt = _c;
                continue;
            }

            if (_dir == Car.DIRECT_LEFT)
            {
                _c._x -= _speed;

                if (_c._x <= (0 - Car.CAR_WIDTH))
                {
                    _c._state = Car.STATE_INACTIVE;
                    _empt = _c;
                    continue;
                }

                if (_c._x >= (Frog_SB.FIELD_WIDTH - Car.CAR_WIDTH)) _generate = false;
            }
            else
            {
                _c._x += _speed;

                if (_c._x >= Frog_SB.FIELD_WIDTH)
                {
                    _c._state = Car.STATE_INACTIVE;
                    _empt = _c;
                    continue;
                }

                if (_c._x <= Car.CAR_WIDTH) _generate = false;
            }

            if (_c._type != Car.CAR_NULL) arr[startindx++] = _c;
        }

        if (!_generate) return startindx;

        _generate = false;

        if (Frog_SB._rnd.getInt(_freq) == (_freq >> 1)) _generate = true;

        if (_type == TYPE_STREAM) _generate = !_generate;

        if (_empt == null)
        {
            for (int li = 0; li < _car_array.length; li++)
            {
                if (_car_array[li]._state == Car.STATE_INACTIVE)
                {
                    _empt = _car_array[li];
                    break;
                }
            }
            return startindx;
        }

        if (_dir == Car.DIRECT_LEFT)
            _empt._x = Frog_SB.FIELD_WIDTH;
        else
            _empt._x = 0 - Car.CAR_WIDTH;

        if (_generate)
        {
            _empt._type = Frog_SB._rnd.getInt(Car.MAX_TYPE);
            arr[startindx++] = _empt;
        }
        else
        {
            _empt._type = Car.CAR_NULL;
        }
        _empt._state = Car.STATE_ACTIVE;
        _empt._frame = 0;
        _empt._tick = 0;

        return startindx;
    }

    public Flow(int y, int direct, int type, int speed, int freq)
    {
        _y = y;
        _dir = direct;
        _type = type;
        _speed = speed;
        _freq = freq;

        switch (_type)
        {
            case TYPE_SINGLE:
                _car_array = new Car[1];
                break;
            case TYPE_STREAM:
                {
                    _car_array = new Car[Frog_SB.FIELD_WIDTH / Car.CAR_WIDTH + 1];
                }
                ;
                break;
        }

        for (int li = 0; li < _car_array.length; li++)
        {
            Car _ncar = new Car(li * Car.CAR_WIDTH, _y, _dir);
            if (_type == TYPE_STREAM)
            {
                _ncar._state = Car.STATE_ACTIVE;
            }
            _car_array[li] = _ncar;
        }
    }
}
