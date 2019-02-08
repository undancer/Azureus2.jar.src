package com.aelitis.azureus.core.vuzefile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract interface VuzeFile
{
  public abstract String getName();
  
  public abstract VuzeFileComponent[] getComponents();
  
  public abstract VuzeFileComponent addComponent(int paramInt, Map paramMap);
  
  public abstract byte[] exportToBytes()
    throws IOException;
  
  public abstract Map exportToMap()
    throws IOException;
  
  public abstract String exportToJSON()
    throws IOException;
  
  public abstract void write(File paramFile)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/vuzefile/VuzeFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */