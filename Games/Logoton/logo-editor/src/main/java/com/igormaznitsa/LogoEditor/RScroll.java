package com.igormaznitsa.LogoEditor;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.applet.Applet;

public class RScroll extends ScrollPane
{
    protected EditPicture _picture;

    public boolean isUndo()
    {
        return _picture.isUndo();
    }

    public boolean isRedo()
    {
        return _picture.isRedo();
    }

    public void redo()
    {
        if (_picture.isRedo()) _picture.copyRedoToImage();
    }

    public void loadImageFromArray(int w,int h,int [] arr)
    {
        _picture.initImage(w,h);
        _picture.loadImageFromArray(w,h,arr);
    }

    public void undo()
    {
        if (_picture.isUndo()) _picture.copyUndoToImage();
    }

    public void scrollUp()
    {
        _picture.scrollUp();
    }

    public void scrollDown()
    {
        _picture.scrollDown();
    }

    public void scrollLeft()
    {
        _picture.scrollLeft();
    }

    public void scrollRight()
    {
        _picture.scrollRight();
    }

    public void clearImage()
    {
        _picture.clearImage();
    }

    public void inverseImage()
    {
        _picture.inverseImage();
    }

    public void horizMirror()
    {
        _picture.horizMirror();
    }

    public void setPattern(int [] pattern,int w,int h)
    {
        _picture.setCurrentPattern(pattern,w,h);
    }

    public void vertMirror()
    {
        _picture.vertMirror();
    }

    public void updateImage()
    {
        _picture.repaint();
    }

    public void setCommandState(int state)
    {
        _picture._select_rectangle = null;
        _picture.setCommandState(state);
    }

    public RScroll(Applet apl,ActionListener lstnr) throws IOException
    {
        super();
        _picture = new EditPicture(apl,100,30);
        _picture.setActionListener(lstnr);
        add(_picture, null, 0);
        doLayout();
    }
}
