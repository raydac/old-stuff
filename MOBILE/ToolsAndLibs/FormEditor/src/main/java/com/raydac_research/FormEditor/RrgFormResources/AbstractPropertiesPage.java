package com.raydac_research.FormEditor.RrgFormResources;

import javax.swing.*;
import java.io.IOException;
import java.awt.*;

public interface AbstractPropertiesPage
{
    public JPanel getPanel(Component _parent);
    public void fillPropertiesFromResource(AbstractRrgResource _resource);
    public void fillResourceFromProperties(AbstractRrgResource _resource) throws IOException;
    public AbstractRrgResource makeNewResource(String _id) throws IOException;
    public String isDataOk();
}
