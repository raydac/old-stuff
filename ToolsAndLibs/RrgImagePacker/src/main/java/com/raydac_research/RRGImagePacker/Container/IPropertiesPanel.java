package com.raydac_research.RRGImagePacker.Container;

import javax.swing.*;

public interface IPropertiesPanel
{
    public String isDataOk();
    public void fillObjectFromPanel(Object _object);
    public JPanel getPanel(Object _obj,Container _container);
}
