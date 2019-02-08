package com.aelitis.net.upnpms;

import java.net.URL;

public abstract interface UPNPMSItem
  extends UPNPMSNode
{
  public static final String IC_AUDIO = "audio";
  public static final String IC_VIDEO = "video";
  public static final String IC_IMAGE = "image";
  public static final String IC_OTHER = "other";
  
  public abstract String getItemClass();
  
  public abstract long getSize();
  
  public abstract URL getURL();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/UPNPMSItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */