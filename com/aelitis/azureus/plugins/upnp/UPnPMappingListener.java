package com.aelitis.azureus.plugins.upnp;

public abstract interface UPnPMappingListener
{
  public abstract void mappingChanged(UPnPMapping paramUPnPMapping);
  
  public abstract void mappingDestroyed(UPnPMapping paramUPnPMapping);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/upnp/UPnPMappingListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */