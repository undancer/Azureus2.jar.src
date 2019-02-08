package com.aelitis.azureus.core.devices;

public abstract interface DeviceMediaRendererTemplate
  extends DeviceTemplate
{
  public abstract int getRendererSpecies();
  
  public abstract TranscodeProfile[] getProfiles();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceMediaRendererTemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */