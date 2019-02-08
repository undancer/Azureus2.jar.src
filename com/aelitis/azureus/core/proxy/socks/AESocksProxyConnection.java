package com.aelitis.azureus.core.proxy.socks;

import com.aelitis.azureus.core.proxy.AEProxyConnection;
import java.io.IOException;

public abstract interface AESocksProxyConnection
{
  public abstract AESocksProxy getProxy();
  
  public abstract AEProxyConnection getConnection();
  
  public abstract String getUsername();
  
  public abstract String getPassword();
  
  public abstract void disableDNSLookups();
  
  public abstract void enableDNSLookups();
  
  public abstract boolean areDNSLookupsEnabled();
  
  public abstract void connected()
    throws IOException;
  
  public abstract boolean isClosed();
  
  public abstract void close()
    throws IOException;
  
  public abstract void setDelegate(AESocksProxyPlugableConnection paramAESocksProxyPlugableConnection);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxyConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */