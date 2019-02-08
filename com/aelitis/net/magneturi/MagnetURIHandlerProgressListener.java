package com.aelitis.net.magneturi;

public abstract interface MagnetURIHandlerProgressListener
{
  public abstract void reportSize(long paramLong);
  
  public abstract void reportActivity(String paramString);
  
  public abstract void reportCompleteness(int paramInt);
  
  public abstract boolean cancelled();
  
  public abstract boolean verbose();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/magneturi/MagnetURIHandlerProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */