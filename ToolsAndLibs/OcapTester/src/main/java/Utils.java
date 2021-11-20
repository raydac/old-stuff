

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.io.DataInputStream;
import java.io.InputStream;

public class Utils
{
    public static Class p_This;
    public static final String RESOURCEPATH = "/";
    private static MediaTracker p_mediaTracker;
    
    public static final Object loadArray(String _resource) throws Throwable
    {
        final int ARRAY_BYTE = 0;
        final int ARRAY_CHAR = 1;
        final int ARRAY_SHORT = 2;
        final int ARRAY_INT = 3;
        final int ARRAY_LONG = 4;

        DataInputStream p_instr = new DataInputStream(getResourceAsStream(_resource));

        byte[] ab_byteArr = null;
        char[] ach_charArr = null;
        short[] ash_shortArr = null;
        int[] ai_intArr = null;
        long[] al_longArr = null;

        int i_type = p_instr.readUnsignedByte();
        int i_length = p_instr.readUnsignedShort();
        int i_byteSize = p_instr.readUnsignedByte();

        // System.out.println("type "+i_type);
        // System.out.println("len "+i_length);
        // System.out.println("bsize "+i_byteSize);

        switch (i_type)
        {
            case ARRAY_BYTE:
            {
                ab_byteArr = new byte[i_length];
            }
                ;
                break;
            case ARRAY_CHAR:
            {
                ach_charArr = new char[i_length];
            }
                ;
                break;
            case ARRAY_SHORT:
            {
                ash_shortArr = new short[i_length];
            }
                ;
                break;
            case ARRAY_INT:
            {
                ai_intArr = new int[i_length];
            }
                ;
                break;
            case ARRAY_LONG:
            {
                al_longArr = new long[i_length];
            }
                ;
                break;
        }

        if (p_instr.readUnsignedByte() == 1)
        {
            int i_index = 0;
            while (i_index < i_length)
            {
                int i_len = p_instr.readUnsignedByte();
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                    {
                        l_val = p_instr.readByte();
                    }
                        ;
                        break;
                    case 2:
                    {
                        l_val = p_instr.readShort();
                    }
                        ;
                        break;
                    case 4:
                    {
                        l_val = p_instr.readInt();
                    }
                        ;
                        break;
                    case 8:
                    {
                        l_val = p_instr.readLong();
                    }
                        ;
                        break;
                }

                int li = 0;

                while (li <= i_len)
                {
                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                        {
                            ab_byteArr[li] = (byte) l_val;
                        }
                            ;
                            break;
                        case ARRAY_CHAR:
                        {
                            ach_charArr[li] = (char) l_val;
                        }
                            ;
                            break;
                        case ARRAY_SHORT:
                        {
                            ash_shortArr[li] = (short) l_val;
                        }
                            ;
                            break;
                        case ARRAY_INT:
                        {
                            ai_intArr[li] = (int) l_val;
                        }
                            ;
                            break;
                        case ARRAY_LONG:
                        {
                            al_longArr[li] = l_val;
                        }
                            ;
                            break;
                    }

                    i_index++;
                    li++;
                }
            }
        }
        else
        {
            int i_index = 0;
            while (i_index < i_length)
            {
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                    {
                        l_val = p_instr.readByte();
                    }
                        ;
                        break;
                    case 2:
                    {
                        l_val = p_instr.readShort();
                    }
                        ;
                        break;
                    case 4:
                    {
                        l_val = p_instr.readInt();
                    }
                        ;
                        break;
                    case 8:
                    {
                        l_val = p_instr.readLong();
                    }
                        ;
                        break;
                }

                switch (i_type)
                {
                    case ARRAY_BYTE:
                    {
                        ab_byteArr[i_index] = (byte) l_val;
                    }
                        ;
                        break;
                    case ARRAY_CHAR:
                    {
                        ach_charArr[i_index] = (char) l_val;
                    }
                        ;
                        break;
                    case ARRAY_SHORT:
                    {
                        ash_shortArr[i_index] = (short) l_val;
                    }
                        ;
                        break;
                    case ARRAY_INT:
                    {
                        ai_intArr[i_index] = (int) l_val;
                    }
                        ;
                        break;
                    case ARRAY_LONG:
                    {
                        al_longArr[i_index] = l_val;
                    }
                        ;
                        break;
                }

                i_index++;
            }
        }

        closeStream(p_instr);
        p_instr = null;
        
        switch (i_type)
        {
            case ARRAY_BYTE:
            {
                return ab_byteArr;
            }
            case ARRAY_CHAR:
            {
                return ach_charArr;
            }
            case ARRAY_SHORT:
            {
                return ash_shortArr;
            }
            case ARRAY_INT:
            {
                return ai_intArr;
            }
            case ARRAY_LONG:
            {
                return al_longArr;
            }
        }

        return null;
    }

    public static final void loadArrayFromStream(InputStream _inStream, int _length, byte[] _array) throws Throwable
    {
        int i_lengthForRead = _length;
        int i_position = 0;

        while (i_lengthForRead > 0)
        {
            int i_readLen = _inStream.read(_array, i_position, i_lengthForRead);
            i_position += i_readLen;
            i_lengthForRead -= i_readLen;
        }
    }

    public static final byte[] loadArrayFromStream(InputStream _inStream, int _length) throws Throwable
    {
        byte[] ab_newArray = new byte[_length];
        loadArrayFromStream(_inStream, _length, ab_newArray);
        return ab_newArray;
    }

    public static final InputStream getResourceAsStream(String _resourceName) throws Throwable
    {
        InputStream p_inStream = p_This.getResourceAsStream(_resourceName);
        if (p_inStream==null) throw new Throwable(_resourceName);
        return p_inStream;
    }
    
    /**
     * Загрузка изображения из ресурса с заданным именем
     * @param _resourceName имя ресурса
     * @return объект Image если удалось его загрузить
     * @throws Throwable порождается в случае проблем с загрузкой
     */
    public static final Image loadImageFromResource(String _resourceName) throws Throwable
    {
        _resourceName = RESOURCEPATH+_resourceName;
        
        java.net.URL p_url = p_This.getResource(_resourceName); 

        System.out.println("Load image from "+p_url);
        
        Image p_result = Toolkit.getDefaultToolkit().getImage(p_url);
        int i_label = (int)System.currentTimeMillis();

        if (p_mediaTracker == null) p_mediaTracker = new MediaTracker(startup.p_This);
        
        p_mediaTracker.addImage(p_result, i_label);
        try
        {
            p_mediaTracker.waitForID(i_label);
        }
        catch(Throwable _thr){}
        finally
        {
            p_mediaTracker.removeImage(p_result,i_label);
        }

        if (p_result.getWidth(null) < 0 || p_result.getHeight(null) < 0)
            p_result = null;

        if (p_result==null) System.out.println("Can't load "+_resourceName);
        
        return p_result;
    }

    /**
     * Создает прозрачное изображение
     * @param _width ширина в пикселях
     * @param _height высота в пикселях
     * @return созданный объект-картинка
     */
    public static final Image createTransparentImage(int _width, int _height)
    {
        return startup.p_This.createImage(_width,_height);
    }

    /**
     * Создает изображение из данных в массиве
     * @param _imageArray массив содержащий данные
     * @param _pos начальная позиция данных
     * @param _len длинна данных
     * @return созданное изображение или null если не удалось создать 
     * @throws Throwable порождается если произошла ошибка
     */
    public static final Image createImageFromArray(byte[] _imageArray, int _pos, int _len) throws Throwable
    {
        java.awt.Image p_image = java.awt.Toolkit.getDefaultToolkit().createImage(_imageArray, _pos, _len);
        
        if (p_mediaTracker == null) p_mediaTracker = new MediaTracker(startup.p_This);
        
        p_mediaTracker.addImage(p_image, 0);
        p_mediaTracker.waitForAll();
        p_mediaTracker.removeImage(p_image);
        if (p_image.getWidth(null) < 0 || p_image.getHeight(null) < 0) p_image = null;
        return p_image;
    }

    /**
     * Загрузка блока, содержащего раздельные тайловые картинки
     * 
     * @param _resource имя ресурса
     * @return массив картинок 
     * @throws Throwable порождается если была ошибка загрузки
     */
    public static final Image[] loadSeparatedImagesContainer(String _resource) throws Throwable
    {
        _resource = RESOURCEPATH + _resource;

        InputStream p_inStream =getResourceAsStream(_resource);
        int i_imagesNumber = p_inStream.read() + 1;

        int i_maxFileSize = p_inStream.read() << 8;
        i_maxFileSize |= p_inStream.read();

        byte[] ab_pngArray = new byte[i_maxFileSize];

        final Image[] ap_resultArray = new Image[i_imagesNumber];
        int i_indx = 0;
        while (i_imagesNumber != 0)
        {
            int i_size = p_inStream.read() << 8;
            i_size |= p_inStream.read();

            Utils.loadArrayFromStream(p_inStream, i_size, ab_pngArray);
            Image p_tileImage = Utils.createImageFromArray(ab_pngArray, 0, i_size);
            ap_resultArray[i_indx++] = p_tileImage;
            i_imagesNumber--;
        }

        closeStream(p_inStream);
        p_inStream = null;
        ab_pngArray = null;
        return ap_resultArray;
    }

    public static final void closeStream(InputStream _stream)
    {
        if (_stream!=null)
        {
            try
            {
                _stream.close();
            }
            catch(Throwable _thr)
            {
            }
        }
    }
}
