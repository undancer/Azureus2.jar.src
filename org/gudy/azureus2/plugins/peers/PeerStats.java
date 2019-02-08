package org.gudy.azureus2.plugins.peers;

public abstract interface PeerStats
{
  public abstract int getDownloadAverage();
  
  public abstract int getReception();
  
  public abstract int getUploadAverage();
  
  public abstract int getTotalAverage();
  
  public abstract long getTotalDiscarded();
  
  public abstract long getTotalSent();
  
  public abstract long getTotalReceived();
  
  public abstract int getStatisticSentAverage();
  
  public abstract int getPermittedBytesToReceive();
  
  public abstract void permittedReceiveBytesUsed(int paramInt);
  
  public abstract int getPermittedBytesToSend();
  
  public abstract void permittedSendBytesUsed(int paramInt);
  
  public abstract void received(int paramInt);
  
  public abstract void sent(int paramInt);
  
  public abstract void discarded(int paramInt);
  
  public abstract long getTimeSinceConnectionEstablished();
  
  public abstract void setDownloadRateLimit(int paramInt);
  
  public abstract int getDownloadRateLimit();
  
  public abstract void setUploadRateLimit(int paramInt);
  
  public abstract int getUploadRateLimit();
  
  public abstract long getOverallBytesRemaining();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */