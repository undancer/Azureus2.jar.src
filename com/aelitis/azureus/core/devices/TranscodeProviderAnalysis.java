package com.aelitis.azureus.core.devices;

public abstract interface TranscodeProviderAnalysis
{
  public static final int PT_TRANSCODE_REQUIRED = 1;
  public static final int PT_DURATION_MILLIS = 2;
  public static final int PT_VIDEO_WIDTH = 3;
  public static final int PT_VIDEO_HEIGHT = 4;
  public static final int PT_SOURCE_SIZE = 6;
  public static final int PT_ESTIMATED_XCODE_SIZE = 7;
  public static final int PT_FORCE_TRANSCODE = 5;
  
  public abstract void cancel();
  
  public abstract boolean foundVideoStream();
  
  public abstract boolean getBooleanProperty(int paramInt);
  
  public abstract void setBooleanProperty(int paramInt, boolean paramBoolean);
  
  public abstract long getLongProperty(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeProviderAnalysis.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */