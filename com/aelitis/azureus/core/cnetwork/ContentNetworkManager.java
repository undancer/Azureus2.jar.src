package com.aelitis.azureus.core.cnetwork;

public abstract interface ContentNetworkManager
{
  public abstract ContentNetwork[] getContentNetworks();
  
  public abstract ContentNetwork getContentNetwork(long paramLong);
  
  public abstract ContentNetwork getStartupContentNetwork();
  
  public abstract ContentNetwork getContentNetworkForURL(String paramString);
  
  public abstract void addContentNetwork(long paramLong)
    throws ContentNetworkException;
  
  public abstract void addListener(ContentNetworkListener paramContentNetworkListener);
  
  public abstract void removeListener(ContentNetworkListener paramContentNetworkListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/ContentNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */