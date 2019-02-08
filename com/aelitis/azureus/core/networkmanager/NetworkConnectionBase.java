package com.aelitis.azureus.core.networkmanager;

public abstract interface NetworkConnectionBase
{
  public abstract ConnectionEndpoint getEndpoint();
  
  public abstract void notifyOfException(Throwable paramThrowable);
  
  public abstract OutgoingMessageQueue getOutgoingMessageQueue();
  
  public abstract IncomingMessageQueue getIncomingMessageQueue();
  
  public abstract TransportBase getTransportBase();
  
  public abstract int getMssSize();
  
  public abstract boolean isIncoming();
  
  public abstract boolean isLANLocal();
  
  public abstract void setUploadLimit(int paramInt);
  
  public abstract int getUploadLimit();
  
  public abstract void setDownloadLimit(int paramInt);
  
  public abstract int getDownloadLimit();
  
  public abstract LimitedRateGroup[] getRateLimiters(boolean paramBoolean);
  
  public abstract void addRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract void removeRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/NetworkConnectionBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */