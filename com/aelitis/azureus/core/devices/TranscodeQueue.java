package com.aelitis.azureus.core.devices;

import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;

public abstract interface TranscodeQueue
{
  public abstract TranscodeJob add(TranscodeTarget paramTranscodeTarget, TranscodeProfile paramTranscodeProfile, DiskManagerFileInfo paramDiskManagerFileInfo, boolean paramBoolean)
    throws TranscodeException;
  
  public abstract TranscodeJob add(TranscodeTarget paramTranscodeTarget, TranscodeProfile paramTranscodeProfile, DiskManagerFileInfo paramDiskManagerFileInfo, int paramInt, boolean paramBoolean)
    throws TranscodeException;
  
  public abstract TranscodeJob[] getJobs();
  
  public abstract void pause();
  
  public abstract boolean isPaused();
  
  public abstract void resume();
  
  public abstract int getJobCount();
  
  public abstract TranscodeJob getCurrentJob();
  
  public abstract boolean isTranscoding();
  
  public abstract long getMaxBytesPerSecond();
  
  public abstract void setMaxBytesPerSecond(long paramLong);
  
  public abstract void addListener(TranscodeQueueListener paramTranscodeQueueListener);
  
  public abstract void removeListener(TranscodeQueueListener paramTranscodeQueueListener);
  
  public abstract void addActionListener(TranscodeQueueActionListener paramTranscodeQueueActionListener);
  
  public abstract void removeActionListener(TranscodeQueueActionListener paramTranscodeQueueActionListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */