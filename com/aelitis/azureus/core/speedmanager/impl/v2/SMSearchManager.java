package com.aelitis.azureus.core.speedmanager.impl.v2;

public abstract interface SMSearchManager
{
  public static final String UPLOAD = "Upload";
  public static final String DOWNLOAD = "Download";
  
  public abstract void setEndOfSearch(String paramString, int paramInt);
  
  public abstract void setEndOfSearch(String paramString, int paramInt1, int paramInt2);
  
  public abstract boolean startSearch(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMSearchManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */