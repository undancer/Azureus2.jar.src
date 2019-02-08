package com.aelitis.azureus.core.drivedetector;

import java.io.File;
import java.util.Map;

public abstract interface DriveDetector
{
  public abstract void driveDetected(File paramFile, Map paramMap);
  
  public abstract void driveRemoved(File paramFile);
  
  public abstract void addListener(DriveDetectedListener paramDriveDetectedListener);
  
  public abstract void removeListener(DriveDetectedListener paramDriveDetectedListener);
  
  public abstract DriveDetectedInfo[] getDetectedDriveInfo();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/DriveDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */