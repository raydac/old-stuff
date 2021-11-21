package mtv.slideshow;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Display;

public interface MenuActionListener
{
    /**
     * Возвращаем объект, который будет отображать меню
     * @return Display объект
     */
    public Display getDisplay();

    /**
     * Запрос на объект-изображение
     * @param _index индекс затребованного изображения
     * @return объект-изображение
     */
    public Image getImageForIndex(int _index);

    /**
     * Запрос на строку
     * @param _index индекс затребованной строки
     * @return строка
     */
    public String getStringForIndex(int _index);

    /**
     * Processing a command for an item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     */
    public void processListItem(int _screenId, int _itemId);

    /**
     * Обработка команды
     * @param _screen текущий объект отображения экрана
     * @param _screenId  идентификатор текущего экрана
     * @param _commandId идентификатор команды
     * @param _selectedId идентификатор выбранного элемента в списке, для форм всегда -1
     */
    public void processCommand(Displayable _screen,int _screenId, int _commandId, int _selectedId);

    /**
     * Запрос на разрешение добавления команды в список команд
     * @param _screenId  идентификатор экрана
     * @param _commandId идентификатор команды
     * @return true если команда разрешена к добавлению, false если команда запрещена
     */
    public boolean enableCommand(int _screenId, int _commandId);

    /**
     * Заполняем настраевыемый экран
     * @param _screenId  идентификатор экрана
     * @return сформированный экран
     */
    public Displayable customScreen(int _screenId);

    /**
     * Формируем настраевыемый элемент
     * @param _screenId  идентификатор формируемого экрана
     * @param _itemId идентификатор формируемого элемента
     * @param _getImage true, если ожидается Image объект для List screen и false, если ожидается String объект
     * @return новый элемент Item(для Form) или String/Image объект(для List)
     */
    public Object customItem(int _screenId, int _itemId, boolean _getImage);

    /**
     * Запрос на добавление элемента в формируемый экран (если элемент опциональный)
     * @param _screenId  идентификатор формируемого экрана
     * @param _itemId  идентификатор добавляемого элемента
     * @return если возвращаем true то элемент добавляется, если false то не добавляется
     */
    public boolean enableItem(int _screenId, int _itemId);

    /**
     * Обрабатываем событие закрытия экрана
     * @param _screen Объект отображающий экран
     * @param _screenId Идентификатор экрана
     */
    public void onExitScreen(Displayable _screen, int _screenId);
}
