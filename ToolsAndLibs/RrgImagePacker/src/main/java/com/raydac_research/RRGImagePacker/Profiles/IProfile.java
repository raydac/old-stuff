package com.raydac_research.RRGImagePacker.Profiles;

import com.raydac_research.RRGImagePacker.Container.PaletteCnt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface IProfile
{
    /**
     * Возвращает имя профиля
     * @return
     */
    public String getProfileName();

    /**
     * Возвращает максимально допустимые значения для размера картинок
     * @return
     */
    public Dimension getMaxImageDimension();

    /**
     * Проверка, может ли устройство производить операции по флипу изображения
     * @return TRUE если да, FALSE если нет
     */
    public boolean doesSupportImageFlip();

    /**
     * Проверка, может ли устройство производить операции по вращению изображения
     * @return TRUE если да, FALSE если нет
     */
    public boolean doesSupportImageRotate();

    /**
     * Проверка, работает ли быстро функция вывода куска изображения на устройстве
     * @return TRUE если быстро, FALSE если медленно
     */
    public boolean isRegionOutputFast();

    /**
     * Проверка, можно ли использовать ImageRef формат
     * @return TRUE если можно, FALSE если нельзя
     */
    public boolean doesSupportImageRefFormat();

    /**
     * Возвращает количество бит на альфа канал
     * @return
     */
    public int getBitsPerAlpha();

    /**
     * Возвращает количество бит на R канал
     * @return
     */
    public int getBitsPerRed();

    /**
     * Возвращает количество бит на G канал
     * @return
     */
    public int getBitsPerGreen();

    /**
     * Возвращает количество бит на B канал
     * @return
     */
    public int getBitsPerBlue();

    /**
     * Возвращает количество бит на точку
     * @return
     */
    public int getBitsPerPixel();

    /**
     * Возвращает размер памяти устройства в байтах
     * @return
     */
    public int getMemorySize();

    /**
     * Возвращает поддерживаемые форматы
     * @return
     */
    public int [] getSupportedGraphicFormats();

    /**
     * Сконвертировать формат в поддерживаемый
     * @param _format заданный формат картинки
     * @return тип поддерживаемого формата, наиболее близкого к заданному
     */
    public int convertFormatToSupported(int _format);

    /**
     * Обработка ARGB изображения
     * @param _argbImage ARGB картинка
     * @return обработанная картинка
     */
    public BufferedImage processImage(BufferedImage _argbImage) throws IOException;

    /**
     * Обработка палитры
     * @param _palette палитра
     * @return обработанная палитра
     */
    public PaletteCnt processPalette(PaletteCnt _palette) throws IOException;
}
