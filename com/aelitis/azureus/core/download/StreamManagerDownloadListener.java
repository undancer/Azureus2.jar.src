package com.aelitis.azureus.core.download;

public abstract interface StreamManagerDownloadListener
{
  public abstract void updateActivity(String paramString);
  
  public abstract void updateStats(int paramInt1, int paramInt2, long paramLong, int paramInt3);
  
  public abstract void ready();
  
  public abstract void failed(Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/StreamManagerDownloadListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */