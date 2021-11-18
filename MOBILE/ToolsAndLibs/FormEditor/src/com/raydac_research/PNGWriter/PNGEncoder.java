package com.raydac_research.PNGWriter;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class PNGEncoder
{
    // the image producer and output stream supplied by the user
    private OutputStream out;
    private Image p_image;

    public static MediaTracker p_mtracker = new MediaTracker(new Button());

    // user-specified options; default values are shown to the right
    private int bufferSize;		// MAX_CHUNK_DATA_SIZE
    private int compressionLevel;	// Deflater.DEFAULT_COMPRESSION
    private Integer backgroundColor;	// null (means do not output)
    private boolean alpha;// false, to create alpha channel in the image (if it needs)

    // the maximum size of the data portion of a chunk, as defined in the PNG
    // specification
    private static final long MAX_CHUNK_DATA_SIZE = 0x7fffffff;  // (2^31) - 1

    // this little guy computes the chunk CRCs
    private final CRC32 crcGenerator = new CRC32();

    // buffers for the filtered scan lines, made global so that they can easily
    // be allocated only once and then reused for each scan line
    private byte[] filteredNone;
    private byte[] filteredSub;
    private byte[] filteredUp;
    private byte[] filteredAverage;
    private byte[] filteredPaeth;

    private boolean lg_outsidepalette;
    private Palette p_outsidepalette;
    private boolean lg_removepalette;

    // the PNG values that indicate the filter algorithm used for a scan line
    private static final byte FILTER_TYPE_NONE = 0;
    private static final byte FILTER_TYPE_SUB = 1;
    private static final byte FILTER_TYPE_UP = 2;
    private static final byte FILTER_TYPE_AVERAGE = 3;
    private static final byte FILTER_TYPE_PAETH = 4;

    private Integer p_alphaColor;

    // Yes, I am anal when it comes to safe coding.  The mess below is where
    // I defined some static, fixed byte arrays.  The first one is the PNG
    // signature (the first 8 bytes of every PNG file), and the rest define
    // the chunk types.
    private static final ImmutableByteArray PNG_SIGNATURE = new ImmutableByteArray(
            new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10}
    );  // end PNG_SIGNATURE definition

    private static final ImmutableByteArray CHUNK_TYPE_IHDR =
            new ImmutableByteArray(
                    new byte[]{(byte) 'I', (byte) 'H', (byte) 'D', (byte) 'R'}
            );  // end CHUNK_TYPE_IHDR definition

    private static final ImmutableByteArray CHUNK_TYPE_PLTE =
            new ImmutableByteArray(
                    new byte[]{(byte) 'P', (byte) 'L', (byte) 'T', (byte) 'E'}
            );  // end CHUNK_TYPE_PLTE definition

    private static final ImmutableByteArray CHUNK_TYPE_tRNS =
            new ImmutableByteArray(
                    new byte[]{(byte) 't', (byte) 'R', (byte) 'N', (byte) 'S'}
            );  // end CHUNK_TYPE_tRNS definition

    private static final ImmutableByteArray CHUNK_TYPE_bKGD =
            new ImmutableByteArray(
                    new byte[]{(byte) 'b', (byte) 'K', (byte) 'G', (byte) 'D'}
            );  // end CHUNK_TYPE_bKGD definition

    private static final ImmutableByteArray CHUNK_TYPE_IDAT =
            new ImmutableByteArray(
                    new byte[]{(byte) 'I', (byte) 'D', (byte) 'A', (byte) 'T'}
            );  // end CHUNK_TYPE_IDAT definition

    private static final ImmutableByteArray CHUNK_TYPE_IEND =
            new ImmutableByteArray(
                    new byte[]{(byte) 'I', (byte) 'E', (byte) 'N', (byte) 'D'}
            );  // end CHUNK_TYPE_IEND definition


    public PNGEncoder(Image _image, OutputStream out, Palette _outpalette, boolean _removepalette)
    {
        lg_removepalette = _removepalette;
        p_alphaColor = null;
        if (_outpalette != null)
        {
            _outpalette.resetTransparents();
            lg_outsidepalette = _removepalette; // was "true;"
            p_outsidepalette = _outpalette;
        }
        else
        {
            lg_outsidepalette = false;
        }

        this.bufferSize = (int) MAX_CHUNK_DATA_SIZE;
        this.compressionLevel = Deflater.DEFAULT_COMPRESSION;
        this.backgroundColor = null;

        p_image = _image;
        this.out = out;

    }  // end constructor


    /**
     * <p>
     * Sets the size of the IDAT chunks in the output image (except
     * for the last chunk, which is whatever is left).
     * </p>
     * <p>
     * By default, the size is the maximum chunk data length as defined in the
     * PNG Specification, Version 1.0, which is (2^31) - 1
     * </p>
     *
     * @param	size	the new maximum IDAT chunk size
     *
     * @throws	IllegalArgumentException	if <tt>size &lt;= 0 || size
     *		&gt; (2^31) - 1</tt>
     */
    public synchronized void setIDATSize(int size)
    {
        if (size <= 0 || size > MAX_CHUNK_DATA_SIZE)
        {
            throw new IllegalArgumentException("size: " + size);
        }  // end if

        this.bufferSize = size;
    }  // end setIDATSize()


    public synchronized void setAlpha(boolean _alpha)
    {
        alpha = _alpha;
    }

    /**
     * <p>
     * Sets the compression level for the compressed portions of the PNG stream
     * (<tt>IDAT</tt> and <tt>zTXt</tt> chunks).  Valid values are 0
     * (no compression, fastest) through 9 (best compression, slowest).
     * </p>
     * <p>
     * By default, the compression level is
     * <tt>java.util.zip.Deflater.DEFAULT_COMPRESSION</tt>
     * </p>
     *
     * @param	compressionLevel	the new compression level
     *
     * @throws	IllegalArgumentException	if <tt>compressionLevel</tt> is
     *		less than 0 or greater than 9
     */
    public synchronized void setCompressionLevel(int compressionLevel)
    {
        if (compressionLevel < 0 || compressionLevel > 9)
        {
            throw new IllegalArgumentException("compressionLevel: "
                    + compressionLevel);
        }  // end if

        this.compressionLevel = compressionLevel;
    }  // end setCompressionLevel()

    /**
     * <p>
     * Sets the background color for the image.  This is the color against
     * which a viewer may choose to display the image.  This is especially
     * useful for images that contain transparency information.
     * </p>
     * <p>
     * Only the red, green, and blue components of the specified color are used
     * to define the background.  The alpha component is ignored.
     * </p>
     * <p>
     * A couple words of warning:
     * </p>
     * <ul>
     * <li>
     * <p>
     * If your image is grayscale, and you want it to be encoded as grayscale
     * rather than true color in order to save space, then the background color,
     * if supplied, must be grayscale.
     * </p>
     * </li>
     * <li>
     * <p>
     * If you know that your image contains exactly 256 colors, and you want it
     * to be encoded as a palette-indexed PNG, then the background color, if
     * supplied, must match one of the colors that occurs in the image.  If it
     * does not, then your image will end up having 257 colors and will not be
     * encoded as a palette-indexed PNG.
     * </p>
     * </li>
     * <p>
     * By default, no background color information is output to the PNG stream.
     * </p>
     *
     * @param	color	the background color, or <strong>null</strong> to
     *		prevent background color information from appearing in the PNG
     *		stream
     */
    public synchronized void setBackgroundColor(Integer color)
    {
        this.backgroundColor = (color.intValue() >>> 24) == 255 ? color : new Integer(color.intValue() & 0xFFFFFF);
    }  // end setBackgroundColor()


    /**
     * Enodes the image as a PNG and writes it to the output stream.
     *
     * @throws	IOException	if an I/O error occurs
     */
    public synchronized void encodeImage() throws IOException
    {
        Consumer consumer = new Consumer();
        writePNG(consumer.getImageData());
    }  // end encodeImage()

    public ImageData getImageDataObject() throws IOException
    {
        Consumer consumer = new Consumer();
        return consumer.getImageData();
    }  // end encodeImage()

    private void writeTransparentsForPalette() throws IOException
    {
        int i_lastindx = -1;
        byte [] ab_alphas = p_outsidepalette.getAlphas();
        for(int li=0;li<ab_alphas.length;li++)
        {
            if ((ab_alphas[li]&0xFF)<255) i_lastindx = li;
        }
        if (i_lastindx<0) return;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(ab_alphas.length);
        for(int li=0;li<=i_lastindx;li++)
        {
            p_baos.write(ab_alphas[li]);
        }

        writeChunk(CHUNK_TYPE_tRNS.getBytes(), p_baos.toByteArray());
    }

    //
    // writePNG()
    //
    // Purpose: Encodes the given image data to a PNG stream and writes it to
    //          the OutputStream specified by the client programmer in the
    //		constructor.
    //
    //		The IDAT chunks are written to the stream immediately each time
    //		the buffer size limit is reached, to ensure fastest transfers in
    //		the case of writing to a network socket.
    //
    // Params:	ImageData	data	the image data to write to the stream
    //
    // Throws:	IOException	if an I/O error occurs
    //
    public void writePNG(ImageData data) throws IOException
    {
        writePNGSignature();
        writeHeader(data);

        // either the image will have a palette, or it might have a single alpha
        if (!data.hasSingleAlpha() && data.hasPalette())
        {
            if (!lg_removepalette) writePalette(data.getPalette());
            if (lg_outsidepalette) writeTransparentsForPalette();
        }
        else if (data.hasSingleAlpha())
        {
            if (!alpha)
            {
                if (!lg_removepalette)
                    writePalette(data.getPalette());

                if (lg_outsidepalette)
                    writeTransparentsForPalette();
                else
                  if (lg_removepalette)
                      writeTransparencyOneColor(data.getAlphaColor(), data.isGrayscale());
            }
        }  // end if

        // if the user has specified a background color, then write it out
        if (backgroundColor != null)
        {
            writeBackground(data);
        }  // end if

        writeData(data);
        writeEnd();

        out.flush();  // really don't need this, but it eases my mind
    }  // end writePNG()

    //
    // writePNGSignature()
    //
    // Purpose: Simply writes the 8-byte PNG signature to the output stream
    //		provided by the client programmer.
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writePNGSignature() throws IOException
    {
        out.write(PNG_SIGNATURE.getBytes());
    }  // end writePNGSignature()

    //
    // writeHeader()
    //
    // Purpose: Writes the PNG IHDR chunk to the output stream provided by the
    //		client programmer.
    //
    // Params:	ImageData	data	information about the image
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeHeader(ImageData data) throws IOException
    {
        // buffer for the chunk data, so that we can call writeChunk()
        ByteArrayOutputStream chunkData = new ByteArrayOutputStream();

        writeInt(data.getWidth(), chunkData);  // 4-byte width
        writeInt(data.getHeight(), chunkData);  // 4-byte height
        chunkData.write(data.getBitDepth());    // 1-byte bit depth

        // compute the "color type" field of the header
        int colorType = 0;
        if (data.isPaletteIndexed())
        {
            if (data.blackwhite)
            {
                colorType = 0;
            }
            else
            {
                if (alpha && data.hasSingleAlpha())
                {
                    if (data.isGrayscale())
                    {
                        colorType = 4;
                    }
                    else
                    {
                        colorType = 6;
                    }
                }
                else
                {
                    colorType = 3;
                }
            }
        }
        else
        {
            if (!data.isGrayscale())
            {
                colorType |= 2;
            }  // end if
            if (data.hasAlphaChannel())
            {
                colorType |= 4;
            }  // end if
        }  // end if/else

        chunkData.write(colorType);		// 1-byte color type

        chunkData.write(0);	// 1-byte compression method (always 0)
        chunkData.write(0);	// 1-byte filter method (always 0)
        chunkData.write(0);	// 1-byte interlace flag

        chunkData.close();

        writeChunk(CHUNK_TYPE_IHDR.getBytes(), chunkData.toByteArray());
    }  // end writeHeader()

    //
    // writePalette()
    //
    // Purpose: Writes the PLTE chunk to the stream.  Also writes a tRNS chunk
    //		if one or more palette entry is not fully opaque.
    //
    // Params:	Palette		palette		the palette to write
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writePalette(Palette palette) throws IOException
    {
        // The buffer needs to be 3 times the size of the palette because each
        // entry has a byte for each of the three color components (the alpha
        // component is not stored in the PLTE chunk)
        byte[] data = new byte[palette.getSize() * 3];
        for (int i = 0; i < palette.getSize(); ++i)
        {
            Integer color = palette.getColorAt(i);
            data[i * 3] = (byte) (color.intValue() >>> 16);
            data[i * 3 + 1] = (byte) (color.intValue() >>> 8);
            data[i * 3 + 2] = (byte) color.intValue();
        }  // end for

        writeChunk(CHUNK_TYPE_PLTE.getBytes(), data);

        if (!lg_outsidepalette)
        {
            // Here is where we deal with transparency in the palette.  The palette
            // entries have already been sorted so that all non-fully-opaque entries
            // are at the front.  The tRNS chunk is only as big as it needs to be,
            // and it is only present if the palette contains at least one
            // non-fully-opaque color.
            byte[] alphas = palette.getAlphas();
            if (alphas.length > 0)
            {
                writeChunk(CHUNK_TYPE_tRNS.getBytes(), alphas);
            }  // end if
        }
    }  // end writePalette()

    //
    // writeTransparency()
    //
    // Purpose: Writes a tRNS chunk with the specified color.
    //
    //		This is used in the case that the image is not palette indexed
    //		and the image has some total transparency but no partial
    //		transparency.
    //
    // Params:	Color	color		the color that indicates transparency
    //		boolena	grayscale	whether the image is grayscale
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeTransparencyOneColor(Integer color, boolean grayscale) throws IOException
    {
        byte[] data = grayscale ? new byte[2] : new byte[6];

        data[0] = (byte) ((color.intValue() >>> 16) & 0xFF);

        if (!grayscale)
        {
            data[2] = (byte) ((color.intValue() >>> 8) & 0xFF);
            data[4] = (byte) (color.intValue() & 0xFF);
        }  // end if

        writeChunk(CHUNK_TYPE_tRNS.getBytes(), data);
    }  // end writeTransparencyOneColor()

    //
    // writeBackground()
    //
    // Purpose: Write a bKGD chunk that contains the user-specified background
    //		color.
    //
    //		This method does not check backgroundColor to see whether it
    //		has been set, since this is a private method and should only
    //		be called in the case that the background color has indeed been
    //		set.
    //
    // Note: This code depends on the bit depth for non-palette-indexed images
    //       always being 8.
    //
    // Params:	ImageData	data	information about the image
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeBackground(ImageData data) throws IOException
    {
        // The chunk data size can be at most 6 bytes
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(6);

        if (data.isPaletteIndexed())
        {
            bytes.write(data.getPalette().getColorIndex(backgroundColor.intValue()));
        }
        else
        {
            bytes.write(0);
            bytes.write((backgroundColor.intValue() >>> 24) & 0xFF);

            if (!data.isGrayscale())
            {
                bytes.write(0);
                bytes.write((backgroundColor.intValue() >>> 16) & 0xFF);
                bytes.write(0);
                bytes.write(backgroundColor.intValue() & 0xFF);
            }  // end if
        }  // end if/else

        bytes.close();

        writeChunk(CHUNK_TYPE_bKGD.getBytes(), bytes.toByteArray());
    }  // end writeBackground()

    //
    // writeData()
    //
    // Purpose: Filter and compress the image data and write it to a series
    //		of 1 or more IDAT chunks.
    //
    // Params:	ImageData	data	the image to encode
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeData(ImageData data) throws IOException
    {
        // This is a close upper bound on the scan line size, used to allocate
        // the filtered line buffers.  The "+ 1" at the end is to ensure the
        // size is big enough in the cases where the number of bits in the scan
        // line is not a multiple of 8.
        int scanLineSize = 0;
        if (alpha && data.hasSingleAlpha())
        {
            if (data.grayscale)
                scanLineSize = data.getWidth() << 1;
            else
                scanLineSize = data.getWidth() * 4;
        }
        else
        {
            scanLineSize = data.getBitsPerPixel() * data.getWidth() / 8 + 1;
        }

        // Here, we allocate those global filtered scan line buffers.  We make
        // them one greater than the scan line size to accomodate the
        // filter-type byte at the beginning.
        filteredNone = new byte[scanLineSize + 1];
        filteredSub = new byte[scanLineSize + 1];
        filteredUp = new byte[scanLineSize + 1];
        filteredAverage = new byte[scanLineSize + 1];
        filteredPaeth = new byte[scanLineSize + 1];

        // Go ahead and assign the filter types, since they will never change.
        filteredNone[0] = FILTER_TYPE_NONE;
        filteredSub[0] = FILTER_TYPE_SUB;
        filteredUp[0] = FILTER_TYPE_UP;
        filteredAverage[0] = FILTER_TYPE_AVERAGE;
        filteredPaeth[0] = FILTER_TYPE_PAETH;

        // the object that will give us the scan lines
        NonInterlaceScanLineProvider provider = new NonInterlaceScanLineProvider(data);

        // This output stream does the job of compressing and buffering the data
        // and generating the IDAT chunks, so all we do is write the filtered
        // scan lines to this stream.
        OutputStream out = new DeflaterOutputStream(new IDATOutputStream(), new Deflater(compressionLevel));

        byte[] prevScanLine = null;
        while (provider.hasNextScanLine())
        {
            byte[] scanLine = provider.nextScanLine();

            // We MUST use this version of the write() method, because the
            // length of the data varies while the length of the buffer returned
            // by filterScanLine() does not.
            out.write(filterScanLine(scanLine, prevScanLine, data), 0, scanLine.length + 1);
            prevScanLine = provider.passFinished() ? null : scanLine;
        }  // end while

        out.close();

        // We're done with those global buffers, so make them elegable for
        // garbage collection.
        filteredNone = null;
        filteredSub = null;
        filteredUp = null;
        filteredAverage = null;
        filteredPaeth = null;
    }  // end writeData()

    //
    // filterScanLine()
    //
    // Purpose: Filter an image scan line.
    //
    //		This method fills and returns a reference to one of the global
    //		filtered scan line buffers.
    //
    // Params:	byte[]	scanLine	the scan line to filter
    //
    //		byte[]	prevScanLine	the previous scan line (needed for some
    //					of the filtering algorithms).  This is
    //					null if the scan line is the first in
    //					the current pass through the image.
    //
    //		ImageData	data	the image data
    //
    // Returns	byte[]	one of teh global scan line buffers
    //
    private byte[] filterScanLine(byte[] scanLine, byte[] prevScanLine, ImageData data)
    {
        // This buffer is going to be needed regardless of whether the image is
        // palette index.
        System.arraycopy(scanLine, 0, filteredNone, 1, scanLine.length);

        // Always use the NONE algorithm for indexed images.
        if (data.isPaletteIndexed())
        {
            return filteredNone;
        }  // end if

        // Number of bytes in a pixel; always an even division here.
        int bytesPerPixel = data.getBitsPerPixel() / 8;

        // Sums of differences for each filter method; used to select which
        // filter method to use.
        int sumNone = 0;
        int sumSub = 0;
        int sumUp = 0;
        int sumAverage = 0;
        int sumPaeth = 0;

        // To save time in determining which filter method to use, only go 1/4
        // of the scan line with every filter (Java is slow).
        int stop = scanLine.length <= 50 ? scanLine.length
                : scanLine.length / 4;

        // Calculate the sums of differences for each filter.
        for (int i = 0; i < stop; ++i)
        {
            sumNone += Math.abs((int) filteredNone[i + 1]);
            sumSub += filterSub(scanLine, i, bytesPerPixel);
            sumUp += filterUp(scanLine, i, prevScanLine);
            sumAverage += filterAverage(scanLine, i, prevScanLine,
                    bytesPerPixel);
            sumPaeth += filterPaeth(scanLine, i, prevScanLine,
                    bytesPerPixel);
        }  // end for

        // Select the filter type based on the sums of differences.
        int type = FILTER_TYPE_NONE;
        byte[] result = null;
        if (sumPaeth <= sumAverage && sumPaeth <= sumUp && sumPaeth <= sumSub && sumPaeth <= sumNone)
        {
            type = FILTER_TYPE_PAETH;
            result = filteredPaeth;
        }
        else if (sumAverage <= sumUp && sumAverage <= sumSub && sumAverage <= sumNone)
        {
            type = FILTER_TYPE_AVERAGE;
            result = filteredAverage;
        }
        else if (sumUp <= sumSub && sumUp <= sumNone)
        {
            type = FILTER_TYPE_UP;
            result = filteredUp;
        }
        else if (sumSub <= sumNone)
        {
            type = FILTER_TYPE_SUB;
            result = filteredSub;
        }  // end if/else

        if (type == FILTER_TYPE_NONE)
        {
            return filteredNone;
        }  // end if

        // Finish filtering the scan line according to the selected filter
        // method.
        for (int i = stop; i < scanLine.length; ++i)
        {
            switch (type)
            {
                case FILTER_TYPE_SUB:
                    filterSub(scanLine, i, bytesPerPixel);
                    break;
                case FILTER_TYPE_UP:
                    filterUp(scanLine, i, prevScanLine);
                    break;
                case FILTER_TYPE_AVERAGE:
                    filterAverage(scanLine, i, prevScanLine, bytesPerPixel);
                    break;
                case FILTER_TYPE_PAETH:
                    filterPaeth(scanLine, i, prevScanLine, bytesPerPixel);
                    break;
            }  // end switch
        }  // end for
        return result;
    }  // end filterScanLine()

    //
    // filterSub()
    //
    // Purpose: Calculate the filtered value for the specified byte in a scan
    //          line according to the SUB filter method, and store the result
    //		at the appropriate index in the global filteredSub buffer.
    //
    // Params:	byte[]	scanLine	the scan line
    //		int	i		the index of the byte to filter
    //		int	bytesPerPixel	the number of bytes in a pixel
    //
    // Returns:	int	the absolute value of the filtered byte
    //
    private int filterSub(byte[] scanLine, int i, int bytesPerPixel)
    {
        int left = (i - bytesPerPixel < 0) ? 0
                : scanLine[i - bytesPerPixel] & 0xff;

        int result = (scanLine[i] & 0xff) - left;
        while (result < 0)
        {
            result += 256;
        }  // end while
        result %= 256;

        filteredSub[i + 1] = (byte) result;

        return Math.abs((int) filteredSub[i + 1]);
    }  // end filterSub()

    //
    // filterUp()
    //
    // Purpose: Calculate the filtered value for the specified byte in a scan
    //		line according to the UP filter method, and store the result
    //		at the appropriate index in the global filteredUp buffer.
    //
    // Params:	byte[]	scanLine	the scan line
    //		int	i		the index of the byte to filter
    //		byte[]	prevScanLine	the previous scan line
    //
    // Returns:	int	the absolute value of the filtered byte
    //
    private int filterUp(byte[] scanLine, int i, byte[] prevScanLine)
    {
        int up = prevScanLine == null ? 0 : prevScanLine[i] & 0xff;

        int result = (scanLine[i] & 0xff) - up;
        while (result < 0)
        {
            result += 256;
        }  // end while
        result %= 256;

        filteredUp[i + 1] = (byte) result;

        return Math.abs((int) filteredUp[i + 1]);
    }  // end filterUp()

    //
    // filterAverage()
    //
    // Purpose: Calculate the filtered value for the specified byte in a scan
    //		line according to the AVERAGE filter method, and store the
    //		result	at the appropriate index in the global
    //		filteredAverage buffer.
    //
    // Params:	byte[]	scanLine	the scan line
    //		int	i		the index of the byte to filter
    //		byte[]	prevScanLine	the previous scan line
    //		int	bytesPerPixel	the number of bytes in a pixel
    //
    // Returns:	int	the absolute value of the filtered byte
    //
    private int filterAverage(byte[] scanLine, int i, byte[] prevScanLine, int bytesPerPixel)
    {
        int left = (i - bytesPerPixel < 0) ? 0
                : scanLine[i - bytesPerPixel] & 0xff;
        int up = prevScanLine == null ? 0 : prevScanLine[i] & 0xff;

        int average = (left + up) >> 1;

        int result = (scanLine[i] & 0xff) - average;
        while (result < 0)
        {
            result += 256;
        }  // end while
        result %= 256;

        filteredAverage[i + 1] = (byte) result;

        return Math.abs((int) filteredAverage[i + 1]);
    }  // end filterAverage()

    //
    // filterPaeth()
    //
    // Purpose: Calculate the filtered value for the specified byte in a scan
    //		line according to the PAETH filter method, and store the
    //		result	at the appropriate index in the global filteredPaeth
    //		buffer.
    //
    // Params:	byte[]	scanLine	the scan line
    //		int	i		the index of the byte to filter
    //		byte[]	prevScanLine	the previous scan line
    //		int	bytesPerPixel	the number of bytes in a pixel
    //
    // Returns:	int	the absolute value of the filtered byte
    //
    private int filterPaeth(byte[] scanLine, int i, byte[] prevScanLine, int bytesPerPixel)
    {
        int a = (i - bytesPerPixel < 0) ? 0
                : scanLine[i - bytesPerPixel] & 0xff;
        int b = (prevScanLine == null) ? 0 : prevScanLine[i] & 0xff;
        int c = (i - bytesPerPixel < 0 || prevScanLine == null)
                ? 0 : prevScanLine[i - bytesPerPixel] & 0xff;

        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        int result = scanLine[i] & 0xff;
        if (pa <= pb && pa <= pc)
        {
            result -= a;
        }
        else if (pb <= pc)
        {
            result -= b;
        }
        else
        {
            result -= c;
        }  // end if/else

        while (result < 0)
        {
            result += 256;
        }  // end while
        result %= 256;

        filteredPaeth[i + 1] = (byte) result;

        return Math.abs((int) filteredPaeth[i + 1]);
    }  // end filterPaeth()

    //
    // writeEnd()
    //
    // Purpose:	Writes the IEND chunk to the output stream provided by the
    //		client programmer.
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeEnd() throws IOException
    {
        writeChunk(CHUNK_TYPE_IEND.getBytes(), new byte[0]);
    }  // end writeEnd()

    //
    // writeChunk()
    //
    // Purpose: Write a PNG chunk to the output stream provided by the client
    //		programmer and flush the stream.
    //
    // Params:	byte[]	type	the 4-byte PNG chunk type
    //		byte[]	data	the data portion of the chunk
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeChunk(byte[] type, byte[] data) throws IOException
    {
        // The data cannot be bigger than MAX_CHUNK_DATA_SIZE bytes.
        if (data.length > MAX_CHUNK_DATA_SIZE)
        {
            throw new IllegalArgumentException("data too long: " + data.length);
        }  // end if

        writeInt(data.length, out);	// 4-byte length
        out.write(type);		// 4-byte type
        out.write(data);		// data (length specified in length
        //  field)
        // Calculate the CRC for the chunk.
        crcGenerator.reset();
        crcGenerator.update(type);
        crcGenerator.update(data);

        writeInt((int) crcGenerator.getValue(), out);  // 4-byte CRC

        // ...and send it on its way
        out.flush();
    }  // end writeChunk()

    //
    // writeInt()
    //
    // Purpose: Write an integer as a series of 4 bytes to an output stream,
    //		high byte first.
    //
    // Params:	int		value	the integer to write
    //		OutputStream	out	the stream to write to
    //
    // Throws:	IOException	if an I/O error occurs
    //
    private void writeInt(int value, OutputStream out) throws IOException
    {
        out.write((value >>> 24) & 0xff);
        out.write((value >>> 16) & 0xff);
        out.write((value >>> 8) & 0xff);
        out.write((value) & 0xff);
    }  // end writeInt()

    //
    // class ImageData
    //
    // Purpose: Contains information relavent to PNG about the image to encode,
    //		as well as the image pixel data.
    //
    public class ImageData
    {
        // width and height of the image
        private int width;
        private int height;

        // the color palette; only used if the image is palette-indexed
        private Palette palette;

        // some boolean flags about the image
        private boolean paletteIndexed;		// true if palette-indexed
        private boolean grayscale;		// true if grayscale
        private boolean gotAlphaChannel;	// true if has an alpha channel
        private boolean blackwhite; // true if the image is blackwhite without alpha

        // If the image has a single fully transparent color, and no partially
        // transparent colors, then this color will be it.  Otherwise, this
        // is null, so it also acts as a flag indicating whether the image has
        // such a property.
        private Integer singleAlpha;

        // The image data.
        private int[] pixels;

        // A random number generator, used in getScrambledColorComponents()
        private Random random = new Random();

        //
        // constructor
        //
        // Params:	Color[][]	the 2-D array of pixel colors
        //
        public ImageData(int[] pixels, int _w, int _h) throws IOException
        {
            this.pixels = pixels;

            // initialize image dimensions
            this.width = _w;
            this.height = _h;

            // These flags are set to their defaults here, but may be changed
            // by processColor() to reflect the actual properties of the image
            // before the constructor returns.
            this.paletteIndexed = true;
            this.grayscale = true;
            this.gotAlphaChannel = false;
            this.singleAlpha = null;
            this.blackwhite = true;

            // The set of all the colors in the image.  Its uses include
            // counting the number of distinct colors in the image and finding
            // a color that does not occur in the image (for the single alhpa
            // case).
            Set paletteColors = new HashSet();

            // If the user has set a background color, then include it as one
            // of the image colors.
            if (backgroundColor != null)
            {
                if (lg_outsidepalette)
                {
                    if (!p_outsidepalette.containsColor(backgroundColor.intValue()))
                    {
                        throw new IOException("The background color value is not supported by the palette");
                    }
                }
                else
                    this.processColor(backgroundColor.intValue(), paletteColors);
            }  // end if

            // Process every pixel of the image.
            for (int row = 0; row < height; ++row)
            {
                for (int col = 0; col < width; ++col)
                {
                    int c = pixels[row * width + col];

                    // If we haven't seen this color yet, then process it.
                    if (lg_outsidepalette)
                    {
                        if ((c & 0xFF000000) == 0) p_alphaColor = new Integer(c);
                        if (!p_outsidepalette.containsColor(c | 0xFF000000))
                        {
                            throw new IOException("The image contains a color which is not supported by the required palette [0x" + Integer.toHexString(c) + "]");
                        }
                        else
                        {
                            this.processColor(c, paletteColors);
                        }
                    }
                    else if (!paletteColors.contains(new Integer(c)))
                    {
                        this.processColor(c, paletteColors);
                    }  // end if
                }  // end for
            }  // end for

            if (lg_outsidepalette)
            {
                if (p_outsidepalette.getSize() < paletteColors.size()) throw new IOException("You have got more colors in the image than in the palette");
            }

            // Either the image will be palette indexed or it might have a
            // single alpha color.
            if (this.paletteIndexed)
            {
                if (lg_outsidepalette)
                {
                    if (!this.blackwhite) this.palette = p_outsidepalette;

                }
                else if (!this.blackwhite) this.palette = new Palette(paletteColors);
            }
            else if (this.singleAlpha != null)
            {
                // It is important that the red, green, and blue values of the
                // transparent color do not match any of those in the image.
                if (!lg_outsidepalette)
                {
                    this.singleAlpha = findUniqueColor(paletteColors);
                }
                else
                {
                    this.singleAlpha = getAlphaColor(paletteColors);
                    if (this.singleAlpha == null) throw new IOException("Error when I find the alpha color");
                }
            }  //end if

        }  // end constructor


        private Integer getAlphaColor(Set _colors)
        {
            if (lg_outsidepalette)
            {
                return p_alphaColor;
            }
            else
            {
                Iterator p_iter = _colors.iterator();
                while (p_iter.hasNext())
                {
                    Integer p_int = (Integer) p_iter.next();
                    if ((p_int.intValue() & 0xFF000000) == 0) return p_int;
                }
            }
            return null;
        }

        //
        // processColor()
        //
        // Purpose: Given a color from the image, update the image's properties
        //          and add the color to the set of colors.
        //
        // Params:	Color	color		the color to inspect
        //		Set	paletteColors	the set of all the colors seen
        //					 in the image so far
        //
        private void processColor(int color, Set paletteColors) throws IOException
        {
            int i_a = color >>> 24;
            int i_r = (color >>> 16) & 0xFF;
            int i_g = (color >>> 8) & 0xFF;
            int i_b = color & 0xFF;

            // If we haven't got an alpha channel yet, then see if this color
            // has transparency.
            if (!this.gotAlphaChannel)
            {
                // If it is completely transparent, then set the single alpha
                // color.
                if (i_a == 0)
                {
                    if (this.singleAlpha == null)
                    {
                        this.singleAlpha = new Integer(color);
                    }  // end if
                    this.blackwhite = false;
                }
                else
                // If it's only partially transparent, then add the alpha
                // channel.
                    if (i_a < 255)
                    {
                        if (lg_outsidepalette)
                        {
                            throw new IOException("You have a partially transparent value in your image");
                        }
                        this.gotAlphaChannel = true;
                        this.singleAlpha = null;
                        this.blackwhite = false;
                    }  // end if/else
            }  // end if

            // If we've been grayscale so far but this color isn't gray, then
            // cancel the image's grayscale status.
            if (this.grayscale && (i_r != i_g || i_r != i_b))
            {
                this.grayscale = false;
            }  // end if

            if (this.blackwhite && ((i_r | i_g | i_b) != 0) && ((i_r & i_g & i_b) != 0xFF))
            {
                this.blackwhite = false;
            }

            if (lg_outsidepalette)
            {
                if (!p_outsidepalette.containsColor(color | 0xFF000000))
                    throw new IOException("You have a color which is not supported by the palette [0x" + Integer.toHexString(color) + "]");

                int i_indx = p_outsidepalette.getColorIndex(color | 0xFF000000);
                p_outsidepalette.alphas[i_indx] = (byte)i_a;

            }
            paletteColors.add(new Integer(color));

            // If this color put us over the limit for maximum palette size,
            // then cancel the image's palette-indexed status.
            if (this.paletteIndexed && paletteColors.size() > 256)
            {
                this.paletteIndexed = false;
            }  // end if
        }  // end processColor()

        //
        // findUniqueColor()
        //
        // Purpose: Given a set of colors, come up with a color that isn't in
        //          the set.
        //
        // Params:	Set	color		the set of colors
        //
        // Returns:	Color	a color that isn't in the set, or Color.black
        //			if the set contains every color (but in that
        //			case, this method won't return for a long, long
        //			time... about 16.8 million iterations)
        //
        private Integer findUniqueColor(Set colors)
        {
            // Get some random-ordered color components to minimize the chance
            // of a worst-case run through the colors.
            int[] reds = getScrambledColorComponents();
            int[] greens = getScrambledColorComponents();
            int[] blues = getScrambledColorComponents();

            for (int r = 0; r <= 255; ++r)
            {
                for (int g = 0; g <= 255; ++g)
                {
                    for (int b = 0; b <= 255; ++b)
                    {
                        Integer color = new Integer((((reds[r] & 0xFF) << 24) & 0xFF) & ((greens[g] & 0xFF) << 16) & (blues[b] & 0xFF));
                        if (!colors.contains(color))
                        {
                            return color;
                        }  // end if
                    }  // end for
                }  // end for
            }  // end for

            if (lg_outsidepalette)
                return null;  // shut up the compiler
            else
                return new Integer(0);
        }  // end findUniqueColor()


        //
        // getScrambledColorComponents()
        //
        // Purpose: Create an array of the values 0 - 255 in random order.
        //
        // Returns:	int[]	the array of random-ordered values
        //
        private int[] getScrambledColorComponents()
        {
            // First create the array sorted.
            int[] array = new int[256];
            for (int i = 0; i < array.length; ++i)
            {
                array[i] = i;
            }  // end for

            // Then scramble it.
            for (int i = 0; i < array.length; ++i)
            {
                int j = i + random.nextInt(array.length - i);

                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }  // end for

            return array;
        }  // end getScrambledColorComponents()

        //
        // getWidth()
        //
        public int getWidth()
        {
            return width;
        }  // end getWidth()

        //
        // getHeight()
        //
        public int getHeight()
        {
            return height;
        }  // end getHeight()

        //
        // getBitDepth()
        //
        // Purpose: Calculate and return the image's bit depth.  This is not
        //          necessarily the same as the number of bits per pixel.  The
        //          only two cases where that is true is when the image is
        //          palette-indexed or when the image is grayscale with no alpha
        //          channel.
        //
        // Returns:	int	the image's bit depth
        //
        public int getBitDepth()
        {
            if (this.blackwhite) return 1;

            if (alpha && hasSingleAlpha()) return 8;

            // This is not true for PNG images in general, but it is with this
            // particular encoder since Java's RGB color model only uses 8 bits
            // per color component.
            if (!isPaletteIndexed())
            {
                return 8;
            }  // end if

            // Find the smallest legal bit depth that is big enough to index
            // every entry in the palette.
            int size = palette.getSize();
            if (size > 16)
            {
                return 8;
            }
            else if (size > 4)
            {
                return 4;
            }
            else if (size > 2)
            {
                return 2;
            }
            else
            {
                return 1;
            }  // end if/else
        }  // end getBitDepth()

        //
        // getChannelCount()
        //
        // Purpose: Calculate and return the number of channels (the number of
        //          components per color) in the image.
        //
        // Returns:	int	the number of channels in the image
        //
        public int getChannelCount()
        {
            // Palette entries always have red, green, and blue
            if (isPaletteIndexed())
            {
                return 3;
            }  // end getChannelCount()

            // Grascale has one channel, color has three
            int count = isGrayscale() ? 1 : 3;

            // Add one for alpha if we've got it.
            if (hasAlphaChannel())
            {
                ++count;
            }  // end if

            return count;
        }  // end getChannelCount()

        //
        // isPaletteIndexed()
        //
        public boolean isPaletteIndexed()
        {
            return paletteIndexed;
        }  // end isPaletteIndexed()

        //
        // hasPalette()
        //
        public boolean hasPalette()
        {
            return palette != null;
        }  // end hasPalette()

        //
        // getPalette()
        //
        public Palette getPalette()
        {
            return palette;
        }  // end getPalette()

        //
        // isGrayscale()
        //
        public boolean isGrayscale()
        {
            return grayscale;
        }  // end isGrayscale()

        //
        // hasAlphaChannel()
        //
        public boolean hasAlphaChannel()
        {
            return gotAlphaChannel;
        }  // end hasAlphaChannel()

        //
        // hasSingleAlpha()
        //
        public boolean hasSingleAlpha()
        {
            return singleAlpha != null;
        }  // end hasSingleAlpha()

        //
        // getAlphaColor()
        //
        public Integer getAlphaColor()
        {
            return singleAlpha;
        }  // end getAlphaColor()

        //
        // getBitsPerPixel()
        //
        // Purpose: Calculate and return the number of bits in a pixel.  For
        //          a palette-indexed image, this is simply the bit depth.
        //          Otherwise, it is the bit depth times the number of channels.
        //
        // Returns:	int	the number of bits used to encode a pixel
        //
        public int getBitsPerPixel()
        {
            if (isPaletteIndexed())
            {
                return getBitDepth();
            }
            else
            {
                return getBitDepth() * getChannelCount();
            }  // end if
        }  // end getBitsPerPixel()

        //
        // getPixelBits()
        //
        // Purpose: Create a byte array that contains the bits for the specified
        //          pixel.  The size of the array will be just big enough for
        //          the number of bits per pixel.
        //
        // Params:	int	x	the x coordinate of the pixel
        // 		int	y	the y coordinate of the pixel
        //
        // Returns:	byte[]	the bits for the pixel
        //
        public byte[] getPixelBits(int x, int y)
        {
            int bitsPerPixel = 0;

            if (alpha && hasSingleAlpha())
            {
                if (grayscale)
                {
                    bitsPerPixel = 2;
                }
                else
                {
                    bitsPerPixel = 4;
                }
            }
            else
            {
                bitsPerPixel = getBitsPerPixel();
                bitsPerPixel = bitsPerPixel % 8 == 0 ? bitsPerPixel / 8 : bitsPerPixel / 8 + 1;
            }

            // Make the array just big enough to hold all the bits.
            byte[] bits = new byte[bitsPerPixel];

            int i_xy = y * width + x;

            if (alpha && hasSingleAlpha())
            {
                if (grayscale)
                {
                    bits[0] = (byte) pixels[i_xy];
                    bits[1] = (byte) (pixels[i_xy] >>> 24);
                }
                else
                {
                    int off = 0;
                    bits[off++] = (byte) (pixels[i_xy] >>> 16);
                    bits[off++] = (byte) (pixels[i_xy] >>> 8);
                    bits[off++] = (byte) (pixels[i_xy]);
                    bits[off] = (byte) (pixels[i_xy] >>> 24);
                }
            }
            else
            {
                // If the image is palette-indexed, then the pixel will definitely
                // fit in one byte.
                if (isPaletteIndexed() && !this.blackwhite)
                {
                    bits[0] = (byte) palette.getColorIndex(pixels[i_xy]/* | 0xFF000000*/);
                    return bits;
                }  // end if
                else if (this.blackwhite)
                {
                    bits[0] = (byte) pixels[i_xy];
                    return bits;
                }
                else
                {
                    int off = 0;

                    bits[off++] = (byte) (pixels[i_xy] >>> 16);

                    if (!isGrayscale())
                    {
                        bits[off++] = (byte) (pixels[i_xy] >>> 8);
                        bits[off++] = (byte) (pixels[i_xy]);
                    }  // end if
                }
            }
/*
            // The image is not palette-indexed, so fill in the bytes with the
            // color components.  This bit of code relies on the fact that
            // in this encoder the bit depth for a non-palette-indexed image
            // is always 8


            if (hasAlphaChannel())
            {
                bits[off++] = (byte) (pixels[y][x] >>> 24);
            }  // end if
  */
            return bits;
        }  // end getPixelBits()

        //
        // getPixels()
        //
        public int[] getPixels()
        {
            return pixels;
        }  // end getPixels()

        //
        // getFreeSpaceLeft()
        //
        public int getFreeSpaceLeft()
        {
          int i_color;
          if(p_alphaColor!=null) i_color = p_alphaColor.intValue();
            else i_color = pixels[(height>>1)*width];
          for(int x = 0; x < width; x++)
          {
           int offset = x;
           for(int y = 0; y < height; y++)
           {
              if(pixels[offset]!=i_color)
                return x;
              offset += width;
           }
          }
          return width;
        } // end getFreeSpaceLeft()

        //
        // getFreeSpaceRight()
        //
        public int getFreeSpaceRight()
        {
          int i_color;
          if(p_alphaColor!=null) i_color = p_alphaColor.intValue();
            else i_color = pixels[((height>>1)+1)*width-1];
          for(int x = 0; x < width; x++)
          {
           int offset = width-x-1;
           for(int y = 0; y < height; y++)
           {
              if(pixels[offset]!=i_color)
                return x;
              offset += width;
           }
          }
          return width;
        } // end getFreeSpaceRight()

        //
        // end getFreeSpaceTop()
        //
        public int getFreeSpaceTop()
        {
          int i_color;
          if(p_alphaColor!=null) i_color = p_alphaColor.intValue();
            else i_color = pixels[width>>1];
          int offset = 0;
          for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
              if(pixels[offset++]!=i_color)
               return y;
          return height;
        } // end getFreeSpaceTop()

        //
        // getFreeSpaceBottom()
        //
        public int getFreeSpaceBottom()
        {
          int i_color;
          int offset = height*width-1;
          if(p_alphaColor!=null) i_color = p_alphaColor.intValue();
            else i_color = pixels[offset-(width>>1)];
          for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
              if(pixels[offset--]!=i_color)
               return y;
          return height;
        } // end getFreeSpaceBottom()

        //
        // cropImage(int left,int right, int top,int bottom)
        //
        public void cropImage(int left,int right, int top,int bottom)
        {
          int width = this.width - left - right;
          int height = this.height - top - bottom;
          if(left>=0 && right>=0 && top>=0 && bottom>=0 && height>=0 && width>=0)
          {
            int[]dst_pixels = new int[width*height];
            int offsetSrc = top*this.width + left;
            int addXSrc = this.width - width;
            int offsetDst = 0;
            for(int j = 0;j<height;j++)
            {
               for(int i = 0;i<width;i++)
                 dst_pixels[offsetDst++] = pixels[offsetSrc++];
               offsetSrc += addXSrc;
            }
            pixels = dst_pixels;
            this.width = width;
            this.height = height;
          }
        } // end cropImage(int left,int right, int top,int bottom)
    }  // end class ImageData

    //
    // class Consumer
    //
    // Purpose: Construct the image from the image producer.
    //
    private class Consumer
    {
        ImageData p_imagedata;

        public Consumer() throws IOException
        {
            int i_width = p_image.getWidth(null);
            int i_height = p_image.getHeight(null);

            p_mtracker.addImage(p_image, 0);

            try
            {
                p_mtracker.waitForAll();
            }
            catch (InterruptedException e)
            {
                return;
            }

            p_mtracker.removeImage(p_image);

            int[] ai_grabarray = new int[i_width * i_height];
            PixelGrabber p_pixgrab = new PixelGrabber(p_image, 0, 0, i_width, i_height, ai_grabarray, 0, i_width);

            try
            {
                p_pixgrab.grabPixels();
            }
            catch (InterruptedException e)
            {
                return;
            }

            p_image = null;
            p_imagedata = new ImageData(ai_grabarray, i_width, i_height);
        }

        public ImageData getImageData()
        {
            return p_imagedata;
        }

    }  // end class Consumer


    //
    // class ImmutableByteArray
    //
    // Purpose: A byte array that cannot be modified.
    //
    private static class ImmutableByteArray
    {

        private byte[] bytes;


        //
        // constructor
        //
        // Params:	byte[]	bytes	the array that this object will contain
        //
        public ImmutableByteArray(byte[] bytes)
        {
            this.bytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
        }  // end class ImmutableByteArray()


        //
        // getBytes()
        //
        // Purpose: Get a *copyFrom* of the bytes contained in this object.
        //
        // Returns:	byte[]	the bytes contained in this object
        //
        public byte[] getBytes()
        {
            byte[] toGo = new byte[bytes.length];
            System.arraycopy(bytes, 0, toGo, 0, bytes.length);

            return toGo;
        }  // end getBytes()

    }  // end class ImmutableByteArray

    //
    // class NonInterlaceScanLineProvider
    //
    // Purpose: Provide image scan lines in a single pass, in top-down order.
    //
    private class NonInterlaceScanLineProvider
    {

        // the ImageData to get the scan lines from
        private ImageData data;

        // the current image row index
        private int index;

        // the object that puts together the scanline.  It is more efficient to
        // reuse the same one each time, rather than create a new
        // ScanLineBuilder for each call to nextScanLine()
        private ScanLineBuilder scanLineBuilder;


        //
        // constructor
        //
        // Params:	ImageData	data	the source of the image data
        //
        public NonInterlaceScanLineProvider(ImageData data)
        {
            this.data = data;
            this.index = 0;
            this.scanLineBuilder = new ScanLineBuilder(data);
        }  // end constructor


        //
        // hasNextScanLine()
        //
        public boolean hasNextScanLine()
        {
            return index < data.getHeight();
        }  // end hasNextScanLine()


        //
        // nextScanLine()
        //
        public byte[] nextScanLine()
        {
            if (!this.hasNextScanLine())
            {
                throw new NoSuchElementException("no more scan lines");
            }  // end if

            for (int i = 0; i < data.getWidth(); ++i)
            {
                scanLineBuilder.writePixel(data.getPixelBits(i, index));
            }  // end for

            ++index;

            return scanLineBuilder.finish();
        }  // end nextScanLine()


        //
        // passFinished()
        //
        public boolean passFinished()
        {
            return !this.hasNextScanLine();
        }  // end passFinished()

    }  // end class NonInterlaceScanLineProvider


    //
    // class ScanLineBuilder
    //
    // Purpose: Pack pixel bits into a scan line.
    //
    private class ScanLineBuilder
    {

        // the buffer for when the scan line does not end on a byte boundary.
        // The bits are put into this buffer in the higher bits first.
        private int buffer;

        // the bit index into the above buffer
        private int bufferIndex;

        // the number of bits in a pixel
        private int bitsPerPixel;

        // the scan line buffer
        private ByteArrayOutputStream scanLine;

        //
        // constructor
        //
        // Params:	ImageData	data	information about the image
        //
        public ScanLineBuilder(ImageData data)
        {
            this.bitsPerPixel = data.getBitsPerPixel();
            this.scanLine = new ByteArrayOutputStream(data.getWidth() * bitsPerPixel / 8 + 1);
        }  // end constructor

        //
        // writePixel()
        //
        // Purpose: Add the bits of a pixel to the scan line buffer.
        //
        // Params:	byte[]	pixel	the pixel bits
        //
        public void writePixel(byte[] pixel)
        {
            // The bits per pixel with be either less than 8 or equal to 8.
            // This is not true for PNG images in general, but it is true for
            // this particular PNG encoder because colors in the Java RGB color
            // model have 8 bits per component.

            if (bitsPerPixel < 8)
            {
                /*
                // Calculate the number of bits to shift the buffer over so that
                // the bits we want to set are in the lowest position.
                int shift = 8 - bufferIndex - bitsPerPixel;

                    // Set the given bits by right shifting, ORing, and finally
                    // shifting back to the left.
                    buffer = ((buffer >>> shift) | (pixel[0] & 0xff)) << shift;
                */
                int i_shift = 8 - bufferIndex - bitsPerPixel;
                int i_mask = (1 << bitsPerPixel) - 1;
                buffer |= ((pixel[0] & i_mask) << i_shift);


                // Increment the buffer index by the number of bits in a pixel,
                // and flush our little byte buffer to the overall buffer if it
                // is full.
                bufferIndex += bitsPerPixel;
                if (bufferIndex >= 8)
                {
                    this.flush();
                }  // end if
            }
            else
            {
                // This part's a no-brainer...except that we have to use the
                // write(byte[],int,int) version, because the write(byte[])
                // version is declared to throw an exception, and I don't want
                // to be bothered with that.

                scanLine.write(pixel, 0, pixel.length);
            }  // end if
        }  // end writePixel()

        //
        // finish()
        //
        // Purpose: Get the contents of the scan line and reset the internal
        //          state to be ready to start building a fresh scan line.
        //
        // Returns:	byte[]	the scan line that was built
        //
        public byte[] finish()
        {
            // Write out our little byte buffer if it isn't empty.
            if (bufferIndex > 0)
            {
                this.flush();
            }  // end if

            byte[] line = scanLine.toByteArray();

            scanLine.reset();

            return line;
        }  // end finish()

        //
        // flush()
        //
        // Purpose: Write the byte buffer to the main scan line buffer.
        //
        private void flush()
        {
            scanLine.write(buffer);
            bufferIndex = 0;
            buffer = 0;
        }  // end flush()

    }  // end class ScanLineBuilder


    //
    // class IDATOutputStream
    //
    // Purpose: Generate IDAT chunks that are bufferSize bytes long
    //		(not including the length, type, and CRC fields).
    //
    private class IDATOutputStream extends OutputStream
    {
        // the data buffer
        private ByteArrayOutputStream buffer;

        // whether or not this stream has been closed
        private boolean closed;

        //
        // constructor
        //
        public IDATOutputStream()
        {
            // Heh heh...we do NOT want to pass in bufferSize as the argument
            // to the ByteArrayOutputStream constructor.  Can we say
            // OutOfMemoryError?  2 KB is a reasonable size.

            this.buffer = new ByteArrayOutputStream(2048);
            this.closed = false;
        }  // end constructor


        //
        // close()
        //
        // Purpose: Flushes and closes this stream.
        //
        // Throws:	IOException	if an I/O error occurs
        //
        public void close() throws IOException
        {
            if (closed)
            {
                return;
            }  // end if

            this.flush();
            this.buffer.close();
            this.closed = true;
        }  // end close()


        //
        // flush()
        //
        // Purpose: Writes the buffer to an IDAT chunk and resets the buffer.
        //
        // Throws:	IOException	if an I/O error occurs
        //
        public void flush() throws IOException
        {
            if (closed)
            {
                throw new IOException("stream is closed");
            }  // end if

            if (buffer.size() == 0)
            {
                return;
            }  // end if

            writeChunk(CHUNK_TYPE_IDAT.getBytes(), buffer.toByteArray());

            buffer.reset();
        }  // end flush()


        //
        // write(byte[])
        //
        public void write(byte[] b) throws IOException
        {
            this.write(b, 0, b.length);
        }  // end write()

        //
        // write(byte[],int,int)
        //
        public void write(byte[] b, int off, int len) throws IOException
        {
            if (closed)
            {
                throw new IOException("stream is closed");
            }  // end if

            while (buffer.size() + len > bufferSize)
            {
                int numToWrite = bufferSize - buffer.size();

                buffer.write(b, off, numToWrite);
                this.flush();

                off += numToWrite;
                len -= numToWrite;
            }  // end while

            buffer.write(b, off, len);
            if (buffer.size() >= bufferSize)
            {
                this.flush();
            }  // end if
        }  // write()

        //
        // write(int)
        //
        public void write(int b) throws IOException
        {
            if (closed)
            {
                throw new IOException("stream is closed");
            }  // end if

            buffer.write(b);
            if (buffer.size() >= bufferSize)
            {
                this.flush();
            }  // end if
        }  // end write()

    }  // end class IDATOutputStream

    public static void packingPNGtoLitePNG(byte[] _inarray, DataOutputStream _outputstream) throws IOException
    {
        final int HEADER_IHDR = 0;
        final int HEADER_PLTE = 1;
        final int HEADER_IDAT = 2;
        final int HEADER_tRNS = 3;

        int i_offset = 8;
        int i_len = 8;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(512);

        // Writing two bytes for length of PNG file
        p_baos.write(0);
        p_baos.write(0);

        while (i_offset < _inarray.length)
        {
            // Reading of the chunk length field
            int i_chunklen = 0;
            i_chunklen = (_inarray[i_offset++] & 0xFF) << 24;
            i_chunklen |= ((_inarray[i_offset++] & 0xFF) << 16);
            i_chunklen = ((_inarray[i_offset++] & 0xFF) << 8);
            i_chunklen |= (_inarray[i_offset++] & 0xFF);

            // Reading of the chunk name field
            String s_chnk = "";
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];

            if (s_chnk.equals("IHDR"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_IHDR);

                int i_wdth = 0;
                i_wdth = (_inarray[i_offset++] & 0xFF) << 24;
                i_wdth |= ((_inarray[i_offset++] & 0xFF) << 16);
                i_wdth = ((_inarray[i_offset++] & 0xFF) << 8);
                i_wdth |= (_inarray[i_offset++] & 0xFF);

                if (i_wdth > 255) throw new IOException("Too big width in the image");

                int i_hght = 0;
                i_hght = (_inarray[i_offset++] & 0xFF) << 24;
                i_hght |= ((_inarray[i_offset++] & 0xFF) << 16);
                i_hght = ((_inarray[i_offset++] & 0xFF) << 8);
                i_hght |= (_inarray[i_offset++] & 0xFF);

                if (i_hght > 255) throw new IOException("Too big height in the image");

                int i_bitdepth = _inarray[i_offset++] & 0xFF;
                int i_colortype = _inarray[i_offset++] & 0xFF;

                // Compression method
                if (_inarray[i_offset++] != 0) throw new IOException("Unsupported compression method");

                // Filter method
                if (_inarray[i_offset++] != 0) throw new IOException("Unsupported filter method");

                // Interlace method
                int i_interlace = (_inarray[i_offset++] & 0xFF);
                if (i_interlace > 1) throw new IOException("Unsupported interlace mode");

                // Reading CRC
                i_offset += 4;

                // Writing data to stream

                // Writing of the width of the image
                p_baos.write(i_wdth);
                // Writing of the height of the image
                p_baos.write(i_hght);
                // Writing of the bitdepth and the colortype of the image
                p_baos.write(((i_bitdepth << 4) & 0xF0) | ((i_colortype & 0x0F) | (i_interlace << 3)));


                String s_statistic = i_wdth + "x" + i_hght;

                s_statistic += " bpp: " + i_bitdepth;
                if (i_bitdepth > 8)
                {
                    s_statistic = "!" + s_statistic;
                    //Toolkit.getDefaultToolkit().beep();
                }

                switch (i_colortype)
                {
                    case 0:
                        s_statistic += " GRAY";
                        break;
                    case 2:
                        s_statistic += " RGB";
                        break;
                    case 3:
                        {
                            s_statistic += " PALETTE";
                            s_statistic = "!" + s_statistic;
                            //Toolkit.getDefaultToolkit().beep();
                        }
                        ;
                        break;
                    case 4:
                        s_statistic += " GRAY+ALPHA";
                        break;
                    case 6:
                        s_statistic += " RGB+ALPHA";
                        break;
                    default :
                        throw new IOException("Unknown color type [" + i_colortype + "]");
                }

                if (i_interlace == 1) s_statistic += " (!)INTERLACED";
//                if(lg_verbose)
//                   System.out.println(s_statistic);

            }
            else if (s_chnk.equals("IDAT"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_IDAT);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("PLTE"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_PLTE);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("tRNS"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_tRNS);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("IEND"))
            {
                i_len += 12;
                break;
            }
            else
            {
                i_offset += i_chunklen + 4;
            }
        }

        p_baos.flush();
        byte[] ab_litepng = p_baos.toByteArray();
        ab_litepng[0] = (byte) (i_len >>> 8);
        ab_litepng[1] = (byte) i_len;

        // Writing the lite png to output stream
        _outputstream.write(ab_litepng);
        _outputstream.flush();
        ab_litepng = null;
    }

}
