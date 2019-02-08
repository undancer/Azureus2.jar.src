package org.gudy.azureus2.plugins.download;

public abstract interface DownloadStats
{
  public static final int HEALTH_STOPPED = 1;
  public static final int HEALTH_NO_TRACKER = 2;
  public static final int HEALTH_NO_REMOTE = 3;
  public static final int HEALTH_OK = 4;
  public static final int HEALTH_KO = 5;
  public static final int HEALTH_ERROR = 6;
  
  public abstract String getStatus();
  
  public abstract String getStatus(boolean paramBoolean);
  
  public abstract String getDownloadDirectory();
  
  public abstract String getTargetFileOrDir();
  
  public abstract String getTrackerStatus();
  
  public abstract int getCompleted();
  
  public abstract int getDownloadCompleted(boolean paramBoolean);
  
  public abstract int getCheckingDoneInThousandNotation();
  
  public abstract void resetUploadedDownloaded(long paramLong1, long paramLong2);
  
  public abstract long getDownloaded();
  
  public abstract long getDownloaded(boolean paramBoolean);
  
  public abstract long getRemaining();
  
  public abstract long getRemainingExcludingDND();
  
  public abstract long getUploaded();
  
  public abstract long getUploaded(boolean paramBoolean);
  
  public abstract long getDiscarded();
  
  public abstract long getDownloadAverage();
  
  public abstract long getDownloadAverage(boolean paramBoolean);
  
  public abstract long getUploadAverage();
  
  public abstract long getUploadAverage(boolean paramBoolean);
  
  public abstract long getTotalAverage();
  
  public abstract String getElapsedTime();
  
  public abstract String getETA();
  
  public abstract long getETASecs();
  
  public abstract long getHashFails();
  
  public abstract int getShareRatio();
  
  public abstract long getTimeStarted();
  
  public abstract long getTimeStartedSeeding();
  
  public abstract float getAvailability();
  
  public abstract long getSecondsDownloading();
  
  public abstract long getSecondsOnlySeeding();
  
  public abstract long getSecondsSinceLastDownload();
  
  public abstract long getSecondsSinceLastUpload();
  
  public abstract int getHealth();
  
  public abstract long getBytesUnavailable();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */