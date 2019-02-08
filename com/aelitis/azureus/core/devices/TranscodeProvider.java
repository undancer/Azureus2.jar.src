package com.aelitis.azureus.core.devices;

import java.io.File;
import java.net.URL;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;

public abstract interface TranscodeProvider
{
  public static final int TP_VUZE = 1;
  
  public abstract int getID();
  
  public abstract String getName();
  
  public abstract TranscodeProfile[] getProfiles();
  
  public abstract TranscodeProfile[] getProfiles(String paramString);
  
  public abstract TranscodeProfile getProfile(String paramString);
  
  public abstract TranscodeProfile addProfile(File paramFile)
    throws TranscodeException;
  
  public abstract TranscodeProviderAnalysis analyse(TranscodeProviderAdapter paramTranscodeProviderAdapter, DiskManagerFileInfo paramDiskManagerFileInfo, TranscodeProfile paramTranscodeProfile)
    throws TranscodeException;
  
  public abstract TranscodeProviderJob transcode(TranscodeProviderAdapter paramTranscodeProviderAdapter, TranscodeProviderAnalysis paramTranscodeProviderAnalysis, boolean paramBoolean, DiskManagerFileInfo paramDiskManagerFileInfo, TranscodeProfile paramTranscodeProfile, URL paramURL)
    throws TranscodeException;
  
  public abstract File getAssetDirectory();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */