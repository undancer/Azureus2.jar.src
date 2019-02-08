package com.aelitis.azureus.core.devices;

import java.io.File;
import java.net.InetAddress;

public abstract interface DeviceMediaRenderer
  extends Device, TranscodeTarget
{
  public static final int RS_PS3 = 1;
  public static final int RS_XBOX = 2;
  public static final int RS_ITUNES = 3;
  public static final int RS_WII = 4;
  public static final int RS_BROWSER = 5;
  public static final int RS_OTHER = 6;
  
  public abstract int getRendererSpecies();
  
  public abstract boolean canFilterFilesView();
  
  public abstract void setFilterFilesView(boolean paramBoolean);
  
  public abstract boolean getFilterFilesView();
  
  public abstract boolean canCopyToDevice();
  
  public abstract boolean getAutoCopyToDevice();
  
  public abstract void setAutoCopyToDevice(boolean paramBoolean);
  
  public abstract int getCopyToDevicePending();
  
  public abstract boolean canAutoStartDevice();
  
  public abstract boolean getAutoStartDevice();
  
  public abstract void setAutoStartDevice(boolean paramBoolean);
  
  public abstract boolean canCopyToFolder();
  
  public abstract void setCanCopyToFolder(boolean paramBoolean);
  
  public abstract File getCopyToFolder();
  
  public abstract void setCopyToFolder(File paramFile);
  
  public abstract int getCopyToFolderPending();
  
  public abstract boolean getAutoCopyToFolder();
  
  public abstract void setAutoCopyToFolder(boolean paramBoolean);
  
  public abstract void manualCopy()
    throws DeviceManagerException;
  
  public abstract boolean canAssociate();
  
  public abstract void associate(DeviceManager.UnassociatedDevice paramUnassociatedDevice);
  
  public abstract boolean canShowCategories();
  
  public abstract void setShowCategories(boolean paramBoolean);
  
  public abstract boolean getShowCategories();
  
  public abstract boolean isRSSPublishEnabled();
  
  public abstract void setRSSPublishEnabled(boolean paramBoolean);
  
  public abstract long getAutoShareToTagID();
  
  public abstract void setAutoShareToTagID(long paramLong);
  
  public abstract InetAddress getAddress();
  
  public abstract boolean canRestrictAccess();
  
  public abstract String getAccessRestriction();
  
  public abstract void setAccessRestriction(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceMediaRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */