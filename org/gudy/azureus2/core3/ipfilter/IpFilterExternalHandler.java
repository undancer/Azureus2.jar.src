package org.gudy.azureus2.core3.ipfilter;

import java.net.InetAddress;

public abstract interface IpFilterExternalHandler
{
  public abstract boolean isBlocked(byte[] paramArrayOfByte, String paramString);
  
  public abstract boolean isBlocked(byte[] paramArrayOfByte, InetAddress paramInetAddress);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/IpFilterExternalHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */