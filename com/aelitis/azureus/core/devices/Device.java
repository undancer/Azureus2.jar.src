package com.aelitis.azureus.core.devices;

import com.aelitis.azureus.core.vuzefile.VuzeFile;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

public abstract interface Device
{
  public static final int DT_UNKNOWN = 0;
  public static final int DT_INTERNET_GATEWAY = 1;
  public static final int DT_CONTENT_DIRECTORY = 2;
  public static final int DT_MEDIA_RENDERER = 3;
  public static final int DT_INTERNET = 4;
  public static final int DT_OFFLINE_DOWNLOADER = 5;
  
  public abstract int getType();
  
  public abstract String getID();
  
  public abstract String getName();
  
  public abstract void setName(String paramString, boolean paramBoolean);
  
  public abstract String getClassification();
  
  public abstract String getShortDescription();
  
  public abstract void alive();
  
  public abstract boolean isAlive();
  
  public abstract boolean isLivenessDetectable();
  
  public abstract boolean isBusy();
  
  public abstract boolean isManual();
  
  public abstract void setHidden(boolean paramBoolean);
  
  public abstract boolean isHidden();
  
  public abstract void setTagged(boolean paramBoolean);
  
  public abstract boolean isTagged();
  
  public abstract boolean isBrowsable();
  
  public abstract browseLocation[] getBrowseLocations();
  
  public abstract InetAddress getAddress();
  
  public abstract void setAddress(InetAddress paramInetAddress);
  
  public abstract void setTransientProperty(Object paramObject1, Object paramObject2);
  
  public abstract Object getTransientProperty(Object paramObject);
  
  public abstract String[][] getDisplayProperties();
  
  public abstract void requestAttention();
  
  public abstract void remove();
  
  public abstract void setCanRemove(boolean paramBoolean);
  
  public abstract boolean canRemove();
  
  public abstract String getInfo();
  
  public abstract String getError();
  
  public abstract String getStatus();
  
  public abstract void addListener(DeviceListener paramDeviceListener);
  
  public abstract void removeListener(DeviceListener paramDeviceListener);
  
  public abstract String getString();
  
  public abstract boolean isGenericUSB();
  
  public abstract void setGenericUSB(boolean paramBoolean);
  
  public abstract String getImageID();
  
  public abstract void setImageID(String paramString);
  
  public abstract boolean isNameAutomatic();
  
  public abstract void setExportable(boolean paramBoolean);
  
  public abstract boolean isExportable();
  
  public abstract URL getWikiURL();
  
  public abstract VuzeFile getVuzeFile()
    throws IOException;
  
  public abstract TranscodeProfile[] getDirectTranscodeProfiles();
  
  public static abstract interface browseLocation
  {
    public abstract String getName();
    
    public abstract URL getURL();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/Device.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */