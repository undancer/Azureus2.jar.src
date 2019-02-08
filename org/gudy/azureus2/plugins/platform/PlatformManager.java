package org.gudy.azureus2.plugins.platform;

import java.io.File;

public abstract interface PlatformManager
{
  public static final int LOC_USER_DATA = 1;
  public static final int LOC_MUSIC = 2;
  public static final int LOC_DOCUMENTS = 3;
  public static final int LOC_VIDEO = 4;
  
  public abstract boolean isAdditionalFileTypeRegistered(String paramString1, String paramString2)
    throws PlatformManagerException;
  
  public abstract void registerAdditionalFileType(String paramString1, String paramString2, String paramString3, String paramString4)
    throws PlatformManagerException;
  
  public abstract void unregisterAdditionalFileType(String paramString1, String paramString2)
    throws PlatformManagerException;
  
  public abstract void showFile(String paramString)
    throws PlatformManagerException;
  
  public abstract File getLocation(long paramLong)
    throws PlatformManagerException;
  
  public abstract String getComputerName();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/platform/PlatformManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */