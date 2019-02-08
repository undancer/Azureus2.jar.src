package com.aelitis.azureus.core.devices;

import java.util.Map;
import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;

public abstract interface DeviceManagerDiscoveryListener
{
  public abstract boolean browseReceived(TrackerWebPageRequest paramTrackerWebPageRequest, Map<String, Object> paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceManagerDiscoveryListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */