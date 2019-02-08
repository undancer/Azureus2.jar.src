package com.aelitis.azureus.core.speedmanager;

public abstract interface SpeedManagerAdapter
{
  public abstract int getCurrentProtocolUploadSpeed(int paramInt);
  
  public abstract int getCurrentDataUploadSpeed(int paramInt);
  
  public abstract int getCurrentUploadLimit();
  
  public abstract void setCurrentUploadLimit(int paramInt);
  
  public abstract int getCurrentDownloadLimit();
  
  public abstract void setCurrentDownloadLimit(int paramInt);
  
  public abstract int getCurrentProtocolDownloadSpeed(int paramInt);
  
  public abstract int getCurrentDataDownloadSpeed(int paramInt);
  
  public abstract Object getLimits();
  
  public abstract void setLimits(Object paramObject, boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */