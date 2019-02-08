package com.aelitis.net.upnp;

public abstract interface UPnPLogListener
{
  public static final int TYPE_ALWAYS = 1;
  public static final int TYPE_ONCE_PER_SESSION = 2;
  public static final int TYPE_ONCE_EVER = 3;
  
  public abstract void log(String paramString);
  
  public abstract void logAlert(String paramString, boolean paramBoolean, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPLogListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */