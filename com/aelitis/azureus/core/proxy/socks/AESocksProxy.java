package com.aelitis.azureus.core.proxy.socks;

public abstract interface AESocksProxy
{
  public static final String PV_4 = "V4";
  public static final String PV_4a = "V4a";
  public static final String PV_5 = "V5";
  
  public abstract int getPort();
  
  public abstract AESocksProxyPlugableConnection getDefaultPlugableConnection(AESocksProxyConnection paramAESocksProxyConnection);
  
  public abstract void setNextSOCKSProxy(String paramString1, int paramInt, String paramString2);
  
  public abstract String getNextSOCKSProxyHost();
  
  public abstract int getNextSOCKSProxyPort();
  
  public abstract String getNextSOCKSProxyVersion();
  
  public abstract void setAllowExternalConnections(boolean paramBoolean);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */