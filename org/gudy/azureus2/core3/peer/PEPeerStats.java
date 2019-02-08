package org.gudy.azureus2.core3.peer;

public abstract interface PEPeerStats
{
  public abstract PEPeer getPeer();
  
  public abstract void setPeer(PEPeer paramPEPeer);
  
  public abstract void dataBytesSent(int paramInt);
  
  public abstract void protocolBytesSent(int paramInt);
  
  public abstract void dataBytesReceived(int paramInt);
  
  public abstract void protocolBytesReceived(int paramInt);
  
  public abstract void bytesDiscarded(int paramInt);
  
  public abstract void hasNewPiece(int paramInt);
  
  public abstract void statisticalSentPiece(int paramInt);
  
  public abstract long getDataReceiveRate();
  
  public abstract long getProtocolReceiveRate();
  
  public abstract long getTotalDataBytesReceived();
  
  public abstract long getTotalProtocolBytesReceived();
  
  public abstract long getDataSendRate();
  
  public abstract long getProtocolSendRate();
  
  public abstract long getTotalDataBytesSent();
  
  public abstract long getTotalProtocolBytesSent();
  
  public abstract long getSmoothDataReceiveRate();
  
  public abstract long getTotalBytesDiscarded();
  
  public abstract long getEstimatedDownloadRateOfPeer();
  
  public abstract long getEstimatedUploadRateOfPeer();
  
  public abstract long getEstimatedSecondsToCompletion();
  
  public abstract long getTotalBytesDownloadedByPeer();
  
  public abstract void diskReadComplete(long paramLong);
  
  public abstract int getTotalDiskReadCount();
  
  public abstract int getAggregatedDiskReadCount();
  
  public abstract long getTotalDiskReadBytes();
  
  public abstract void setUploadRateLimitBytesPerSecond(int paramInt);
  
  public abstract void setDownloadRateLimitBytesPerSecond(int paramInt);
  
  public abstract int getUploadRateLimitBytesPerSecond();
  
  public abstract int getDownloadRateLimitBytesPerSecond();
  
  public abstract int getPermittedBytesToSend();
  
  public abstract void permittedSendBytesUsed(int paramInt);
  
  public abstract int getPermittedBytesToReceive();
  
  public abstract void permittedReceiveBytesUsed(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */