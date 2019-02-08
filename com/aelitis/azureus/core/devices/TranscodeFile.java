package com.aelitis.azureus.core.devices;

import java.io.File;
import java.net.URL;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;

public abstract interface TranscodeFile
{
  public static final String PT_COMPLETE = "comp";
  public static final String PT_COPIED = "copied";
  public static final String PT_COPY_FAILED = "copy_fail";
  public static final String PT_CATEGORY = "cat";
  public static final String PT_TAGS = "tags";
  
  public abstract String getName();
  
  public abstract DiskManagerFileInfo getSourceFile()
    throws TranscodeException;
  
  public abstract DiskManagerFileInfo getTargetFile()
    throws TranscodeException;
  
  public abstract String getProfileName();
  
  public abstract long getCreationDateMillis();
  
  public abstract boolean isComplete();
  
  public abstract boolean getTranscodeRequired();
  
  public abstract boolean isCopiedToDevice();
  
  public abstract long getCopyToDeviceFails();
  
  public abstract void retryCopyToDevice();
  
  public abstract boolean isTemplate();
  
  public abstract long getDurationMillis();
  
  public abstract long getVideoWidth();
  
  public abstract long getVideoHeight();
  
  public abstract long getEstimatedTranscodeSize();
  
  public abstract String[] getCategories();
  
  public abstract void setCategories(String[] paramArrayOfString);
  
  public abstract String[] getTags(boolean paramBoolean);
  
  public abstract void setTags(String[] paramArrayOfString);
  
  public abstract Device getDevice();
  
  public abstract File getCacheFileIfExists();
  
  public abstract TranscodeJob getJob();
  
  public abstract URL getStreamURL();
  
  public abstract URL getStreamURL(String paramString);
  
  public abstract void delete(boolean paramBoolean)
    throws TranscodeException;
  
  public abstract void setTransientProperty(Object paramObject1, Object paramObject2);
  
  public abstract Object getTransientProperty(Object paramObject);
  
  public abstract boolean isDeleted();
  
  public abstract boolean isCopyingToDevice();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */