package org.gudy.azureus2.plugins.tracker;

public abstract interface TrackerPeer
{
  public abstract boolean isSeed();
  
  public abstract long getAmountLeft();
  
  public abstract long getDownloaded();
  
  public abstract long getUploaded();
  
  public abstract String getIP();
  
  public abstract int getPort();
  
  public abstract byte[] getPeerID();
  
  public abstract String getIPRaw();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */