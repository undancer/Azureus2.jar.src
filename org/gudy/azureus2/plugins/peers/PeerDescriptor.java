package org.gudy.azureus2.plugins.peers;

public abstract interface PeerDescriptor
{
  public abstract String getIP();
  
  public abstract int getTCPPort();
  
  public abstract int getUDPPort();
  
  public abstract boolean useCrypto();
  
  public abstract String getPeerSource();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */