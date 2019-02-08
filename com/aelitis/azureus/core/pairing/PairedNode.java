package com.aelitis.azureus.core.pairing;

import java.net.InetAddress;
import java.util.List;

public abstract interface PairedNode
{
  public abstract String getAccessCode();
  
  public abstract List<InetAddress> getAddresses();
  
  public abstract List<PairedService> getServices();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairedNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */