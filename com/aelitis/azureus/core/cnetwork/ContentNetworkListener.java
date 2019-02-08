package com.aelitis.azureus.core.cnetwork;

public abstract interface ContentNetworkListener
{
  public abstract void networkAdded(ContentNetwork paramContentNetwork);
  
  public abstract void networkAddFailed(long paramLong, Throwable paramThrowable);
  
  public abstract void networkChanged(ContentNetwork paramContentNetwork);
  
  public abstract void networkRemoved(ContentNetwork paramContentNetwork);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/ContentNetworkListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */