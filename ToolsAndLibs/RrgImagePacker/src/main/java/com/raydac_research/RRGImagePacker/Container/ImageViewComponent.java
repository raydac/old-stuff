package com.raydac_research.RRGImagePacker.Container;

import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageViewComponent extends Component
{
    protected ImageCnt p_imageContainer;
    protected int i_Scale;

    protected BufferedImage p_outImage;

    protected static final Color CROP_BORDER_COLOR = Color.GREEN;
    protected static final Color TILE_COLOR = Color.RED;

    public void setScale(int _scale)
    {
        i_Scale = _scale;
        repaint();
    }

    public void setSize(Dimension _dim)
    {
        if (p_imageContainer != null)
        {
            if (!p_imageContainer.isExternalImageLink())
            {
                BufferedImage p_bi = p_outImage;
                super.setSize(new Dimension(p_bi.getWidth()*i_Scale,p_bi.getHeight()*i_Scale));
            }
        }
        super.setSize(new Dimension(80,80));
    }

    public int getWidth()
    {
        return getSize().width;
    }

    public int getHeight()
    {
        return getSize().height;
    }

    public Rectangle getBounds()
    {
        Rectangle p_rect = new Rectangle(0,0,getWidth(),getHeight());
        return p_rect;
    }

    public int getX()
    {
        return 0;
    }

    public int getY()
    {
        return 0;
    }

    public void setBounds(int x, int y, int width, int height)
    {
    }

    public void setLocation(int x, int y)
    {
    }

    public void setSize(int width, int height)
    {
    }

    public Dimension getPreferredSize()
    {
        return getSize();
    }

    public Dimension getSize()
    {
        if (p_imageContainer != null)
        {
            if (!p_imageContainer.isExternalImageLink())
            {
                BufferedImage p_bi = p_outImage;
                if (p_bi!=null)
                {
                    return new Dimension(p_bi.getWidth()*i_Scale,p_bi.getHeight()*i_Scale);
                }
            }
        }
        return new Dimension(80,80);
    }

    public Dimension getMaximumSize()
    {
        return getSize();
    }

    public Dimension getMinimumSize()
    {
        return getSize();
    }

    public void setImageContainer(ImageCnt _cnt)
    {
        p_imageContainer = _cnt;
        p_outImage = p_imageContainer.getImage();
        repaint();
    }

    public ImageViewComponent()
    {
        p_imageContainer = null;
        i_Scale = 1;
    }

    public void update(Graphics _g)
    {
        paint(_g);
    }

    public void paint(Graphics _g)
    {
        _g.setColor(Color.blue);
        _g.fillRect(0,0,getWidth(),getHeight());

        if (p_imageContainer == null) return;
        if (p_imageContainer.isExternalImageLink()) return;

        BufferedImage p_bufImage = p_outImage;

        if (p_bufImage == null)
        {
            _g.setColor(Color.black);
            _g.fillRect(0,0,getWidth(),getHeight());
            _g.setColor(Color.red);
            _g.drawString("No image",5,getHeight()>>1);
            return;
        }

        int i_width =  p_bufImage.getWidth()*i_Scale;
        int i_height = p_bufImage.getHeight()*i_Scale;

        if (p_bufImage != null)
        {
            _g.drawImage(p_bufImage,getX(),getY(),i_width,i_height,null);
            _g.setColor(CROP_BORDER_COLOR);

            if (p_imageContainer.isCropped())
            {
                Rectangle p_cropRect = p_imageContainer.getCropRegion();
                _g.drawRect(p_cropRect.x*i_Scale,p_cropRect.y*i_Scale,p_cropRect.width*i_Scale,p_cropRect.height*i_Scale);
            }

            if (p_imageContainer.isTile())
            {
                Dimension p_tileDim = p_imageContainer.getTileCellDim();
                int i_w = p_tileDim.width * i_Scale;
                int i_h = p_tileDim.height * i_Scale;

                _g.setColor(TILE_COLOR);

                for(int lx=0;lx<i_width;lx+=i_w)
                {
                    _g.drawLine(lx,0,lx,i_height);
                }

                for(int ly=0;ly<i_height;ly+=i_h)
                {
                    _g.drawLine(0,ly,i_width,ly);
                }
            }
        }
    }

}
