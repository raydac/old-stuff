/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 29.03.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogoEditor;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class REditorPanel extends Panel implements ActionListener
{
    protected Panel _top_panel;
    protected RPanel _tool_panel;
    protected PhonePreview _previewscreen;

    protected RButton _save_button;
    protected RScroll _scroll_pane;

    protected Object _last_eventer = null;
    protected Applet _apl;

    protected static final String ACTION_TEXTSELECT = "TEXT_SELECT";
    protected static final String ACTION_SAVEIMAGE = "SAVE_IMAGE";

    protected RButton _gr_pencil;
    public static final String ACTION_GR_PENCIL = "GR_PENCIL";
    protected RButton _gr_line;
    public static final String ACTION_GR_LINE = "GR_LINE";
    protected RButton _gr_fillrect;
    public static final String ACTION_GR_FILLRECT = "GR_FILLRECT";
    protected RButton _gr_rect;
    public static final String ACTION_GR_RECT = "GR_RECT";
    protected RButton _gr_circle;
    public static final String ACTION_GR_CIRCLE = "GR_CIRCLE";
    protected RButton _gr_fillcircle;
    public static final String ACTION_GR_FILLCIRCLE = "GR_FILLCIRCLE";
    protected RButton _gr_inverse;
    public static final String ACTION_GR_INVERSE = "GR_INVERSE";
    protected RButton _gr_hmirror;
    public static final String ACTION_GR_HMIRROR = "GR_HMIRROR";
    protected RButton _gr_vmirror;
    public static final String ACTION_GR_VMIRROR = "GR_VMIRROR";
    protected RButton _gr_text;
    public static final String ACTION_GR_TEXT = "GR_TEXT";
    protected RButton _gr_redo;
    public static final String ACTION_GR_REDO = "GR_REDO";
    protected RButton _gr_undo;
    public static final String ACTION_GR_UNDO = "GR_UNDO";
    protected RButton _gr_clear;
    public static final String ACTION_GR_CLEAR = "GR_CLEAR";
    protected RButton _gr_scrollleft;
    public static final String ACTION_GR_SCROLLEFT = "GR_SCROLLLEFT";
    protected RButton _gr_scrollright;
    public static final String ACTION_GR_SCROLLRIGHT = "GR_SCROLLRIGHT";
    protected RButton _gr_scrolltop;
    public static final String ACTION_GR_SCROLLTOP = "GR_SCROLLTOP";
    protected RButton _gr_scrollbottom;
    public static final String ACTION_GR_SCROLLBOTTOM = "GR_SCROLLBOTTOM";
    protected RButton _gr_select;
    public static final String ACTION_GR_SELECT = "GR_SELECT";

    protected PatternLine _patternline;
    protected Image _preview_image;
    protected ActionListener _act_lstnr;

    public void setActionListener(ActionListener lst)
    {
        _act_lstnr = lst;
    }

    public void loadImageFromArray(int w, int h, int[] arr)
    {
        setImageSize(w,h);
        _scroll_pane.loadImageFromArray(w, h, arr);
        _scroll_pane.updateImage();
        _scroll_pane._picture.updateImageFromArray(_preview_image,PhonePreview.COLOR_FOREGROUND,PhonePreview.COLOR_BACKGROUND);
        _previewscreen.setImage(_preview_image);
    }

    public int getImageWidth()
    {
        return _scroll_pane._picture._image_width;
    }

    public int getImageHeight()
    {
        return _scroll_pane._picture._image_height;
    }

    public synchronized int [] getImageArray()
    {
        int [] _arr = new int[_scroll_pane._picture._image_array.length];
        for(int li=0;li<_arr.length;li++)
        {
            if (_scroll_pane._picture._image_array[li]!=0) _arr[li]=0; else _arr[li]=0xFFFFFF;
        }
        return _arr;
    }

    public void actionPerformed(ActionEvent e)
    {
        String _act = e.getActionCommand();
        boolean _resetbuttons = true;
        if (_act.equals(EditPicture.ACTION_IMAGECHANGED))
        {
            _scroll_pane.updateImage();
            _resetbuttons = false;
        }
        if (_act.equals(EditPicture.ACTION_COMMIT))
        {
            if (_scroll_pane.isUndo()) _gr_undo.setEnabled(true); else _gr_undo.setEnabled(false);
            if (_scroll_pane.isRedo()) _gr_redo.setEnabled(true); else _gr_redo.setEnabled(false);
            _scroll_pane.updateImage();
            _scroll_pane._picture.updateImageFromArray(_preview_image,PhonePreview.COLOR_FOREGROUND,PhonePreview.COLOR_BACKGROUND);
            _previewscreen.setImage(_preview_image);
            _resetbuttons = false;
        }
        else
        {
            if (_act.equals(ACTION_GR_CIRCLE))
            {
                _scroll_pane.setCommandState(EditPicture.GR_CIRCLE);
            }
            else if (_act.equals(ACTION_GR_CLEAR))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.clearImage();
            }
            else if (_act.equals(ACTION_GR_FILLCIRCLE))
            {
                _scroll_pane.setCommandState(EditPicture.GR_FILLCIRCLE);
            }
            else if (_act.equals(ACTION_GR_FILLRECT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_FILLRECT);
            }
            else if (_act.equals(ACTION_GR_HMIRROR))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.horizMirror();
            }
            else if (_act.equals(ACTION_GR_INVERSE))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.inverseImage();
            }
            else if (_act.equals(ACTION_GR_LINE))
            {
                _scroll_pane.setCommandState(EditPicture.GR_LINE);
            }
            else if (_act.equals(ACTION_GR_PENCIL))
            {
                _scroll_pane.setCommandState(EditPicture.GR_PENCIL);
            }
            else if (_act.equals(ACTION_GR_RECT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_RECT);
            }
            else if (_act.equals(ACTION_GR_REDO))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.redo();
            }
            else if (_act.equals(ACTION_GR_SCROLLBOTTOM))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.scrollDown();
            }
            else if (_act.equals(ACTION_GR_SCROLLEFT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.scrollLeft();
            }
            else if (_act.equals(ACTION_GR_SCROLLRIGHT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.scrollRight();
            }
            else if (_act.equals(ACTION_GR_SCROLLTOP))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.scrollUp();
            }
            else if (_act.equals(ACTION_GR_SELECT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_SELECT);
            }
            else if (_act.equals(ACTION_GR_TEXT))
            {
                _scroll_pane.setCommandState(EditPicture.GR_TEXT);
                if (_act_lstnr != null) _act_lstnr.actionPerformed(new ActionEvent(this, 0, ACTION_TEXTSELECT));
            }
            else if (_act.equals(ACTION_GR_UNDO))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.undo();
            }
            else if (_act.equals(ACTION_GR_VMIRROR))
            {
                _scroll_pane.setCommandState(EditPicture.GR_NONE);
                _scroll_pane.vertMirror();
            }
            else if (_act.equals(PatternLine.ACTION_SELECTED))
            {
                _scroll_pane.setCommandState(EditPicture.GR_PATTERN);
                _scroll_pane.setPattern(_patternline.getSelectedImage(), PatternLine.ICON_WIDTH, PatternLine.ICON_HEIGHT);
            }
            else if (_act.equals(ACTION_SAVEIMAGE))
            {
                if (_act_lstnr != null) _act_lstnr.actionPerformed(new ActionEvent(this, 0, ACTION_SAVEIMAGE));
            }

            if (_resetbuttons)
            {
                Object _btn = e.getSource();
                if (!_btn.equals(_patternline))
                    _patternline.setSelectIndex(-1);

                Component[] _cmp = _tool_panel.getComponents();
                for (int li = 0; li < _cmp.length; li++)
                {
                    RButton __btn = (RButton) _cmp[li];
                    if (!__btn.equals(_btn)) __btn.unselect();
                }

                _cmp = _top_panel.getComponents();
                for (int li = 0; li < _cmp.length; li++)
                {
                    RButton __btn = (RButton) _cmp[li];
                    if (!__btn.equals(_btn)) __btn.unselect();
                }
            }
        }
    }

    protected void clearPreviewScreen()
    {
        Graphics gr = _preview_image.getGraphics();
        gr.setColor(PhonePreview.COLOR_BACKGROUND);
        gr.fillRect(0,0,_preview_image.getWidth(null),_preview_image.getHeight(null));
        gr.dispose();
    }

    public void initEditor()
    {
        _gr_undo.setEnabled(false);
        _gr_redo.setEnabled(false);
    }

    public void setImageSize(int w,int h)
    {
        _scroll_pane._picture.initImage(w,h);
        _preview_image = common.createImage(_apl,w,h);
    }

    public REditorPanel(Applet apl,Color color) throws IOException
    {
        super();
        setBackground(color);
        _apl = apl;
        setLayout(new BorderLayout(0, 0));
        _top_panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        _tool_panel = new RPanel(new VerticalLayout(5, VerticalLayout.CENTER));

        _save_button = new RButton(apl, "Save image", common.loadImageResource(apl, "saveup.gif"), common.loadImageResource(apl, "savedwn.gif"), null, false, ACTION_SAVEIMAGE);
        _save_button.setActionListener(this);

        _patternline = new PatternLine(apl);
        _patternline.setActionListener(this);
        _patternline.setBackground(Color.white);
        _patternline.setForeground(Color.yellow);

        _gr_circle = new RButton(apl, "Circle", common.loadImageResource(apl, "cercle.gif"), common.loadImageResource(apl, "cercle_on.gif"), null, true, ACTION_GR_CIRCLE);
        _gr_circle.setActionListener(this);
        _gr_clear = new RButton(apl, "Clear", common.loadImageResource(apl, "del.gif"), common.loadImageResource(apl, "del_on.gif"), null, false, ACTION_GR_CLEAR);
        _gr_clear.setActionListener(this);
        _gr_fillcircle = new RButton(apl, "Filled circle", common.loadImageResource(apl, "round.gif"), common.loadImageResource(apl, "round_on.gif"), null, true, ACTION_GR_FILLCIRCLE);
        _gr_fillcircle.setActionListener(this);
        _gr_fillrect = new RButton(apl, "Filled rectangle", common.loadImageResource(apl, "sq.gif"), common.loadImageResource(apl, "sq_on.gif"), null, true, ACTION_GR_FILLRECT);
        _gr_fillrect.setActionListener(this);
        _gr_hmirror = new RButton(apl, "Horizontal mirror", common.loadImageResource(apl, "gor.gif"), common.loadImageResource(apl, "gor_on.gif"), null, false, ACTION_GR_HMIRROR);
        _gr_hmirror.setActionListener(this);
        _gr_inverse = new RButton(apl, "Inverse", common.loadImageResource(apl, "inv.gif"), common.loadImageResource(apl, "inv_on.gif"), null, false, ACTION_GR_INVERSE);
        _gr_inverse.setActionListener(this);
        _gr_line = new RButton(apl, "Line", common.loadImageResource(apl, "line.gif"), common.loadImageResource(apl, "line_on.gif"), null, true, ACTION_GR_LINE);
        _gr_line.setActionListener(this);
        _gr_pencil = new RButton(apl, "Pencil", common.loadImageResource(apl, "draw.gif"), common.loadImageResource(apl, "draw_on.gif"), null, true, ACTION_GR_PENCIL);
        _gr_pencil.setActionListener(this);
        _gr_rect = new RButton(apl, "Rectangle", common.loadImageResource(apl, "rec.gif"), common.loadImageResource(apl, "rec_on.gif"), null, true, ACTION_GR_RECT);
        _gr_rect.setActionListener(this);
        _gr_redo = new RButton(apl, "Redo", common.loadImageResource(apl, "redo.gif"), common.loadImageResource(apl, "redo_on.gif"), common.loadImageResource(apl, "redo_off.gif"), false, ACTION_GR_REDO);
        _gr_redo.setActionListener(this);
        _gr_undo = new RButton(apl, "Undo", common.loadImageResource(apl, "undo.gif"), common.loadImageResource(apl, "undo_on.gif"), common.loadImageResource(apl, "undo_off.gif"), false, ACTION_GR_UNDO);
        _gr_undo.setActionListener(this);
        _gr_scrollbottom = new RButton(apl, "Sroll down", common.loadImageResource(apl, "down.gif"), common.loadImageResource(apl, "down_on.gif"), null, false, ACTION_GR_SCROLLBOTTOM);
        _gr_scrollbottom.setActionListener(this);
        _gr_scrollleft = new RButton(apl, "Sroll left", common.loadImageResource(apl, "left.gif"), common.loadImageResource(apl, "left_on.gif"), null, false, ACTION_GR_SCROLLEFT);
        _gr_scrollleft.setActionListener(this);
        _gr_scrollright = new RButton(apl, "Sroll right", common.loadImageResource(apl, "right.gif"), common.loadImageResource(apl, "right_on.gif"), null, false, ACTION_GR_SCROLLRIGHT);
        _gr_scrollright.setActionListener(this);
        _gr_scrolltop = new RButton(apl, "Sroll up", common.loadImageResource(apl, "up.gif"), common.loadImageResource(apl, "up_on.gif"), null, false, ACTION_GR_SCROLLTOP);
        _gr_scrolltop.setActionListener(this);
        _gr_select = new RButton(apl, "Select", common.loadImageResource(apl, "area.gif"), common.loadImageResource(apl, "area_on.gif"), null, true, ACTION_GR_SELECT);
        _gr_select.setActionListener(this);
        _gr_text = new RButton(apl, "Text", common.loadImageResource(apl, "text.gif"), common.loadImageResource(apl, "text_on.gif"), null, false, ACTION_GR_TEXT);
        _gr_text.setActionListener(this);
        _gr_vmirror = new RButton(apl, "Vertical mirror", common.loadImageResource(apl, "vert.gif"), common.loadImageResource(apl, "vert_on.gif"), null, false, ACTION_GR_VMIRROR);
        _gr_vmirror.setActionListener(this);

        _tool_panel.add(_gr_pencil);
        _tool_panel.add(_gr_line);
        _tool_panel.add(_gr_fillrect);
        _tool_panel.add(_gr_rect);
        _tool_panel.add(_gr_circle);
        _tool_panel.add(_gr_fillcircle);
        _tool_panel.add(_gr_text);

        _top_panel.add(_save_button);

        _top_panel.add(_gr_undo);
        _top_panel.add(_gr_redo);
        _top_panel.add(_gr_clear);
        _top_panel.add(_gr_hmirror);
        _top_panel.add(_gr_vmirror);
        _top_panel.add(_gr_select);
        _top_panel.add(_gr_inverse);

        _top_panel.add(_gr_scrollleft);
        _top_panel.add(_gr_scrollright);
        _top_panel.add(_gr_scrolltop);
        _top_panel.add(_gr_scrollbottom);

        _previewscreen = new PhonePreview(apl,new Rectangle(27,56,101,64),false);

        initEditor();

        _scroll_pane = new RScroll(apl, this);

        RPanel _rp = new RPanel(new FlowLayout(FlowLayout.CENTER));
        _rp.add(_previewscreen);

        _top_panel.setBackground(color);
        _scroll_pane.setBackground(color);
        _tool_panel.setBackground(color);

        add("North", _top_panel);
        add("South", _patternline);
        add("Center", _scroll_pane);
        add("West", _tool_panel);
        add("East",_rp);
    }
}
