package com.aelitis.azureus.core.devices;

import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;

public abstract interface TranscodeJob
{
  public static final int ST_QUEUED = 0;
  public static final int ST_RUNNING = 1;
  public static final int ST_PAUSED = 2;
  public static final int ST_COMPLETE = 3;
  public static final int ST_CANCELLED = 4;
  public static final int ST_FAILED = 5;
  public static final int ST_STOPPED = 6;
  public static final int ST_REMOVED = 7;
  
  public abstract String getName();
  
  public abstract TranscodeTarget getTarget();
  
  public abstract TranscodeProfile getProfile();
  
  public abstract DiskManagerFileInfo getFile();
  
  public abstract TranscodeFile getTranscodeFile();
  
  public abstract int getTranscodeRequirement();
  
  public abstract int getIndex();
  
  public abstract int getState();
  
  public abstract long getDownloadETA();
  
  public abstract int getPercentComplete();
  
  public abstract long getETASecs();
  
  public abstract String getETA();
  
  public abstract String getError();
  
  public abstract void setEnableAutoRetry(boolean paramBoolean);
  
  public abstract boolean getEnableAutoRetry();
  
  public abstract void setPreferDirectInput(boolean paramBoolean);
  
  public abstract boolean getPreferDirectInput();
  
  public abstract boolean canPause();
  
  public abstract void pause();
  
  public abstract void resume();
  
  public abstract void queue();
  
  public abstract void stop();
  
  public abstract void remove()
    throws TranscodeActionVetoException;
  
  public abstract void removeForce();
  
  public abstract void moveUp();
  
  public abstract void moveDown();
  
  public abstract long getProcessTime();
  
  public abstract void analyseNow(TranscodeAnalysisListener paramTranscodeAnalysisListener)
    throws TranscodeException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */