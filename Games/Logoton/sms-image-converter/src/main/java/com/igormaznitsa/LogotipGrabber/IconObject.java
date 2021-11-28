/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 03.04.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogotipGrabber;

import java.io.IOException;
import java.util.StringTokenizer;

public class IconObject
{
    protected String _name;
    protected int _width;
    protected int _height;
    protected int _type;

    public int getWidth()
    {
        return _width;
    }

    public int getHeight()
    {
        return _height;
    }

    public int getType()
    {
        return _type;
    }

    public String getName()
    {
        return _name;
    }

    public IconObject(String str) throws IOException
    {
        StringTokenizer tkn = new StringTokenizer(str,"+");

        try
        {
            _name = tkn.nextToken();
            String _wdhd = tkn.nextToken();
            String _tp = tkn.nextToken();

            tkn = new StringTokenizer(_wdhd,"xX");
            String _w =tkn.nextToken();
            String _h =tkn.nextToken();

            _width = Integer.parseInt(_w);
            _height= Integer.parseInt(_h);

            _type = Integer.parseInt(_tp);
        }
        catch (Exception e)
        {
            throw new IOException("I can't decode \"list\" parameter");
        }
    }
}
