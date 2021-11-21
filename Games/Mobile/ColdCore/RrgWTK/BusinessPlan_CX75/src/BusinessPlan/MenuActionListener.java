package BusinessPlan;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Item;

public interface MenuActionListener
{
    public Display getDisplay();
    public Image getImageForIndex(int _index);
    public String getStringForIndex(int _index);
    public void processListItem(int _screenId, int _itemId);

    public void processFormItem(int _screenId,int _index,int _commandID,Item _formItem);

    public void processCommand(Displayable _screen,int _screenId, int _commandId, int _selectedId);

    public boolean enableCommand(int _screenId, int _commandId);

    public Displayable customScreen(int _screenId);

    public Object customItem(int _screenId, int _itemId, boolean _getImage);

    public boolean enableItem(int _screenId, int _itemId);

    public void onExitScreen(Displayable _screen, int _screenId);
}