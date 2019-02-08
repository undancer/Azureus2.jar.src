package org.gudy.azureus2.plugins.peers;

public abstract interface PeerManagerStats
{
  public abstract int getConnectedSeeds();
  
  public abstract int getConnectedLeechers();
  
  public abstract long getDownloaded();
  
  public abstract long getUploaded();
  
  public abstract long getDownloadAverage();
  
  public abstract long getUploadAverage();
  
  public abstract long getDiscarded();
  
  public abstract long getHashFailBytes();
  
  public abstract int getPermittedBytesToReceive();
  
  public abstract void permittedReceiveBytesUsed(int paramInt);
  
  public abstract int getPermittedBytesToSend();
  
  public abstract void permittedSendBytesUsed(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerManagerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */