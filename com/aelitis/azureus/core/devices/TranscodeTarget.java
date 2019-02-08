package com.aelitis.azureus.core.devices;

import java.io.File;

public abstract interface TranscodeTarget
{
  public static final int TRANSCODE_UNKNOWN = -1;
  public static final int TRANSCODE_NEVER = 1;
  public static final int TRANSCODE_WHEN_REQUIRED = 2;
  public static final int TRANSCODE_ALWAYS = 3;
  
  public abstract String getID();
  
  public abstract Device getDevice();
  
  public abstract TranscodeFile[] getFiles();
  
  public abstract File getWorkingDirectory();
  
  public abstract void setWorkingDirectory(File paramFile);
  
  public abstract TranscodeProfile[] getTranscodeProfiles();
  
  public abstract TranscodeProfile getDefaultTranscodeProfile()
    throws TranscodeException;
  
  public abstract void setDefaultTranscodeProfile(TranscodeProfile paramTranscodeProfile);
  
  public abstract TranscodeProfile getBlankProfile();
  
  public abstract int getTranscodeRequirement();
  
  public abstract void setTranscodeRequirement(int paramInt);
  
  public abstract boolean getAlwaysCacheFiles();
  
  public abstract void setAlwaysCacheFiles(boolean paramBoolean);
  
  public abstract boolean isTranscoding();
  
  public abstract boolean isNonSimple();
  
  public abstract boolean isAudioCompatible(TranscodeFile paramTranscodeFile);
  
  public abstract void addListener(TranscodeTargetListener paramTranscodeTargetListener);
  
  public abstract void removeListener(TranscodeTargetListener paramTranscodeTargetListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */