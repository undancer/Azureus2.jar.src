package com.aelitis.azureus.core.devices;

public abstract interface TranscodeProfile
{
  public abstract String getUID();
  
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract boolean isStreamable();
  
  public abstract String getIconURL();
  
  public abstract int getIconIndex();
  
  public abstract String getFileExtension();
  
  public abstract String getDeviceClassification();
  
  public abstract TranscodeProvider getProvider()
    throws TranscodeException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */