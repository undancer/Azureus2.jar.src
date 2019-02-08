package org.gudy.azureus2.core3.tracker.host;

public abstract interface TRHostPeer
{
  public abstract boolean isSeed();
  
  public abstract long getUploaded();
  
  public abstract long getDownloaded();
  
  public abstract long getAmountLeft();
  
  public abstract String getIP();
  
  public abstract String getIPRaw();
  
  public abstract int getPort();
  
  public abstract byte[] getPeerID();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */