package com.aelitis.azureus.core.drivedetector;

import java.io.File;
import java.util.Map;

public abstract interface DriveDetectedInfo
{
  public abstract File getLocation();
  
  public abstract Object getInfo(String paramString);
  
  public abstract Map<String, Object> getInfoMap();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/DriveDetectedInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */