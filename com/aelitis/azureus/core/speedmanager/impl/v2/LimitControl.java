package com.aelitis.azureus.core.speedmanager.impl.v2;

public abstract interface LimitControl
{
  public abstract SMUpdate adjust(float paramFloat);
  
  public abstract void updateLimits(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void updateSeedSettings(float paramFloat);
  
  public abstract void updateStatus(int paramInt1, SaturatedMode paramSaturatedMode1, int paramInt2, SaturatedMode paramSaturatedMode2, TransferMode paramTransferMode);
  
  public abstract void setDownloadUnlimitedMode(boolean paramBoolean);
  
  public abstract boolean isDownloadUnlimitedMode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/LimitControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */