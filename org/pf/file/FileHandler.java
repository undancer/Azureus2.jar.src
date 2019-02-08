package org.pf.file;

import java.io.File;

public abstract interface FileHandler
{
  public abstract boolean handleFile(File paramFile);
  
  public abstract boolean handleException(Exception paramException, File paramFile);
  
  public abstract boolean directoryEnd(File paramFile);
  
  public abstract boolean directoryStart(File paramFile, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/pf/file/FileHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */