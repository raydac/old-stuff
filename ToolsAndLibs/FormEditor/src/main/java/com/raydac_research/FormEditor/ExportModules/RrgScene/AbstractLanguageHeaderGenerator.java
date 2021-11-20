package com.raydac_research.FormEditor.ExportModules.RrgScene;

import java.io.PrintStream;
import java.io.IOException;

public interface AbstractLanguageHeaderGenerator
{
    public String getExtension()throws IOException;
    public void generateHeader(String _fileNameWithoutExt,PrintStream _outStream,RrgScene _scene) throws IOException;
}
