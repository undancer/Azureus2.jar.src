package org.gudy.azureus2.core3.download;

public abstract interface DownloadManagerStats
{
  public abstract int getCompleted();
  
  public abstract int getDownloadCompleted(boolean paramBoolean);
  
  public abstract void setDownloadCompletedBytes(long paramLong);
  
  public abstract long getDownloadCompletedBytes();
  
  public abstract void recalcDownloadCompleteBytes();
  
  public abstract long getTotalDataBytesReceived();
  
  public abstract long getTotalGoodDataBytesReceived();
  
  public abstract long getTotalProtocolBytesReceived();
  
  public abstract long getSessionDataBytesReceived();
  
  public abstract long getTotalDataBytesSent();
  
  public abstract long getTotalProtocolBytesSent();
  
  public abstract long getSessionDataBytesSent();
  
  public abstract void resetTotalBytesSentReceived(long paramLong1, long paramLong2);
  
  public abstract long getRemaining();
  
  public abstract long getDiscarded();
  
  public abstract long getHashFailBytes();
  
  public abstract long getHashFailCount();
  
  public abstract int getShareRatio();
  
  public abstract void setShareRatio(int paramInt);
  
  public abstract long getDataReceiveRate();
  
  public abstract long getProtocolReceiveRate();
  
  public abstract long getDataSendRate();
  
  public abstract long getProtocolSendRate();
  
  public abstract long getTotalAverage();
  
  public abstract long getTotalAveragePerPeer();
  
  public abstract void setRecentHistoryRetention(boolean paramBoolean);
  
  public abstract int[][] getRecentHistory();
  
  public abstract String getElapsedTime();
  
  public abstract long getTimeStarted();
  
  public abstract long getTimeStartedSeeding();
  
  public abstract long getETA();
  
  public abstract long getSmoothedETA();
  
  public abstract long getPeakDataReceiveRate();
  
  public abstract long getPeakDataSendRate();
  
  public abstract long getSmoothedDataReceiveRate();
  
  public abstract long getSmoothedDataSendRate();
  
  public abstract float getAvailability();
  
  public abstract long getSecondsDownloading();
  
  public abstract long getSecondsOnlySeeding();
  
  public abstract void setCompleted(int paramInt);
  
  public abstract int getUploadRateLimitBytesPerSecond();
  
  public abstract void setUploadRateLimitBytesPerSecond(int paramInt);
  
  public abstract int getDownloadRateLimitBytesPerSecond();
  
  public abstract void setDownloadRateLimitBytesPerSecond(int paramInt);
  
  public abstract int getTimeSinceLastDataReceivedInSeconds();
  
  public abstract int getTimeSinceLastDataSentInSeconds();
  
  public abstract long getAvailWentBadTime();
  
  public abstract long getBytesUnavailable();
  
  public abstract void restoreSessionTotals(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6);
  
  public abstract long getRemainingExcludingDND();
  
  public abstract long getSizeExcludingDND();
  
  public abstract int getPercentDoneExcludingDND();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */