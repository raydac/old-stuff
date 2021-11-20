package mtv.slideshow;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Display;

public interface MenuActionListener
{
    /**
     * ���������� ������, ������� ����� ���������� ����
     * @return Display ������
     */
    public Display getDisplay();

    /**
     * ������ �� ������-�����������
     * @param _index ������ �������������� �����������
     * @return ������-�����������
     */
    public Image getImageForIndex(int _index);

    /**
     * ������ �� ������
     * @param _index ������ ������������� ������
     * @return ������
     */
    public String getStringForIndex(int _index);

    /**
     * Processing a command for an item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     */
    public void processListItem(int _screenId, int _itemId);

    /**
     * ��������� �������
     * @param _screen ������� ������ ����������� ������
     * @param _screenId  ������������� �������� ������
     * @param _commandId ������������� �������
     * @param _selectedId ������������� ���������� �������� � ������, ��� ���� ������ -1
     */
    public void processCommand(Displayable _screen,int _screenId, int _commandId, int _selectedId);

    /**
     * ������ �� ���������� ���������� ������� � ������ ������
     * @param _screenId  ������������� ������
     * @param _commandId ������������� �������
     * @return true ���� ������� ��������� � ����������, false ���� ������� ���������
     */
    public boolean enableCommand(int _screenId, int _commandId);

    /**
     * ��������� ������������� �����
     * @param _screenId  ������������� ������
     * @return �������������� �����
     */
    public Displayable customScreen(int _screenId);

    /**
     * ��������� ������������� �������
     * @param _screenId  ������������� ������������ ������
     * @param _itemId ������������� ������������ ��������
     * @param _getImage true, ���� ��������� Image ������ ��� List screen � false, ���� ��������� String ������
     * @return ����� ������� Item(��� Form) ��� String/Image ������(��� List)
     */
    public Object customItem(int _screenId, int _itemId, boolean _getImage);

    /**
     * ������ �� ���������� �������� � ����������� ����� (���� ������� ������������)
     * @param _screenId  ������������� ������������ ������
     * @param _itemId  ������������� ������������ ��������
     * @return ���� ���������� true �� ������� �����������, ���� false �� �� �����������
     */
    public boolean enableItem(int _screenId, int _itemId);

    /**
     * ������������ ������� �������� ������
     * @param _screen ������ ������������ �����
     * @param _screenId ������������� ������
     */
    public void onExitScreen(Displayable _screen, int _screenId);
}
