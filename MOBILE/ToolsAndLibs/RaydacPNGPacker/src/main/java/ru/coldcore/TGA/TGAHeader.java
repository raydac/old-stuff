package ru.coldcore.TGA;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.IOException;

public class TGAHeader
{
    private int idLength;
    private byte[] id;
    private boolean hasColorMap;

    private int imageType;
    private boolean isCompressed;
    private int firstColorMapEntryIndex;
    private int numberColorMapEntries;
    private int bitsPerColorMapEntry;
    private int colorMapEntrySize;
    private int colorMapSize;
    private int xOrigin;
    private int yOrigin;
    private int width;
    private int height;
    private int bitsPerPixel;
    private int imageDescriptor;
    private boolean leftToRight;
    private boolean bottomToTop;
    private int colorMapDataOffset;
    private int pixelDataOffset;

    public TGAHeader()
    {
    }

    public TGAHeader(final MemoryCacheImageInputStream inputStream) throws IOException
    {
        // read the data
        readHeader(inputStream);
    }

    public void readHeader(final MemoryCacheImageInputStream inputStream) throws IOException
    {
        // read in the header as per the spec
        idLength = inputStream.readUnsignedByte();

        hasColorMap = (inputStream.readUnsignedByte() == 1); // 1 == true, 0 == false    
        imageType = inputStream.readUnsignedByte();

        firstColorMapEntryIndex = inputStream.readUnsignedShort();
        numberColorMapEntries = inputStream.readUnsignedShort();
        bitsPerColorMapEntry = inputStream.readByte();

        xOrigin = inputStream.readUnsignedShort();
        yOrigin = inputStream.readUnsignedShort();
        width = inputStream.readUnsignedShort();
        height = inputStream.readUnsignedShort();

        bitsPerPixel = inputStream.readByte();
        imageDescriptor = inputStream.readByte();

        // determine if the image is compressed
        isCompressed = ((imageType == TGAConstants.RLE_COLOR_MAP) ||
                (imageType == TGAConstants.RLE_TRUE_COLOR) ||
                (imageType == TGAConstants.RLE_MONO));

        // compute the size of the color map field in bytes
        switch (bitsPerColorMapEntry)
        {
            case 8:
            default:
                colorMapEntrySize = 1;
                break;
            case 15:
            case 16:
                colorMapEntrySize = 2;
                break;
            case 24:
            case 32:
                colorMapEntrySize = 3;
                break;
        }
        colorMapSize = colorMapEntrySize * numberColorMapEntries; // in bytes 

        // set the pixel ordering from the imageDescriptor bit mask
        // (bit set indicates false)
        leftToRight = ((imageDescriptor & TGAConstants.LEFT_RIGHT_BIT) == 0);
        bottomToTop = ((imageDescriptor & TGAConstants.BOTTOM_TOP_BIT) == 0);

        // read the image id based whose length is idLength
        if (idLength > 0)
        {
            // allocate the space for the id
            id = new byte[idLength];

            // read the id
            inputStream.read(id, 0, idLength);
        } /* else -- the idLength was not positive */

        // compute the color map and pixel data offsets.  The color map data 
        // offset is the current offset.
        // NOTE:  the conversion to int is OK since the maximum size of the
        //        color map data is 65536 bytes.
        final long currentOffset = inputStream.getStreamPosition();
        colorMapDataOffset = (int) currentOffset;
        if (hasColorMap)
        {
            // there is a color map so the pixel data offset is the current
            // offset + the size of the color map data
            pixelDataOffset = colorMapDataOffset + colorMapSize;
        }
        else /* there is no color map */
        {
            // there is no color map so the pixel data offset is the current
            // offset
            pixelDataOffset = (int) currentOffset;
        }
    }

    public int getIdLength()
    {
        return idLength;
    }

    public boolean hasColorMap()
    {
        return hasColorMap;
    }

    public int getImageType()
    {
        return imageType;
    }

    public String getImageTypeString()
    {
        switch (imageType)
        {
            case TGAConstants.NO_IMAGE:
                return "NO IMAGE";

            case TGAConstants.COLOR_MAP:
                return "COLOR MAP";

            case TGAConstants.TRUE_COLOR:
                return "TRUE COLOR";

            case TGAConstants.MONO:
                return "MONOCHROME";

            case TGAConstants.RLE_COLOR_MAP:
                return "RLE COMPRESSED COLOR MAP";

            case TGAConstants.RLE_TRUE_COLOR:
                return "RLE COMPRESSED TRUE COLOR";

            case TGAConstants.RLE_MONO:
                return "RLE COMPRESSED MONOCHROME";

            default:
                return "UNKNOWN";
        }
    }

    public boolean isCompressed()
    {
        return isCompressed;
    }

    public int getFirstColorMapEntryIndex()
    {
        return firstColorMapEntryIndex;
    }

    public int getColorMapLength()
    {
        return numberColorMapEntries;
    }

    public int getBitsPerColorMapEntry()
    {
        return bitsPerColorMapEntry;
    }

    public int getXOrigin()
    {
        return xOrigin;
    }

    public int getYOrigin()
    {
        return yOrigin;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getBitsPerPixel()
    {
        return bitsPerPixel;
    }

    public int getSamplesPerPixel()
    {
        // FIXME:  this is overly simplistic but it is accurate
        return (bitsPerPixel == 32) ? 4 : 3;
    }

    public int getImageDescriptor()
    {
        return imageDescriptor;
    }

    public boolean isLeftToRight()
    {
        return leftToRight;
    }

    public boolean isBottomToTop()
    {
        return bottomToTop;
    }

    public int getColorMapDataOffset()
    {
        return colorMapDataOffset;
    }

    public int getPixelDataOffset()
    {
        return pixelDataOffset;
    }
}