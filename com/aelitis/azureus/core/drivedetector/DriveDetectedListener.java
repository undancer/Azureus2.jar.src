package com.aelitis.azureus.core.drivedetector;

public abstract interface DriveDetectedListener
{
  public abstract void driveDetected(DriveDetectedInfo paramDriveDetectedInfo);
  
  public abstract void driveRemoved(DriveDetectedInfo paramDriveDetectedInfo);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/DriveDetectedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */