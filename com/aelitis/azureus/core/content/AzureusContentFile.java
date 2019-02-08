package com.aelitis.azureus.core.content;

import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;

public abstract interface AzureusContentFile
{
  public static final String PT_TITLE = "title";
  public static final String PT_CREATOR = "creator";
  public static final String PT_DATE = "date";
  public static final String PT_DURATION = "duration";
  public static final String PT_VIDEO_WIDTH = "video_width";
  public static final String PT_VIDEO_HEIGHT = "video_height";
  public static final String PT_CATEGORIES = "cats";
  public static final String PT_TAGS = "tags";
  public static final String PT_PERCENT_DONE = "percent";
  public static final String PT_ETA = "eta";
  
  public abstract DiskManagerFileInfo getFile();
  
  public abstract Object getProperty(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusContentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */